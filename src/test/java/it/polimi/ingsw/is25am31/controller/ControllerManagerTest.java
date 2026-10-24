package it.polimi.ingsw.is25am31.controller;

import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.network.AsyncRunner;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ControllerManagerTest {
    private ControllerManager controllerManager;
    private StubVirtualView stubVirtualView;
    private UUID clientId;

    static class StubVirtualView implements VirtualView {
        public UpdateMessage lastUpdate;
        public List<Integer> shownGames = new ArrayList<>();
        public boolean connectionTestCalled = false;
        public UUID receivedUuid;
        public CountDownLatch latch = new CountDownLatch(1);
        public boolean throwExceptionOnConnectionTest = false;

        @Override
        public void showUpdate(UpdateMessage msg) {
            lastUpdate = msg;
            latch.countDown();
        }

        @Override
        public void showGameList(List<Integer> gameList) {
            shownGames = gameList;
            latch.countDown();
        }

        @Override
        public void setUUID(UUID clientId) { receivedUuid = clientId;}

        @Override
        public void connectionTest() throws Exception {
            if (throwExceptionOnConnectionTest) {
                throw new Exception("Simulated disconnection");
            }
            connectionTestCalled = true;
        }

        @Override public void showCard(it.polimi.ingsw.is25am31.model.cards.Card card) {}
        @Override public void showComponent(it.polimi.ingsw.is25am31.model.components.Component component) {}
        @Override public void startGame() {}
        @Override public void lockClient() {}
        @Override public void setNickname() {}
        @Override public void startAssemblingPhase(List<it.polimi.ingsw.is25am31.model.player.Player> players, int level) {}
        @Override public void showShip(it.polimi.ingsw.is25am31.model.player.Ship ship) {}
        @Override public void showFaceUpComponentList(List<it.polimi.ingsw.is25am31.model.components.Component> component) {}
        @Override public void updatePositions(Map<Integer, Integer> positions) {}
        @Override public void effectResolution() {}
        @Override public void startFlightPhase(it.polimi.ingsw.is25am31.model.player.Ship ship, Map<Integer, Integer> positions) {}
        @Override public void showSubDeck(List<it.polimi.ingsw.is25am31.model.cards.Card> subDeck) {}
        @Override public void crewPlacingPhase(it.polimi.ingsw.is25am31.model.player.Ship ship, Map<Integer, Integer> positions) {}
        @Override public void showSecondsLeft(int count, int secondsLeft) {}
        @Override public void finishFinalHourglass() {}
        @Override public void showSecondsLeftForGui(int counter, int secondsLeft) {}
        @Override public void showFinalRanking(Map<it.polimi.ingsw.is25am31.model.player.Player, Integer> ranking) {}
        @Override public void notifyDefeat(int loseType) {}
        @Override public void setGameLevel(int level) {}
        @Override public void connectionResponse(it.polimi.ingsw.is25am31.network.ConnectionResponse connectionResponse) {}
        @Override public void playerJoined(String newPlayer, List<String> players) {}
        @Override public void newLeader(boolean isNewLeader) {}
        @Override public void diceResult(int diceResult) {}
        @Override public void roundResult(boolean roundWon) {}
        @Override public void turnChange(boolean isMyTurn, int turnType, int currentPlayer) {}
    }

    @BeforeEach
    void setUp() {
        controllerManager = new ControllerManager();
        stubVirtualView = new StubVirtualView();
        clientId = UUID.randomUUID();
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    @Test
    void connectUUID() throws Exception {
        {
            controllerManager.connectUUID(stubVirtualView);
            assertTrue(stubVirtualView.latch.await(2, TimeUnit.SECONDS));
            assertNotNull(stubVirtualView.receivedUuid);
            ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
            assertTrue(clients.containsKey(stubVirtualView.receivedUuid));
            assertEquals(UpdateMessage.CREATE_OR_JOIN, stubVirtualView.lastUpdate);
        }
        {
            controllerManager = new ControllerManager();
            StubVirtualView view1 = new StubVirtualView();
            StubVirtualView view2 = new StubVirtualView();
            controllerManager.connectUUID(view1);
            controllerManager.connectUUID(view2);
            ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
            assertEquals(2, clients.size());
            assertTrue(view1.latch.await(2, TimeUnit.SECONDS));
            assertTrue(view2.latch.await(2, TimeUnit.SECONDS));
            assertNotNull(view1.receivedUuid);
            assertNotNull(view2.receivedUuid);
            assertNotEquals(view1.receivedUuid, view2.receivedUuid);
            assertEquals(UpdateMessage.CREATE_OR_JOIN, view1.lastUpdate);
            assertEquals(UpdateMessage.CREATE_OR_JOIN, view2.lastUpdate);
        }
    }

    @Test
    void createNewGame() throws Exception {
        ConcurrentMap<UUID, StubVirtualView> clients = getPrivateField(controllerManager, "clients");
        ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
        ConcurrentMap<Integer, Controller> game = getPrivateField(controllerManager, "game");
        ConcurrentMap<UUID, Integer> connections = getPrivateField(controllerManager, "connections");
        clients.put(clientId, stubVirtualView);
        int initialGameNumber = getPrivateField(controllerManager, "gameNumber");
        controllerManager.createNewGame(clientId);
        int newGameNumber = getPrivateField(controllerManager, "gameNumber");
        assertEquals(initialGameNumber + 1, newGameNumber, "Game number should increment by 1");
        assertNotNull(controllers.get(clientId), "Controller should be present for clientId");
        boolean controllerFoundInGame = false;
        for (Controller c : game.values()) {
            if (c == controllers.get(clientId)) {
                controllerFoundInGame = true;
                break;
            }
        }
        assertTrue(controllerFoundInGame, "Controller should be in game map");
        int connectionValue = connections.get(clientId);
        assertTrue(connectionValue == 5 || connectionValue == 4, "Connection counter should be 5 (initial) or 4 (after first check)");
    }

    @Test
    void viewListGame() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        clients.put(clientId, stubVirtualView);
        controllerManager.createNewGame(clientId);
        controllerManager.viewListGame(clientId);
        assertTrue(stubVirtualView.latch.await(2, TimeUnit.SECONDS));
        assertEquals(List.of(1), stubVirtualView.shownGames);
    }

    @Test
    void ConnectToGame() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
        ConcurrentMap<UUID, Integer> connections = getPrivateField(controllerManager, "connections");
        UUID client1 = UUID.randomUUID();
        clients.put(client1, new StubVirtualView());
        controllerManager.createNewGame(client1);
        UUID client2 = UUID.randomUUID();
        clients.put(client2, new StubVirtualView());
        controllerManager.connectToGame(1, client2);
        assertSame(controllers.get(client1), controllers.get(client2));
        int connectionValue = connections.get(client2);
        assertTrue(connectionValue == 5 || connectionValue == 4, "Connection counter should be 5 (initial) or 4 (after first check)");
        UUID client3 = UUID.randomUUID();
        clients.put(client3, new StubVirtualView());
        controllerManager.createNewGame(client3);
        Controller controller = controllers.get(client3);
        Game gameModel = controller.getModel();
        Field maxPlayersField = Game.class.getDeclaredField("maxPlayers");
        maxPlayersField.setAccessible(true);
        maxPlayersField.set(gameModel, 1);
        UUID client4 = UUID.randomUUID();
        clients.put(client4, new StubVirtualView());
        assertThrows(RuntimeException.class, () -> controllerManager.connectToGame(2, client4));
        UUID client5 = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> controllerManager.connectToGame(999, client5));
    }


    @Test
    void removeGame() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        clients.put(clientId, stubVirtualView);
        controllerManager.createNewGame(clientId);
        int gameNumber = getPrivateField(controllerManager, "gameNumber");
        ConcurrentMap<Integer, Controller> game = getPrivateField(controllerManager, "game");
        assertTrue(game.containsKey(gameNumber));
        controllerManager.removeGame(gameNumber);
        assertFalse(game.containsKey(gameNumber));
    }

    @Test
    void checkPlayersConnection() throws Exception {
        {
            UUID clientId1 = UUID.randomUUID();
            StubVirtualView stubView1 = new StubVirtualView();
            ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
            clients.put(clientId1, stubView1);
            controllerManager.createNewGame(clientId1);
            stubView1.throwExceptionOnConnectionTest = true;
            Thread.sleep(5000);
            ConcurrentMap<Integer, Controller> game = getPrivateField(controllerManager, "game");
            ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
            Controller controller = controllers.get(clientId1);
            assertFalse(game.containsValue(controller));
            assertTrue(controllers.containsKey(clientId1));
        }
        {
            UUID clientId2 = UUID.randomUUID();
            StubVirtualView stubView2 = new StubVirtualView();
            ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
            clients.put(clientId2, stubView2);
            controllerManager.createNewGame(clientId2);
            ConcurrentMap<UUID, Integer> connections = getPrivateField(controllerManager, "connections");
            int initialCounter = connections.get(clientId2);
            stubView2.connectionTestCalled = false;
            Thread.sleep(5000);
            assertTrue(stubView2.connectionTestCalled);
            assertTrue(connections.get(clientId2) < initialCounter);
            assertTrue(connections.get(clientId2) > 0);
        }
        {
            UUID clientId3 = UUID.randomUUID();
            StubVirtualView stubView3 = new StubVirtualView();

            ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
            clients.put(clientId3, stubView3);
            controllerManager.createNewGame(clientId3);
            ConcurrentMap<UUID, Integer> connections = getPrivateField(controllerManager, "connections");
            int initialCounter = connections.get(clientId3);
            stubView3.connectionTestCalled = false;
            Thread.sleep(5000);
            assertTrue(stubView3.connectionTestCalled);
            assertTrue(connections.get(clientId3) < initialCounter);
            assertTrue(connections.containsKey(clientId3));
        }
    }

    @Test
    void increment() throws Exception {
        ConcurrentMap<UUID, Integer> connections = getPrivateField(controllerManager, "connections");
        UUID existingClientId = UUID.randomUUID();
        connections.put(existingClientId, 3);
        controllerManager.increment(existingClientId);
        assertEquals(4, connections.get(existingClientId).intValue());
        UUID nonExistingClientId = UUID.randomUUID();
        assertFalse(connections.containsKey(nonExistingClientId));
        assertThrows(NullPointerException.class, () -> controllerManager.increment(nonExistingClientId));
        assertFalse(connections.containsKey(nonExistingClientId));
    }

    @Test
    void getController() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        clients.put(clientId, stubVirtualView);
        controllerManager.createNewGame(clientId);
        ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
        Controller expectedController = controllers.get(clientId);
        assertEquals(expectedController, controllerManager.getController(clientId));
        UUID nonExistingId = UUID.randomUUID();
        assertNull(controllerManager.getController(nonExistingId));
    }

    @Test
    void support() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        clients.put(clientId, stubVirtualView);
        controllerManager.createNewGame(clientId);
        UUID clientId2 = UUID.randomUUID();
        StubVirtualView stubView2 = new StubVirtualView();
        clients.put(clientId2, stubView2);
        controllerManager.connectToGame(1, clientId2);
        ConcurrentMap<Integer, Controller> game = getPrivateField(controllerManager, "game");
        ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
        Controller gameController = controllers.get(clientId);
        stubVirtualView.throwExceptionOnConnectionTest = true;
        Thread.sleep(5000);
        assertFalse(game.containsValue(gameController));
        assertTrue(controllers.containsKey(clientId));
        assertFalse(controllers.containsKey(clientId2));
    }

    @Test
    void viewListGame_shouldOnlyShowGamesWithAvailableSlots() throws Exception {
        ConcurrentMap<UUID, VirtualView> clients = getPrivateField(controllerManager, "clients");
        clients.put(clientId, stubVirtualView);
        controllerManager.createNewGame(clientId);
        UUID clientId2 = UUID.randomUUID();
        StubVirtualView stubView2 = new StubVirtualView();
        clients.put(clientId2, stubView2);
        controllerManager.createNewGame(clientId2);
        ConcurrentMap<UUID, Controller> controllers = getPrivateField(controllerManager, "controllers");
        Controller controller1 = controllers.get(clientId);
        Game model1 = controller1.getModel();
        Field maxPlayersField = Game.class.getDeclaredField("maxPlayers");
        maxPlayersField.setAccessible(true);
        maxPlayersField.set(model1, 1);
        controllerManager.viewListGame(clientId2);
        assertTrue(stubView2.latch.await(2, TimeUnit.SECONDS));
        assertEquals(List.of(2), stubView2.shownGames);
    }
}