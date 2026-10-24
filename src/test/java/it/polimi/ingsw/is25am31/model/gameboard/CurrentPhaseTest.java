package it.polimi.ingsw.is25am31.model.gameboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrentPhaseTest {
    @Test
    void constructorAndGetterTest() {
        for(Phase phase : Phase.values()) {
            CurrentPhase currentPhase = new CurrentPhase(phase);
            assertEquals(phase, currentPhase.getPhase());
        }
    }

    @Test
    void setterTest() {
        CurrentPhase currentPhase = new CurrentPhase(Phase.NEWGAME);
        currentPhase.setPhase(Phase.INITIALIZENICKNAME);
        assertEquals(Phase.INITIALIZENICKNAME, currentPhase.getPhase());
        currentPhase.setPhase(Phase.ASSEMBLE);
        assertEquals(Phase.ASSEMBLE, currentPhase.getPhase());
        currentPhase.setPhase(Phase.FLIGHT);
        assertEquals(Phase.FLIGHT, currentPhase.getPhase());
        currentPhase.setPhase(Phase.FLIGHT_ENDED);
        assertEquals(Phase.FLIGHT_ENDED, currentPhase.getPhase());
    }

    @Test
    void stateModificationTest() {
        CurrentPhase cp1 = new CurrentPhase(Phase.NEWGAME);
        CurrentPhase cp2 = new CurrentPhase(Phase.INITIALIZENICKNAME);
        assertNotEquals(cp1.getPhase(), cp2.getPhase());
        cp1.setPhase(Phase.ASSEMBLE);
        assertEquals(Phase.ASSEMBLE, cp1.getPhase());
        assertEquals(Phase.INITIALIZENICKNAME, cp2.getPhase());
    }

    @Test
    void edgeCaseTest() {
        CurrentPhase currentPhase = new CurrentPhase(Phase.NEWGAME);
        currentPhase.setPhase(Phase.NEWGAME);
        assertEquals(Phase.NEWGAME, currentPhase.getPhase());
        currentPhase.setPhase(Phase.FLIGHT_ENDED);
        currentPhase.setPhase(Phase.NEWGAME);
        assertEquals(Phase.NEWGAME, currentPhase.getPhase());
    }

    @Test
    void unusualTransitionsTest() {
        CurrentPhase cp = new CurrentPhase(Phase.FLIGHT_ENDED);
        cp.setPhase(Phase.INITIALIZENICKNAME);
        assertEquals(Phase.INITIALIZENICKNAME, cp.getPhase());
        cp.setPhase(Phase.FLIGHT);
        assertEquals(Phase.FLIGHT, cp.getPhase());
    }
}