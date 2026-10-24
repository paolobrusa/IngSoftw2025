package it.polimi.ingsw.is25am31.controller;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.is25am31.model.cards.*;
import it.polimi.ingsw.is25am31.model.gameboard.Phase;
import it.polimi.ingsw.is25am31.model.player.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.network.*;
import it.polimi.ingsw.is25am31.view.ClientState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ControllerTest {
    private Controller controller;
    private Game model;
    private UUID clientId1;
    private UUID clientId2;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
        model = controller.getModel();
        clientId1 = UUID.randomUUID();
        clientId2 = UUID.randomUUID();

        model.setMaxPlayers(2);
        model.setLevel(2);
        model.shuffleCards();

        Map<UUID, ClientData> activeClients = new HashMap<>();
        ClientData clientData1 = new ClientData(new TestVirtualView());
        clientData1.playerId = 0;
        clientData1.nickname= "Mario";
        activeClients.put(clientId1, clientData1);

        ClientData clientData2 = new ClientData(new TestVirtualView());
        clientData2.playerId = 1;
        clientData2.nickname= "Luigi";
        activeClients.put(clientId2, clientData2);

        setPrivateField(controller, "activeClients", activeClients);
        setPrivateField(controller, "ids", Arrays.asList(0, 1));

        player1 = new Player(0, "Mario", model.getGameboard());
        player2 = new Player(1, "Luigi", model.getGameboard());
        model.getPlayers().add(player1);
        model.getPlayers().add(player2);

        player1.setShip(new Ship(model.getGameboard()));
        player2.setShip(new Ship(model.getGameboard()));

        Map<Player, ClientState> players = new LinkedHashMap<>();
        players.put(player1, ClientState.FLIGHT);
        players.put(player2, ClientState.FLIGHT);
        setPrivateField(controller, "players", players);

        model.orderPlayers();
        model.setCurrentPlayer(player1);
        model.setCurrentPhase(Phase.FLIGHT);

    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getPrivateField(Object target, String fieldName) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestVirtualView implements VirtualView {
        public Ship lastShownShip;
        public Component lastShownComponent;
        public List<Component> lastShownComponentList;
        public List<Card> lastShownSubDeck;
        public UpdateMessage lastUpdateMessage;

        @Override
        public void showUpdate(UpdateMessage msg) {
            lastUpdateMessage = msg;
        }
        @Override public void showShip(Ship ship) {lastShownShip = ship;}
        @Override public void showComponent(Component component) {lastShownComponent = component;}
        @Override public void showFaceUpComponentList(List<Component> component) {lastShownComponentList = component;}
        @Override public void showCard(Card card) {}
        @Override public void startGame() {}
        @Override public void lockClient() {}
        @Override public void setNickname() {}
        @Override public void startAssemblingPhase(List<Player> players, int level) {}
        @Override public void updatePositions(Map<Integer, Integer> positions) {}
        @Override public void effectResolution() {}
        @Override public void startFlightPhase(Ship ship, Map<Integer, Integer> positions) {}
        @Override public void connectionTest() {}
        @Override public void crewPlacingPhase(Ship ship, Map<Integer, Integer> positions) {}
        @Override public void showSecondsLeft(int count, int secondsLeft) {}
        @Override public void finishFinalHourglass() {}
        @Override public void showSecondsLeftForGui(int counter, int secondsLeft) {}
        @Override public void showFinalRanking(Map<Player, Integer> ranking) {}
        @Override public void notifyDefeat(int loseType) {}
        @Override public void setGameLevel(int level) {}
        @Override public void setUUID(UUID clientId) {}
        @Override public void connectionResponse(ConnectionResponse connectionResponse) {}
        @Override public void playerJoined(String newPlayer, List<String> players) {}
        @Override public void newLeader(boolean isNewLeader) {}
        @Override public void diceResult(int diceResult) {}
        @Override public void roundResult(boolean roundWon) {}
        @Override public void turnChange(boolean isMyTurn, int turnType, int currentPlayer) {}
        @Override public void showGameList(List<Integer> gameList) {}
        @Override public void showSubDeck(List<Card> subDeck) { lastShownSubDeck = subDeck; }
    }

    @Test
    public void testNewConnection() throws Exception {
        model.setMaxPlayers(3);
        List<Integer> newIds = new LinkedList<>(Arrays.asList(0, 1));
        setPrivateField(controller, "ids", newIds);
        UUID newClientId = UUID.randomUUID();
        VirtualView newClient = new TestVirtualView();
        controller.newConnection(newClient, newClientId);
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        assertTrue(activeClients.containsKey(newClientId));
        assertNotEquals(-1, activeClients.get(newClientId).playerId);
    }

    @Test
    public void testGetPlayerName() {
        String name = controller.getPlayerName(clientId1);
        assertEquals("Mario", name);
        String name2 = controller.getPlayerName(clientId2);
        assertEquals("Luigi", name2);
    }

    @Test
    public void testSetMaxPlayers() {
        controller.setMaxPlayers(clientId1, 3);
        assertEquals(3, model.getMaxPlayers());
    }

    @Test
    public void testStartGame() throws Exception {
        controller.startGame(clientId1, 1);
        assertEquals(1, model.getLevel());
    }

    @Test
    public void testAddPlayer() throws Exception {
        assertNotNull(model.getPlayer(0));
        assertEquals("Mario", model.getPlayer(0).getNickname());
    }

    @Test
    public void testEndCrewPlacingPhase() throws Exception {
        Map<Player, ClientState> players = new LinkedHashMap<>();
        players.put(player1, ClientState.LOCKED);
        players.put(player2, ClientState.LOCKED);
        setPrivateField(controller, "players", players);
        controller.endCrewPlacingPhase(clientId1);
        assertEquals(Phase.FLIGHT, model.getCurrentPhase());
    }

    @Test
    public void testLeaveGame() throws Exception {
        Map<UUID, ClientData> activeClientsBefore = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        TestVirtualView view1 = (TestVirtualView) activeClientsBefore.get(clientId1).client;
        TestVirtualView view2 = (TestVirtualView) activeClientsBefore.get(clientId2).client;
        controller.leaveGame(clientId1);
        Thread.sleep(500);
        assertEquals(UpdateMessage.QUIT_GAME, view1.lastUpdateMessage);
        assertEquals(UpdateMessage.PLAYER_DISCONNECTED, view2.lastUpdateMessage);
        Map<UUID, ClientData> activeClientsAfter = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        Map<UUID, ClientData> passiveClientsAfter = (Map<UUID, ClientData>) getPrivateField(controller, "passiveClients");
        assertTrue(activeClientsAfter.containsKey(clientId1));
        assertTrue(activeClientsAfter.containsKey(clientId2));
        assertTrue(passiveClientsAfter.isEmpty());
    }

    @Test
    public void testPlaceRocket() throws Exception {
        Map<Player, ClientState> players = new LinkedHashMap<>();
        players.put(player1, ClientState.ENDED_ASSEMBLE);
        players.put(player2, ClientState.ENDED_ASSEMBLE);
        setPrivateField(controller, "players", players);
        controller.placeRocket(clientId1, 1);
        controller.placeRocket(clientId2, 2);
        assertEquals(7, player1.getRocket().getPosition());
        assertEquals(4, player2.getRocket().getPosition());
    }

    @Test
    public void testDrawCard() throws Exception {
        Map<Player, ClientState> players = new LinkedHashMap<>();
        players.put(player1, ClientState.FLIGHT);
        players.put(player2, ClientState.LOCKED);
        setPrivateField(controller, "players", players);
        model.setCurrentPlayer(player1);
        controller.drawCard(clientId1);
        assertNotNull(model.getCurrentCard());
    }

    @Test
    public void testRollDice() throws Exception {
        Card testCard = new MeteorSwarmCard(1, 1,"MeteorSwarm", false, new ArrayList<>());
        model.setCurrentCard(testCard);
        controller.rollDice(clientId1);
        assertTrue(model.getDiceResult() > 0);
    }

    @Test
    public void testHandleCrewRequest() throws Exception {
        Map<Side, Connector> connectors = new HashMap<>();
        connectors.put(Side.FRONT, Connector.UNIVERSAL);
        connectors.put(Side.BACK, Connector.UNIVERSAL);
        connectors.put(Side.LEFT, Connector.UNIVERSAL);
        connectors.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(1, "Cabin", connectors, Side.FRONT, false, false);
        player1.getShip().addComponent(1, 1, cabin);
        controller.handleCrewRequest(clientId1, 1, 1, AlienColor.NONE);
        assertEquals(2, player1.getCrew());
    }

    @Test
    public void testAbandonRace() throws Exception {
        model.getRemainingPlayers().clear();
        model.getRemainingPlayers().add(player1);
        model.getRemainingPlayers().add(player2);
        Map<UUID, ClientData> passiveClients = new HashMap<>();
        setPrivateField(controller, "passiveClients", passiveClients);
        controller.abandonRace(clientId1);
        Player modelPlayer = model.getPlayer(player1.getId());
        assertTrue(modelPlayer.hasAbandoned());
        assertEquals(1, passiveClients.size());
    }

    @Test
    public void testHourglass() throws Exception {
        controller.runHourglass();
        HourGlass hourGlass = (HourGlass) getPrivateField(controller, "hourGlass");
        assertTrue(hourGlass.isRunning());
    }

    @Test
    public void testRunFinalHourglass() {
        setPrivateField(controller, "hourglassCounter", 3);
        assertEquals(0, controller.runFinalHourglass());
    }

    @Test
    public void testStopHourglass() {
        controller.runHourglass();
        controller.stopHourglass();
        HourGlass hourGlass = (HourGlass) getPrivateField(controller, "hourGlass");
        assertFalse(hourGlass.isRunning());
    }

    @Test
    public void testViewShip() throws Exception {
        player2.setShip(new Ship(model.getGameboard()));
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.viewShip(clientId1, "Luigi");
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownShip, "Should have shown a ship");
        assertEquals(player2.getShip(), view.lastShownShip);
    }


    @Test
    public void testDrawComponent() throws Exception {
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.drawComponent(clientId1);
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownComponent, "Should have shown a component after drawing");
    }

    @Test
    public void testPutComponentBack() throws Exception {
        Component comp = model.drawComponent();
        int initialSize = model.getFaceUpComponentList().size();
        controller.putComponentBack(comp, clientId1);
        assertEquals(initialSize + 1, model.getFaceUpComponentList().size());
    }

    @Test
    public void testGetFaceUpComponentList() throws Exception {
        Component comp1 = model.drawComponent();
        Component comp2 = model.drawComponent();
        controller.putComponentBack(comp1, clientId1);
        controller.putComponentBack(comp2, clientId1);
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.getFaceUpComponentList(clientId1);
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownComponentList);
        assertTrue(view.lastShownComponentList.size() >= 2);
        assertTrue(view.lastShownComponentList.contains(comp1));
        assertTrue(view.lastShownComponentList.contains(comp2));
    }

    @Test
    public void testDrawFaceUpComponent() throws Exception {
        Component comp = model.drawComponent();
        controller.putComponentBack(comp, clientId1);
        int compId = comp.getId();
        int initialSize = model.getFaceUpComponentList().size();
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.drawFaceUpComponent(compId, clientId1);
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownComponent);
        assertEquals(compId, view.lastShownComponent.getId());
        assertEquals(initialSize - 1, model.getFaceUpComponentList().size());
        assertFalse(model.getFaceUpComponentList().contains(comp));
    }

    @Test
    public void testDrawFromReserved() throws Exception {
        Component reservedComp = new Engine(102, "ReservedEngine", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 1);
        player1.getShip().addComponent(0, 5, reservedComp);
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.drawFromReserved(0, clientId1);
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownComponent);
        assertEquals(102, view.lastShownComponent.getId());
    }

    @Test
    public void testRemoveComponent() throws Exception {
        player1.getShip().addComponent(1, 1, new Engine(1, "Engine", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 1));
        controller.removeComponent(clientId1, 1, 1);
        assertNull(player1.getShip().getComponent(1, 1));
    }

    @Test
    public void testAddComponentToShip() throws Exception {
        Component engine = new Engine(1, "Engine", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 1);
        controller.addComponentToShip(clientId1, 1, 1, engine);
        assertEquals(engine, player1.getShip().getComponent(1, 1));
    }


    @Test
    public void testStartHourGlass() throws Exception {
        controller.startHourGlass(clientId1);
        HourGlass hourGlass = (HourGlass) getPrivateField(controller, "hourGlass");
        assertTrue(hourGlass.isRunning());
    }

    @Test
    public void testShowHourglassSecondsLeft() throws Exception {
        HourGlass hourGlass = (HourGlass) getPrivateField(controller, "hourGlass");
        hourGlass.start();
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.showHourglassSecondsLeft(clientId1);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertTrue(hourGlass.isRunning());
    }

    @Test
    public void testStartFinalHourGlass() throws Exception {
        setPrivateField(controller, "hourglassCounter", 3);
        controller.startFinalHourGlass(clientId1);
        HourGlass hourGlass = (HourGlass) getPrivateField(controller, "hourGlass");
        assertTrue(hourGlass.isRunning());
        int counter = (int) getPrivateField(controller, "hourglassCounter");
        assertEquals(4, counter);
    }

    @Test
    public void testDrawSubDeck() throws Exception {
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        controller.drawSubDeck(clientId1, 0);
        Thread.sleep(200);
        TestVirtualView view = (TestVirtualView) activeClients.get(clientId1).client;
        assertNotNull(view.lastShownSubDeck, "Should have shown subdeck after drawing");
    }

    @Test
    public void testSetCardContext() throws Exception {
        List<List<Good>> goods1 = new ArrayList<>();
        goods1.add(new ArrayList<>(List.of(Good.RED)));
        Card testCard1 = new PlanetsCard(1, 1, "Test", false, 1, goods1, 0);
        model.setCurrentCard(testCard1);
        Map<String, Object> params1 = new HashMap<>();
        params1.put("planetIndex", 0);
        controller.setCardContext(clientId1, params1);
        assertEquals(0, ((PlanetsCard) model.getCurrentCard()).currentPlanet);
        model.setCurrentCard(null);
        model.setCurrentPlayer(null);
        List<List<Good>> goods2 = new ArrayList<>();
        goods2.add(new ArrayList<>(List.of(Good.RED)));
        Card testCard2 = new PlanetsCard(1, 1, "Test", false, 1, goods2, 0);
        model.setCurrentCard(testCard2);
        model.setCurrentPlayer(player1);
        Map<String, Object> params2 = new HashMap<>();
        params2.put("planetIndex", 0);
        controller.setCardContext(clientId1, params2);
        assertEquals(0, ((PlanetsCard) model.getCurrentCard()).currentPlanet);
        model.setCurrentCard(null);
        model.setCurrentPlayer(null);
        List<List<Good>> goods3 = new ArrayList<>();
        goods3.add(new ArrayList<>(List.of(Good.RED)));
        Card testCard3 = new PlanetsCard(1, 1, "Test", false, 1, goods3, 0);
        model.setCurrentCard(testCard3);
        model.setCurrentPlayer(player2);
        Map<String, Object> params3 = new HashMap<>();
        params3.put("planetIndex", 0);
        assertThrows(RuntimeException.class, () -> controller.setCardContext(clientId1, params3));
        model.setCurrentCard(null);
        model.setCurrentPlayer(null);
        Card meteorCard = new MeteorSwarmCard(4, 1, "MeteorSwarm", false, new ArrayList<>());
        model.setCurrentCard(meteorCard);
        model.setCurrentPlayer(player1);
        Map<String, Object> params4 = new HashMap<>();
        params4.put("planetIndex", 0);
        assertThrows(RuntimeException.class, () -> controller.setCardContext(clientId1, params4));
        model.setCurrentCard(null);
        model.setCurrentPlayer(null);
        List<List<Good>> goods5 = new ArrayList<>();
        goods5.add(new ArrayList<>(List.of(Good.RED)));
        Card testCard5 = new PlanetsCard(1, 1, "Test", false, 1, goods5, 0);
        model.setCurrentCard(testCard5);
        model.setCurrentPlayer(player1);
        Map<String, Object> params5 = new HashMap<>();
        params5.put("decline", true);
        controller.setCardContext(clientId1, params5);
        assertEquals(player2, model.getCurrentPlayer());
    }

    @Test
    public void testIsGameOver() throws Exception {
        Map<UUID, ClientData> activeClients = (Map<UUID, ClientData>) getPrivateField(controller, "activeClients");
        activeClients.remove(clientId2);
        Method isGameOver = Controller.class.getDeclaredMethod("isGameOver", UUID.class);
        isGameOver.setAccessible(true);
        boolean result = (boolean) isGameOver.invoke(controller, clientId1);
        assertTrue(result);
    }

    @Test
    public void TestEndAssemblePhase() throws Exception {
        Map<Player, ClientState> players = new LinkedHashMap<>();
        players.put(player1, ClientState.ASSEMBLE);
        players.put(player2, ClientState.ASSEMBLE);
        setPrivateField(controller, "players", players);
        controller.endAssemblePhase(clientId1);
        Map<Player, ClientState> updatedPlayers = (Map<Player, ClientState>) getPrivateField(controller, "players");
        assertEquals(ClientState.ENDED_ASSEMBLE, updatedPlayers.get(player1));
        model.setLevel(1);
        players = new LinkedHashMap<>();
        players.put(player1, ClientState.ASSEMBLE);
        players.put(player2, ClientState.ASSEMBLE);
        setPrivateField(controller, "players", players);
        controller.endAssemblePhase(clientId1);
        controller.endAssemblePhase(clientId2);
        updatedPlayers = (Map<Player, ClientState>) getPrivateField(controller, "players");
        assertEquals(ClientState.FLIGHT, updatedPlayers.get(player1));
        assertEquals(ClientState.FLIGHT, updatedPlayers.get(player2));
        assertEquals(Phase.FLIGHT, model.getCurrentPhase());
        model.setLevel(2);
        players = new LinkedHashMap<>();
        players.put(player1, ClientState.ASSEMBLE);
        players.put(player2, ClientState.ASSEMBLE);
        setPrivateField(controller, "players", players);
        controller.endAssemblePhase(clientId1);
        updatedPlayers = (Map<Player, ClientState>) getPrivateField(controller, "players");
        assertEquals(ClientState.ENDED_ASSEMBLE, updatedPlayers.get(player1));
        assertEquals(ClientState.ASSEMBLE, updatedPlayers.get(player2));
    }


}