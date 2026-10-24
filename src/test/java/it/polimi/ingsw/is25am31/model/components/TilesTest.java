package it.polimi.ingsw.is25am31.model.components;

import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import org.junit.jupiter.api.Test;
import java.util.List;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class TilesTest {
    @Test
    void numberOfTilesTest() throws Exception {
        Gameboard gameboard = new Gameboard(2);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(152, ((List<Component>)value).size());
    }

    @Test
    void numberOfDifferentComponentsLevel2Test() throws Exception {
        Gameboard gameboard = new Gameboard(2);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(36, ((List<Component>)value).stream().filter(component -> component.getName().equals("Cannon")).count());
        assertEquals(30, ((List<Component>)value).stream().filter(component -> component.getName().equals("Engine")).count());
        assertEquals(17, ((List<Component>)value).stream().filter(component -> component.getName().equals("Cabin")).count());
        assertEquals(17, ((List<Component>)value).stream().filter(component -> component.getName().equals("BatteryComponent")).count());
        assertEquals(8, ((List<Component>)value).stream().filter(component -> component.getName().equals("ShieldGenerator")).count());
        assertEquals(12, ((List<Component>)value).stream().filter(component -> component.getName().equals("AlienLifeSupport")).count());
        assertEquals(24, ((List<Component>)value).stream().filter(component -> component.getName().equals("CargoHold")).count());
    }

    @Test
    void numberOfDifferentComponentsTestFlightTest() throws Exception {
        Gameboard gameboard = new Gameboard(1);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(36, ((List<Component>)value).stream().filter(component -> component.getName().equals("Cannon")).count());
        assertEquals(30, ((List<Component>)value).stream().filter(component -> component.getName().equals("Engine")).count());
        assertEquals(17, ((List<Component>)value).stream().filter(component -> component.getName().equals("Cabin")).count());
        assertEquals(17, ((List<Component>)value).stream().filter(component -> component.getName().equals("BatteryComponent")).count());
        assertEquals(8, ((List<Component>)value).stream().filter(component -> component.getName().equals("ShieldGenerator")).count());
        assertEquals(0, ((List<Component>)value).stream().filter(component -> component.getName().equals("AlienLifeSupport")).count());
        assertEquals(24, ((List<Component>)value).stream().filter(component -> component.getName().equals("CargoHold")).count());
    }

    @Test
    void pickUpFaceDownComponentTest() throws Exception {
        Gameboard gameboard = new Gameboard(2);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Component component = tiles.getFaceDownComponent();
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(151, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("faceUpComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(0, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("usedComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(1, ((List<Component>)value).size());
    }

    @Test
    void putDownComponentTest() throws Exception {
        Gameboard gameboard = new Gameboard(2);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Component component = tiles.getFaceDownComponent();
        tiles.putDownComponent(component);
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(151, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("faceUpComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(1, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("usedComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(0, ((List<Component>)value).size());
    }

    @Test
    void pickUpFaceUpComponentTest() throws Exception {
        Gameboard gameboard = new Gameboard(2);
        Tiles tiles = new Tiles(gameboard.getLevel());
        Component component = tiles.getFaceDownComponent();
        tiles.putDownComponent(component);
        tiles.getFaceUpComponent(component);
        Field field = Tiles.class.getDeclaredField("faceDownComponents");
        field.setAccessible(true);
        Object value = field.get(tiles);
        assertEquals(151, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("faceUpComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(0, ((List<Component>)value).size());
        field = Tiles.class.getDeclaredField("usedComponents");
        field.setAccessible(true);
        value = field.get(tiles);
        assertEquals(1, ((List<Component>)value).size());
    }
}