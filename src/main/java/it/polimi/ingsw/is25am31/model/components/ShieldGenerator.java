package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.cards.Size;

import java.io.Serial;
import java.util.Map;

/**
 * Represents a shield generator component that can protect specific sides of the spaceship.
 * The shield generator can be activated for one-time use and its orientation can be changed
 * to protect different sides of the ship.
 */
public class ShieldGenerator extends Component {
    @Serial
    private static final long serialVersionUID = 4957423055727884300L;
    private OrientationShield orientationShield;
    private boolean active;

    public ShieldGenerator(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("connector") Map<Side, Connector> connector,
                           @JsonProperty("orientation") Side orientation,
                           @JsonProperty("OrientationShield") OrientationShield orientationShield) {
        super(id, name, connector, orientation);
        this.orientationShield = orientationShield;
        active = false;
    }

    /**
     * Gets the sides protected by this shield generator.
     *
     * @return the orientation configuration indicating which sides are protected
     */
    public OrientationShield protectedSides() {
        return orientationShield;
    }

    /**
     * Checks if the shield generator is currently active and automatically deactivates it.
     * This method implements a one-time use mechanism where checking the status consumes the activation.
     *
     * @return {@code true} if the shield was active (and is now deactivated),
     *         {@code false} if it was already inactive
     */
    @Override
    public boolean isActive() {
        if (!active)
            return false;
        active = false;
        return true;
    }

    /**
     * Activates the shield generator, making it ready for one-time use.
     */
    @Override
    public void activate() { active = true; }

    /**
     * Cycles the shield orientation to the next configuration.
     * The orientation cycles through all available OrientationShield enum values.
     */
    public void setOrientationShield() {
        this.orientationShield = OrientationShield.values()[(this.orientationShield.ordinal() + 1) % OrientationShield.values().length];
    }

    /**
     * Gets the current orientation of the shield protection.
     *
     * @return the current shield orientation configuration
     */
    @Override
    public OrientationShield getOrientationShield() {
        return orientationShield;
    }

    /**
     * Generates an ASCII art representation of the shield generator component.
     * The representation shows the shield direction based on the component's orientation.
     *
     * @return an array of strings representing the ASCII art, with each string being one line
     */
    @Override
    public String[] getAsciiArt() {
        String line;
        switch (orientation) {
            case FRONT -> line = "^> SHIELD";
            case LEFT  -> line = "<^ SHIELD";
            case BACK -> line = "SHIELD <v";
            case RIGHT   -> line = "SHIELD v>";
            default         -> line = "SHIELD";
        }

        return new String[]{
                "           ",
                center(line, 11),
                "           "
        };
    }


    /**
     * Centers a string within a specified width by adding spaces on both sides.
     *
     * @param text the string to center
     * @param width the total width in which to center the string
     * @return the centered string with padding spaces, or the original string if it's already wider than the specified width
     */
    public static String center(String text, int width) {
        int padding = width - text.length();
        if (padding <= 0) return text;
        int padStart = padding / 2;
        int padEnd = padding - padStart;
        return " ".repeat(padStart) + text + " ".repeat(padEnd);
    }


}