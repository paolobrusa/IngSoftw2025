package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * Represents a Cannon component with configurable power and orientation.
 * The Cannon can calculate firepower based on its type and position.
 * The class supports single-power and multi-power cannons.
 * The class extends the Component base class.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class Cannon extends Component {
    @Serial
    private static final long serialVersionUID = -8990841057375227834L;
    private int power;
    private boolean active;

    /**
     * Constructs a cannon with specified power and orientation
     *
     * @param id           unique component identifier
     * @param name         descriptive name of the cannon
     * @param connector    map of side-to-connector mappings
     * @param orientation  initial orientation of the component
     * @param power        fire power rating
     */

    public Cannon(@JsonProperty("id") int id,
                  @JsonProperty("name") String name,
                  @JsonProperty("connector") Map<Side, Connector> connector,
                  @JsonProperty("orientation") Side orientation,
                  @JsonProperty("power") int power) {
        super(id, name, connector, orientation);
        this.power = power;
        this.active = power == 1;
    }

    /**
     * Checks if this multi-power cannon is currently active
     *
     * @return true if active or single-power, false otherwise
     */

    @Override
    public boolean isActive(){
        return active;
    }

    /**
     * Activates this multi-power cannon for the next fire calculation
     */

    @Override
    public void activate() {
        if (power == 1) {
            throw new RuntimeException("No active or inactive status for single-power cannons");
        }
        active = true;
    }

    /**
     * Calculates the fire power this turn based on cannon type and orientation
     * Single-power cannons produce 1.0 if facing FRONT connector, else 0.5
     *
     * @return fire power value
     */
    public double calculateFirePower() {
        if(power == 1){
            if(getConnector(Side.FRONT) == Connector.CANNON)
                return 1;
            else
                return 0.5;
        }else{
            if(!isActive())
                return 0;
            else if(getConnector(Side.FRONT) == Connector.CANNON){
                active = false;
                return 2;
            }else{
                active = false;
                return 1;
            }
        }
    }

    public int getPower() {
        return power;
    }

    /**
     * Centers a string within a given width by padding spaces
     *
     * @param str   input string
     * @param width total width for centering
     * @return centered string
     */

    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    /**
     * Returns an ASCII art representation of the cannon,
     * showing orientation arrows and power label
     *
     * @return array of ASCII art lines
     */

    @Override
    public String[] getAsciiArt() {
        String label = power == 2 ? "CANNON++" : "CANNON";
        String content = switch (orientation) {
            case FRONT -> "^" + label + "^";
            case BACK  -> "v" + label + "v";
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

