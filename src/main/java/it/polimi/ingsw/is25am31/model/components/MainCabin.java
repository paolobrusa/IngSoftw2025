package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * Represents the main cabin component of a spaceship.
 * The main cabin is the central component that holds crew members and has a specific color.
 * It can accommodate a maximum of 2 crew members.
 */
public class MainCabin extends Component{
    @Serial
    private static final long serialVersionUID = 6748325258851426350L;
    private Color color;
    private int crewMembers;

    /**
     * Creates a new MainCabin component with the specified properties.
     * The cabin is initialized with 2 crew members by default.
     *
     * @param id the unique identifier for this component
     * @param name the name of the component
     * @param connector a map of connectors indexed by their side position
     * @param orientation the current orientation of the component
     * @param color the color of the main cabin
     */
    public MainCabin(@JsonProperty("id") int id,
                     @JsonProperty("name") String name,
                     @JsonProperty("connector") Map<Side, Connector> connector,
                     @JsonProperty("orientation") Side orientation,
                     @JsonProperty("color") Color color) {
        super(id, name, connector, orientation);
        this.color = color;
        this.crewMembers = 2;
    }

    /**
     * Modifies the number of crew members in the main cabin by the specified amount.
     * The main cabin can accommodate between 0 and 2 crew members (inclusive).
     *
     * @param change the amount to add (if positive) or subtract (if negative)
     *               from the current number of crew members
     * @return {@code true} if the modification was successfully applied,
     *         {@code false} if the change would result in an invalid number of crew members
     *         (less than 0 or greater than 2)
     */
    public boolean modifyCrewMembers(int change) {
        int newCount = crewMembers + change;
        if (newCount < 0 || newCount > 2) {
            //numero di membri non valido
            return false;
        }
        crewMembers = newCount;
        return true;
    }

    /**
     * Sets the color of the main cabin.
     *
     * @param color the new color for the main cabin
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the color of the main cabin.
     *
     * @return the current color of the main cabin
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the current number of crew members in the main cabin.
     *
     * @return the number of crew members currently in the cabin
     */
    public int getCrewMembers() {
        return crewMembers;
    }

    /**
     * Gets the current number of crew members in the main cabin.
     *
     * @return the number of crew members currently in the cabin
     */
    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    /**
     * Generates an ASCII art representation of the main cabin component.
     * The representation shows the current crew member count out of the maximum capacity.
     *
     * @return an array of strings representing the ASCII art, with each string being one line
     */
    @Override
    public String[] getAsciiArt() {
        int maxCrew = 2;
        String stato = "MAIN CAB " + crewMembers + "/" + maxCrew;

        return new String[]{
                "           ",
                center(stato, 12),
                "           "
        };
    }


}

