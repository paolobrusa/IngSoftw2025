package it.polimi.ingsw.is25am31.model.components;

/**
 * Enumeration representing the four cardinal sides or directions.
 * This enum defines the four primary sides that can be used to represent
 * spatial orientation, positioning, or directional references within the game.
 * The sides follow a clockwise order starting from the front position.
 * These sides can be used for various purposes such as:
 * Defining the orientation of game components,
 * Specifying connection points on game pieces,
 * Indicating movement directions,
 * Representing spatial relationships between game elements.
 * The available sides are:
 * {@link #FRONT} - Front side (forward direction),
 * {@link #RIGHT} - Right side (clockwise from front),
 * {@link #BACK} - Back side (opposite to front),
 * {@link #LEFT} - Left side (counter-clockwise from front).
 */
public enum Side {
    FRONT, RIGHT, BACK, LEFT;
}
