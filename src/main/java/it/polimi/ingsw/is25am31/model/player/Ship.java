package it.polimi.ingsw.is25am31.model.player;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am31.model.cards.Size;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Represents a player's spaceship in the game, managing components placement,
 * grid layout, combat mechanics, and ship validation.
 * The ship consists of a 5x7 grid where components can be placed. It includes
 * functionality for:
 * Component placement and removal,
 * Combat mechanics (cannon, meteor impacts),
 * Resource management (batteries, goods, crew),
 * Ship validation and connectivity checks,
 * Reserved component management.
 * The ship always contains a MainCabin at the center position (2,3) which
 * serves as the core component for connectivity validation.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class Ship implements Serializable {
    @Serial
    private static final long serialVersionUID = 6473586165244113651L;
    private final List<Component> components;
    private final List<Component> componentsRemoved;
    private final List<Component> reservedComponents;
    private final Component[][] grid;
    private boolean[][] usableSlot;
    private Gameboard gameboard;


    /**
     * Creates an empty ship with default grid size 5x7 and empty component lists.
     */
    public Ship() {
        usableSlot = new boolean[5][7];
        grid = new Component[5][7];
        components = new ArrayList<>();
        componentsRemoved = new ArrayList<>();
        reservedComponents = new LinkedList<>();
    }

    /**
     * Checks if a slot at given coordinates is available for placement or reservation.
     * @param line Row index in grid.
     * @param column Column index in grid.
     * @return true if slot is usable; false otherwise.
     */
    public boolean getUsableSlot(int line, int column) {
        return usableSlot[line][column];
    }

    /**
     * Constructs a ship initialized for the given gameboard level.
     * Loads usable slots configuration from JSON resources and places the MainCabin at center.
     * @param gameboard Gameboard on which the ship is placed.
     */
    public Ship(Gameboard gameboard) {
        this.gameboard = gameboard;
        grid = new Component[5][7];
        components = new ArrayList<Component>();
        componentsRemoved = new ArrayList<Component>();
        usableSlot = new boolean[5][7];
        reservedComponents = new ArrayList<>();
        reservedComponents.add(null);
        reservedComponents.add(null);

        Map<Side, Connector> sideMap = new HashMap<>();
        sideMap.put(Side.FRONT, Connector.UNIVERSAL);
        sideMap.put(Side.BACK, Connector.UNIVERSAL);
        sideMap.put(Side.LEFT, Connector.UNIVERSAL);
        sideMap.put(Side.RIGHT, Connector.UNIVERSAL);
        grid[2][3] = new MainCabin(0, "MainCabin", sideMap, Side.FRONT, null);
        grid[2][3].setX(2);
        grid[2][3].setY(3);
        components.add(grid[2][3]);


        if (gameboard.getLevel() == 1 || gameboard.getLevel() == 2) {
            ObjectMapper mapper = new ObjectMapper();
            String fileName = gameboard.getLevel() == 1 ? "usableSlotLevel1.json" : "usableSlotLevel2.json";
            try (InputStream is = Ship.class.getClassLoader().getResourceAsStream(fileName)) {
                if (is == null) {
                    throw new RuntimeException("File JSON not in resources: " + fileName);
                }
                usableSlot = mapper.readValue(is, boolean[][].class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns the 2D array of components on the ship grid.
     * @return Component grid.
     */
    public Component[][] getGrid() {
        return grid;
    }


    /**
     * Activates a cannon at (x1,y1) consuming one battery from (x2,y2).
     * Restores battery if activation fails.
     * @param x1 Cannon's row.
     * @param y1 Cannon's column.
     * @param x2 Battery's row.
     * @param y2 Battery's column.
     * @throws RuntimeException if coordinates invalid or components mismatch.
     */
    public void activateCannon(int x1, int y1, int x2, int y2) {
        if(getComponent(x1, y1) == null || getComponent(x2, y2) == null) {
            throw new RuntimeException("One or more selected spots contain no components");
        }
        Component battery = getComponent(x2, y2);
        if(!battery.getName().equals("BatteryComponent")) {
            throw new RuntimeException("Selected component is not a battery");
        }
        Component cannon = getComponent(x1, y1);
        if(!cannon.getName().equals("Cannon")) {
            throw new RuntimeException("Selected component is not a cannon");
        }

        removeBattery(x2, y2);
        try{
            cannon.activate();
        }catch(Exception e){
            battery.modifyCapacity(1);
        }
    }

    /**
     * Activates an engine at (x1,y1) consuming one battery from (x2,y2).
     * Restores battery if activation fails.
     * @param x1 Engine's row.
     * @param y1 Engine's column.
     * @param x2 Battery's row.
     * @param y2 Battery's column.
     * @throws RuntimeException if coordinates invalid or components mismatch.
     */
    public void activateEngine(int x1, int y1, int x2, int y2) {
        if(getComponent(x1, y1) == null || getComponent(x2, y2) == null) {
            throw new RuntimeException("One or more selected spots contain no components");
        }
        Component battery = getComponent(x2, y2);
        if(!battery.getName().equals("BatteryComponent")) {
            throw new RuntimeException("Selected component is not a battery");
        }
        Component engine = getComponent(x1, y1);
        if(!engine.getName().equals("Engine")) {
            throw new RuntimeException("Selected component is not an engine");
        }

        removeBattery(x2, y2);
        try{
            engine.activate();
        }catch(Exception e){
            battery.modifyCapacity(1);
        }
    }

    /**
     * Activates a shield generator at (x1,y1) consuming one battery from (x2,y2).
     * Restores battery if activation fails.
     * @param x1 Shield's row.
     * @param y1 Shield's column.
     * @param x2 Battery's row.
     * @param y2 Battery's column.
     * @throws RuntimeException if coordinates invalid or components mismatch.
     */
    public void activateShield(int x1, int y1, int x2, int y2) {
        if(getComponent(x1, y1) == null || getComponent(x2, y2) == null) {
            throw new RuntimeException("One or more selected spots contain no components");
        }
        Component battery = getComponent(x2, y2);
        if(!battery.getName().equals("BatteryComponent")) {
            throw new RuntimeException("Selected component is not a battery");
        }
        Component shield = getComponent(x1, y1);
        if(!shield.getName().equals("ShieldGenerator")) {
            throw new RuntimeException("Selected component is not a Shield");
        }

        removeBattery(x2, y2);
        try{
            shield.activate();
        }catch(Exception e){
            battery.modifyCapacity(1);
        }
    }

    /**
     * Sets the color of the ship's main cabin for player identification.
     * @param color Color to apply.
     */
    public void setColor(Color color){
        ((MainCabin)getComponent(2,3 )).setColor(color);
    }

    /**
     * Attempts to add a component at specified grid position if slot is usable.
     * Handles special reserve slots in first row columns 5 and 6.
     * @param line Row index.
     * @param column Column index.
     * @param component Component to place.
     * @return true if placement succeeds.
     * @throws RuntimeException if slot invalid or already occupied.
     */
    public boolean addComponent(int line, int column, Component component) {
        try{
            if(!usableSlot[line][column]) {
                throw new RuntimeException("Invalid position");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid position");
        }
        if (line == 0 && (column == 5 || column == 6)) {
            component.setX(line);
            component.setY(column);
            grid[line][column] = component;
            usableSlot[line][column] = false;
            if(column == 5) {
                reservedComponents.set(0, component);
            }
            else {
                reservedComponents.set(1, component);
            }
        }
        else {
            //rotateComponent(component, newOrient);
            component.setX(line);
            component.setY(column);
            components.add(component);
            grid[line][column] = component;
            usableSlot[line][column] = false;
            if (!reservedComponents.isEmpty() && (reservedComponents.getFirst() != null || reservedComponents.getLast() != null)) {
                if (reservedComponents.stream().filter(Objects::nonNull).anyMatch(x -> x.getId() == component.getId())) {
                    Component c = reservedComponents.stream().filter(y -> y.getId() == component.getId()).findFirst().get();
                    if (grid[0][5] != null) {
                        if (grid[0][5].getId() == c.getId()) {
                            usableSlot[0][5] = true;
                            grid[0][5] = null;
                        }
                    }
                    if (grid[0][6] != null) {
                        if (grid[0][6].getId() == c.getId()) {
                            usableSlot[0][6] = true;
                            grid[0][6] = null;
                        }
                    }
                    int index = IntStream.range(0, reservedComponents.size())
                            .filter(i -> reservedComponents.get(i).getId() == component.getId())
                            .findFirst()
                            .orElse(-1);
                    reservedComponents.set(index, null);
                }
            }
        }
        return true;
    }

    /**
     * Retrieves a reserved component by reserve index (0 or 1).
     * @param index Position in reservation list.
     * @return Reserved Component or null.
     */
    public Component getReservedComponent(int index) {
        return reservedComponents.get(index);
    }

    /**
     * Returns list of currently reserved components.
     * @return List of reserved components (size 2).
     */
    public List<Component> getReservedComponentsList() {
        return reservedComponents;
    }

    /**
     * Counts aliens of given color currently in all cabins except main cabin.
     * @param color AlienColor to count.
     * @return Number of aliens of that color.
     */
    public int getAlienCount(AlienColor color) {
        int count = 0;
        for(Component comp : components) {
            //controlla se nella cabina è presente un alieno del colore cercato
            if(comp.getName().equals("Cabin") && !comp.getName().equals("MainCabin")) {
                Cabin cabin = (Cabin)comp;
                if(cabin.getCurrentAlien() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Moves a reserved component from (x,y) into a usable slot (line,column).
     * @param line Destination row.
     * @param column Destination column.
     * @param x Original row of reserved component.
     * @param y Original column of reserved component.
     * @return true if move succeeds.
     * @throws RuntimeException if invalid position or no reserved component at origin.
     */
    public boolean addComponentFromShip(int line, int column, int x, int y) {
        ListIterator<Component> iterator = reservedComponents.listIterator();
        if (usableSlot[line][column]) {
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if (component.getX() == x && component.getY() == y) {
                    grid[x][y] = null;
                    component.setX(line);
                    component.setY(column);
                    components.add(component);
                    grid[line][column] = component;
                    usableSlot[line][column] = false;
                    iterator.remove();
                    return true;
                }
            }
        }
        throw new RuntimeException("invalid position");
    }

    /**
     * Removes the last-added component at specified slot if it matches removal order.
     * @param line Row index.
     * @param column Column index.
     * @return false always.
     * @throws RuntimeException if removal is not last component.
     */
    public boolean removeComponent(int line, int column) {
        if (components.getLast() == getComponent(line, column)) {
            components.removeLast();
            grid[line][column] = null;
            usableSlot[line][column] = true;
        } else {
            throw new RuntimeException("invalid removed component");
        }
        return false;
    }

    /**
     * Retrieves component at given coordinates.
     * @param line Row index.
     * @param colomn Column index.
     * @return Component at that slot or throws if out of bounds.
     */
    public Component getComponent(int line, int colomn) {
        try {
            return grid[line][colomn];
        }catch(IndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid position");
        }
    }

    /**
     * Counts the total number of exposed non-engine, non-cannon connectors on ship perimeter.
     * @return Number of exposed connectors.
     */
    public int getExposedConnectors() {
        int exposedConnectors = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) { //DA SISTEMARE LATI ESTERNI C'E' PROBLEMA
                if (grid[i][j] == null)
                    continue;
                if ((i == grid.length - 1 || grid[i + 1][j] == null) && grid[i][j].getConnector(Side.BACK) != Connector.NONE
                        && grid[i][j].getConnector(Side.BACK) != Connector.ENGINE && grid[i][j].getConnector(Side.BACK) != Connector.CANNON)
                    exposedConnectors++;
                if ((i == 0 || grid[i - 1][j] == null) && grid[i][j].getConnector(Side.FRONT) != Connector.NONE
                        && grid[i][j].getConnector(Side.FRONT) != Connector.ENGINE && grid[i][j].getConnector(Side.FRONT) != Connector.CANNON)
                    exposedConnectors++;
                if ((j == grid[i].length - 1 || grid[i][j + 1] == null) && grid[i][j].getConnector(Side.RIGHT) != Connector.NONE
                        && grid[i][j].getConnector(Side.RIGHT) != Connector.ENGINE && grid[i][j].getConnector(Side.RIGHT) != Connector.CANNON)
                    exposedConnectors++;
                if ((j == 0 || grid[i][j - 1] == null) && grid[i][j].getConnector(Side.LEFT) != Connector.NONE
                        && grid[i][j].getConnector(Side.LEFT) != Connector.ENGINE && grid[i][j].getConnector(Side.LEFT) != Connector.CANNON)
                    exposedConnectors++;
            }
        }
        return exposedConnectors;
    }

    /**
     * Calculates total crew aboard (humans + aliens).
     * @return Sum of crew members across components.
     */
    public int getCrewNumber() {
        int crewNumber = 0;
        for (Component comp : components) {
            crewNumber += comp.getCrewMembers() == -1 ? 0 : comp.getCrewMembers();
        }
        return crewNumber; //totale membri equipaggio (NON C'E' DISTINZIONE TRA ALIENI E UMANI)
    }

    /**
     * Calculates crew number excluding aliens.
     * @return Number of human crew members.
     */
    public int getCrewNumberWithoutAlien() {
        int crewNumber = 0;
        for (Component comp : components) {
            if(comp.getCurrentAlien() == AlienColor.NONE)
                crewNumber += comp.getCrewMembers() == -1 ? 0 : comp.getCrewMembers();
        }
        return crewNumber;
    }

    /**
     * Calculates ship's total engine thrust, boosted by brown aliens.
     * @return Engine power value.
     */
    public int getEnginePower() {
        int enginePower = 0;
        int alienCount = 0;
        for (Component comp : components) {
            if (comp.getCurrentAlien().equals(AlienColor.BROWN)) {
                alienCount++;
            }
            enginePower += comp.calculateThrust();
        }
        if (enginePower > 0) {
            enginePower += alienCount * 2;
        }
        return enginePower;
    }

    /**
     * Calculates ship's total cannon firepower, boosted by purple aliens.
     * @return Cannon power as double.
     */
    public double getCannonPower() {
        double cannonPower = 0;
        int alienCount = 0;
        for (Component comp : components) {
            if (comp.getCurrentAlien().equals(AlienColor.PURPLE))
                alienCount++;
            cannonPower += comp.calculateFirePower();
        }
        if (cannonPower > 0) {
            cannonPower += alienCount * 2;
        }
        return cannonPower;
    }

    /**
     * Returns list of all active components on ship.
     * @return List of components.
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Counts total goods stored across all cargo holds.
     * @return Number of goods.
     */
    public int getGoodCount() {
        int goodCount = 0;
        for (Component comp : components) {
            if (comp.getName().equals("CargoHold") && comp.getContains() != null && !comp.getContains().isEmpty())
                goodCount += comp.getContains().size();
        }
        return goodCount;
    }

    /**
     * Sums remaining battery units from all battery components.
     * @return Battery count.
     */
    public int getBatteryCount() {
        int batteryCount = 0;
        for (Component comp : components) {
            if (comp.getName().equals("BatteryComponent") && comp.getCapacity() > 0)
                batteryCount += comp.getCapacity();
        }
        return batteryCount;
    }

    private int getMaxGoodValue() {
        int maxGoodValue = 0;
        for (Component comp : components) {
            if(comp.getMaxGoodValue() > maxGoodValue) {
                maxGoodValue = comp.getMaxGoodValue();
            }
        }
        return maxGoodValue;
    }

    /**
     * Removes one unit of specified good from cargo hold at (line,column) following priority order.
     * @param line Row of cargo hold.
     * @param column Column of cargo hold.
     * @param good Good to remove.
     * @throws RuntimeException if slot invalid or good not present.
     */
    public void removeGoods(int line, int column, Good good) {
        if(getComponent(line, column) == null){
            throw new RuntimeException("Selected spot contains no components");
        } else if (!getComponent(line, column).getName().equals("CargoHold")) {
            throw new RuntimeException("Selected component is not a cargo hold");
        } else if (getMaxGoodValue() > good.getValue()){
            throw new RuntimeException("You have to remove goods in order of value");
        }
        int red = 0;
        int green = 0;
        int blue = 0;
        int yellow = 0;
        List<Component> cargoHold = components.stream()
                .filter(component -> component.getName().equals("CargoHold"))
                .toList();
        ListIterator<Component> iterator = components.listIterator();
        for (Component component : cargoHold) {
            if (component.getContains().isEmpty())
                continue;
            for (Good goods : component.getContains()) { // ho dovuto usare due cicli for dato che i goods sono una lista
                if (goods.equals(Good.RED)) { // e devo iterarli tutti per contarli e fare il controllo nell iterator
                    red++;
                } else if (goods.equals(Good.GREEN)) {
                    green++;
                } else if (goods.equals(Good.BLUE)) {
                    blue++;
                } else if (goods.equals(Good.YELLOW)) {
                    yellow++;
                }
            }
        }
        if (red == 0 && green == 0 && blue == 0 && yellow == 0) {
            return;
        }
        while (iterator.hasNext()) {
            Component comp = iterator.next();
            if (comp.getX() == line && comp.getY() == column && comp.getName().equals("CargoHold")) {
                if (good.equals(Good.RED) && red != 0) {
                    boolean removed = comp.removeContains(good);
                    if (!removed) {
                        throw new RuntimeException("non è presente la merce in questione");
                    }
                } else if (good.equals(Good.YELLOW) && yellow != 0 && red == 0) {
                    boolean removed = comp.removeContains(good);
                    if (!removed) {
                        throw new RuntimeException("non è presente la merce in questione");
                    }
                } else if (good.equals(Good.GREEN) && green != 0 && red == 0 && yellow == 0) {
                    boolean removed = comp.removeContains(good);
                    if (!removed) {
                        throw new RuntimeException("non è presente la merce in questione");
                    }
                } else if (good.equals(Good.BLUE) && blue != 0 && red == 0 && green == 0 && yellow == 0) {
                    boolean removed = comp.removeContains(good);
                    if (!removed) {
                        throw new RuntimeException("non è presente la merce in questione");
                    }
                } else {
                    throw new RuntimeException("non è presente la merce in questione");
                }
            }
        }
    }

    /**
     * Decreases battery capacity by one unit at specified slot.
     * @param line Row index.
     * @param column Column index.
     * @throws RuntimeException if component missing, not a battery, or no capacity.
     */
    public void removeBattery(int line, int column) {
        Component comp = getComponent(line, column);
        if(comp == null){
            throw new RuntimeException("Selected spot contains no components");
        } else if (!comp.getName().equals("BatteryComponent")) {
            throw new RuntimeException("Selected component is not a battery");
        }
        if (comp.getCapacity() <= 0)
            throw new RuntimeException("There are no batteries to remove");
        comp.modifyCapacity(-1);
    }

    /**
     * Handles a cannon hit result: determines if a component is destroyed and removes it.
     * @param x Dice result indicating impact line/column index.
     * @param side Side from which shot arrives.
     * @param size Size of the cannonball (SMALL or LARGE).
     */
    public void hitByCannon(int x, Side side, Size size) {
        if (((side == Side.FRONT || side == Side.BACK) && (x < 4 || x > 10)) ||
                ((side == Side.RIGHT || side == Side.LEFT) && (x < 5 || x > 9))) {
            return;
        }
        if(side == Side.FRONT || side == Side.BACK) {
            x = x - 4;
        }
        else if(side == Side.RIGHT || side == Side.LEFT) {
            x = x - 5;
        }
        switch (size) {
            case SMALL:
                switch (side) {
                    case FRONT:
                        for (int i = 0; i < grid.length; i++) {
                            if (grid[i][x] != null) {
                                if (components.stream()
                                        .filter(component -> component.getName().equals("ShieldGenerator"))
                                        .noneMatch(comp -> (comp.protectedSides() == OrientationShield.UPLEFT || comp.protectedSides() == OrientationShield.UPRIGHT) && comp.isActive())) {
                                    componentsRemoved.add(grid[i][x]);
                                    components.remove(grid[i][x]);
                                    usableSlot[i][x] = true;
                                    grid[i][x] = null;
                                    validateShipNextPhase();
                                }
                                return;
                            }
                        }
                        break;
                    case BACK:
                        for (int i = grid.length - 1; i >= 0; i--) {
                            if (grid[i][x] != null) {
                                if (components.stream()
                                        .filter(component -> component.getName().equals("ShieldGenerator"))
                                        .noneMatch(comp -> (comp.protectedSides() == OrientationShield.DOWNRIGHT || comp.protectedSides() == OrientationShield.DOWNLEFT) && comp.isActive())) {
                                    componentsRemoved.add(grid[i][x]);
                                    components.remove(grid[i][x]);
                                    usableSlot[i][x] = true;
                                    grid[i][x] = null;
                                    validateShipNextPhase();
                                }
                                return;
                            }
                        }
                        break;
                    case LEFT:
                        for (int i = 0; i < grid[x].length; i++) {
                            if (grid[x][i] != null) {
                                if (components.stream()
                                        .filter(component -> component.getName().equals("ShieldGenerator"))
                                        .noneMatch(comp -> (comp.protectedSides() == OrientationShield.UPLEFT || comp.protectedSides() == OrientationShield.DOWNLEFT) && comp.isActive())) {
                                    componentsRemoved.add(grid[x][i]);
                                    components.remove(grid[x][i]);
                                    usableSlot[x][i] = true;
                                    grid[x][i] = null;
                                    validateShipNextPhase();
                                }
                                return;
                            }
                        }
                        break;
                    case RIGHT:
                        for (int i = grid[x].length - 1; i >= 0; i--) {
                            if (grid[x][i] != null) {
                                if (components.stream()
                                        .filter(component -> component.getName().equals("ShieldGenerator"))
                                        .noneMatch(comp -> (comp.protectedSides() == OrientationShield.UPRIGHT || comp.protectedSides() == OrientationShield.DOWNRIGHT) && comp.isActive())) {
                                    componentsRemoved.add(grid[x][i]);
                                    components.remove(grid[x][i]);
                                    usableSlot[x][i] = true;
                                    grid[x][i] = null;
                                    validateShipNextPhase();
                                }
                                return;
                            }
                        }
                        break;
                }
                break;
            case LARGE:
                switch (side) {
                    case FRONT, LEFT:
                        frontLeft(x, side);
                        break;
                    case BACK, RIGHT:
                        backRight(x, side);
                        break;
                }
        }
    }

    private void frontLeft(int x, Side side) {
        int gridLength;
        if(side == Side.FRONT) {
            gridLength = grid.length;
            for (int i = 0; i < gridLength; i++) {
                if (grid[i][x] != null) {
                    componentsRemoved.add(grid[i][x]);
                    components.remove(grid[i][x]);
                    usableSlot[i][x] = true;
                    grid[i][x] = null;
                    validateShipNextPhase();
                    return;
                }
            }
        }
        if(side == Side.LEFT) {
            gridLength = grid[x].length;
            for (int i = 0; i < gridLength; i++) {
                if (grid[x][i] != null) {
                    componentsRemoved.add(grid[x][i]);
                    components.remove(grid[x][i]);
                    usableSlot[x][i] = true;
                    grid[x][i] = null;
                    validateShipNextPhase();
                    return;
                }
            }
        }
    }

    private void backRight(int x, Side side) {
        int gridLength;
        if(side == Side.BACK) {
            gridLength = grid.length;
            for (int i = gridLength - 1; i >= 0; i--) {
                if (grid[i][x] != null) {
                    componentsRemoved.add(grid[i][x]);
                    components.remove(grid[i][x]);
                    usableSlot[i][x] = true;
                    grid[i][x] = null;
                    validateShipNextPhase();
                    return;
                }
            }
        }
        if(side == Side.RIGHT) {
            gridLength = grid[x].length;
            for (int i = gridLength - 1; i >= 0; i--) {
                if (grid[x][i] != null) {
                    componentsRemoved.add(grid[x][i]);
                    components.remove(grid[x][i]);
                    usableSlot[x][i] = true;
                    grid[x][i] = null;
                    validateShipNextPhase();
                    return;
                }
            }
        }
    }

    /**
     * Handles a meteor impact: determines component destruction based on size and side.
     * @param x Impact coordinate adjusted by side.
     * @param side Side from which meteor arrives.
     * @param size Meteor size (SMALL or LARGE).
     */
    public void hitByMeteor(int x, Side side, Size size) {
        if (((side == Side.FRONT || side == Side.BACK) && (x < 4 || x > 10)) ||
            ((side == Side.RIGHT || side == Side.LEFT) && (x < 5 || x > 9))) {
            return;
        }
        if(side == Side.FRONT || side == Side.BACK) {
            x = x - 4;
        }
        else if(side == Side.RIGHT || side == Side.LEFT) {
            x = x - 5;
        }
        switch (size){
            case LARGE -> {
                switch (side) {
                    case FRONT:
                        largeMeteorFL(x, Side.FRONT);
                        break;
                    case BACK:
                        largeMeteorBR(x, Side.BACK);
                        break;
                    case LEFT:
                        largeMeteorFL(x, Side.LEFT);
                        break;
                    case RIGHT:
                        largeMeteorBR(x, Side.RIGHT);
                        break;
                }
            }
            case SMALL -> {
                switch (side) {
                    case FRONT:
                        smallMeteorFL(x, grid.length, OrientationShield.UPLEFT, OrientationShield.UPRIGHT);
                        break;
                    case BACK:
                        smallMeteorBR(x, grid.length, OrientationShield.DOWNLEFT, OrientationShield.DOWNRIGHT);
                        break;
                    case LEFT:
                        smallMeteorFL(x, grid[x].length, OrientationShield.DOWNLEFT, OrientationShield.UPLEFT);
                        break;
                    case RIGHT:
                        smallMeteorBR(x, grid[x].length, OrientationShield.DOWNRIGHT, OrientationShield.UPRIGHT);
                        break;
                }
            }
        }
    }

    private void smallMeteorFL(int x, int gridLength, OrientationShield orientationShield, OrientationShield orientationShield2) {
        Component comp = null;
        Side side = null;
        for(int i = 0; i < gridLength; i++) {
            if (gridLength == grid.length){
                comp = grid[i][x];
                side = Side.FRONT;
            }
            else if (gridLength == grid[x].length){
                comp = grid[x][i];
                side = Side.LEFT;
            }
            if(comp == null)
                continue;
            if(comp.getConnector(side) == Connector.NONE)
                return;
            if((comp.getConnector(side) != Connector.NONE) &&
                    components.stream()
                            .filter(component -> component.getName().equals("ShieldGenerator"))
                            .noneMatch(component -> (component.protectedSides() == orientationShield || component.protectedSides() == orientationShield2) && component.isActive())){
                if (comp == grid[i][x]){
                    components.remove(grid[i][x]);
                    componentsRemoved.add(grid[i][x]);
                    usableSlot[i][x] = true;
                    grid[i][x] = null;
                    validateShipNextPhase();
                    return;
                }
                else if (comp == grid[x][i]){
                    components.remove(grid[x][i]);
                    componentsRemoved.add(grid[x][i]);
                    usableSlot[x][i] = true;
                    grid[x][i] = null;
                    validateShipNextPhase();
                    return;
                }
            }
            else
                return;
        }
    }

    private void smallMeteorBR(int x, int gridLength, OrientationShield orientationShield, OrientationShield orientationShield2) {
        Component comp = null;
        Side side = null;
        for(int i = gridLength - 1; i >= 0; i--) {
            if (gridLength == grid.length){
                comp = grid[i][x];
                side = Side.BACK;
            }
            else if (gridLength == grid[x].length){
                comp = grid[x][i];
                side = Side.RIGHT;
            }
            if(comp == null) //DA QUA IN POI SI PUO ESTRARRE UN METODO SEPARATO
                continue;
            if(comp.getConnector(side) == Connector.NONE)
                return;
            if((comp.getConnector(side) != Connector.NONE) &&
                    components.stream() //QUI SI PUO RENDERE PIU BELLO E OTTIMIZZARLO
                            .filter(component -> component.getName().equals("ShieldGenerator"))
                            .noneMatch(component -> (component.protectedSides() == orientationShield || component.protectedSides() == orientationShield2) && component.isActive())){
                if (comp == grid[i][x]){
                    components.remove(grid[i][x]);
                    componentsRemoved.add(grid[i][x]);
                    usableSlot[i][x] = true;
                    grid[i][x] = null;
                    validateShipNextPhase();
                    return;
                }
                else if (comp == grid[x][i]){
                    components.remove(grid[x][i]);
                    componentsRemoved.add(grid[x][i]);
                    usableSlot[x][i] = true;
                    grid[x][i] = null;
                    validateShipNextPhase();
                    return;
                }
            }
            else
                return;
        }
    }

    private void largeMeteorFL(int x, Side side) {
        if(side == Side.FRONT) {
            if(components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && component.getY() == x)
                    .noneMatch(component -> component.isActive() || component.getPower() == 1)) {
                for(int i = 0; i < grid.length; i++) {
                    if (grid[i][x] != null) {
                        components.remove(grid[i][x]);
                        componentsRemoved.add(grid[i][x]);
                        usableSlot[i][x] = true;
                        grid[i][x] = null;
                        validateShipNextPhase();
                        return;
                    }
                }
            }
        }
        else if(side == Side.LEFT) {
            if(components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && component.getX() == x)
                    .noneMatch(component -> component.isActive() || component.getPower() == 1)) {
                if(gameboard.getLevel() == 2 && components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && (component.getX() == x+1 || component.getX() == x-1))
                        .anyMatch(component -> component.isActive() || component.getPower() == 1)){
                    return;
                }
                for(int i = 0; i < grid[x].length; i++) {
                    if (grid[x][i] != null) {
                        components.remove(grid[x][i]);
                        componentsRemoved.add(grid[x][i]);
                        usableSlot[x][i] = true;
                        grid[x][i] = null;
                        validateShipNextPhase();
                        return;
                    }
                }
            }
        }
    }

    private void largeMeteorBR(int x, Side side) {
        if(side == Side.BACK) {
            if(components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && component.getY() == x)
                    .noneMatch(component -> component.isActive() || component.getPower() == 1)) {
                if(gameboard.getLevel() == 2 && components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && (component.getY() == x+1 || component.getY() == x-1))
                        .anyMatch(component -> component.isActive() || component.getPower() == 1)){
                    return;
                }
                for(int i = grid.length - 1; i >= 0; i--) {
                    if (grid[i][x] != null) {
                        components.remove(grid[i][x]);
                        componentsRemoved.add(grid[i][x]);
                        usableSlot[i][x] = true;
                        grid[i][x] = null;
                        validateShipNextPhase();
                        return;
                    }
                }
            }
        }
        else if(side == Side.RIGHT) {
            if(components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && component.getX() == x)
                    .noneMatch(component -> component.isActive() || component.getPower() == 1)) {
                if(gameboard.getLevel() == 2 && components.stream().filter(component -> component.getName().equals("Cannon") && component.getConnector(side) == Connector.CANNON && (component.getX() == x+1 || component.getX() == x-1))
                        .anyMatch(component -> component.isActive() || component.getPower() == 1)){
                    return;
                }
                for(int i = grid[x].length - 1; i >= 0; i--) {
                    if (grid[x][i] != null) {
                        components.remove(grid[x][i]);
                        componentsRemoved.add(grid[x][i]);
                        usableSlot[x][i] = true;
                        grid[x][i] = null;
                        validateShipNextPhase();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Validates ship structure at end of phase and removes disconnected or conflicting components
     * and reserved components
     */
    public void validateShipNextPhase() {
        removeReservedComponents();
        Component mainComponent = null;
        try {
            mainComponent = components.stream()
                    .filter(component -> component.getName().equals("MainCabin")).findFirst().orElse(components.stream()
                            .filter(component -> component.getName().equals("Cabin") && component.getCrewMembers() == 2).findFirst()
                            .orElse(components.stream()
                                    .filter(component -> component.getName().equals("Cabin") && component.getCrewMembers() == 1).findFirst()
                                    .orElse(components.stream()
                                            .filter(component -> component.getName().equals("Cabin") && component.getCrewMembers() == 1).findFirst()
                                    .orElse(components.getFirst()))));
        } catch (Exception e) {
            return;
        }
        Set<Component> connectedComponents = new HashSet<>();
        dfsConnectedComponents(mainComponent, connectedComponents);
        validateConnections(connectedComponents);
        removeRemainingDiscComp(mainComponent);
    }

    private void removeReservedComponents() {
        ListIterator<Component> iterator = reservedComponents.listIterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component != null) {
                usableSlot[component.getX()][component.getY()] = true;
                grid[component.getX()][component.getY()] = null;
                componentsRemoved.add(component);
                iterator.remove();
            }
        }
    }

    private void validateConnections(Set<Component> connectedComponents) {
        Set<Component> componentSet = new HashSet<>(connectedComponents);
        Map<Component, List<Component>> conflictMap = new HashMap<>();
        List<Object[]> allConflicts = conflictingComponents(componentSet);

        for (Object[] conflict : allConflicts) {
            Component component1 = (Component) conflict[0];
            Component component2 = (Component) conflict[1];
            conflictMap.computeIfAbsent(component1, k -> new ArrayList<>()).add(component2);
            conflictMap.computeIfAbsent(component2, k -> new ArrayList<>()).add(component1);
        }

        while (!conflictMap.isEmpty()) {
            Component worstComponent = null;
            int maxConflicts = -1;

            for (Map.Entry<Component, List<Component>> entry : conflictMap.entrySet()) {
                if (entry.getValue().size() > maxConflicts) {
                    maxConflicts = entry.getValue().size();
                    worstComponent = entry.getKey();
                } else if (entry.getValue().size() == maxConflicts && worstComponent != null) {
                    Component toRemove = componentToRemove(worstComponent, entry.getKey());
                    if (toRemove != worstComponent) {
                        worstComponent = entry.getKey();
                    }
                }
            }
            if (worstComponent != null) {
                usableSlot[worstComponent.getX()][worstComponent.getY()] = true;
                grid[worstComponent.getX()][worstComponent.getY()] = null;
                componentsRemoved.add(worstComponent);
                components.remove(worstComponent);
                componentSet.remove(worstComponent);
                List<Component> conflictingWith = conflictMap.remove(worstComponent);
                if (conflictingWith != null) {
                    for (Component other : conflictingWith) {
                        List<Component> otherConflicts = conflictMap.get(other);
                        if (otherConflicts != null) {
                            otherConflicts.remove(worstComponent);
                            if (otherConflicts.isEmpty()) {
                                conflictMap.remove(other);
                            }
                        }
                    }
                }
            }
        }
        Iterator<Component> iterator = componentSet.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (exposedEngineOrCannon(component)) {
                usableSlot[component.getX()][component.getY()] = true;
                grid[component.getX()][component.getY()] = null;
                componentsRemoved.add(component);
                components.remove(component);
                iterator.remove();
            }
        }
    }

    private List<Object[]> conflictingComponents(Set<Component> components) {
        List<Object[]> conflicts = new ArrayList<>();

        for (Component component : components) {
            if (component.getX() > 0 && grid[component.getX()-1][component.getY()] != null && components.contains(grid[component.getX()-1][component.getY()])) {
                Connector currentFront = component.getConnector(Side.FRONT);
                Connector aboveBack = grid[component.getX()-1][component.getY()].getConnector(Side.BACK);
                if (adjacentConnectors(currentFront, aboveBack)) {
                    conflicts.add(new Object[]{component, grid[component.getX()-1][component.getY()], Side.FRONT, Side.BACK});
                }
            }
            if (component.getX() < grid.length - 1 && grid[component.getX()+1][component.getY()] != null && components.contains(grid[component.getX()+1][component.getY()])) {
                Connector currentBack = component.getConnector(Side.BACK);
                Connector belowFront = grid[component.getX()+1][component.getY()].getConnector(Side.FRONT);

                if (adjacentConnectors(currentBack, belowFront)) {
                    conflicts.add(new Object[]{component, grid[component.getX()+1][component.getY()], Side.BACK, Side.FRONT});
                }
            }
            if (component.getY() > 0 && grid[component.getX()][component.getY()-1] != null && components.contains(grid[component.getX()][component.getY()-1])) {
                Connector currentLeft = component.getConnector(Side.LEFT);
                Connector leftRight = grid[component.getX()][component.getY()-1].getConnector(Side.RIGHT);
                if (adjacentConnectors(currentLeft, leftRight)) {
                    conflicts.add(new Object[]{component, grid[component.getX()][component.getY()-1], Side.LEFT, Side.RIGHT});
                }
            }
            if (component.getY() < grid[0].length - 1 && grid[component.getX()][component.getY()+1] != null && components.contains(grid[component.getX()][component.getY()+1])) {
                Connector currentRight = component.getConnector(Side.RIGHT);
                Connector rightLeft = grid[component.getX()][component.getY()+1].getConnector(Side.LEFT);
                if (adjacentConnectors(currentRight, rightLeft)) {
                    conflicts.add(new Object[]{component, grid[component.getX()][component.getY()+1], Side.RIGHT, Side.LEFT});
                }
            }
        }
        return conflicts;
    }

    private Component componentToRemove(Component c1, Component c2) {
        //mantenere i componenti più vicini alla MainCabin
        Component mainCabin = grid[2][3];
        int dist1 = distance(c1, mainCabin);
        int dist2 = distance(c2, mainCabin);
        if (dist1 != dist2) {
            if (dist1 > dist2) {
                return c1;
            }
            else {
                return c2;
            }
        }

        //mantenere i componenti con più connessioni valide
        int connections1 = totConnections(c1);
        int connections2 = totConnections(c2);

        if (connections1 != connections2) {
            if (connections1 < connections2) {
                return c1;
            }
            else {
                return c2;
            }
        }
        //possiamo aggiungere altre condizioni
        return c1;
    }

    private int distance(Component c1, Component c2) {
        return Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY());
    }

    private int totConnections(Component component) {
        int count = 0;
        int x = component.getX();
        int y = component.getY();

        if (x > 0 && grid[x-1][y] != null) {
            if (formConnection(component.getConnector(Side.FRONT), grid[x-1][y].getConnector(Side.BACK))) {
                count++;
            }
        }
        if (x < grid.length - 1 && grid[x+1][y] != null) {
            if (formConnection(component.getConnector(Side.BACK), grid[x+1][y].getConnector(Side.FRONT))) {
                count++;
            }
        }
        if (y > 0 && grid[x][y-1] != null) {
            if (formConnection(component.getConnector(Side.LEFT), grid[x][y-1].getConnector(Side.RIGHT))) {
                count++;
            }
        }
        if (y < grid[0].length - 1 && grid[x][y+1] != null) {
            if (formConnection(component.getConnector(Side.RIGHT), grid[x][y+1].getConnector(Side.LEFT))) {
                count++;
            }
        }
        return count;
    }

    private boolean exposedEngineOrCannon(Component component) {
        if (component.getConnector(Side.FRONT) == Connector.ENGINE ||
                component.getConnector(Side.LEFT) == Connector.ENGINE ||
                component.getConnector(Side.RIGHT) == Connector.ENGINE) {
            return true;
        }
        if (component.getConnector(Side.FRONT) == Connector.CANNON &&
                component.getX() > 0 && grid[component.getX() - 1][component.getY()] != null) {
            return true;
        }
        if (component.getConnector(Side.BACK) == Connector.CANNON &&
                component.getX() < grid.length - 1 && grid[component.getX() + 1][component.getY()] != null) {
            return true;
        }
        if (component.getConnector(Side.LEFT) == Connector.CANNON &&
                component.getY() > 0 && grid[component.getX()][component.getY() - 1] != null) {
            return true;
        }
        if (component.getConnector(Side.RIGHT) == Connector.CANNON &&
                component.getY() < grid[0].length - 1 && grid[component.getX()][component.getY() + 1] != null) {
            return true;
        }
        return false;
    }

    private boolean adjacentConnectors(Connector c1, Connector c2) {
        if ((c1 == Connector.UNIVERSAL && c2 == Connector.NONE) ||
                (c2 == Connector.UNIVERSAL && c1 == Connector.NONE)) {
            return true;
        }

//        if ((c1 == Connector.UNIVERSAL && (c2 == Connector.ENGINE || c2 == Connector.CANNON)) ||
//                (c2 == Connector.UNIVERSAL && (c1 == Connector.ENGINE || c1 == Connector.CANNON))) {
//            return true;
//        }

        if (c1 == Connector.UNIVERSAL || c2 == Connector.UNIVERSAL) {
            return false;
        }

        if (c1 == Connector.NONE || c2 == Connector.NONE) {
            return c1 != Connector.NONE || c2 != Connector.NONE;
        }

        return c1 != c2;
    }

    private boolean formConnection(Connector c1, Connector c2) {
        if ((c1 == Connector.UNIVERSAL && c2 != Connector.NONE && c2 != Connector.ENGINE && c2 != Connector.CANNON) ||
                (c2 == Connector.UNIVERSAL && c1 != Connector.NONE && c1 != Connector.ENGINE && c1 != Connector.CANNON)) {
            return true;
        }
        if (c1 == Connector.NONE || c2 == Connector.NONE) {
            return false;
        }
        return c1 == c2;
    }

    private void removeRemainingDiscComp(Component mainComponent) {
        Set<Component> connectedComponents = new HashSet<>();
        dfsConnectedComponents(mainComponent, connectedComponents);
        ListIterator<Component> iterator = components.listIterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (!connectedComponents.contains(component)) {
                usableSlot[component.getX()][component.getY()] = true;
                grid[component.getX()][component.getY()] = null;
                componentsRemoved.add(component);
                iterator.remove();
            }
        }
    }

    private void dfsConnectedComponents(Component startComp, Set<Component> connectedComponents) {
        int x = startComp.getX();
        int y = startComp.getY();

        if (connectedComponents.contains(startComp)) {
            return;
        }
        connectedComponents.add(startComp);

        if (x > 0 && grid[x-1][y] != null && formConnection(startComp.getConnector(Side.FRONT), grid[x-1][y].getConnector(Side.BACK))) {
            dfsConnectedComponents(grid[x-1][y], connectedComponents);
        }
        if (x < grid.length - 1 && grid[x+1][y] != null && formConnection(startComp.getConnector(Side.BACK), grid[x+1][y].getConnector(Side.FRONT))) {
            dfsConnectedComponents(grid[x+1][y], connectedComponents);
        }
        if (y > 0 && grid[x][y-1] != null && formConnection(startComp.getConnector(Side.LEFT), grid[x][y-1].getConnector(Side.RIGHT))) {
            dfsConnectedComponents(grid[x][y-1], connectedComponents);
        }
        if (y < grid[0].length - 1 && grid[x][y+1] != null && formConnection(startComp.getConnector(Side.RIGHT), grid[x][y+1].getConnector(Side.LEFT))) {
            dfsConnectedComponents(grid[x][y+1], connectedComponents);
        }
    }
}
