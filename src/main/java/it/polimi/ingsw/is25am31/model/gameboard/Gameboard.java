package it.polimi.ingsw.is25am31.model.gameboard;

import it.polimi.ingsw.is25am31.model.player.Rocket;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Gameboard class represents the board used in the game, containing a specified
 * number of boxes based on the game level. It keeps track of player rockets
 * and their positions on the board.
 */
public class Gameboard implements Serializable {
    @Serial
    private static final long serialVersionUID = -5114695590568327406L;
    private final int level;
    private int boxes;
    private Map<Rocket, Integer> rockets = new HashMap<>();

    /**
     * Constructs a gameboard with a specified difficulty level
     *
     * @param level difficulty level (1 or 2)
     */
    public Gameboard(int level) {
        this.level = level;
        if(this.level == 2) {
            this.boxes = 24;
        }
        else if(this.level == 1) {
            this.boxes = 18;
        }
    }

    /**
     * Returns the difficulty level of the board
     *
     * @return level of the gameboard
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the total number of boxes on the board
     *
     * @return number of boxes available
     */

    public int getBoxes() {
        return boxes;
    }

    /**
     * Registers or updates a rocket's position on the board
     *
     * @param position index of the box where the rocket is placed
     * @param rocket   Rocket instance to register
     */

    public void setRockets(int position, Rocket rocket) {
        rockets.put(rocket, position);
    }

    /**
     * Retrieves the mapping of all rockets to their positions
     *
     * @return map of rockets and their board positions
     */

    public Map<Rocket, Integer> getRockets() {
        return rockets;
    }

    /**
     * Removes a rocket's registration from the board
     *
     * @param rocket Rocket instance to remove
     */
    public void removeRockets(Rocket rocket) { rockets.remove(rocket);}
}
