package it.polimi.ingsw.is25am31.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.is25am31.customerrors.NameErrorException;
import it.polimi.ingsw.is25am31.model.cards.*;
import it.polimi.ingsw.is25am31.model.gameboard.Phase;
import it.polimi.ingsw.is25am31.model.player.Ship;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;

import java.util.*;

public class GameTest {
    private Game game;
    private final Map<Side, Connector> mainCabinConnectors = Map.of(
            Side.FRONT, Connector.UNIVERSAL,
            Side.BACK, Connector.UNIVERSAL,
            Side.LEFT, Connector.UNIVERSAL,
            Side.RIGHT, Connector.UNIVERSAL
    );

    private Map<Side, Connector> createConnectors(Side side, Connector type) {
        Map<Side, Connector> connectors = new EnumMap<>(Side.class);
        connectors.put(side, type);
        return connectors;
    }

    @Test
    void testGameInitialization() {
        Game game = new Game();
        assertEquals(Phase.NEWGAME, game.getCurrentPhase());
        assertEquals(5, game.getMaxPlayers());
        assertFalse(game.isGameStarted());
        assertNull(game.getCurrentPlayer());
        assertEquals(0, game.getPlayers().size());
        assertEquals(0, game.getRemainingPlayers().size());
        assertEquals(0, game.getEliminatedPlayers().size());
    }

    @Test
    void addPlayer_DuplicateName() {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        try {
            game.addPlayer(1, "Alice");
        } catch (NameErrorException e) {
            fail("Unexpected exception");
        }
        assertThrows(NameErrorException.class, () -> game.addPlayer(2, "alice"));
    }

    @Test
    void testSetLevel() {
        Game game = new Game();
        game.setLevel(1);
        assertEquals(1, game.getLevel());
        assertEquals(Phase.INITIALIZENICKNAME, game.getCurrentPhase());
        assertNotNull(game.getGameboard());
        assertEquals(0, game.getDiceResult());

        game.setLevel(2);
        assertEquals(2, game.getLevel());
    }

    @Test
    void testSetMaxPlayersInvalidValues() {
        Game game = new Game();
        assertThrows(IllegalArgumentException.class, () -> game.setMaxPlayers(1));
        assertThrows(IllegalArgumentException.class, () -> game.setMaxPlayers(5));
        assertThrows(IllegalArgumentException.class, () -> game.setMaxPlayers(0));
        assertThrows(IllegalArgumentException.class, () -> game.setMaxPlayers(-1));
    }

    @Test
    void testSetMaxPlayersValidValues() {
        Game game = new Game();
        game.setMaxPlayers(2);
        assertEquals(2, game.getMaxPlayers());
        assertTrue(game.isGameStarted());

        game.setMaxPlayers(3);
        assertEquals(3, game.getMaxPlayers());

        game.setMaxPlayers(4);
        assertEquals(4, game.getMaxPlayers());
    }

    @Test
    void testViewShip() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Ship ship = game.viewShip(1);
        assertNotNull(ship);
        assertEquals(ship, game.getPlayer(1).getShip());

