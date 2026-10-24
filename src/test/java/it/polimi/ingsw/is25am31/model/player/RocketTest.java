package it.polimi.ingsw.is25am31.model.player;

import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketTest {
    private Gameboard gameboard;

    @BeforeEach
    void setUp() {
        gameboard = new Gameboard(1);
    }

    @Test
    void testInitialPosition() {
        Rocket rocket = new Rocket(gameboard);
        assertEquals(-1, rocket.getPosition());
    }

    @Test
    void testSetPosition() {
        Rocket rocket = new Rocket(gameboard);
        rocket.setPosition(5);
        assertEquals(5, rocket.getPosition());
        rocket.setPosition(-3);
        assertEquals(-3, rocket.getPosition());
        rocket.setPosition(0);
        assertEquals(0, rocket.getPosition());
    }

    @Test
    void testChangePosition() {
        Rocket rocket = new Rocket(gameboard);
        rocket.setPosition(0);
        rocket.changePosition(4);
        assertEquals(4, rocket.getPosition());
        rocket.changePosition(3);
        assertEquals(7, rocket.getPosition());
        rocket.changePosition(-1);
        assertEquals(6, rocket.getPosition());
        rocket.changePosition(0);
        assertEquals(6,rocket.getPosition());
    }

    @Test
    void testCombinedOperations() {
        Rocket rocket = new Rocket(gameboard);
        rocket.setPosition(10);
        rocket.changePosition(-7);
        assertEquals(3, rocket.getPosition());
        rocket.setPosition(-5);
        rocket.changePosition(3);
        assertEquals(-2, rocket.getPosition());
    }

    @Test
    void changePosition(){
        Player player = new Player(1, "Player", gameboard);
        player.getRocket().setPosition(1);
        Player player3 = new Player(3, "Player3", gameboard);
        player3.getRocket().setPosition(-3);
        Player player2 = new Player(2, "Player2", gameboard);
        player2.getRocket().setPosition(-1);
        player2.getRocket().changePosition(-2);
        assertEquals(-4, player2.getRocket().getPosition());
    }
}