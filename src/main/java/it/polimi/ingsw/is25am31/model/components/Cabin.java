package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * Represents a cabin component in the game, capable of holding human crew members
 * or alien inhabitants with specific life support requirements.
 */
public class Cabin extends Component {
    @Serial
    private static final long serialVersionUID = -5960436148188613951L;
    private int crewMembers;
    private final boolean hasBrownAlienLifeSupport;
    private final boolean hasPurpleAlienLifeSupport;
    private AlienColor currentAlien;


    private static final int MAX_CREW = 2;

    /**
     * Constructs a cabin with specified modules and orientation
     *
     * @param id                         unique component identifier
     * @param name                       descriptive name of the cabin
     * @param connector                  map of side-to-connector mappings
     * @param orientation                initial orientation of the component
     * @param hasBrownAlienModule        true if brown alien life support module is present
     * @param hasPurpleAlienModule       true if purple alien life support module is present
     */
    public Cabin(@JsonProperty("id") int id,
                 @JsonProperty("name") String name,
                 @JsonProperty("connector") Map<Side, Connector> connector,
                 @JsonProperty("orientation") Side orientation,
                 @JsonProperty("hasBrownAlienLifeSupport") boolean hasBrownAlienModule,
                 @JsonProperty("hasPurpleAlienLifeSupport") boolean hasPurpleAlienModule) {
        super(id, name, connector, orientation);
        this.crewMembers = 0;
        this.hasBrownAlienLifeSupport = hasBrownAlienModule;
        this.hasPurpleAlienLifeSupport = hasPurpleAlienModule;
        this.currentAlien = AlienColor.NONE;

    }

    /**
     * Attempts to add an alien to the cabin if empty
     *
     * @param alien AlienColor to add
     * @return true if alien was added, false otherwise
     */
    public boolean addAlien(AlienColor alien) {
        if (crewMembers > 0 || currentAlien != AlienColor.NONE) {
            return false;
        }
        currentAlien = alien;
        crewMembers++;
        return true;
    }

    /**
     * Removes the alien from the cabin
     *
     * @return true if an alien was removed, false otherwise
     */
    public boolean removeAlien() {
        if (currentAlien != AlienColor.NONE) {
            currentAlien = null;
            crewMembers--;
            return true;
        }
        return false;
    }

    /**
     * Modifies the number of human crew members
     *
     * @param change positive to add, negative to remove
     * @return true if operation successful, false otherwise
     */

    @Override
    public boolean modifyCrewMembers(int change) {
        if (currentAlien != AlienColor.NONE) {
            if (change == -1) {
                removeAlien();
                return true;
            }
            return false; // NON si può aggiungere umano con alieno
        }

        int newCount = crewMembers + change;
        if (newCount < 0 || newCount > MAX_CREW) {
            return false;
        }
        crewMembers = newCount;
        return true;
    }

    /**
     * Checks if brown alien life support module is installed
     *
     * @return true if brown module present
     */
    public boolean hasBrownAlienLifeSupport() {
        return hasBrownAlienLifeSupport;
    }

    /**
     * Checks if purple alien life support module is installed
     *
     * @return true if purple module present
     */
    public boolean hasPurpleAlienLifeSupport() {
        return hasPurpleAlienLifeSupport;
    }

    /**
     * Returns the current number of crew members
     *
     * @return crew member count
     */
    @Override
    public int getCrewMembers() {
        return crewMembers;
    }

    /**
     * Returns the current alien color occupying the cabin
     *
     * @return AlienColor or NONE
     */
    public AlienColor getCurrentAlien() {
        return currentAlien;
    }

    /**
     * Centers a string within a fixed width, padding with spaces
     *
     * @param str   input string
     * @param width target width
     * @return padded string of length width
     */

    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }


    /**
     * Returns an ASCII art representation of the cabin,
     * showing crew or alien status
     *
     * @return array of ASCII art lines
     */

    @Override
    public String[] getAsciiArt() {
        String stato;

        if (currentAlien == AlienColor.BROWN) {
            stato = "CABIN B.A.";
        } else if (currentAlien == AlienColor.PURPLE) {
            stato = "CABIN P.A.";
        } else {
            stato = "CABIN " + crewMembers + "/2";
        }

        return new String[]{
                "           ",
                center(stato, 11),
                "           "
        };
    }


}