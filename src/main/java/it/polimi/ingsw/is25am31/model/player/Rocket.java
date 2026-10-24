package it.polimi.ingsw.is25am31.model.player;

import it.polimi.ingsw.is25am31.model.components.Color;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a player's rocket on the game's gameboard, which can be assigned a color
 * and position. The rocket can move across the board while handling collisions with other rockets.
 */
public class Rocket implements Serializable {
    @Serial
    private static final long serialVersionUID = -5534718502048229137L;
    private int position;
    private Color color;
    private Gameboard gameboard;

    /**
     * Constructs a new rocket associated with a specific gameboard
     *
     * @param gameboard reference to the gameboard
     */
    public Rocket(Gameboard gameboard) {
        position = -1;
        color = null;
        this.gameboard = gameboard;
    }

    /**
     * Returns the current position of the rocket
     *
     * @return position index on the gameboard
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the rocket's position on the gameboard and registers it
     *
     * @param position new position index
     */
    public void setPosition(int position) {
        this.position = position;
        gameboard.setRockets(position, this);
    }

    /**
     * Moves the rocket by a relative offset, handling collisions with other rockets
     *
     * @param offset number of spaces to move (positive or negative)
     */
    public void changePosition(int offset) {
        int shifts = 0;
        for (Rocket other : gameboard.getRockets().keySet()) {
            int target = this.position + offset;
            if (gameboard.getRockets().get(other) == target && other != this) {
                offset += (offset > 0 ? 1 : -1);
                shifts = 1;
            }
        }
        if (shifts == 1) {
            changePosition(offset);
        } else {
            gameboard.removeRockets(this);
            this.position += offset;
            gameboard.setRockets(this.position, this);
        }
    }

    /**
     * Returns the color assigned to the rocket
     *
     * @return Color of the rocket
     */
    public Color getColor() {
        return color;
    }

    /**
     * Assigns a color to the rocket
     *
     * @param color chosen color for the rocket
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
