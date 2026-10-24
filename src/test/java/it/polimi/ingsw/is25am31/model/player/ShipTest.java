package it.polimi.ingsw.is25am31.model.player;

import it.polimi.ingsw.is25am31.model.cards.Size;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private Cannon createCannon(int id, Side cannonSide, int power, boolean active) {
        Map<Side, Connector> connectors = new EnumMap<>(Side.class);
        for (Side side : Side.values()) {
            connectors.put(side, side == cannonSide ? Connector.CANNON : Connector.UNIVERSAL);
        }
        Cannon cannon = new Cannon(id, "Cannon", connectors, cannonSide, power);
        if (active) cannon.activate();
        return cannon;
    }

    private ShieldGenerator createShield(OrientationShield orientation, boolean active) {
        Map<Side, Connector> shieldMap = new EnumMap<>(Side.class);
        shieldMap.put(Side.FRONT, Connector.UNIVERSAL);
        shieldMap.put(Side.BACK, Connector.UNIVERSAL);
        shieldMap.put(Side.LEFT, Connector.UNIVERSAL);
        shieldMap.put(Side.RIGHT, Connector.UNIVERSAL);
        ShieldGenerator shield = new ShieldGenerator(1, "ShieldGenerator", shieldMap, Side.FRONT, orientation);
        if (active) {
            shield.activate();
        }
        return shield;
    }

    private CargoHold createSimpleComponent(int id) {
        Map<Side, Connector> compMap = new EnumMap<>(Side.class);
        compMap.put(Side.FRONT, Connector.UNIVERSAL);
        compMap.put(Side.BACK, Connector.UNIVERSAL);
        compMap.put(Side.LEFT, Connector.UNIVERSAL);
        compMap.put(Side.RIGHT, Connector.UNIVERSAL);
        return new CargoHold(id, "CargoHold", compMap, Side.FRONT, 2, false);
    }

    @Test
    void addComponent() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> mappa = new HashMap<>();
        mappa.put(Side.BACK, Connector.NONE);
        mappa.put(Side.FRONT, Connector.UNIVERSAL);
        mappa.put(Side.RIGHT, Connector.DOUBLE);
        mappa.put(Side.LEFT, Connector.SINGLE);
    }

    @Test
    void validateShipNextPhase() {
        Gameboard gameboard = new Gameboard(2);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> mappa = new HashMap<>();
        mappa.put(Side.BACK, Connector.ENGINE);
        mappa.put(Side.FRONT, Connector.NONE);
        mappa.put(Side.RIGHT, Connector.DOUBLE);
        mappa.put(Side.LEFT, Connector.SINGLE);
        Map<Side, Connector> mappa2 = new HashMap<>();
        mappa2.put(Side.BACK, Connector.SINGLE);
        mappa2.put(Side.FRONT, Connector.CANNON);
        mappa2.put(Side.RIGHT, Connector.DOUBLE);
        mappa2.put(Side.LEFT, Connector.SINGLE);
        Map<Side, Connector> mappa3 = new HashMap<>();
        mappa3.put(Side.BACK, Connector.CANNON);
        mappa3.put(Side.FRONT, Connector.SINGLE);
        mappa3.put(Side.RIGHT, Connector.DOUBLE);
        mappa3.put(Side.LEFT, Connector.DOUBLE);
        Map<Side, Connector> mappa4 = new HashMap<>();
        mappa4.put(Side.BACK,  Connector.UNIVERSAL);
        mappa4.put(Side.FRONT, Connector.UNIVERSAL);
        mappa4.put(Side.RIGHT, Connector.UNIVERSAL);
        mappa4.put(Side.LEFT,  Connector.UNIVERSAL);
        Map<Side, Connector> mappa5 = new HashMap<>();
        mappa5.put(Side.BACK,  Connector.SINGLE);
        mappa5.put(Side.FRONT, Connector.NONE);
        mappa5.put(Side.RIGHT, Connector.DOUBLE);
        mappa5.put(Side.LEFT,  Connector.SINGLE);
        Map<Side, Connector> mappa6 = new HashMap<>();
        mappa6.put(Side.BACK,  Connector.NONE);
        mappa6.put(Side.FRONT, Connector.SINGLE);
        mappa6.put(Side.RIGHT, Connector.SINGLE);
        mappa6.put(Side.LEFT,  Connector.SINGLE);
        Map<Side, Connector> mappa7 = new HashMap<>();
        mappa7.put(Side.BACK,  Connector.SINGLE);
        mappa7.put(Side.FRONT, Connector.SINGLE);
        mappa7.put(Side.RIGHT, Connector.SINGLE);
        mappa7.put(Side.LEFT,  Connector.SINGLE);
        Component cannon = new Cannon(1, "Cannon", mappa2, Side.FRONT, 2);
        cannon.activate();
        //Component battery = new BatteryComponent(2, "battery", mappa, Side.FRONT, 2);
        Component cannon2 = new Cannon(2, "cannon2", mappa3, Side.FRONT, 1);
        Component engine = new Engine(3, "engine", mappa, Side.FRONT, 3);
        Component shield = new ShieldGenerator(4, "shied", mappa4, Side.FRONT, OrientationShield.UPLEFT);
        //shield.activate();
        Component Cabin = new CargoHold(5, "Cargo", mappa5, Side.FRONT, 2, false);
        Component cargo = new CargoHold(5, "Cargo", mappa6, Side.FRONT, 2, false);
        //ship.addComponent(2, 2, engine);
        ship.addComponent(2, 4, cannon);
        ship.addComponent(1, 2, cargo);
        ship.addComponent(2, 2, shield);
        ship.addComponent(2, 1, engine);
        ship.addComponent(1, 3, Cabin);
        //ship.addComponent(3, 5, cargo);
        ship.validateShipNextPhase();
        ship.hitByCannon(7, Side.FRONT, Size.SMALL);
    }

    @Test
    void testUsableSlotsLevel1() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        assertFalse(ship.getUsableSlot(2, 3));
        assertTrue(ship.getUsableSlot(1, 2));
        assertTrue(ship.getUsableSlot(3, 4));
        assertFalse(ship.getUsableSlot(0, 0));
    }

    @Test
    void hitByMeteorTest(){
        Gameboard gameboard = new Gameboard(2);
        Player player = new Player(1, "testPlayer", gameboard);
        Component component = new Component(1, "testComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.UNIVERSAL)), Side.FRONT);
        player.getShip().addComponent(3, 3, component);
        assertFalse(player.getShip().getUsableSlot(3, 3));
        player.getShip().hitByMeteor(7, Side.BACK, Size.SMALL);
        assertTrue(player.getShip().getUsableSlot(3, 3));
        component = new Component(1, "testComponent", new HashMap<>(Map.of(Side.FRONT, Connector.UNIVERSAL, Side.BACK, Connector.UNIVERSAL, Side.RIGHT, Connector.UNIVERSAL, Side.LEFT, Connector.NONE)), Side.FRONT);
        player.getShip().addComponent(3, 3, component);
        player.getShip().hitByMeteor(8, Side.LEFT, Size.SMALL);
        assertFalse(player.getShip().getUsableSlot(3, 3));
        player.getShip().hitByMeteor(8, Side.LEFT, Size.LARGE);
        assertTrue(player.getShip().getUsableSlot(3, 3));
    }

    @Test
    void activateCannon() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        // create battery with capacity 1
        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(1, "BatteryComponent", batteryMap, Side.FRONT, 1);
        //create double cannon (power 2)
        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(2, "Cannon", cannonMap, Side.FRONT, 2);
        ship.addComponent(2, 2, battery);
        ship.addComponent(2, 1, cannon);
        ship.activateCannon(2, 1, 2, 2);
        assertTrue(cannon.isActive());
        assertEquals(0, battery.getCapacity());
    }

    @Test
    void activateEngine() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(1, "BatteryComponent", batteryMap, Side.FRONT, 1);

        Map<Side, Connector> engineMap = new EnumMap<>(Side.class);
        engineMap.put(Side.BACK, Connector.ENGINE);
        engineMap.put(Side.FRONT, Connector.UNIVERSAL);
        engineMap.put(Side.LEFT, Connector.UNIVERSAL);
        engineMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Engine engine = new Engine(2, "Engine", engineMap, Side.FRONT, 2);

        ship.addComponent(2, 2, battery);
        ship.addComponent(2, 1, engine);
        ship.activateEngine(2, 1, 2, 2);

        assertTrue(engine.isActive());
        assertEquals(0, battery.getCapacity());
    }

    @Test
    void activateShield() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(1, "BatteryComponent", batteryMap, Side.FRONT, 1);

        Map<Side, Connector> shieldMap = new EnumMap<>(Side.class);
        shieldMap.put(Side.FRONT, Connector.UNIVERSAL);
        shieldMap.put(Side.BACK, Connector.UNIVERSAL);
        shieldMap.put(Side.LEFT, Connector.UNIVERSAL);
        shieldMap.put(Side.RIGHT, Connector.UNIVERSAL);
        ShieldGenerator shield = new ShieldGenerator(2, "ShieldGenerator", shieldMap, Side.FRONT, OrientationShield.UPLEFT);

        ship.addComponent(2, 2, battery);
        ship.addComponent(2, 1, shield);
        ship.activateShield(2, 1, 2, 2);

        assertTrue(shield.isActive());
        assertEquals(0, battery.getCapacity());
    }

    @Test
    void reserveComponent() {
        Gameboard gameboard = new Gameboard(2);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);
        ship.addComponent(0, 5, cannon);
        assertNotNull(ship.getReservedComponent(0));
        assertEquals(cannon, ship.getReservedComponent(0));
        assertEquals(cannon, ship.getComponent(0, 5));
    }

    @Test
    void getReservedComponent() {
        Gameboard gameboard = new Gameboard(2);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);
        Map<Side, Connector> engineMap = new EnumMap<>(Side.class);
        engineMap.put(Side.BACK, Connector.ENGINE);
        engineMap.put(Side.FRONT, Connector.UNIVERSAL);
        engineMap.put(Side.LEFT, Connector.UNIVERSAL);
        engineMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Engine engine = new Engine(2, "Engine", engineMap, Side.FRONT, 2);
        ship.addComponent(0, 5, cannon);
        ship.addComponent(0, 6, engine);
        assertEquals(cannon, ship.getReservedComponent(0));
        assertEquals(engine, ship.getReservedComponent(1));
    }

    @Test
    void getAlienCount() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> cabinMap = new EnumMap<>(Side.class);
        cabinMap.put(Side.FRONT, Connector.UNIVERSAL);
        cabinMap.put(Side.BACK, Connector.UNIVERSAL);
        cabinMap.put(Side.LEFT, Connector.UNIVERSAL);
        cabinMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(1, "Cabin", cabinMap, Side.FRONT, true, false);
        cabin.addAlien(AlienColor.BROWN);

        ship.addComponent(2, 2, cabin);

        assertEquals(1, ship.getAlienCount(AlienColor.BROWN));
        assertEquals(0, ship.getAlienCount(AlienColor.PURPLE));
    }

    @Test
    void addComponentFromShip() {
        Gameboard gameboard = new Gameboard(2);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);
        ship.addComponent(0, 5, cannon);
        assertTrue(ship.addComponentFromShip(2, 2, 0, 5));
        assertEquals(cannon, ship.getComponent(2, 2));
        assertNull(ship.getComponent(0, 5));
    }

    @Test
    void removeComponent() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);

        ship.addComponent(2, 2, cannon);
        assertNotNull(ship.getComponent(2, 2));

        ship.removeComponent(2, 2);
        assertNull(ship.getComponent(2, 2));
        assertTrue(ship.getUsableSlot(2, 2));
    }

    @Test
    void getComponent() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);

        ship.addComponent(2, 2, cannon);
        assertEquals(cannon, ship.getComponent(2, 2));
    }

    @Test
    void getExposedConnectors() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, Connector.SINGLE);
        connectors.put(Side.BACK, Connector.SINGLE);
        connectors.put(Side.LEFT, Connector.SINGLE);
        connectors.put(Side.RIGHT, Connector.SINGLE);
        Component component = new Component(1, "Test", connectors, Side.FRONT);
        ship.addComponent(1, 2, component);
        assertEquals(8, ship.getExposedConnectors());
    }

    @Test
    void getCrewNumber() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> cabinMap = new EnumMap<>(Side.class);
        cabinMap.put(Side.FRONT, Connector.UNIVERSAL);
        cabinMap.put(Side.BACK, Connector.UNIVERSAL);
        cabinMap.put(Side.LEFT, Connector.UNIVERSAL);
        cabinMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(1, "Cabin", cabinMap, Side.FRONT, false, false);
        cabin.modifyCrewMembers(2);

        ship.addComponent(2, 2, cabin);
        assertEquals(4, ship.getCrewNumber());
    }

    @Test
    void getCrewNumberWithoutAlien() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cabinMap = new EnumMap<>(Side.class);
        cabinMap.put(Side.FRONT, Connector.UNIVERSAL);
        cabinMap.put(Side.BACK, Connector.UNIVERSAL);
        cabinMap.put(Side.LEFT, Connector.UNIVERSAL);
        cabinMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(1, "Cabin", cabinMap, Side.FRONT, true, false);
        cabin.addAlien(AlienColor.BROWN);
        ship.addComponent(2, 2, cabin);
        assertEquals(2, ship.getCrewNumberWithoutAlien());
    }

    @Test
    void getEnginePower() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(3, "BatteryComponent", batteryMap, Side.FRONT, 1);

        Map<Side, Connector> engineMap = new EnumMap<>(Side.class);
        engineMap.put(Side.BACK, Connector.ENGINE);
        engineMap.put(Side.FRONT, Connector.UNIVERSAL);
        engineMap.put(Side.LEFT, Connector.UNIVERSAL);
        engineMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Engine engine = new Engine(1, "Engine", engineMap, Side.FRONT, 2);

        Map<Side, Connector> cabinMap = new EnumMap<>(Side.class);
        cabinMap.put(Side.FRONT, Connector.UNIVERSAL);
        cabinMap.put(Side.BACK, Connector.UNIVERSAL);
        cabinMap.put(Side.LEFT, Connector.UNIVERSAL);
        cabinMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(2, "Cabin", cabinMap, Side.FRONT, true, false);
        cabin.addAlien(AlienColor.BROWN);

        ship.addComponent(1, 2, battery);
        ship.addComponent(1, 3, engine);
        ship.addComponent(1, 4, cabin);
        ship.activateEngine(1, 3, 1, 2);

        assertEquals(4, ship.getEnginePower());
    }

    @Test
    void getCannonPower() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(3, "BatteryComponent", batteryMap, Side.FRONT, 1);

        Map<Side, Connector> cannonMap = new EnumMap<>(Side.class);
        cannonMap.put(Side.FRONT, Connector.CANNON);
        cannonMap.put(Side.BACK, Connector.UNIVERSAL);
        cannonMap.put(Side.LEFT, Connector.UNIVERSAL);
        cannonMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cannon cannon = new Cannon(1, "Cannon", cannonMap, Side.FRONT, 2);

        Map<Side, Connector> cabinMap = new EnumMap<>(Side.class);
        cabinMap.put(Side.FRONT, Connector.UNIVERSAL);
        cabinMap.put(Side.BACK, Connector.UNIVERSAL);
        cabinMap.put(Side.LEFT, Connector.UNIVERSAL);
        cabinMap.put(Side.RIGHT, Connector.UNIVERSAL);
        Cabin cabin = new Cabin(2, "Cabin", cabinMap, Side.FRONT, false, true);
        cabin.addAlien(AlienColor.PURPLE);

        ship.addComponent(2, 1, battery);
        ship.addComponent(2, 2, cannon);
        ship.addComponent(2, 4, cabin);
        ship.activateCannon(2, 2, 2, 1);
        assertEquals(4.0, ship.getCannonPower());
    }

    @Test
    void getGoodCount() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cargoMap = new EnumMap<>(Side.class);
        cargoMap.put(Side.FRONT, Connector.UNIVERSAL);
        cargoMap.put(Side.BACK, Connector.UNIVERSAL);
        cargoMap.put(Side.LEFT, Connector.UNIVERSAL);
        cargoMap.put(Side.RIGHT, Connector.UNIVERSAL);
        CargoHold cargo = new CargoHold(1, "CargoHold", cargoMap, Side.FRONT, 2, true); // Speciale per merci rosse
        cargo.addContains(Good.RED);
        cargo.addContains(Good.BLUE);
        ship.addComponent(2, 2, cargo);
        assertEquals(2, ship.getGoodCount());
    }

    @Test
    void getBatteryCount() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(1, "BatteryComponent", batteryMap, Side.FRONT, 3);
        ship.addComponent(2, 2, battery);
        assertEquals(3, ship.getBatteryCount());
    }

    @Test
    void removeGoods() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Map<Side, Connector> cargoMap = new EnumMap<>(Side.class);
        cargoMap.put(Side.FRONT, Connector.UNIVERSAL);
        cargoMap.put(Side.BACK, Connector.UNIVERSAL);
        cargoMap.put(Side.LEFT, Connector.UNIVERSAL);
        cargoMap.put(Side.RIGHT, Connector.UNIVERSAL);
        CargoHold cargo = new CargoHold(1, "CargoHold", cargoMap, Side.FRONT, 2, true);
        cargo.addContains(Good.RED);
        cargo.addContains(Good.BLUE);
        ship.addComponent(2, 2, cargo);
        ship.removeGoods(2, 2, Good.RED);
        assertEquals(1, cargo.getContains().size());
        assertFalse(cargo.getContains().contains(Good.RED));
    }

    @Test
    void removeBattery() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);

        Map<Side, Connector> batteryMap = new EnumMap<>(Side.class);
        batteryMap.put(Side.FRONT, Connector.UNIVERSAL);
        batteryMap.put(Side.BACK, Connector.UNIVERSAL);
        batteryMap.put(Side.LEFT, Connector.UNIVERSAL);
        batteryMap.put(Side.RIGHT, Connector.UNIVERSAL);
        BatteryComponent battery = new BatteryComponent(1, "BatteryComponent", batteryMap, Side.FRONT, 3);
        ship.addComponent(2, 2, battery);
        ship.removeBattery(2, 2);
        assertEquals(2, battery.getCapacity());
    }


    @Test
    void removeGoodsTest() {
        Gameboard gameboard = new Gameboard(1);
        Ship ship = new Ship(gameboard);
        Player player = new Player(1, "Test", gameboard);
        Map<Side, Connector> cargoMap = new HashMap<>();
        cargoMap.put(Side.FRONT, Connector.UNIVERSAL);
        cargoMap.put(Side.BACK, Connector.UNIVERSAL);
        cargoMap.put(Side.LEFT, Connector.UNIVERSAL);
        cargoMap.put(Side.RIGHT, Connector.UNIVERSAL);
        CargoHold cargo = new CargoHold(1, "CargoHold", cargoMap, Side.FRONT, 2, true);
        cargo.addContains(Good.RED);
        cargo.addContains(Good.BLUE);
        ship.addComponent(2, 2, cargo);
        ship.removeGoods(2, 2, Good.RED);
        assertEquals(1, cargo.getContains().size());
        assertFalse(cargo.getContains().contains(Good.RED));
        assertThrows(RuntimeException.class, () -> ship.removeGoods(2, 2, Good.GREEN));
        Component nonCargo = new Component(2, "NotCargo", cargoMap, Side.FRONT);
        ship.addComponent(1, 2, nonCargo);
        assertThrows(RuntimeException.class, () -> ship.removeGoods(1, 2, Good.RED));
    }

    @Test
    void testFrontLeft() {
        {
            Gameboard gameboard = new Gameboard(1);
            Ship ship = new Ship(gameboard);
            Map<Side, Connector> connectors = new EnumMap<>(Side.class);
            connectors.put(Side.FRONT, Connector.UNIVERSAL);
            connectors.put(Side.BACK, Connector.UNIVERSAL);
            connectors.put(Side.LEFT, Connector.UNIVERSAL);
            connectors.put(Side.RIGHT, Connector.UNIVERSAL);
            Component component = new BatteryComponent(1, "BatteryComponent", connectors, Side.FRONT, 1);
            ship.addComponent(0, 3, component);
            assertNotNull(ship.getComponent(0, 3));
            ship.hitByCannon(7, Side.FRONT, Size.LARGE);
            assertNull(ship.getComponent(0, 3));
            assertTrue(ship.getUsableSlot(0, 3));
        }
        {
            Gameboard gameboard = new Gameboard(1);
            Ship ship = new Ship(gameboard);
            int targetColumn = 0;
            int diceRoll = targetColumn + 4;
            for (int i = 0; i < 5; i++) {
                assertNull(ship.getComponent(i, targetColumn));
            }
            boolean[][] initialUsableSlots = new boolean[5][7];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    initialUsableSlots[i][j] = ship.getUsableSlot(i, j);
                }
            }
            ship.hitByCannon(diceRoll, Side.FRONT, Size.LARGE);
            for (int i = 0; i < 5; i++) {
                assertNull(ship.getComponent(i, targetColumn));
            }
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    assertEquals(initialUsableSlots[i][j], ship.getUsableSlot(i, j),
                            "Slot [" + i + "][" + j + "] è cambiato inaspettatamente");
                }
            }
        }
        {
            Gameboard gameboard = new Gameboard(1);
            Ship ship = new Ship(gameboard);
            assertDoesNotThrow(() -> ship.hitByCannon(7, Side.LEFT, Size.LARGE));
        }
    }

    @Test
    void hitByCannon() {
        Gameboard gameboard1 = new Gameboard(1);
        Ship ship1 = new Ship(gameboard1);
        CargoHold comp1 = createSimpleComponent(1);
        ship1.addComponent(0, 3, comp1);
        ship1.hitByCannon(7, Side.FRONT, Size.SMALL);
        assertNull(ship1.getComponent(0, 3));
        assertTrue(ship1.getUsableSlot(0, 3));

        Gameboard gameboard2 = new Gameboard(1);
        Ship ship2 = new Ship(gameboard2);
        ShieldGenerator shield = createShield(OrientationShield.UPLEFT, true);
        ship2.addComponent(1, 2, shield);
        CargoHold comp2 = createSimpleComponent(2);
        ship2.addComponent(0, 3, comp2);
        ship2.hitByCannon(7, Side.FRONT, Size.SMALL);
        assertNotNull(ship2.getComponent(0, 3));

        Gameboard gameboard3 = new Gameboard(1);
        Ship ship3 = new Ship(gameboard3);
        CargoHold comp3 = createSimpleComponent(3);
        ship3.addComponent(4, 4, comp3);
        ship3.hitByCannon(8, Side.BACK, Size.SMALL);
        assertNull(ship3.getComponent(4, 4));

        Gameboard gameboard4 = new Gameboard(1);
        Ship ship4 = new Ship(gameboard4);
        CargoHold comp4 = createSimpleComponent(4);
        ship4.addComponent(3, 1, comp4);
        ship4.hitByCannon(8, Side.LEFT, Size.SMALL);
        assertNull(ship4.getComponent(3, 1));

        Gameboard gameboard5 = new Gameboard(1);
        Ship ship5 = new Ship(gameboard5);
        CargoHold comp5 = createSimpleComponent(5);
        ship5.addComponent(3, 5, comp5);
        ship5.hitByCannon(8, Side.RIGHT, Size.SMALL);
        assertNull(ship5.getComponent(3, 5));

        Gameboard gameboard6 = new Gameboard(1);
        Ship ship6 = new Ship(gameboard6);
        CargoHold comp6 = createSimpleComponent(6);
        ship6.addComponent(1, 2, comp6);
        ship6.hitByCannon(6, Side.FRONT, Size.LARGE);
        assertNull(ship6.getComponent(1, 2));

        Gameboard gameboard7 = new Gameboard(1);
        Ship ship7 = new Ship(gameboard7);
        CargoHold comp7 = createSimpleComponent(7);
        ship7.addComponent(0, 3, comp7);
        ship7.hitByCannon(3, Side.FRONT, Size.SMALL);
        assertNotNull(ship7.getComponent(0, 3));
    }

    @Test
    void testSmallMeteorFL() {
        Gameboard gameboard1 = new Gameboard(2);
        Ship ship1 = new Ship(gameboard1);
        CargoHold target1 = createSimpleComponent(2);
        ship1.addComponent(1, 3, target1);
        assertNotNull(ship1.getComponent(1, 3));
        ship1.hitByMeteor(7, Side.FRONT, Size.SMALL);
        assertNull(ship1.getComponent(1, 3));

        Gameboard gameboard2 = new Gameboard(2);
        Ship ship2 = new Ship(gameboard2);
        CargoHold target2 = createSimpleComponent(3);
        ShieldGenerator shield2 = createShield(OrientationShield.UPRIGHT, true);
        ship2.addComponent(1, 3, target2);
        ship2.addComponent(2, 2, shield2);
        assertNotNull(ship2.getComponent(1, 3));
        ship2.hitByMeteor(7, Side.FRONT, Size.SMALL);
        assertNotNull(ship2.getComponent(1, 3));

        Gameboard gameboard3 = new Gameboard(2);
        Ship ship3 = new Ship(gameboard3);
        CargoHold target3 = createSimpleComponent(4);
        ship3.addComponent(3, 1, target3);
        assertNotNull(ship3.getComponent(3, 1));
        ship3.hitByMeteor(8, Side.LEFT, Size.SMALL);
        assertNull(ship3.getComponent(3, 1));

        Gameboard gameboard4 = new Gameboard(2);
        Ship ship4 = new Ship(gameboard4);
        CargoHold target4 = createSimpleComponent(5);
        ShieldGenerator shield4 = createShield(OrientationShield.UPLEFT, true);
        ship4.addComponent(3, 1, target4);
        ship4.addComponent(2, 0, shield4);
        assertNotNull(ship4.getComponent(3, 1));
        ship4.hitByMeteor(8, Side.LEFT, Size.SMALL);
        assertNotNull(ship4.getComponent(3, 1));
    }
    @Test
    void testSmallMeteorBR() {
        Gameboard gameboard1 = new Gameboard(2);
        Ship ship1 = new Ship(gameboard1);
        CargoHold target1 = createSimpleComponent(6);
        ship1.addComponent(3, 3, target1);
        assertNotNull(ship1.getComponent(3, 3));
        ship1.hitByMeteor(7, Side.BACK, Size.SMALL);
        assertNull(ship1.getComponent(3, 3));

        Gameboard gameboard2 = new Gameboard(2);
        Ship ship2 = new Ship(gameboard2);
        CargoHold target2 = createSimpleComponent(7);
        ShieldGenerator shield2 = createShield(OrientationShield.DOWNRIGHT, true);
        ship2.addComponent(3, 3, target2);
        ship2.addComponent(4, 2, shield2);
        assertNotNull(ship2.getComponent(3, 3));
        ship2.hitByMeteor(7, Side.BACK, Size.SMALL);
        assertNotNull(ship2.getComponent(3, 3));

        Gameboard gameboard3 = new Gameboard(2);
        Ship ship3 = new Ship(gameboard3);
        CargoHold target3 = createSimpleComponent(8);
        ShieldGenerator shield3 = createShield(OrientationShield.DOWNRIGHT, true);
        ship3.addComponent(3, 5, target3);
        ship3.addComponent(2, 6, shield3);
        assertNotNull(ship3.getComponent(3, 5));
        ship3.hitByMeteor(8, Side.RIGHT, Size.SMALL);
        assertNotNull(ship3.getComponent(3, 5));
    }

    @Test
    void testLargeMeteorFL() throws Exception {
        Gameboard gameboard1 = new Gameboard(2);
        Ship ship1 = new Ship(gameboard1);
        Component target1 = createSimpleComponent(1);
        ship1.addComponent(1, 3, target1);
        Method method = Ship.class.getDeclaredMethod("largeMeteorFL", int.class, Side.class);
        method.setAccessible(true);
        method.invoke(ship1, 3, Side.FRONT);
        assertNull(ship1.getGrid()[1][3], "Componente dovrebbe essere rimosso");
        Gameboard gameboard2 = new Gameboard(2);
        Ship ship2 = new Ship(gameboard2);
        Cannon cannon2 = createCannon(1, Side.FRONT, 2, true);
        Component target2 = createSimpleComponent(2);
        ship2.addComponent(1, 3, cannon2);
        ship2.addComponent(3, 3, target2);
        method.invoke(ship2, 3, Side.FRONT);
        assertNotNull(ship2.getGrid()[3][3], "Componente target dovrebbe rimanere");
        assertNotNull(ship2.getGrid()[1][3], "Componente dovrebbe rimanere");
        Gameboard gameboard3 = new Gameboard(2);
        Ship ship3 = new Ship(gameboard3);
        Cannon cannon3 = createCannon(1, Side.LEFT, 2, true);
        Component target3 = createSimpleComponent(2);
        ship3.addComponent(3, 0, cannon3);
        ship3.addComponent(3, 3, target3);
        method.invoke(ship3, 3, Side.LEFT);
        assertNotNull(ship3.getGrid()[3][3], "Componente dovrebbe rimanere");
    }

    @Test
    void testLargeMeteorBR() throws Exception {
        {
            Gameboard gameboard = new Gameboard(1);
            Ship ship = new Ship(gameboard);
            Component target = createSimpleComponent(1);
            ship.addComponent(3, 3, target);
            Method method = Ship.class.getDeclaredMethod("largeMeteorBR", int.class, Side.class);
            method.setAccessible(true);
            method.invoke(ship, 3, Side.BACK);
            assertNull(ship.getGrid()[3][3], "Componente dovrebbe essere rimosso");
        }
        {
            Gameboard gameboard = new Gameboard(2); // Livello 2
            Ship ship = new Ship(gameboard);
            Component target = createSimpleComponent(2);
            Cannon cannon = createCannon(1, Side.BACK, 2, true);
            ship.addComponent(3, 3, target);
            ship.addComponent(3, 4, cannon);
            Method method = Ship.class.getDeclaredMethod("largeMeteorBR", int.class, Side.class);
            method.setAccessible(true);
            method.invoke(ship, 3, Side.BACK);
            assertNotNull(ship.getGrid()[3][3], "Componente NON dovrebbe essere rimosso");
        }
        {
            Gameboard gameboard = new Gameboard(1);
            Ship ship = new Ship(gameboard);
            Component target = createSimpleComponent(3);
            ship.addComponent(3, 5, target);
            Method method = Ship.class.getDeclaredMethod("largeMeteorBR", int.class, Side.class);
            method.setAccessible(true);
            method.invoke(ship, 3, Side.RIGHT);
            assertNull(ship.getGrid()[3][5], "Componente dovrebbe essere rimosso");
        }
        {
            Gameboard gameboard = new Gameboard(2);
            Ship ship = new Ship(gameboard);
            Component target = createSimpleComponent(4);
            Cannon cannon = createCannon(1, Side.RIGHT, 1, false);
            ship.addComponent(3, 5, target);
            ship.addComponent(2, 5, cannon);
            Method method = Ship.class.getDeclaredMethod("largeMeteorBR", int.class, Side.class);
            method.setAccessible(true);
            method.invoke(ship, 3, Side.RIGHT);
            assertNotNull(ship.getGrid()[3][5], "Componente NON dovrebbe essere rimosso");
        }
    }
    @Test
    void testBackRight() throws Exception {
        Gameboard gameboard1 = new Gameboard(2);
        Ship ship1 = new Ship(gameboard1);
        Component comp1 = createSimpleComponent(1);
        ship1.addComponent(4, 4, comp1);
        Method method = Ship.class.getDeclaredMethod("backRight", int.class, Side.class);
        method.setAccessible(true);
        method.invoke(ship1, 4, Side.BACK);
        assertNull(ship1.getComponent(4, 4));
        assertTrue(ship1.getUsableSlot(4, 4));
        Gameboard gameboard2 = new Gameboard(2);
        Ship ship2 = new Ship(gameboard2);
        Component comp2 = createSimpleComponent(2);
        ship2.addComponent(3, 6, comp2);
        method.invoke(ship2, 3, Side.RIGHT);
        assertNull(ship2.getComponent(3, 6));
        assertTrue(ship2.getUsableSlot(3, 6));
        Gameboard gameboard3 = new Gameboard(2);
        Ship ship3 = new Ship(gameboard3);
        Component comp3 = createSimpleComponent(3);
        ship3.addComponent(2, 2, comp3);
        method.invoke(ship3, 0, Side.BACK);
        assertNotNull(ship3.getComponent(2, 2));
    }

}