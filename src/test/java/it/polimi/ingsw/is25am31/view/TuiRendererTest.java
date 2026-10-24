package it.polimi.ingsw.is25am31.view;

import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.view.tui.TuiRenderer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TuiRendererTest {

    @Test
    void drawShip() {
        Ship ship = createDemoShip();
        TuiRenderer renderer =TuiRenderer.getInstance();
        try {
            renderer.drawShip(ship);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Ship createDemoShip() {
        Ship ship = new Ship();



        Map<Side, Connector> allU = new HashMap<>();
        allU.put(Side.FRONT, Connector.UNIVERSAL);
        allU.put(Side.BACK, Connector.UNIVERSAL);
        allU.put(Side.LEFT, Connector.UNIVERSAL);
        allU.put(Side.RIGHT, Connector.UNIVERSAL);

        Map<Side, Connector> backS = new HashMap<>();
        backS.put(Side.BACK, Connector.SINGLE);

        Map<Side, Connector> frontS = new HashMap<>();
        frontS.put(Side.FRONT, Connector.SINGLE);

        Map<Side, Connector> rightS = new HashMap<>();
        rightS.put(Side.RIGHT, Connector.SINGLE);

        Map<Side, Connector> backD = new HashMap<>();
        backD.put(Side.BACK, Connector.DOUBLE);

        Map<Side, Connector> rightU = new HashMap<>();
        rightU.put(Side.RIGHT, Connector.UNIVERSAL);


        // Inserimento componenti test
        ship.getGrid()[0][3] = new AlienLifeSupport(
                1,
                "ALIEN lIFE",
                backS,
                Side.FRONT,
                AlienColor.BROWN
        );

        ship.getGrid()[2][3] = new MainCabin(
                2,
                "MAIN CAB",
                allU,
                Side.FRONT,
                Color.BLUE );

        ship.getGrid()[3][2] = new Engine(
                3,
                "ENGINE",
                frontS,
                Side.FRONT,
                2 );

        ship.getGrid()[2][1] = new Cannon(
                4,
                "CANNON",
                backD,
                Side.FRONT,
                1);

        ship.getGrid()[2][4] = new BatteryComponent(
                5,
                "BATTERY",
                rightS,
                Side.FRONT,
                3);



        return ship;
    }

    @Test
    void showRocketsPositionTest() {

        TuiRenderer renderer = TuiRenderer.getInstance();


        List<Player> players = new ArrayList<>();

        Gameboard fake = createFakeGameboard();

        Player p1 = new Player(1,"Paolo", fake);
        Player p2 = new Player(2,"Giovanni", fake);
        Player p3 = new Player(3,"Edoardo", fake);
        Player p4 = new Player(4,"Diletta", fake);



        p1.getRocket().setPosition(18);
        p1.getRocket().setColor(Color.BLUE);
        p2.getRocket().setPosition(1);
        p2.getRocket().setColor(Color.YELLOW);
        p3.getRocket().setPosition(4);
        p3.getRocket().setColor(Color.GREEN);
        p4.getRocket().setPosition(3);
        p4.getRocket().setColor(Color.RED);

        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);


        renderer.showRocketsPosition(players, fake.getLevel());
    }

    private Gameboard createFakeGameboard() {
        return new Gameboard(1);
    }

    @Test
    void showsDrawnComponentTest() {
        TuiRenderer renderer = TuiRenderer.getInstance();


        Map<Side, Connector> frontS = new HashMap<>();
        frontS.put(Side.LEFT, Connector.SINGLE);
        frontS.put(Side.RIGHT, Connector.SINGLE);

        Component cargo = new MainCabin(
                1,
                "MAIN CAB",
                frontS,
                Side.FRONT,
                Color.RED



        );

        //cargo.addContains(Good.RED);

        renderer.showsDrawnComponent(cargo);
    }


}
