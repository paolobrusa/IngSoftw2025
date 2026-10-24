package it.polimi.ingsw.is25am31.model.gameboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameboardTest {

    @Test
    void constructor_Level1_Sets18Boxes() {
        Gameboard gameboard = new Gameboard(1);
        assertEquals(1, gameboard.getLevel());
        assertEquals(18, gameboard.getBoxes());
    }

    @Test
    void constructor_Level2_Sets24Boxes() {
        Gameboard gameboard = new Gameboard(2);
        assertEquals(2, gameboard.getLevel());
        assertEquals(24, gameboard.getBoxes());
    }

    @Test
    void constructor_InvalidLevel_Sets0Boxes() {
        Gameboard gameboard0 = new Gameboard(0);
        assertEquals(0, gameboard0.getLevel());
        assertEquals(0, gameboard0.getBoxes());
        Gameboard gameboard3 = new Gameboard(3);
        assertEquals(3, gameboard3.getLevel());
        assertEquals(0, gameboard3.getBoxes());
        Gameboard gameboardNeg = new Gameboard(-5);
        assertEquals(-5, gameboardNeg.getLevel());
        assertEquals(0, gameboardNeg.getBoxes());
    }
    @Test
    void getters_ReturnImmutableValues() {
        Gameboard gameboard = new Gameboard(1);
        assertDoesNotThrow(() -> gameboard.getLevel());
        assertDoesNotThrow(() -> gameboard.getBoxes());
    }
}