        assertNull(game.viewShip(999)); // ID inesistente
    }

    @Test
    void testGetPlayerFromEliminatedPlayers() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        game.abandonRace(1);
        Player alice = game.getPlayer(1);
        assertNotNull(alice);
        assertEquals("Alice", alice.getNickname());
        assertTrue(alice.hasAbandoned());
    }

    @Test
    void addPlayer() {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        assertThrows(IllegalArgumentException.class, () -> {
            game.addPlayer(1, "Alice");
            game.addPlayer(2, "Bob");
            game.addPlayer(3, "Carlo");
        });
    }

    @Test
    void completeAssembly() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        game.completeAssembly(1);
        game.completeAssembly(2);

        assertEquals(Phase.FLIGHT, game.getCurrentPhase());
        assertEquals(2, game.getFinishingOrder().size());
    }

    @Test
    void testInitializeShip() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Luca");
        game.addPlayer(2, "Mario");

        game.initializeShip();

        for (Player p : game.getPlayers()) {
            assertNotNull(p.getShip());
            assertNotNull(p.getRocket());
            assertEquals(2, p.getCrew());
        }
    }

    @Test
    void testSetPositionInvalidValues() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);

        assertThrows(IllegalArgumentException.class, () -> game.setPosition(game.getPlayer(1), -1));
        assertThrows(IllegalArgumentException.class, () -> game.setPosition(game.getPlayer(1), 5));
    }

    @Test
    void handleCrewRequest_AddAlienWithLifeSupport() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        AlienLifeSupport als = new AlienLifeSupport(0, "AlienLifeSupport",createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, AlienColor.BROWN);
        Cabin cabin = new Cabin(1, "Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, false, false);
        assertTrue(game.addComponentToShip(1, 2, 4, als));
        assertTrue(game.addComponentToShip(1, 2, 5, cabin));

        game.handleCrewRequest(1, 2, 5, AlienColor.BROWN);
        assertEquals(AlienColor.BROWN, cabin.getCurrentAlien());
        assertEquals(3, game.getPlayer(1).getCrew());
    }

    @Test
    void testSetPositionLevel1() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);

        game.setPosition(game.getPlayer(1), 1);
        assertEquals(5, game.getPlayer(1).getRocket().getPosition());
        assertEquals(game.getPlayer(1), game.getCurrentPlayer());

        game.addPlayer(2, "Bob");
        game.instantiateRocketForPlayer(2);
        game.setPosition(game.getPlayer(2), 2);
        assertEquals(3, game.getPlayer(2).getRocket().getPosition());
    }

    @Test
    void testSetPositionLevel2() throws NameErrorException {
        Game game = new Game();
        game.setLevel(2);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);

        game.setPosition(game.getPlayer(1), 1);
        assertEquals(7, game.getPlayer(1).getRocket().getPosition());

        game.addPlayer(2, "Bob");
        game.instantiateRocketForPlayer(2);
        game.setPosition(game.getPlayer(2), 2);
        assertEquals(4, game.getPlayer(2).getRocket().getPosition());
    }

    @Test
    void handleCrewRequest_AddAlienWithoutLifeSupport() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Cabin cabin = new Cabin(1,"Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT,false, false);
        game.addComponentToShip(1, 2, 2, cabin);
        assertThrows(RuntimeException.class, () -> game.handleCrewRequest(1, 2, 2, AlienColor.BROWN));
    }

    @Test
    void handleCrewRequest_AddCrewToCabinWithAlien() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        AlienLifeSupport als = new AlienLifeSupport(0, "Alien Support", createConnectors(Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, AlienColor.BROWN);
        Cabin cabin = new Cabin(1,"Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, true, false);
        assertTrue(game.addComponentToShip(1, 3, 3, als));
        assertTrue(game.addComponentToShip(1, 3, 4, cabin));
        game.handleCrewRequest(1, 3, 4, AlienColor.BROWN);
        assertThrows(RuntimeException.class, () -> game.handleCrewRequest(1, 3, 4, AlienColor.NONE), "Dovrebbe impedire l'aggiunta di umani in cabina con alieno");
    }

    @Test
    void handleCrewRequest_AddSecondAlienSameColor() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        AlienLifeSupport als1 = new AlienLifeSupport(0, "Alien Support",createConnectors(Side.RIGHT, Connector.UNIVERSAL),Side.FRONT, AlienColor.BROWN);
        Cabin cabin1 = new Cabin(1, "Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, true, false);
        AlienLifeSupport als2 = new AlienLifeSupport(2, "Alien Support 2", createConnectors(Side.RIGHT, Connector.UNIVERSAL), Side.FRONT, AlienColor.BROWN);
        Cabin cabin2 = new Cabin(3, "Cabin 2", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, true, false);

        assertTrue(game.addComponentToShip(1, 2, 1, als1));
        assertTrue(game.addComponentToShip(1, 2, 2, cabin1));
        assertTrue(game.addComponentToShip(1, 2, 4, als2));
        assertTrue(game.addComponentToShip(1, 2, 5, cabin2));

        game.handleCrewRequest(1, 2, 2, AlienColor.BROWN);
        assertThrows(RuntimeException.class, () -> game.handleCrewRequest(1, 2, 5, AlienColor.BROWN),"Dovrebbe impedire alieni duplicati");
    }

    @Test
    void sellGoodsPhase_ConvertsGoodsToCredits() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Map<Side, Connector> dummyConnectors = Collections.emptyMap();
        CargoHold regularCargo = new CargoHold(1, "CargoHold", dummyConnectors, Side.FRONT, 2, false);
        CargoHold specialCargo = new CargoHold(2, "CargoHold",dummyConnectors, Side.FRONT, 2, true);

        regularCargo.addContains(Good.BLUE);
        regularCargo.addContains(Good.GREEN);
        specialCargo.addContains(Good.RED);
        specialCargo.addContains(Good.YELLOW);

        assertTrue(game.addComponentToShip(1, 3, 3, regularCargo));
        assertTrue(game.addComponentToShip(1, 3, 4, specialCargo));
        game.completeAssembly(1);

        game.sellGoodsPhase();
        Player player = game.getPlayer(1);

        assertEquals(10, player.getCargoCredits(), "I crediti cargo devono corrispondere alla somma dei valori delle merci");
        assertTrue(regularCargo.getContains().isEmpty(), "La stiva regolare deve essere vuota");
        assertTrue(specialCargo.getContains().isEmpty(), "La stiva speciale deve essere vuota");
    }

    @Test
    void testRollDiceInvalidContext() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.setCurrentPlayer(game.getPlayer(1));
        Card invalidCard = new PlanetsCard(1, 1, "TestPlanet",false, 1, List.of(List.of(Good.BLUE)), 0);
        game.setCurrentCard(invalidCard);

        assertThrows(IllegalStateException.class, () -> game.rollDice(1));
    }

    @Test
    void testRollDiceAlreadyRolled() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.setCurrentPlayer(game.getPlayer(1));

        MeteorSwarmCard meteorCard = new MeteorSwarmCard(1, 1, "Meteor", false, new ArrayList<>());
        game.setCurrentCard(meteorCard);

        game.rollDice(1);
        assertThrows(IllegalStateException.class, () -> game.rollDice(1));
    }

    @Test
    void testDrawSubDeckInvalidLevel() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");

        assertThrows(RuntimeException.class, () -> game.drawSubDeck(0));
    }

    @Test
    void testDrawSubDeckInvalidIndex() throws NameErrorException {
        Game game = new Game();
        game.setLevel(2);
        game.addPlayer(1, "Alice");

        assertThrows(RuntimeException.class, () -> game.drawSubDeck(-1));
        assertThrows(RuntimeException.class, () -> game.drawSubDeck(3));
    }

    @Test
    void diceManagement() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.setCurrentPlayer(game.getPlayer(1));

        MeteorSwarmCard testCard = new MeteorSwarmCard(1, 1, "Meteor Swarm", false, new ArrayList<>());
        game.setCurrentCard(testCard);

        int result = game.rollDice(1);
        assertTrue(result >= 2 && result <= 12);
        game.resetDiceResult();
        assertEquals(0, game.getDiceResult());
    }

    @Test
    void endFlightPhase_WithAbandonedPlayer() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Luca");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.completeAssembly(1);
        game.completeAssembly(2);
        game.startFlightPhase();
        Player luca = game.getPlayer(1);
        Player bob = game.getPlayer(2);
        luca.getRocket().setPosition(4);
        bob.getRocket().setPosition(7);
        Map<Side, Connector> engineConnectors = new EnumMap<>(Side.class);
        engineConnectors.put(Side.BACK, Connector.ENGINE);
        Engine engine = new Engine(3, "Engine", engineConnectors, Side.BACK, 1);
        assertTrue(bob.getShip().addComponent(3, 3, engine));
        CargoHold lucaCargo = new CargoHold(1, "CargoHold", Map.of(), Side.FRONT, 2, false);
        lucaCargo.addContains(Good.BLUE);
        lucaCargo.addContains(Good.GREEN);
        luca.getShip().addComponent(1, 3, lucaCargo);
        CargoHold bobCargo = new CargoHold(2, "CargoHold", Map.of(), Side.FRONT, 1, true);
        bobCargo.addContains(Good.RED);
        bob.getShip().addComponent(1, 3, bobCargo);
        game.abandonRace(1);
        Map<Player, Integer> scores = game.endFlightPhase();
        assertEquals(2, scores.get(luca).intValue());
        int expectedBeautyBonus = 2;
        int expectedRouteBonus = 4;
        int expectedScore = bob.getCosmicCredits() + bob.getCargoCredits() + expectedBeautyBonus + expectedRouteBonus - bob.getLostComponents();
        assertEquals(10, scores.get(bob).intValue());
        assertEquals(Phase.FLIGHT_ENDED, game.getCurrentPhase());
    }

    @Test
    void rocketMovementWithCollision() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.completeAssembly(1);
        game.completeAssembly(2);
        game.startFlightPhase();

        Player alice = game.getPlayer(1);
        Player bob = game.getPlayer(2);
        alice.getRocket().setPosition(5);
        bob.getRocket().setPosition(5);
        game.changeRocketPosition(alice, 1); //collisione

        assertEquals(6, alice.getRocket().getPosition());
        assertEquals(5, bob.getRocket().getPosition());
    }

    @Test
    void colorAssignmentUniqueForAllPlayers() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(4);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.addPlayer(3, "Charlie");
        game.addPlayer(4, "Diana");
        Set<Color> assignedColors = new HashSet<>();
        for(Player p : game.getPlayers()) {
            Color c = p.getRocket().getColor();
            assertFalse(assignedColors.contains(c));
            assignedColors.add(c);
        }
        assertEquals(4, assignedColors.size());
    }

    @Test
    void cargoHoldGoodsCounting() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        CargoHold cargo = new CargoHold(1, "Cargo", Collections.emptyMap(), Side.FRONT, 2, true);
        cargo.addContains(Good.RED);
        cargo.addContains(Good.BLUE);
        assertTrue(game.addComponentToShip(1, 3, 4, cargo));
        game.completeAssembly(1);
        game.abandonRace(1);
        Player p = game.getPlayer(1);
        assertNotNull(p);
        assertEquals(3, p.getCargoCredits()); // (4+1)/2 = 2.5 → arrotondato a 2
    }

    @Test
    void testAbandonImmediatelyWithNoComponents() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.getPlayer(1).getShip().getComponents().clear();
        game.getPlayer(2).getShip().getComponents().clear();
        game.abandonRace(1);
        game.abandonRace(2);
        assertEquals(0, game.getRemainingPlayers().size());
        assertEquals(2, game.getEliminatedPlayers().size());
        assertEquals(0, game.getPlayer(1).getCargoCredits()); // verifica che non abbiano ricevuto bonus
        assertEquals(-game.getPlayer(1).getLostComponents(), game.getPlayer(1).getCosmicCredits());
    }

    //controllo corretto arrotondamento per eccesso
    @Test
    void testAbandonWithOddGoods() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        CargoHold cargo = new CargoHold(1, "Cargo", Collections.emptyMap(), Side.FRONT, 3, false);
        cargo.addContains(Good.BLUE);
        cargo.addContains(Good.GREEN);
        cargo.addContains(Good.YELLOW);
        game.addComponentToShip(1, 3, 3, cargo);
        game.abandonRace(1);
        assertEquals(3, game.getPlayer(1).getCargoCredits());

    }

    @Test
    void testAllPlayersAbandon() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.getPlayer(1).getShip().getComponents().clear();
        game.getPlayer(2).getShip().getComponents().clear();
        game.endFlightPhase();
        assertEquals(Phase.FLIGHT_ENDED, game.getCurrentPhase());
        assertTrue(game.getFinishingOrder().isEmpty());
    }

    @Test
    void testNegativeScoreAfterPenalty() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        for(int i=0; i<10; i++) game.getPlayer(1).incrementLostComponents();
        game.abandonRace(1);
        assertEquals(-10, game.getPlayer(1).getCosmicCredits());
    }

    @Test
    void testCargoHoldWithSpecialContainer() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        CargoHold specialCargo = new CargoHold(1, "Cargo", Collections.emptyMap(), Side.FRONT, 2, true);
        specialCargo.addContains(Good.RED);
        game.addComponentToShip(1, 3, 3, specialCargo);
        game.abandonRace(1);
        assertEquals(2, game.getPlayer(1).getCargoCredits());
    }

    @Test
    void testMultipleAbandonsInSamePhase() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(4);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.addPlayer(3, "Charlie");
        game.addPlayer(4, "Diana");
        game.initializeShip();
        game.getPlayers().forEach(p -> p.getShip().getComponents().clear());
        game.endFlightPhase();
        assertEquals(0, game.getRemainingPlayers().size());
        assertEquals(4, game.getEliminatedPlayers().size());
    }

    @Test
    void testDrawSubDeckValid() throws NameErrorException {
        Game game = new Game();
        game.setLevel(2);
        game.addPlayer(1, "Alice");

        List<Card> subDeck = game.drawSubDeck(0);
        assertNotNull(subDeck);
        assertFalse(subDeck.isEmpty());
    }

    @Test
    void testComponentManagement() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Component component = game.drawComponent();
        assertNotNull(component);
        List<Component> faceUpComponents = game.getFaceUpComponentList();
        assertNotNull(faceUpComponents);
        game.putComponentBack(component);
        game.drawFaceUpComponent(component);
        Component componentById = game.getComponent(component.getId());
        assertNotNull(componentById);
    }

    @Test
    void testAddComponentToShipInvalidPlayer() {
        Game game = new Game();
        game.setLevel(1);
        Component component = new Engine(1, "Engine", createConnectors(Side.FRONT, Connector.ENGINE), Side.FRONT, 1);
        assertFalse(game.addComponentToShip(999, 1, 1, component));
    }

    @Test
    void testRemoveComponentFromShipInvalidPlayer() {
        Game game = new Game();
        game.setLevel(1);
        assertFalse(game.removeComponentFromShip(999, 1, 1));
    }

    @Test
    void testRemoveComponentFromShipSuccess() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Component component = new Engine(1, "Engine", createConnectors(Side.FRONT, Connector.ENGINE), Side.FRONT, 1);
        assertTrue(game.addComponentToShip(1, 3, 3, component));
        assertTrue(game.removeComponentFromShip(1, 3, 3));
    }

    @Test
    void testOrderPlayers() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(3);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.addPlayer(3, "Charlie");
        game.initializeShip();

        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);
        game.instantiateRocketForPlayer(3);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);
        game.getPlayer(3).getRocket().setPosition(5);

        game.orderPlayers();

        assertEquals(2, game.getPlayers().get(0).getId()); // Bob primo (pos 15)
        assertEquals(1, game.getPlayers().get(1).getId()); // Alice seconda (pos 10)
        assertEquals(3, game.getPlayers().get(2).getId()); // Charlie terzo (pos 5)
    }

    @Test
    void testOrderPlayersReverse() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(3);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.addPlayer(3, "Charlie");
        game.initializeShip();

        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);
        game.instantiateRocketForPlayer(3);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);
        game.getPlayer(3).getRocket().setPosition(5);

        game.orderPlayersReverse();

        assertEquals(3, game.getPlayers().get(0).getId()); // Charlie primo (pos 5)
        assertEquals(1, game.getPlayers().get(1).getId()); // Alice seconda (pos 10)
        assertEquals(2, game.getPlayers().get(2).getId()); // Bob terzo (pos 15)
    }

    @Test
    void testGetLeader() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);

        game.orderPlayers();

        assertEquals(2, game.getLeader());
    }

    @Test
    void testDrawCardNotLeader() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);

        game.orderPlayers();

        assertThrows(RuntimeException.class, () -> game.drawCard(1));
    }

    @Test
    void testPassTurn() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(3);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.addPlayer(3, "Charlie");

        game.setCurrentPlayer(game.getPlayer(1));
        game.passTurn();
        assertEquals(2, game.getCurrentPlayer().getId());

        game.passTurn();
        assertEquals(3, game.getCurrentPlayer().getId());

        assertThrows(IndexOutOfBoundsException.class, () -> game.passTurn());
        assertEquals(1, game.getCurrentPlayer().getId());
    }

    @Test
    void testCanContinuePlayingNullPlayer() {
        Game game = new Game();
        assertFalse(game.canContinuePlaying(null));
    }

    @Test
    void testCanContinuePlayingAbandonedPlayer() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Player player = game.getPlayer(1);
        player.setAbandoned(true);

        assertFalse(game.canContinuePlaying(player));
    }

    @Test
    void testCanContinuePlayingNoShip() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");

        Player player = game.getPlayer(1);
        assertFalse(game.canContinuePlaying(player));
    }

    @Test
    void testCanContinuePlayingNoRocket() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Player player = game.getPlayer(1);
        assertFalse(game.canContinuePlaying(player));
    }

    @Test
    void testCanContinuePlayingNoCrew() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        Player player = game.getPlayer(1);
        Ship ship = player.getShip();
        ship.getComponents().removeIf(comp -> comp.getName().equals("Cabin") || comp.getName().equals("MainCabin"));
        assertFalse(game.canContinuePlaying(player));
    }

    @Test
    void testCanContinuePlayingNoEnginePower() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        Player player = game.getPlayer(1);
        Ship ship = player.getShip();
        ship.getComponents().removeIf(comp -> comp.getName().equals("Engine"));

        assertFalse(game.canContinuePlaying(player));
    }

    @Test
    void testAutomaticLostNoCrew() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        Player alice = game.getPlayer(1);
        Ship ship = alice.getShip();
        ship.getComponents().removeIf(comp -> comp.getName().equals("Cabin") || comp.getName().equals("MainCabin"));
        int result = game.automaticLost(1);
        assertEquals(2, result);
        assertTrue(alice.hasAbandoned());
    }

    @Test
    void testAutomaticLostTooFarBehind() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(1);
        game.getPlayer(2).getRocket().setPosition(25); // troppo avanti

        int result = game.automaticLost(1);
        assertEquals(3, result);
        assertTrue(game.getPlayer(1).hasAbandoned());
    }

    @Test
    void testAutomaticLostLevel2() throws NameErrorException {
        Game game = new Game();
        game.setLevel(2);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(1);
        game.getPlayer(2).getRocket().setPosition(30); // troppo avanti per livello 2

        int result = game.automaticLost(1);
        assertEquals(3, result);
    }

    @Test
    void testAutomaticLostNoAction() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);

        int result = game.automaticLost(1);
        assertEquals(0, result);
        assertFalse(game.getPlayer(1).hasAbandoned());
    }

    @Test
    void testAutomaticEngineLost() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();

        game.setCurrentPlayer(game.getPlayer(1));

        Player alice = game.getPlayer(1);
        alice.getShip().getComponents().clear();

        boolean result = game.automaticEngineLost(1);
        assertTrue(result);
        assertTrue(alice.hasAbandoned());
        assertEquals(2, game.getCurrentPlayer().getId()); // Turno passato a Bob
    }

    @Test
    void testGetUpdatedPositions() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        game.getPlayer(1).getRocket().setPosition(10);
        game.getPlayer(2).getRocket().setPosition(15);

        Map<Integer, Integer> positions = game.getUpdatedPositions();
        assertEquals(2, positions.size());
        assertEquals(10, positions.get(1).intValue());
        assertEquals(15, positions.get(2).intValue());
    }

    @Test
    void testChangeRocketPositionZero() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);

        Player alice = game.getPlayer(1);
        alice.getRocket().setPosition(5);

        game.changeRocketPosition(alice, 0);

        assertEquals(5, alice.getRocket().getPosition()); // Nessun cambiamento
    }

    @Test
    void testChangeRocketPositionNegative() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");
        game.initializeShip();
        game.instantiateRocketForPlayer(1);
        game.instantiateRocketForPlayer(2);

        Player alice = game.getPlayer(1);
        Player bob = game.getPlayer(2);
        alice.getRocket().setPosition(10);
        bob.getRocket().setPosition(8);

        game.changeRocketPosition(alice, -2); // Collisione con Bob

        assertEquals(7, alice.getRocket().getPosition()); // 8-1 = 7
        assertEquals(8, bob.getRocket().getPosition()); // Bob non si muove
    }

    @Test
    void testRemoveAlienInvalidCabin() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        assertThrows(IllegalArgumentException.class, () -> game.removeAlien(1, 1, 1));
    }

    @Test
    void testRemoveAlienSuccess() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        AlienLifeSupport als = new AlienLifeSupport(0, "AlienLifeSupport", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, AlienColor.BROWN);
        Cabin cabin = new Cabin(1, "Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, false, false);

        game.addComponentToShip(1, 2, 4, als);
        game.addComponentToShip(1, 2, 5, cabin);

        game.handleCrewRequest(1, 2, 5, AlienColor.BROWN);
        assertEquals(AlienColor.BROWN, cabin.getCurrentAlien());

        game.removeAlien(1, 2, 5);
        assertNull(cabin.getCurrentAlien());
        assertEquals(2, game.getPlayer(1).getCrew());
    }

    @Test
    void testShuffleCards() throws NameErrorException {
        Game game = new Game();
        game.setLevel(2);
        game.addPlayer(1, "Alice");
        assertDoesNotThrow(() -> game.shuffleCards());
    }

    @Test
    void testCheckDeckNotEmpty() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        Map<Player, Integer> result = game.checkDeck();
        assertNull(result);
    }

    @Test
    void testInitializeCrew() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Cabin cabin = new Cabin(1, "Cabin", createConnectors(Side.LEFT, Connector.UNIVERSAL), Side.FRONT, false, false);
        game.addComponentToShip(1, 2, 2, cabin);

        Player alice = game.getPlayer(1);
        int initialCrew = alice.getCrew();

        game.initializeCrew(alice);

        assertEquals(initialCrew + 2, alice.getCrew());
    }

    @Test
    void testGetReservedComponent() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");
        game.initializeShip();

        Player alice = game.getPlayer(1);
        Component reserved = game.getReservedComponent(alice, 0);
        assertNull(reserved);
    }

    @Test
    void testSetCurrentPhase() {
        Game game = new Game();
        game.setCurrentPhase(Phase.FLIGHT);
        assertEquals(Phase.FLIGHT, game.getCurrentPhase());

        game.setCurrentPhase(Phase.FLIGHT_ENDED);
        assertEquals(Phase.FLIGHT_ENDED, game.getCurrentPhase());
    }

    @Test
    void testApplyCardEffectNoCard() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.addPlayer(1, "Alice");

        Player alice = game.getPlayer(1);

        assertThrows(IllegalStateException.class, () -> game.applyCardEffect(alice, null));
    }

    @Test
    void testApplyCardEffectWrongTurn() throws NameErrorException {
        Game game = new Game();
        game.setLevel(1);
        game.setMaxPlayers(2);
        game.addPlayer(1, "Alice");
        game.addPlayer(2, "Bob");

        game.setCurrentPlayer(game.getPlayer(1));

        PlanetsCard card = new PlanetsCard(1, 1,"Test Planet", false,1, List.of(List.of(Good.BLUE)),0);
        game.setCurrentCard(card);

        assertThrows(IllegalStateException.class, () -> game.applyCardEffect(game.getPlayer(2), null));
    }
}