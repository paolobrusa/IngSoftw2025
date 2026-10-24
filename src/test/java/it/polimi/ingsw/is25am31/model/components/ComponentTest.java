package it.polimi.ingsw.is25am31.model.components;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.Map;

class ComponentTest {
    private Component component;
    private Map<Side, Connector> connectors;

    @BeforeEach
    void setUp() {
        connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, Connector.SINGLE);
        connectors.put(Side.RIGHT, Connector.NONE);
        connectors.put(Side.BACK, Connector.DOUBLE);
        connectors.put(Side.LEFT, Connector.UNIVERSAL);
        component = new Component(1, "TestComponent", connectors, Side.FRONT);
    }

    @Test
    void modifyOrientation_Left() {
        component.modifyOrientation(Side.LEFT);
        assertEquals(Connector.NONE, component.getConnector(Side.FRONT));
        assertEquals(Connector.DOUBLE, component.getConnector(Side.RIGHT));
        assertEquals(Connector.UNIVERSAL, component.getConnector(Side.BACK));
        assertEquals(Connector.SINGLE, component.getConnector(Side.LEFT));
        assertEquals(Side.RIGHT, component.getOrientation());
    }

    @Test
    void modifyOrientation_Right() {
        component.modifyOrientation(Side.RIGHT);
        assertEquals(Connector.UNIVERSAL, component.getConnector(Side.FRONT));
        assertEquals(Connector.SINGLE, component.getConnector(Side.RIGHT));
        assertEquals(Connector.NONE, component.getConnector(Side.BACK));
        assertEquals(Connector.DOUBLE, component.getConnector(Side.LEFT));
        assertEquals(Side.RIGHT, component.getOrientation());
    }

    @Test
    void modifyOrientation_Back() {
        component.modifyOrientation(Side.BACK);
        assertEquals(Connector.DOUBLE, component.getConnector(Side.FRONT));
        assertEquals(Connector.UNIVERSAL, component.getConnector(Side.RIGHT));
        assertEquals(Connector.SINGLE, component.getConnector(Side.BACK));
        assertEquals(Connector.NONE, component.getConnector(Side.LEFT));
        assertEquals(Side.RIGHT, component.getOrientation());
    }

    @Test
    void modifyOrientation_Front() {
        component.modifyOrientation(Side.FRONT);
        assertEquals(Connector.SINGLE, component.getConnector(Side.FRONT));
        assertEquals(Connector.NONE, component.getConnector(Side.RIGHT));
        assertEquals(Connector.DOUBLE, component.getConnector(Side.BACK));
        assertEquals(Connector.UNIVERSAL, component.getConnector(Side.LEFT));
        assertEquals(Side.RIGHT, component.getOrientation());
    }

    @Test
    void getConnectors_ReturnsCopy() {
        Map<Side, Connector> copy = component.getConnectors();
        copy.put(Side.FRONT, Connector.UNIVERSAL);
        assertNotEquals(Connector.UNIVERSAL, component.getConnector(Side.FRONT));
        assertEquals(Connector.SINGLE, component.getConnector(Side.FRONT));
    }

    @Test
    void getSetXY() {
        component.setX(5);
        component.setY(3);
        assertEquals(5, component.getX());
        assertEquals(3, component.getY());
    }

    @Test
    void getId() {
        assertEquals(1, component.getId());
    }

    @Test
    void getName() {
        assertEquals("TestComponent", component.getName());
    }

    @Test
    void getOrientation() {
        assertEquals(Side.FRONT, component.getOrientation());
    }

    @Test
    void getConnector() {
        assertEquals(Connector.SINGLE, component.getConnector(Side.FRONT));
        assertEquals(Connector.NONE, component.getConnector(Side.RIGHT));
        assertEquals(Connector.DOUBLE, component.getConnector(Side.BACK));
        assertEquals(Connector.UNIVERSAL, component.getConnector(Side.LEFT));
    }

    @Test
    void defaultMethods() {
        assertFalse(component.modifyCrewMembers(1));
        assertEquals(-1, component.getCrewMembers());
        assertEquals(0, component.calculateThrust());
        assertEquals(0.0, component.calculateFirePower());
        assertEquals(AlienColor.NONE, component.getCurrentAlien());
        assertFalse(component.removeContains(Good.RED));
        assertEquals(0, component.getPower());
        assertNull(component.protectedSides());
        assertThrows(RuntimeException.class, () -> component.isActive());
        assertThrows(RuntimeException.class, () -> component.activate());
        component.modifyCapacity(1);
        assertEquals(0, component.getCapacity());
        assertFalse(component.addContains(Good.RED));
        assertNull(component.getContains());
        component.setOrientationShield();
        assertNull(component.getOrientationShield());
        assertFalse(component.isAlienLifeSupport());
        assertEquals(AlienColor.NONE, component.getAlienColor());
        assertFalse(component.isAlienStructural());
    }

    @Test
    void asciiArt() {
        String[] art = component.getAsciiArt();
        assertEquals(3, art.length);
        assertEquals("                ", art[0]);
        assertEquals(" STRUCTURAL M.  ", art[1]);
        assertEquals("                ", art[2]);
    }
}