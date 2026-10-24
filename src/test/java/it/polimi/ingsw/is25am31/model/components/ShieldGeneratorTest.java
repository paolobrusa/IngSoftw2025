package it.polimi.ingsw.is25am31.model.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShieldGeneratorTest {

    private ShieldGenerator shield;
    private Map<Side, Connector> connectors;

    @BeforeEach
    void setUp() {
        connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, Connector.SINGLE);
        connectors.put(Side.RIGHT, Connector.NONE);
        connectors.put(Side.BACK, Connector.DOUBLE);
        connectors.put(Side.LEFT, Connector.UNIVERSAL);
        shield = new ShieldGenerator(1, "TestShield", connectors, Side.FRONT, OrientationShield.UPLEFT);
    }

    @Test
    void constructorAndInitialState() {
        assertEquals(OrientationShield.UPLEFT, shield.getOrientationShield());
        assertFalse(shield.isActive());
    }

    @Test
    void setOrientationShield_RotationCycle() {
        shield.modifyOrientation(Side.RIGHT);
        assertEquals(OrientationShield.UPRIGHT, shield.getOrientationShield());
        shield.setOrientationShield();
        assertEquals(OrientationShield.DOWNRIGHT, shield.getOrientationShield());
        shield.setOrientationShield();
        assertEquals(OrientationShield.DOWNLEFT, shield.getOrientationShield());
        shield.setOrientationShield();
        assertEquals(OrientationShield.UPLEFT, shield.getOrientationShield());
    }

    @Test
    void activationLifecycle() {
        assertFalse(shield.isActive());
        shield.activate();
        assertTrue(shield.isActive());
        assertFalse(shield.isActive());
        shield.activate();
        assertTrue(shield.isActive());
    }

    @Test
    void protectedSides() {
        assertEquals(OrientationShield.UPLEFT, shield.protectedSides());
        shield.setOrientationShield();
        assertEquals(OrientationShield.UPRIGHT, shield.protectedSides());
    }

    @Test
    void getAsciiArt_FrontOrientation() {
        shield = new ShieldGenerator(1, "TestShield", connectors, Side.FRONT, OrientationShield.UPLEFT);
        String[] art = shield.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals(" ^> SHIELD ", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void getAsciiArt_LeftOrientation() {
        shield = new ShieldGenerator(1, "TestShield", connectors, Side.LEFT, OrientationShield.UPLEFT);
        String[] art = shield.getAsciiArt();
        assertEquals(" <^ SHIELD ", art[1]);
    }

    @Test
    void getAsciiArt_BackOrientation() {
        shield = new ShieldGenerator(1, "TestShield", connectors, Side.BACK, OrientationShield.UPLEFT);
        String[] art = shield.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals(" SHIELD <v ", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void getAsciiArt_RightOrientation() {
        shield = new ShieldGenerator(1, "TestShield", connectors, Side.RIGHT, OrientationShield.UPLEFT);
        String[] art = shield.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals(" SHIELD v> ", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void isActiveAfterDeactivation() {
        shield.activate();
        assertTrue(shield.isActive());
        assertFalse(shield.isActive());
    }

    @Test
    void getOrientationShieldAfterRotation() {
        shield.setOrientationShield();
        assertEquals(OrientationShield.UPRIGHT, shield.getOrientationShield());
        shield.setOrientationShield();
        assertEquals(OrientationShield.DOWNRIGHT, shield.getOrientationShield());
    }

    @Test
    void centerUtilityMethod() {
        assertEquals("  Test  ", ShieldGenerator.center("Test", 8));
        assertEquals(" Test  ", ShieldGenerator.center("Test", 7));
        assertEquals("Test", ShieldGenerator.center("Test", 4));
        assertEquals("   Test   ", ShieldGenerator.center("Test", 10));
        assertEquals("     ", ShieldGenerator.center("", 5));
    }

    @Test
    void setOrientationShield() {
        Map<Side, Connector> connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, Connector.SINGLE);
        connectors.put(Side.RIGHT, Connector.NONE);
        connectors.put(Side.BACK, Connector.DOUBLE);
        connectors.put(Side.LEFT, Connector.UNIVERSAL);

        ShieldGenerator shield = new ShieldGenerator(1, "TestComponent", new EnumMap<>(connectors), Side.FRONT, OrientationShield.UPLEFT);

        shield.setOrientationShield();
        assertEquals(OrientationShield.UPRIGHT, shield.getOrientationShield());

        shield.setOrientationShield();
        assertEquals(OrientationShield.DOWNRIGHT, shield.getOrientationShield());

        shield.setOrientationShield();
        assertEquals(OrientationShield.DOWNLEFT, shield.getOrientationShield());

        shield.setOrientationShield();
        assertEquals(OrientationShield.UPLEFT, shield.getOrientationShield());
    }


}