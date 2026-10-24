package it.polimi.ingsw.is25am31.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {
    private Player player;
    private Gameboard gameboard;

    @BeforeEach
    void setUp() {
        gameboard = new Gameboard(1);
        player = new Player(1, "Player", gameboard);
    }

    @Test
    void testConstructor() {
        assertEquals("Player", player.getNickname());
        assertEquals(0, player.getCosmicCredits());
        assertNotNull(player.getRocket());
        assertNotNull(player.getShip());
        assertEquals(0, player.getLostComponents());
        assertEquals(0, player.getCargoCredits());
        assertEquals(0, player.getCrew());
        assertFalse(player.hasAbandoned());
    }

    @Test
    void addCosmicCreditsTest() {
        player.addCosmicCredits(10);
        assertEquals(10, player.getCosmicCredits());
        player.addCosmicCredits(5);
        assertEquals(15, player.getCosmicCredits());
    }

    @Test
    void setRocketTest() {
        Rocket newRocket = new Rocket(gameboard);
        player.setRocket(newRocket);
        assertNotEquals(newRocket, player.getRocket());
    }

    @Test
    void setShipTest() {
        Ship newShip = new Ship(gameboard);
        player.setShip(newShip);
        assertSame(newShip, player.getShip());
    }

    @Test
    void incrementLostComponentsTest() {
        player.incrementLostComponents();
        assertEquals(1, player.getLostComponents());
        player.incrementLostComponents();
        assertEquals(2, player.getLostComponents());
    }

    @Test
    void cargoCreditsTest() {
        player.addCargoCredits(7);
        assertEquals(7, player.getCargoCredits());
        player.addCargoCredits(3);
        assertEquals(10, player.getCargoCredits());
    }

    @Test
    void crewManagementTest() {
        player.setCrew(3);
        assertEquals(3, player.getCrew());
        player.setCrew(0);
        assertEquals(0, player.getCrew());
    }

    @Test
    void abandonmentStatusTest() {
        player.setAbandoned(true);
        assertTrue(player.hasAbandoned());
        player.setAbandoned(false);
        assertFalse(player.hasAbandoned());
    }

    @Test
    void rocketGetterTest() {
        Rocket initialRocket = player.getRocket();
        assertNotNull(initialRocket);
        player.setRocket(new Rocket(gameboard)); // Shouldn't change
        assertSame(initialRocket, player.getRocket());
    }

    @Test
    void shipGetterTest() {
        Ship initialShip = player.getShip();
        assertNotNull(initialShip);
        player.setShip(new Ship(gameboard));
        assertNotSame(initialShip, player.getShip());
    }

    @Test
    void idAndNicknameTest() {
        assertEquals(1, player.getId());
        assertEquals("Player", player.getNickname());
    }
}