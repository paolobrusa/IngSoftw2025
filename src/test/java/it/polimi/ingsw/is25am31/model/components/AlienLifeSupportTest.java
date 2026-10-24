package it.polimi.ingsw.is25am31.model.components;

import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienLifeSupportTest {
    @Test
    void testAlienLifeSupport() {
        Gameboard gameboard = new Gameboard(1);
        Map<Side, Connector> map = new HashMap<>();
        map.put(Side.BACK, Connector.NONE);
        map.put(Side.FRONT, Connector.UNIVERSAL);
        map.put(Side.RIGHT, Connector.DOUBLE);
        map.put(Side.LEFT, Connector.SINGLE);
        AlienLifeSupport alienLife = new AlienLifeSupport(3, "AlienLife", map,  Side.FRONT, AlienColor.BROWN);
        if(gameboard.getLevel() == 1){
            alienLife.setAsStructural();
        }
        assertTrue(alienLife.isStructural());
    }


}