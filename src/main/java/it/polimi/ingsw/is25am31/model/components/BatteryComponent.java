package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * Represents a component with a specific capacity for storing batteries,
 * This class allows for modifications to its current capacity while ensuring it
 * does not exceed its predefined limits.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BatteryComponent extends Component {
    @Serial
    private static final long serialVersionUID = -2017834389267165486L;
    // max battery slots available
    private final int maxCapacity;
    // actual batteries
    private int capacity;

    public BatteryComponent(@JsonProperty("id") int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("connector") Map<Side, Connector> connector,
                            @JsonProperty("orientation") Side orientation,
                            @JsonProperty("capacity") int maxCapacity) {
        super(id, name, connector, orientation);
        this.maxCapacity = maxCapacity;
        this.capacity = maxCapacity;
    }


    public void modifyCapacity(int change) {
        if(this.capacity + change > maxCapacity)
            throw new IllegalArgumentException("Capacity exceeds max capacity");

        this.capacity += change;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxCapacity() { return maxCapacity; }





    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    @Override
    public String[] getAsciiArt() {
        String stato = "BATTERY " + capacity + "/" + maxCapacity;

        return new String[]{
                "           ",
                center(stato, 11),
                "           "
        };
    }



}

