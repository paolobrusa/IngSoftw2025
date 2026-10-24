package it.polimi.ingsw.is25am31.model.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CabinTest {

    private Cabin cabinWithBothModules;
    private Cabin cabinWithOnlyBrown;
    private Map<Side, Connector> dummyConnectors;

    @BeforeEach
    void setUp() {
        dummyConnectors = new EnumMap<>(Side.class);
        dummyConnectors.put(Side.FRONT, Connector.SINGLE);
        dummyConnectors.put(Side.BACK, Connector.SINGLE);
        dummyConnectors.put(Side.LEFT, Connector.SINGLE);
        dummyConnectors.put(Side.RIGHT, Connector.SINGLE);

        cabinWithBothModules = new Cabin(1, "Cabina Alpha", dummyConnectors, Side.FRONT, true, true);
        cabinWithOnlyBrown = new Cabin(2, "Cabina Beta", dummyConnectors, Side.FRONT, true, false);
    }

    @Test
    void testAddAlien_successBrown() {
        assertTrue(cabinWithOnlyBrown.addAlien(AlienColor.BROWN));
        assertEquals(AlienColor.BROWN, cabinWithOnlyBrown.getCurrentAlien());
    }

    /*
    @Test
    void testAddAlien_failWrongSupport() {
        assertFalse(cabinWithOnlyBrown.addAlien(AlienColor.PURPLE));
        assertEquals(AlienColor.NONE, cabinWithOnlyBrown.getCurrentAlien());
    }
    */

    @Test
    void testAddAlien_failWithHumanPresent() {
        cabinWithOnlyBrown.modifyCrewMembers(1); // aggiunge un umano
        assertFalse(cabinWithOnlyBrown.addAlien(AlienColor.BROWN));
        assertEquals(AlienColor.NONE, cabinWithOnlyBrown.getCurrentAlien());

    }

    @Test
    void testAddAlien_failAlreadyHasAlien() {
        cabinWithOnlyBrown.addAlien(AlienColor.BROWN);
        assertFalse(cabinWithOnlyBrown.addAlien(AlienColor.BROWN)); // già c'è un alieno
    }

    @Test
    void testRemoveAlien_success() {
        cabinWithOnlyBrown.addAlien(AlienColor.BROWN);
        assertTrue(cabinWithOnlyBrown.removeAlien());
        assertNull(cabinWithOnlyBrown.getCurrentAlien());
    }

    @Test
    void testRemoveAlien_nothingToRemove() {
        assertFalse(cabinWithOnlyBrown.removeAlien());
    }

    @Test
    void testModifyCrewMembers_addAndRemoveValid() {
        assertTrue(cabinWithBothModules.modifyCrewMembers(1));
        assertEquals(1, cabinWithBothModules.getCrewMembers());

        assertTrue(cabinWithBothModules.modifyCrewMembers(1));
        assertEquals(2, cabinWithBothModules.getCrewMembers());

        assertTrue(cabinWithBothModules.modifyCrewMembers(-1));
        assertEquals(1, cabinWithBothModules.getCrewMembers());
    }

    @Test
    void testModifyCrewMembers_failOverMax() {
        assertTrue(cabinWithBothModules.modifyCrewMembers(2));
        assertFalse(cabinWithBothModules.modifyCrewMembers(1)); // supera max
    }

    @Test
    void testModifyCrewMembers_failNegative() {
        assertFalse(cabinWithBothModules.modifyCrewMembers(-1)); // sotto 0
    }

    @Test
    void testModifyCrewMembers_withAlienFailsToAddHuman() {
        cabinWithOnlyBrown.addAlien(AlienColor.BROWN);
        assertFalse(cabinWithOnlyBrown.modifyCrewMembers(1)); // non puoi aggiungere umani con alieno
    }

    @Test
    void testModifyCrewMembers_minus1RemovesAlien() {
        cabinWithOnlyBrown.addAlien(AlienColor.BROWN);
        assertTrue(cabinWithOnlyBrown.modifyCrewMembers(-1)); // -1 rimuove l’alieno
        assertNull(cabinWithOnlyBrown.getCurrentAlien());
    }

    @Test
    void testSupportModules() {
        assertTrue(cabinWithBothModules.hasBrownAlienLifeSupport());
        assertTrue(cabinWithBothModules.hasPurpleAlienLifeSupport());

        assertTrue(cabinWithOnlyBrown.hasBrownAlienLifeSupport());
        assertFalse(cabinWithOnlyBrown.hasPurpleAlienLifeSupport());
    }


}
