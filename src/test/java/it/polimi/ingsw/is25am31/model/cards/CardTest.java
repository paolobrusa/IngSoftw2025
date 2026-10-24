package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Rocket;
import it.polimi.ingsw.is25am31.model.player.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTest {
    private Player player;
    private Ship ship;
    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game();
        player = new Player(1, "TestPlayer", new Gameboard(1));
        ship = new Ship(new Gameboard(1));
        player.setShip(ship);
    }
    private Context createContext(Map<String, Object> params) {
        return new Context(params);
    }
    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testPlanetsCard() throws Exception {
        List<List<Good>> goods = new ArrayList<>();
        goods.add(new ArrayList<>(List.of(Good.RED, Good.BLUE)));
        goods.add(new ArrayList<>(List.of(Good.GREEN)));
        PlanetsCard card = new PlanetsCard(1, 1, "TestPlanets", true, 2, goods, 1);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("planetIndex", 0);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        Map<Side, Connector> connectors = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        CargoHold cargoHold = new CargoHold(1, "CargoHold", connectors, Side.FRONT, 3, true); // SPECIAL = true
        ship.addComponent(3, 2, cargoHold);
        contextParams.clear();
        contextParams.put("Good", "red");
        contextParams.put("x", 3);
        contextParams.put("y", 2);
        context = createContext(contextParams);
        assertFalse(card.applyEffect(player, context));
        contextParams.put("Good", "blue");
        context = createContext(contextParams);
        assertTrue(card.applyEffect(player, context));
    }

    @Test
    public void testMeteorSwarmCard() throws Exception {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor();
        setField(meteor1, "size", Size.SMALL);
        setField(meteor1, "side", Side.FRONT);
        Meteor meteor2 = new Meteor();
        setField(meteor2, "size", Size.LARGE);
        setField(meteor2, "side", Side.BACK);
        meteors.add(meteor1);
        meteors.add(meteor2);

        MeteorSwarmCard card = new MeteorSwarmCard(1, 1, "TestMeteor", true, meteors);
        Map<Side, Connector> shieldCon = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        ShieldGenerator shield = new ShieldGenerator(1, "ShieldGenerator", shieldCon, Side.FRONT, OrientationShield.UPLEFT);
        ship.addComponent(3, 2, shield);

        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", shieldCon, Side.FRONT, 1);
        ship.addComponent(3, 3, battery);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        contextParams.put("diceResult", 5);
        context = createContext(contextParams);
        assertFalse(card.applyEffect(player, context)); // first meteor
        assertTrue(card.applyEffect(player, context));  // second meteor
    }

    @Test
    public void testOpenSpaceCard() {
        OpenSpaceCard card = new OpenSpaceCard(1, 1, "TestOpenSpace", true);
        Map<Side, Connector> connectors = Map.of(Side.FRONT, Connector.ENGINE, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL);
        Engine engine = new Engine(1, "Engine", connectors, Side.FRONT, 2);
        ship.addComponent(3, 2, engine);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", connectors, Side.FRONT, 1);
        ship.addComponent(3, 3, battery);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        assertTrue(card.applyEffect(player, null));
    }
    @Test
    public void testPiratesCard() {
        PiratesCard card = new PiratesCard(1, 1, "TestPirates", true, 2, List.of(Size.SMALL, Size.LARGE), 3, 1);
        Map<Side, Connector> cannonCon = Map.of( Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonCon, Side.FRONT, 3);
        ship.addComponent(3, 2, cannon);
        Map<Side, Connector> batteryCon = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", batteryCon, Side.FRONT, 3);
        ship.addComponent(3, 3, battery);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        contextParams.put("diceResult", 5);
        ShieldGenerator shieldGenerator = new ShieldGenerator(1, "ShieldGenerator", cannonCon, Side.FRONT, OrientationShield.UPLEFT);
        ship.addComponent(2, 2, shieldGenerator);
        contextParams.put("x1", 2);
        contextParams.put("y1", 2);
        context = createContext(contextParams);
        boolean result = card.applyEffect(player, context);
        if (result) {
            assertTrue(card.isWin());
        } else {
            assertFalse(card.isWin());
            int hitCount = 1;
            boolean finished = false;
            while (!finished) {
                finished = card.applyEffect(player, context);
                hitCount++;
            }
            assertEquals(2, hitCount);
            assertFalse(card.isWin());
        }
    }

    @Test
    public void testPiratesCardLoss() {
        PiratesCard card = new PiratesCard(1, 1, "TestPirates", true, 5, List.of(Size.SMALL, Size.LARGE), 3, 1);
        Map<Side, Connector> cannonCon = Map.of( Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonCon, Side.FRONT, 3);
        ship.addComponent(3, 2, cannon);
        Map<Side, Connector> batteryCon = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", batteryCon, Side.FRONT, 3);
        ship.addComponent(3, 3, battery);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        ShieldGenerator shieldGenerator = new ShieldGenerator(1, "ShieldGenerator", cannonCon, Side.FRONT, OrientationShield.UPLEFT);
        ship.addComponent(2, 2, shieldGenerator);
        contextParams.put("x1", 2);
        contextParams.put("y1", 2);
        contextParams.put("diceResult", 4);
        context = createContext(contextParams);
        boolean firstResult = card.applyEffect(player, context);
        assertFalse(firstResult);
        assertFalse(card.isWin());
        boolean secondResult = card.applyEffect(player, context);
        assertTrue(secondResult);
    }

    @Test
    public void testAbandonedShipCard() {
        AbandonedShipCard card = new AbandonedShipCard(1, 1, "TestAbandonedShip", true, 3, 2, 1);
        Map<Side, Connector> cabinCon = Map.of( Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        Cabin cabin = new Cabin(1, "Cabin", cabinCon, Side.FRONT, false, false);
        cabin.modifyCrewMembers(2);
        ship.addComponent(3, 2, cabin);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x", 3);
        contextParams.put("y", 2);
        Context context = createContext(contextParams);
        assertFalse(card.applyEffect(player, context));
        assertTrue(card.applyEffect(player, context));
    }

    @Test
    public void testCombatZoneCard() throws Exception {
        List<CannonHit> hits = new ArrayList<>();
        CannonHit hit1 = new CannonHit();
        setField(hit1, "size", Size.SMALL);
        setField(hit1, "side", Side.FRONT);
        CannonHit hit2 = new CannonHit();
        setField(hit2, "size", Size.LARGE);
        setField(hit2, "side", Side.BACK);
        hits.add(hit1);
        hits.add(hit2);
        CombatZoneCard card = new CombatZoneCard(1, 1, "TestCombat", true, 0, hits, 2, 1);
        player.setRocket(new Rocket(new Gameboard(1)));
        player.getRocket().setPosition(10);
        card.applyEffect(player, null);
        assertEquals(8, player.getRocket().getPosition());
    }

    @Test
    public void testStardustCardNoComponents() {
        StardustCard card = new StardustCard(1, 1, "TestStardust", true);
        player.setRocket(new Rocket(new Gameboard(1)));
        player.getRocket().setPosition(10);
        int initialPosition = player.getRocket().getPosition();
        int exposedConnectors = player.getShip().getExposedConnectors();
        assertTrue(card.applyEffect(player, null));
        assertEquals(initialPosition - exposedConnectors, player.getRocket().getPosition());
    }

    @Test
    public void testEpidemicCard() {
        EpidemicCard card = new EpidemicCard(1, 1, "TestEpidemic", true);
        Map<Side, Connector> connectors = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        Cabin cabin1 = new Cabin(1, "Cabin", connectors, Side.FRONT, false, false);
        Cabin cabin2 = new Cabin(2, "Cabin", connectors, Side.FRONT, false, false);
        cabin1.modifyCrewMembers(1);
        cabin2.modifyCrewMembers(1);
        ship.addComponent(3, 2, cabin1);
        ship.addComponent(3, 3, cabin2);
        card.applyEffect(player, null);
        assertEquals(1, cabin1.getCrewMembers());
        assertEquals(1, cabin2.getCrewMembers());
    }

    @Test
    public void testStardustCardWithComponents() {
        StardustCard card = new StardustCard(1, 1, "TestStardust", true);
        player.setRocket(new Rocket(new Gameboard(1)));
        player.getRocket().setPosition(15);
        Map<Side, Connector> connectors = Map.of(Side.FRONT, Connector.ENGINE, Side.BACK, Connector.NONE, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.NONE);
        Engine engine = new Engine(1, "Engine", connectors, Side.FRONT, 2);
        ship.addComponent(3, 2, engine);
        int initialPosition = player.getRocket().getPosition();
        int exposedConnectors = player.getShip().getExposedConnectors();
        assertTrue(card.applyEffect(player, null));
        assertEquals(initialPosition - exposedConnectors, player.getRocket().getPosition());
        assertNotEquals(initialPosition, player.getRocket().getPosition());
    }

    @Test
    public void testAbandonedStationCard() {
        List<Good> gainedGoods = new ArrayList<>(List.of(Good.RED, Good.BLUE, Good.GREEN));
        AbandonedStationCard card = new AbandonedStationCard(1, 1, "TestAbandonedStation", true, 2, gainedGoods, 3);
        Map<Side, Connector> connectors = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        Cabin cabin = new Cabin(1, "Cabin", connectors, Side.FRONT, false, false);
        cabin.modifyCrewMembers(3);
        ship.addComponent(2, 2, cabin);
        CargoHold cargoHold1 = new CargoHold(2, "CargoHold", connectors, Side.FRONT, 3, true);
        CargoHold cargoHold2 = new CargoHold(3, "CargoHold", connectors, Side.FRONT, 3, true);
        ship.addComponent(3, 2, cargoHold1);
        ship.addComponent(3, 3, cargoHold2);
        player.setRocket(new Rocket(new Gameboard(1)));
        player.getRocket().setPosition(10);
        Map<String, Object> contextParams = new HashMap<>();
        Context contextForSetup = createContext(contextParams);
        assertDoesNotThrow(() -> card.setCardContext(player, contextForSetup));
        contextParams.put("Good", "red");
        contextParams.put("x", 3);
        contextParams.put("y", 2);
        Context context = createContext(contextParams);
        boolean result1 = card.applyEffect(player, context);
        assertFalse(result1);
        assertEquals(2, card.getIterations());
        contextParams.put("Good", "blue");
        contextParams.put("x", 3);
        contextParams.put("y", 3);
        context = createContext(contextParams);
        boolean result2 = card.applyEffect(player, context);
        assertFalse(result2);
        contextParams.put("Good", "green");
        contextParams.put("x", 3);
        contextParams.put("y", 2);
        context = createContext(contextParams);
        boolean result3 = card.applyEffect(player, context);
        assertTrue(result3);
        assertEquals(7, player.getRocket().getPosition());
    }

    @Test
    public void testSlaversCard() {
        SlaversCard card = new SlaversCard(1, 1, "TestSlavers", true, 3, 2, 10, 1);
        assertEquals(1, card.getId());
        assertTrue(card.isTestFlight());
        assertEquals(EffectType.SLAVERS, card.getType());
        Map<Side, Connector> cannonCon = Map.of(Side.FRONT, Connector.CANNON, Side.BACK, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonCon, Side.FRONT, 2);
        ship.addComponent(3, 2, cannon);
        Map<Side, Connector> batteryCon = Map.of(Side.FRONT, Connector.NONE, Side.BACK, Connector.NONE, Side.LEFT, Connector.NONE, Side.RIGHT, Connector.NONE);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", batteryCon, Side.FRONT, 1);
        ship.addComponent(3, 3, battery);
        Cabin cabin = new Cabin(3, "Cabin", batteryCon, Side.FRONT, false, false);
        cabin.modifyCrewMembers(2);
        ship.addComponent(4, 2, cabin);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = createContext(contextParams);
        card.setCardContext(player, context);
        contextParams.clear();
        contextParams.put("x", 4);
        contextParams.put("y", 2);
        context = createContext(contextParams);
        int initialCrew = cabin.getCrewMembers();
        boolean result = card.applyEffect(player, context);
        while (!result && cabin.getCrewMembers() > 0) {
            result = card.applyEffect(player, context);
        }
        assertTrue(cabin.getCrewMembers() < initialCrew);
        assertTrue(result);
    }

    @Test
    public void testSmugglersCard() {
        List<Good> gainedGoods = new ArrayList<>(List.of(Good.RED, Good.BLUE));
        SmugglersCard card = new SmugglersCard(1, 1, "TestSmugglers", true, 3, 2, gainedGoods, 5);
        assertEquals(1, card.getId());
        assertTrue(card.isTestFlight());
        assertEquals(EffectType.SMUGGLERS, card.getType());
    }

    @Test
    public void testSmugglersCardWin() {
        List<Good> gainedGoods = new ArrayList<>(List.of(Good.RED, Good.BLUE));
        SmugglersCard card = new SmugglersCard(1, 1, "TestSmugglers", true, 1, 2, gainedGoods, 3);
        Map<Side, Connector> cannonCon = Map.of(Side.FRONT, Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", cannonCon, Side.FRONT, 2);
        cannon.activate();
        ship.addComponent(3, 2, cannon);
        Map<Side, Connector> batteryCon = Map.of(Side.FRONT, Connector.NONE);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", batteryCon, Side.FRONT, 1);
        ship.addComponent(3, 3, battery);
        CargoHold cargoHold = new CargoHold(3, "CargoHold", batteryCon, Side.FRONT, 3, true);
        ship.addComponent(4, 2, cargoHold);
        Map<String, Object> contextParams = new HashMap<>();
        contextParams.put("x1", 3);
        contextParams.put("y1", 2);
        contextParams.put("x2", 3);
        contextParams.put("y2", 3);
        Context context = new Context(contextParams);
        card.setCardContext(player, context);
        player.getRocket().setPosition(10);
        contextParams = new HashMap<>();
        contextParams.put("Good", "red");
        contextParams.put("x", 4);
        contextParams.put("y", 2);
        context = new Context(contextParams);
        assertFalse(card.applyEffect(player, context));
        contextParams.put("Good", "blue");
        context = new Context(contextParams);
        assertTrue(card.applyEffect(player, context));
        assertTrue(cargoHold.getContains().contains(Good.RED));
        assertTrue(cargoHold.getContains().contains(Good.BLUE));
        assertEquals(7, player.getRocket().getPosition());
    }

    @Test
    public void testSmugglersCardLoss() {
        List<Good> gainedGoods = new ArrayList<>(List.of(Good.RED, Good.BLUE));
        SmugglersCard card = new SmugglersCard(1, 1, "TestSmugglers", true, 4, 2, gainedGoods, 5);
        Map<Side, Connector> cannonCon = Map.of(Side.FRONT, Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", cannonCon, Side.FRONT, 3);
        ship.addComponent(3, 2, cannon);
        Map<Side, Connector> batteryCon = Map.of(Side.FRONT, Connector.NONE);
        BatteryComponent battery = new BatteryComponent(2, "BatteryComponent", batteryCon, Side.FRONT, 1);
        ship.addComponent(3, 3, battery);
        CargoHold cargoHold = new CargoHold(3, "CargoHold", batteryCon, Side.FRONT, 3, true);
        cargoHold.addContains(Good.RED);
        cargoHold.addContains(Good.BLUE);
        ship.addComponent(4, 2, cargoHold);
        Map<String, Object> initParams = new HashMap<>();
        initParams.put("x1", 3);
        initParams.put("y1", 2);
        initParams.put("x2", 3);
        initParams.put("y2", 3);
        Context initContext = new Context(initParams);
        card.setCardContext(player, initContext);
        Map<String, Object> effectParams1 = new HashMap<>();
        effectParams1.put("Good", "red");
        effectParams1.put("x", 4);
        effectParams1.put("y", 2);
        Context context1 = new Context(effectParams1);
        assertFalse(card.applyEffect(player, context1));
        Map<String, Object> effectParams2 = new HashMap<>();
        effectParams2.put("Good", "blue");
        effectParams2.put("x", 4);
        effectParams2.put("y", 2);
        Context context2 = new Context(effectParams2);
        assertTrue(card.applyEffect(player, context2));
        assertEquals(0, cargoHold.getContains().size());
    }

    @Test
    public void testGetCrewMembers() {
        AbandonedShipCard cardWithCrew = new AbandonedShipCard(1, 1, "Test", true, 2, 1, 1);
        assertDoesNotThrow(cardWithCrew::getCrewMembers);
        OpenSpaceCard cardWithoutCrew = new OpenSpaceCard(1, 1, "Test", true);
        assertThrows(UnsupportedOperationException.class, cardWithoutCrew::getCrewMembers);
    }

    @Test
    public void testCardAsciiArt() {
        List<List<Good>> planetGoods = new ArrayList<>();
        planetGoods.add(List.of(Good.RED));
        planetGoods.add(List.of(Good.BLUE, Good.GREEN));

        PlanetsCard planetsCard = new PlanetsCard(1, 1, "Planets", true, 2, planetGoods, 1);
        assertNotNull(planetsCard.getAsciiArt());

        MeteorSwarmCard meteorCard = new MeteorSwarmCard(1, 1, "Meteor", true, List.of(new Meteor()));
        assertNotNull(meteorCard.getAsciiArt());

        OpenSpaceCard openSpaceCard = new OpenSpaceCard(1, 1, "OpenSpace", true);
        assertNotNull(openSpaceCard.getAsciiArt());

        PiratesCard piratesCard = new PiratesCard(1, 1, "Pirates", true, 3, List.of(Size.SMALL), 5, 1);
        assertNotNull(piratesCard.getAsciiArt());

        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(1, 1, "AbandonedShip", true, 2, 1, 1);
        assertNotNull(abandonedShipCard.getAsciiArt());

        CombatZoneCard combatCard = new CombatZoneCard(1, 1, "CombatZone", true, 0, List.of(new CannonHit()), 2, 1);
        assertNotNull(combatCard.getAsciiArt());

        StardustCard stardustCard = new StardustCard(1, 1, "Stardust", true);
        assertNotNull(stardustCard.getAsciiArt());

        EpidemicCard epidemicCard = new EpidemicCard(1, 1, "Epidemic", true);
        assertNotNull(epidemicCard.getAsciiArt());

        AbandonedStationCard stationCard = new AbandonedStationCard(1, 1, "AbandonedStation", true, 2, List.of(Good.RED), 1);
        assertNotNull(stationCard.getAsciiArt());

        SlaversCard slaversCard = new SlaversCard(1, 1, "Slavers", true, 3, 1, 5, 1);
        assertNotNull(slaversCard.getAsciiArt());

        SmugglersCard smugglersCard = new SmugglersCard(1, 1, "Smugglers", true, 3, 1, List.of(Good.RED), 1);
        assertNotNull(smugglersCard.getAsciiArt());
    }

}