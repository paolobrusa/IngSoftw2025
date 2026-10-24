package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * The Engine class represents an engine component that extends the Component class.
 * It models the behavior and properties of an engine, which includes thrust calculations,
 * activation for multi-power engines, and ASCII art representation.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)

public class Engine extends Component {
    @Serial
    private static final long serialVersionUID = -3472600049867703807L;
    private final int power;
    private boolean active;

    /**
     * Constructs an engine with specified power and orientation
     *
     * @param id           unique component identifier
     * @param name         descriptive name of the engine
     * @param connector    map of side-to-connector mappings
     * @param orientation  initial orientation of the component
     * @param power        thrust power rating
     */

    public Engine(@JsonProperty("id") int id,
                  @JsonProperty("name") String name,
                  @JsonProperty("connector") Map<Side, Connector> connector,
                  @JsonProperty("orientation") Side orientation,
                  @JsonProperty("power") int power) {
        super(id, name, connector, orientation);
        this.power = power;
        active = power == 1;
    }

    /**
     * Calculates thrust produced by the engine this turn
     * Single engines only thrust if connector points back; multi engines thrust when active, then deactivate
     *
     * @return thrust units generated
     */

    public int calculateThrust() {
        if(power == 1 && connector.get(Side.BACK).equals(Connector.ENGINE))
            return 1;
        if(power == 1)
            return 0;
        int result = isActive() ? power : 0;
        active = false;
        return result;
    }

    /**
     * Checks if this multi-power engine is currently active
     *
     * @return true if active; single engines throw if queried
     */

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Activates this multi-power engine for the next thrust calculation
     */

    @Override
    public void activate(){
        if(power == 1)
            throw new RuntimeException("No active or inactive status for this component (it's a single engine)");
        active = true;
    }

    /**
     * Centers a string within a given width by padding spaces
     *
     * @param str   input string
     * @param width total width for centering
     * @return centered string of specified width
     */

    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    /**
     * Returns an ASCII art representation of the engine,
     * showing orientation arrows and power label
     *
     * @return array of ASCII art lines
     */

    @Override
    public String[] getAsciiArt() {
        String label = power == 2 ? "ENGINE++" : "ENGINE";
        String content = switch (orientation) {
            case FRONT -> "v" + label + "v";
            case BACK  -> "^" + label + "^";
            case LEFT  -> "<" + label;
            case RIGHT -> label + ">";
        };
        return new String[]{
                "           ",
                center(content, 11),
                "           "
        };
    }




}
