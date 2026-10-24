package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.controller.Controller;
import it.polimi.ingsw.is25am31.controller.ControllerManager;
import it.polimi.ingsw.is25am31.customerrors.NameErrorException;
import it.polimi.ingsw.is25am31.customerrors.NoCardsLeftException;
import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.network.rmi.client.RmiClient;
import it.polimi.ingsw.is25am31.network.rmi.server.RmiServer;
import it.polimi.ingsw.is25am31.view.tui.TuiRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class DeckTest {
    Game game;

    @BeforeEach
    void setUp() throws NameErrorException {
        game = new Game();
        game.setMaxPlayers(2);
        game.setLevel(2);
        game.addPlayer(0, "testPlayer");
        game.addPlayer(1, "testPlayer2");
    }

    @Test
    void numberOfCardsLevel2Test(){
        Deck deck = new Deck(2);
        for(int i = 0; i < 4; i++)
            assertEquals(3, deck.getSubDeck(i).size());
    }

    @Test
    void numberOfCardsTestFlightTest() throws Exception {
        Deck deck = new Deck(1);
        Field field = Deck.class.getDeclaredField("deck");
        field.setAccessible(true);
        Object value = field.get(deck);
        assertEquals(8, ((List<Card>)value).size());
    }


    @Test
    void deckBuildLevel2Test() {
        Deck deck = new Deck(2);
        Card card;
        List<Card> cards = new ArrayList<>();
        deck.mergeSubDecks();
        for(int i = 0; i < 12; i++){
            card = deck.drawCard();
            cards.add(card);
        }
        assertEquals(4, cards.stream().filter(c -> (c.level == 1)).toList().size());
        assertEquals(8, cards.stream().filter(c -> (c.level == 2)).toList().size());
    }

    @Test
    void deckBuildTestFlightTest() {
        Card card;
        Deck deck = new Deck(1);
        for(int i = 0; i < 8; i++) {
            card = deck.drawCard();
            assertTrue(card.isTestFlight());
        }
    }

    @Test
    void typeTest(){
        Deck deck = new Deck(2);
        deck.mergeSubDecks();
        for (int i = 0; i < 12; i++){
            Card card = deck.drawCard();
            if(card instanceof AbandonedShipCard)
                assertEquals(EffectType.ABANDONED, card.getType());
            else if (card instanceof AbandonedStationCard)
                assertEquals(EffectType.ABANDONED, card.getType());
            else if (card instanceof CombatZoneCard)
                assertEquals(EffectType.COMBATZONE, card.getType());
            else if (card instanceof EpidemicCard)
                assertEquals(EffectType.SPECIAL, card.getType());
            else if (card instanceof MeteorSwarmCard)
                assertEquals(EffectType.METEORSWARM, card.getType());
            else if (card instanceof OpenSpaceCard)
                assertEquals(EffectType.OPENSPACE, card.getType());
            else if (card instanceof PiratesCard)
                assertEquals(EffectType.PIRATES, card.getType());
            else if (card instanceof PlanetsCard)
                assertEquals(EffectType.PLANETS, card.getType());
            else if (card instanceof SlaversCard)
                assertEquals(EffectType.SLAVERS, card.getType());
            else if (card instanceof SmugglersCard)
                assertEquals(EffectType.SMUGGLERS, card.getType());
            else if (card instanceof StardustCard)
                assertEquals(EffectType.SPECIAL, card.getType());
        }
        assertThrows(NoCardsLeftException.class, deck::drawCard);
    }

    @Test
    void abandonedShipTest() throws Exception {
        Cabin cabin1 = new Cabin(1, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        Cabin cabin2 = new Cabin(2, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        Cabin cabin3 = new Cabin(3, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        cabin1.modifyCrewMembers(2);
        cabin2.modifyCrewMembers(2);
        cabin3.modifyCrewMembers(2);
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);
        player.getShip().addComponent(3, 3, cabin1);
        player.getShip().addComponent(2, 2, cabin2);
        player.getShip().addComponent(2, 4, cabin3);
        assertEquals(8, player.getShip().getCrewNumber());

        Card card = new AbandonedShipCard(1, 1, "AbandonedShip", true, 5, 5, 5);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);

        Map<String, Object> map = new HashMap<>();
        for(int i = 0; i < 2; i++){
            map.put("x", 3);
            map.put("y", 3);
            game.applyCardEffect(player, map);
        }
        for(int i = 0; i < 2; i++){
            map.put("x", 2);
            map.put("y", 2);
            game.applyCardEffect(player, map);
        }
        map.put("x", 2);
        map.put("y", 4);
        game.applyCardEffect(player, map);
        assertEquals(5, card.getIterations());
        assertEquals(5, card.getCrewMembers());
        TuiRenderer.getInstance().showCard(card);
        assertEquals(3, player.getShip().getCrewNumber());
        assertEquals(5, player.getRocket().getPosition());
        assertEquals(5, player.getCosmicCredits());
    }

        @Test
        void abandonedStationTest() {
            Player player = game.getPlayer(0);
            player.getRocket().setPosition(10);

            CargoHold cargoHold = new CargoHold(1, "CargoHold", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2, false);
            player.getShip().addComponent(3, 3, cargoHold);
            AbandonedStationCard card = new AbandonedStationCard(1, 1, "test", true, 5, new ArrayList<>(List.of(Good.BLUE)), 5);
            game.setCurrentCard(card);
            game.setCurrentPlayer(player);
            Map<String, Object> map = new HashMap<>();
            map.put("Good", "blue");
            map.put("x", 3);
            map.put("y", 3);
            assertEquals(1, card.getIterations());
            game.applyCardEffect(player, map);
            assertEquals(5, card.getCrewMembers());
            TuiRenderer.getInstance().showCard(card);
            assertEquals(1 , player.getShip().getGoodCount());
            assertEquals(5, player.getRocket().getPosition());
        }


        @Test
        void meteorSwarmTest() throws Exception{
            Meteor meteor = new Meteor();
            Field field = Meteor.class.getDeclaredField("side");
            field.setAccessible(true);
            field.set(meteor, Side.LEFT);
            field = Meteor.class.getDeclaredField("size");
            field.setAccessible(true);
            field.set(meteor, Size.LARGE);
            Card card = new MeteorSwarmCard(1, 1, "meteorSwarmTestCard", false, new ArrayList<>(List.of(meteor)));
            Player player = game.getPlayer(0);
            game.setCurrentCard(card);
            game.setCurrentPlayer(player);
            Component component = new Component(1, "testComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT);
            player.getShip().addComponent(3, 3, component);
            BatteryComponent batteryComponent = new BatteryComponent(2, "BatteryComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
            Cannon cannon = new Cannon(3, "Cannon", new HashMap<>(Map.of(Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
            player.getShip().addComponent(2, 2, batteryComponent);
            player.getShip().addComponent(2, 4, cannon);
            game.rollDice(0);
            game.setCardContext(player, new HashMap<>(Map.of("x1", 2, "y1", 4, "x2", 2, "y2", 2)));
            Map<String, Object> map2 = new HashMap<>();
            map2.put("diceResult", 8);
            assertFalse(player.getShip().getUsableSlot(3, 3));
            assertEquals(1, card.getIterations());
            card.applyEffect(player, new Context(map2));
            TuiRenderer.getInstance().showCard(card);
            assertTrue(player.getShip().getUsableSlot(3, 3));
            assertThrows(UnsupportedOperationException.class, card::getCrewMembers);
        }



    @Test
    void epidemicTest()  {
        Cabin cabin1 = new Cabin(1, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        Cabin cabin2 = new Cabin(2, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        Cabin cabin3 = new Cabin(3, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false);
        Cabin cabin4 = new Cabin(3, "Cabin", Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE), Side.FRONT, false, false);
        Cabin cabin5 = new Cabin(3, "Cabin", Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE), Side.FRONT, false, false);


        cabin1.modifyCrewMembers(2);
        cabin2.modifyCrewMembers(2);
        cabin3.modifyCrewMembers(2);
        cabin4.modifyCrewMembers(2);
        cabin5.modifyCrewMembers(2);
        Player player = game.getPlayer(0);
        player.getShip().addComponent(3, 3, cabin1);
        player.getShip().addComponent(2, 2, cabin2);
        player.getShip().addComponent(2, 4, cabin3);
        player.getShip().addComponent(3, 4, cabin4);
        player.getShip().addComponent(4, 4, cabin5);


        Card card = new EpidemicCard(1, 1, "testEpidemicCard", false);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        game.applyCardEffect(player, null);
        TuiRenderer.getInstance().showCard(card);
        assertEquals(2 , player.getShip().getComponent(4, 4).getCrewMembers());
        assertEquals(8, player.getShip().getCrewNumber());
        assertEquals(1, card.getIterations());
    }

    @Test
    void stardustTest()  {
        Card card = new StardustCard(1, 1, "testStardustCard", false);
        Player player = game.getPlayer(0);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        game.setCardContext(player, null);
        game.applyCardEffect(player, null);
        TuiRenderer.getInstance().showCard(card);
        assertEquals(-5, player.getRocket().getPosition());
    }

    @Test
    void planetsTest() throws InterruptedException {
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);

        CargoHold cargoHold = new CargoHold(1, "CargoHold", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2, false);
        player.getShip().addComponent(3, 3, cargoHold);
        PlanetsCard card = new PlanetsCard(1, 2, "Planets", false, 1, new ArrayList<>(List.of(new ArrayList<>(List.of(Good.BLUE)))), 5);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        TuiRenderer.getInstance().showCard(card);
        Thread.sleep(50);
        Map<String, Object> map = new HashMap<>();
        map.put("Good", "blue");
        map.put("x", 3);
        map.put("y", 3);
        game.setCardContext(player, new HashMap<>(Map.of("planetIndex", 0)));
        assertEquals(1, card.getIterations());
        game.applyCardEffect(player, map);
        assertEquals(1 , player.getShip().getGoodCount());
        assertEquals(5, player.getRocket().getPosition());
    }

    @Test
    void slaversTest(){
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);

        CargoHold cargoHold = new CargoHold(1, "CargoHold", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2, false);
        BatteryComponent batteryComponent = new BatteryComponent(2, "BatteryComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        Cannon cannon = new Cannon(3, "Cannon", new HashMap<>(Map.of(Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        player.getShip().addComponent(3, 3, cargoHold);
        player.getShip().addComponent(2, 2, batteryComponent);
        player.getShip().addComponent(2, 4, cannon);
        SlaversCard card = new SlaversCard(1, 2, "Slavers", false, 1, 5, 5, 5);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        Map<String, Object> map = new HashMap<>();
        map.put("Good", "blue");
        map.put("x", 3);
        map.put("y", 3);
        TuiRenderer.getInstance().showCard(card);
        game.setCardContext(player, new HashMap<>(Map.of("x1", 2, "y1", 4, "x2", 2, "y2", 2)));
        game.applyCardEffect(player, map);
        assertEquals(5 , player.getCosmicCredits());
        assertEquals(1, card.getIterations());
    }

    @Test
    void openSpaceTest(){
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);
        Engine engine = new Engine(1, "Engine", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.ENGINE, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        BatteryComponent batteryComponent = new BatteryComponent(2, "BatteryComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        player.getShip().addComponent(3, 3, engine);
        player.getShip().addComponent(2, 2, batteryComponent);
        OpenSpaceCard card = new OpenSpaceCard(1, 2, "OpenSpace", false);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        Map<String, Object> map = new HashMap<>();
        map.put("Good", "blue");
        map.put("x", 3);
        map.put("y", 3);
        TuiRenderer.getInstance().showCard(card);
        game.setCardContext(player, new HashMap<>(Map.of("x1", 3, "y1", 3, "x2", 2, "y2", 2)));
        game.applyCardEffect(player, map);
        assertEquals(12 , player.getRocket().getPosition());
    }

    @Test
    void PiratesTest(){
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);

        CargoHold cargoHold = new CargoHold(1, "CargoHold", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2, false);
        BatteryComponent batteryComponent = new BatteryComponent(2, "BatteryComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        Cannon cannon = new Cannon(3, "Cannon", new HashMap<>(Map.of(Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        player.getShip().addComponent(3, 3, cargoHold);
        player.getShip().addComponent(2, 2, batteryComponent);
        player.getShip().addComponent(2, 4, cannon);
        PiratesCard card = new PiratesCard(1, 2, "Pirates", false, 1, new ArrayList<>(List.of(Size.LARGE)), 5, 5);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        TuiRenderer.getInstance().showCard(card);
        game.setCardContext(player, new HashMap<>(Map.of("x1", 2, "y1", 4, "x2", 2, "y2", 2)));
        game.rollDice(0);
        game.applyCardEffect(player, null);
        assertEquals(5 , player.getRocket().getPosition());
        assertEquals(5 , player.getCosmicCredits());
        assertEquals(1, card.getIterations());
    }

    @Test
    void SmugglersTest() throws NameErrorException {
        Player player = game.getPlayer(0);
        player.getRocket().setPosition(10);

        CargoHold cargoHold = new CargoHold(1, "CargoHold", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2, false);
        BatteryComponent batteryComponent = new BatteryComponent(2, "BatteryComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        Cannon cannon = new Cannon(3, "Cannon", new HashMap<>(Map.of(Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT, 2);
        player.getShip().addComponent(3, 3, cargoHold);
        player.getShip().addComponent(2, 2, batteryComponent);
        player.getShip().addComponent(2, 4, cannon);
        SmugglersCard card = new SmugglersCard(1, 2, "Smugglers", false, 1, 5, new ArrayList<>(List.of(Good.BLUE)), 5);
        game.setCurrentCard(card);
        game.setCurrentPlayer(player);
        Map<String, Object> map = new HashMap<>();
        map.put("Good", "blue");
        map.put("x", 3);
        map.put("y", 3);
        Context context = new Context(map);
        TuiRenderer.getInstance().showCard(card);
        game.setCardContext(player, new HashMap<>(Map.of("x1", 2, "y1", 4, "x2", 2, "y2", 2)));
        game.applyCardEffect(player, map);
        assertEquals(5 , player.getRocket().getPosition());
        assertEquals(1, card.getIterations());
    }


    private static class TestVirtualView implements VirtualView {
        public TestVirtualView() {}

        @Override
        public void showUpdate(UpdateMessage msg) {}
        @Override public void showShip(Ship ship) {}
        @Override public void showComponent(Component component) {}
        @Override public void showFaceUpComponentList(List<Component> component) {}
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
        @Override public void showSubDeck(List<Card> subDeck) {}
    }

    @Test
    void combatZoneTest() {
        Card card;
        Player player = game.getPlayer(0);
        Player player2 = game.getPlayer(1);
        game.addComponentToShip(0, 2, 2,new Cabin(1, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false));
        game.addComponentToShip(1, 2, 2, new Cabin(1, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, false, false));
        game.setPosition(player, 1);
        game.setPosition(player2, 2);
        game.initializeCrew(player);
        CardFactory cardFactory = new CardFactory();

        do{
            card = cardFactory.getRandomCard(1);
        }while(!(card instanceof CombatZoneCard));
        TuiRenderer.getInstance().showCard(card);
        game.setCurrentPlayer(player);
        game.setCurrentCard(card);

        card.applyEffect(player, null);
        assertEquals(3, game.getPlayer(0).getRocket().getPosition());
        card.applyEffect(player, new Context(new HashMap<>(Map.of("x", 2, "y", 3))));
        card.applyEffect(player, new Context(new HashMap<>(Map.of("x", 2, "y", 3))));
        assertEquals(2, game.getPlayer(0).getShip().getCrewNumber());
        card.applyEffect(player, new Context(new HashMap<>(Map.of("diceResult", 6))));
        assertEquals(1, player.getShip().getComponents().size());

        do{
            card = cardFactory.getRandomCard(2);
        }while(!(card instanceof CombatZoneCard));
        TuiRenderer.getInstance().showCard(card);
        game.setCurrentPlayer(player);
        game.setCurrentCard(card);

        card.applyEffect(player, null);
        assertEquals(-1, game.getPlayer(0).getRocket().getPosition());
        card.applyEffect(player, null);
        card.applyEffect(player, new Context(new HashMap<>(Map.of("diceResult", 7))));
        assertEquals(0, player.getShip().getComponents().size());
    }

    @Test
    void CombatZoneTestLevel1() throws Exception {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Controller controller = new Controller();
        controller.newConnection(new TestVirtualView(), uuid);
        controller.newConnection(new TestVirtualView(), uuid2);
        controller.setMaxPlayers(uuid, 2);
        controller.startGame(uuid,1);
        controller.addPlayer(uuid, "Player_0");
        controller.addPlayer(uuid2, "Player_1");

        Game model = controller.getModel();
        Player player0 = model.getPlayer(0);
        Player player1 = model.getPlayer(1);

        player0.getShip().addComponent(2, 2, new Cabin(1, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, false, false));
        player0.getShip().addComponent(3, 3, new Engine(2, "Engine", Map.of(Side.BACK, Connector.ENGINE), Side.FRONT, 1));
        player0.getShip().addComponent(4, 4, new Cannon(3, "Cannon", Map.of(Side.FRONT, Connector.CANNON), Side.FRONT, 2));

        player1.getShip().addComponent(2, 2, new Cabin(4, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, false, false));
        player1.getShip().addComponent(3, 4, new Cabin(5, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, false, false));
        player1.getShip().addComponent(3, 3, new Engine(6, "Engine", Map.of(Side.BACK, Connector.ENGINE), Side.FRONT, 2));
        player1.getShip().addComponent(4, 4, new Cannon(7, "Cannon", Map.of(Side.FRONT, Connector.CANNON), Side.FRONT, 1));
        controller.getModel().initializeCrew(player0);
        controller.getModel().initializeCrew(player1);
        model.initializeCrew(player0);
        model.initializeCrew(player1);

        assertEquals(4, player0.getShip().getCrewNumber());
        assertEquals(6, player1.getShip().getCrewNumber());
        assertEquals(1, player0.getShip().getEnginePower());
        assertEquals(0, player1.getShip().getEnginePower());
        assertEquals(0, player0.getShip().getCannonPower());
        assertEquals(1.0, player1.getShip().getCannonPower());

        controller.endAssemblePhase(uuid);
        controller.endAssemblePhase(uuid2);

        assertEquals(5, player0.getRocket().getPosition());
        assertEquals(3, player1.getRocket().getPosition());

        List<CannonHit> cannonHits = new ArrayList<>(List.of(new CannonHit(Side.BACK, Size.SMALL)));
        Card card = new CombatZoneCard(100, 1, "Combat Zone Test", true, 1, cannonHits, 2, 2);
        model.setCurrentCard(card);

        model.orderPlayers();
        model.setCurrentPlayer(model.getPlayer(controller.getLeader()));
        assertEquals(player0, model.getCurrentPlayer());

        controller.setEffectContext(uuid, null);
        controller.setEffectContext(uuid2, null);
        controller.setEffectContext(uuid, null);
        assertEquals(2, player0.getRocket().getPosition());

        Map<String, Object> paramsCrew = new HashMap<>();
        paramsCrew.put("x", 2);
        paramsCrew.put("y", 2);
        controller.setEffectContext(uuid, paramsCrew);
        controller.setEffectContext(uuid2, null);
        controller.setEffectContext(uuid2, paramsCrew);
        controller.setEffectContext(uuid2, paramsCrew);
        assertEquals(4, player1.getShip().getCrewNumber());

        controller.setEffectContext(uuid2, null);
        controller.setEffectContext(uuid, null);
        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);
        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);

        controller.setEffectContext(uuid2, null);

        assertEquals(1, ContextValidator.isEffectResolved(0, model));
    }
/*
    @Test
    void CombatZoneTestLevel2() throws Exception {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Controller controller = new Controller();
        controller.newConnection(new TestVirtualView(), uuid);
        controller.newConnection(new TestVirtualView(), uuid2);
        controller.setMaxPlayers(uuid, 2);
        controller.startGame(uuid, 2);
        controller.addPlayer(uuid, "Player_0");
        controller.addPlayer(uuid2, "Player_1");

        Game model = controller.getModel();
        Player player0 = model.getPlayer(0);
        Player player1 = model.getPlayer(1);

        player0.getShip().addComponent(1, 3, new Cannon(1, "Cannon", Map.of(Side.FRONT, Connector.CANNON, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL), Side.FRONT, 1));
        player0.getShip().addComponent(3, 3, new Engine(2, "Engine", Map.of(Side.BACK, Connector.ENGINE, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 1));
        player0.getShip().addComponent(3, 4, new Cabin(3, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL), Side.FRONT, false, false));
        player0.getShip().addComponent(2, 4, new Cabin(4, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL), Side.FRONT, false, false));
        CargoHold cargo = new CargoHold(5, "CargoHold", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 2, true);
        cargo.addContains(Good.RED);
        cargo.addContains(Good.BLUE);
        player0.getShip().addComponent(1, 4, cargo);


        player1.getShip().addComponent(1, 3, new Cannon(6, "Cannon", Map.of(Side.FRONT, Connector.CANNON, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL), Side.FRONT, 2));
        player1.getShip().addComponent(3, 3, new Engine(7, "Engine", Map.of(Side.BACK, Connector.ENGINE, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 2));
        player1.getShip().addComponent(2, 4, new Cabin(8, "Cabin", Map.of(Side.FRONT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL), Side.FRONT, false, false));
        CargoHold cargo2 = new CargoHold(5, "CargoHold", Map.of(Side.FRONT, Connector.UNIVERSAL), Side.FRONT, 2, true);
        cargo2.addContains(Good.RED);
        cargo2.addContains(Good.BLUE);
        player1.getShip().addComponent(1, 4, cargo2);
        controller.getModel().initializeCrew(player0);
        controller.getModel().initializeCrew(player1);

        model.initializeCrew(player0);
        model.initializeCrew(player1);

        assertEquals(1.0, player0.getShip().getCannonPower());
        assertEquals(0, player1.getShip().getCannonPower());
        assertEquals(1, player0.getShip().getEnginePower());
        assertEquals(0, player1.getShip().getEnginePower());
        assertEquals(6, player0.getShip().getCrewNumber());
        assertEquals(4, player1.getShip().getCrewNumber());
        assertEquals(2, player0.getShip().getGoodCount());

        controller.endAssemblePhase(uuid);
        controller.endAssemblePhase(uuid2);
        controller.placeRocket(uuid, 1);
        controller.placeRocket(uuid2, 2);
        controller.endCrewPlacingPhase(uuid);
        controller.endCrewPlacingPhase(uuid2);

        assertEquals(7, player0.getRocket().getPosition());
        assertEquals(4, player1.getRocket().getPosition());

        List<CannonHit> cannonHits = new ArrayList<>(List.of(new CannonHit(Side.FRONT, Size.LARGE), new CannonHit(Side.BACK, Size.LARGE), new CannonHit(Side.RIGHT, Size.SMALL), new CannonHit(Side.LEFT, Size.SMALL)));
        Card card = new CombatZoneCard(101, 2, "Combat Zone Lv2", false, 3, cannonHits, 4, 0);
        model.setCurrentCard(card);

        model.orderPlayers();
        model.setCurrentPlayer(model.getPlayer(controller.getLeader()));
        assertEquals(player0, model.getCurrentPlayer());

        controller.setEffectContext(uuid, null);
        controller.setEffectContext(uuid2, null);
        controller.setEffectContext(uuid2, null);
        assertEquals(7, player0.getRocket().getPosition());
        assertEquals(0, player1.getRocket().getPosition());

        Map<String, Object> paramsGood1 = new HashMap<>();
        paramsGood1.put("Good", "red");
        paramsGood1.put("x", 1);
        paramsGood1.put("y", 4);
        controller.setEffectContext(uuid, null);
        controller.setEffectContext(uuid2, null);
        controller.setEffectContext(uuid2, paramsGood1);
        assertEquals(1, player1.getShip().getGoodCount());

        Map<String, Object> paramsGood2 = new HashMap<>();
        paramsGood2.put("Good", "blue");
        paramsGood2.put("x", 1);
        paramsGood2.put("y", 4);
        controller.setEffectContext(uuid2, paramsGood2);
        controller.setEffectContext(uuid2, null);
        Thread.sleep(100);
        assertEquals(0, player1.getShip().getGoodCount());

        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);
        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);
        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);
        controller.rollDice(uuid2);
        controller.setEffectContext(uuid2, null);

        assertEquals(1, ContextValidator.isEffectResolved(0, model));
    }

 */

}