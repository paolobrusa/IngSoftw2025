package it.polimi.ingsw.is25am31.model.components;

import it.polimi.ingsw.is25am31.model.components.Cannon;
import it.polimi.ingsw.is25am31.model.components.Connector;
import it.polimi.ingsw.is25am31.model.components.Side;
import org.junit.jupiter.api.Test;
import java.util.EnumMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CannonTest {

    private Map<Side, Connector> createConnectors(Connector frontConnector) {
        Map<Side, Connector> connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, frontConnector);
        connectors.put(Side.BACK, Connector.NONE);
        connectors.put(Side.LEFT, Connector.NONE);
        connectors.put(Side.RIGHT, Connector.NONE);
        return connectors;
    }

    @Test
    void constructorSingleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        assertTrue(cannon.isActive());
    }

    @Test
    void constructorDoubleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        assertFalse(cannon.isActive());
    }

    @Test
    void isActiveSingleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        assertTrue(cannon.isActive());
    }

    @Test
    void activateDoubleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        cannon.activate();
        assertTrue(cannon.isActive());
    }

    @Test
    void activateSingleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        assertThrows(RuntimeException.class, cannon::activate);
    }

    @Test
    void calculateFirePower() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        assertEquals(1.0, cannon.calculateFirePower());
    }

    @Test
    void calculateFirePower_SingleCannon_FrontNotCannon_Returns05() {
        Map<Side, Connector> connectors = createConnectors(Connector.ENGINE);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        assertEquals(0.5, cannon.calculateFirePower());
    }

    @Test
    void calculateFirePower_DoubleCannon_Inactive_Returns0() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        assertEquals(0.0, cannon.calculateFirePower());
    }

    @Test
    void calculateFirePower_DoubleCannon_ActiveFrontCannon_Returns2AndDeactivates() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        cannon.activate();
        assertEquals(2.0, cannon.calculateFirePower());
        assertFalse(cannon.isActive());
    }

    @Test
    void calculateFirePower_DoubleCannon_ActiveFrontNotCannon_Returns1AndDeactivates() {
        Map<Side, Connector> connectors = createConnectors(Connector.ENGINE);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        cannon.activate();
        assertEquals(1.0, cannon.calculateFirePower());
        assertFalse(cannon.isActive());
    }

    @Test
    void getPower_ReturnsCorrectValue() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannonSingle = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        Cannon cannonDouble = new Cannon(2, "Cannon++", connectors, Side.FRONT, 2);
        assertEquals(1, cannonSingle.getPower());
        assertEquals(2, cannonDouble.getPower());
    }

    @Test
    void getAsciiArt_FrontOrientation_SingleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.FRONT, 1);
        String[] art = cannon.getAsciiArt();
        assertEquals(" ^CANNON^  ", art[1]);
    }

    @Test
    void getAsciiArt_BackOrientation_DoubleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.BACK, 2);
        String[] art = cannon.getAsciiArt();
        assertEquals("vCANNON++v ", art[1]);
    }

    @Test
    void getAsciiArt_LeftOrientation_SingleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(1, "Cannon", connectors, Side.LEFT, 1);
        String[] art = cannon.getAsciiArt();
        assertTrue(art[1].contains("<CANNON"));
    }

    @Test
    void getAsciiArt_RightOrientation_DoubleCannon() {
        Map<Side, Connector> connectors = createConnectors(Connector.CANNON);
        Cannon cannon = new Cannon(2, "Cannon++", connectors, Side.RIGHT, 2);
        String[] art = cannon.getAsciiArt();
        assertTrue(art[1].contains("CANNON++>"));
    }

}