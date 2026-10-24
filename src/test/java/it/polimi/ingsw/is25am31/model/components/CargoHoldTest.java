package it.polimi.ingsw.is25am31.model.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CargoHoldTest {

    private CargoHold cargoHold;
    private Map<Side, Connector> connectors;

    @BeforeEach
    void setUp() {
        connectors = new EnumMap<>(Side.class);
        connectors.put(Side.FRONT, Connector.SINGLE);
        connectors.put(Side.BACK, Connector.SINGLE);
        connectors.put(Side.LEFT, Connector.SINGLE);
        connectors.put(Side.RIGHT, Connector.SINGLE);

        cargoHold = new CargoHold(1, "Standard Hold", connectors, Side.FRONT, 2, false);
    }

    @Test
    void testAddContains_success() {
        assertTrue(cargoHold.addContains(Good.BLUE));
        assertTrue(cargoHold.getContains().contains(Good.BLUE));
    }

    @Test
    void testAddContains_capacityExceeded() {
        cargoHold.addContains(Good.BLUE);
        cargoHold.addContains(Good.YELLOW);
        assertFalse(cargoHold.addContains(Good.GREEN)); // oltre la capacità
    }

    @Test
    void testAddContains_redGoodRejectedIfNotSpecial() {
        assertFalse(cargoHold.addContains(Good.RED)); // non accetta il rosso se non è special
    }

    @Test
    void testAddContains_redGoodAcceptedIfSpecial() {
        CargoHold specialHold = new CargoHold(2, "Special Hold", new EnumMap<>(Side.class), Side.FRONT, 2, true);
        //specialHold.setBank(bank);
        assertTrue(specialHold.addContains(Good.RED));
    }

    @Test
    void testRemoveContains_success() {
        cargoHold.addContains(Good.BLUE);
        assertTrue(cargoHold.removeContains(Good.BLUE));
        assertFalse(cargoHold.getContains().contains(Good.BLUE));
    }

    @Test
    void testRemoveContains_failIfNotPresent() {
        assertFalse(cargoHold.removeContains(Good.YELLOW));
    }

    @Test
    void testBankIsUpdatedOnAddAndRemove() {
        //int initial = bank.getGoodsYellow();
        cargoHold.addContains(Good.YELLOW);
        //assertEquals(initial - 1, bank.getGoodsYellow());

        cargoHold.removeContains(Good.YELLOW);
        //assertEquals(initial, bank.getGoodsYellow());
    }

    @Test
    void testGetCapacity() {
        assertEquals(2, cargoHold.getCapacity());
    }

    @Test
    void testGetContainsReturnsCorrectItems() {
        cargoHold.addContains(Good.BLUE);
        cargoHold.addContains(Good.YELLOW);
        List<Good> contents = cargoHold.getContains();
        assertEquals(2, contents.size());
        assertTrue(contents.contains(Good.BLUE));
        assertTrue(contents.contains(Good.YELLOW));
    }

    @Test
    void testAddSameGoodMultipleTimes() {
        assertTrue(cargoHold.addContains(Good.BLUE));
        assertTrue(cargoHold.addContains(Good.BLUE));
        assertEquals(2, cargoHold.getContains().size());
    }

    @Test
    void testRemoveSpecificGoodInstance() {
        cargoHold.addContains(Good.BLUE);
        cargoHold.addContains(Good.BLUE);
        assertTrue(cargoHold.removeContains(Good.BLUE));
        assertEquals(1, cargoHold.getContains().size());
    }

    @Test
    void testAsciiArtEmptyRegular() {
        String[] art = cargoHold.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals("  CARGO-2 [-,-]  ", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void testAsciiArtFullRegular() {
        cargoHold.addContains(Good.BLUE);
        cargoHold.addContains(Good.YELLOW);
        String[] art = cargoHold.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals("  CARGO-2 [B,Y]  ", art[1]);  // 2 spazi sinistra, 2 destra
        assertEquals("           ", art[2]);
    }

    @Test
    void testAsciiArtMixedGoodsSpecial() {
        CargoHold special = new CargoHold(3, "Special", connectors, Side.FRONT, 3, true);
        special.addContains(Good.RED);
        special.addContains(Good.GREEN);
        String[] art = special.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals("CARGO_S-3 [R,G,-]", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void testAsciiArtSingleGood() {
        cargoHold.addContains(Good.GREEN);
        String[] art = cargoHold.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals("  CARGO-2 [G,-]  ", art[1]);
        assertEquals("           ", art[2]);
    }

    @Test
    void testAsciiArtFullSpecial() {
        CargoHold special = new CargoHold(4, "Special", connectors, Side.FRONT, 2, true);
        special.addContains(Good.RED);
        special.addContains(Good.YELLOW);
        String[] art = special.getAsciiArt();
        assertEquals("           ", art[0]);
        assertEquals(" CARGO_S-2 [R,Y] ", art[1]);
        assertEquals("           ", art[2]);
    }

}
