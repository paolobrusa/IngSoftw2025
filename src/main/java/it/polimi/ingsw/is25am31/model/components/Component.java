
package it.polimi.ingsw.is25am31.model.components;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;


/**
 * Represents a general component in the game, serving as the base class for various
 * specialized components. This class handles the management of component properties
 * such as orientation, position, and connections to other components. <br>
 * Subclasses of this class are designed to override and extend its functionality
 * to represent specific types of components.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlienLifeSupport.class, name = "AlienLifeSupport"),
        @JsonSubTypes.Type(value = BatteryComponent.class, name = "BatteryComponent"),
        @JsonSubTypes.Type(value = Cabin.class, name = "Cabin"),
        @JsonSubTypes.Type(value = MainCabin.class, name = "MainCabin"),
        @JsonSubTypes.Type(value = Cannon.class, name = "Cannon"),
        @JsonSubTypes.Type(value = CargoHold.class, name = "CargoHold"),
        @JsonSubTypes.Type(value = Engine.class, name = "Engine"),
        @JsonSubTypes.Type(value = ShieldGenerator.class, name = "ShieldGenerator")
})


public class Component implements Serializable {
    @Serial
    private static final long serialVersionUID = -2947971940098052597L;
    protected int id;
    protected String name;
    @JsonProperty("connector")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    protected Map<Side, Connector> connector;
    protected Side orientation;
    protected int x;    //indice di riga
    protected int y;    //indice di colonna

    /*
     *   Se il componente è coperto o scoperto è ora gestito nella classe Tiles
     */


    /**
     * Constructor used by Json to deserialize component properties
     *
     * @param id           unique component identifier
     * @param name         name of the component
     * @param connector    map of side-to-connector mappings
     * @param orientation  initial orientation of the component
     */
    @JsonCreator
    public Component(@JsonProperty("id") int id,
                     @JsonProperty("name") String name,
                     @JsonProperty("connector") Map<Side, Connector> connector,
                     @JsonProperty("orientation") Side orientation) {
        this.id = id;
        this.name = name;
        this.connector = connector;
        this.orientation = orientation;
    }

    /**
     * Returns the name of the component
     *
     * @return component name
     */
    public String getName() {
        return name;
    }

    /**
     * Rotates the component to a new orientation and updates its connectors accordingly
     *
     * @param newOrientation the target orientation
     */
    public void modifyOrientation(Side newOrientation) {
        //Map<Side, Connector> rotated = new EnumMap<>(Side.class);
        Map<Side, Connector> newConnector = new EnumMap<>(Side.class);
        switch (newOrientation) {
            case LEFT:
                newConnector.put(Side.RIGHT, connector.get(Side.BACK));
                newConnector.put(Side.BACK, connector.get(Side.LEFT));
                newConnector.put(Side.LEFT, connector.get(Side.FRONT));
                newConnector.put(Side.FRONT, connector.get(Side.RIGHT));
                break;

            case RIGHT:
                newConnector.put(Side.RIGHT, connector.get(Side.FRONT));
                newConnector.put(Side.BACK, connector.get(Side.RIGHT));
                newConnector.put(Side.LEFT, connector.get(Side.BACK));
                newConnector.put(Side.FRONT, connector.get(Side.LEFT));
                break;

            case BACK:
                newConnector.put(Side.RIGHT, connector.get(Side.LEFT));
                newConnector.put(Side.BACK, connector.get(Side.FRONT));
                newConnector.put(Side.LEFT, connector.get(Side.RIGHT));
                newConnector.put(Side.FRONT, connector.get(Side.BACK));
                break;

            default:
                newConnector = connector;
        }
        this.connector = newConnector;
        this.orientation = Side.values()[(this.orientation.ordinal() + 1) % Side.values().length];
        setOrientationShield();
    }

    /**
     * Returns the current orientation of the component
     *
     * @return orientation side
     */
    public Side getOrientation() {
        return orientation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the connector type on a specified side
     *
     * @param side side to query
     * @return connector type
     */
    public Connector getConnector(Side side) {
        return connector.get(side);
    }

    /**
     * Returns a defensive copy of all side-to-connector mappings
     *
     * @return map of connectors
     */
    public Map<Side, Connector> getConnectors() {
        return new EnumMap<>(connector);
    }

    @JsonIgnore
    public boolean modifyCrewMembers(int change) { return false; }
    @JsonIgnore
    public int getCrewMembers() { return -1; }
    @JsonIgnore
    public int calculateThrust(){return 0;}
    @JsonIgnore
    public double calculateFirePower(){return 0;}
    @JsonIgnore
    public AlienColor getCurrentAlien() {
        return AlienColor.NONE;
    }
    @JsonIgnore
    public boolean removeContains(Good good){return false;}
    @JsonIgnore
    public int getPower() {return 0;}
    @JsonIgnore
    public OrientationShield protectedSides() {
        return null;
    }
    @JsonIgnore
    public boolean isActive() { throw new RuntimeException("No active or inactive status for this component"); }
    @JsonIgnore
    public void activate() {throw new RuntimeException("No active or inactive status for this component");}
    @JsonIgnore
    public void modifyCapacity(int change){}
    @JsonIgnore
    public int getCapacity(){return 0;}
    @JsonIgnore
    public boolean addContains(Good good){ return false; }
    @JsonIgnore
    public List<Good> getContains(){ return null; }
    @JsonIgnore
    public void setOrientationShield() {}
    @JsonIgnore
    public OrientationShield getOrientationShield() {return null;}
    public int getMaxGoodValue(){ return 0;}

    /**
     * Provides ASCII art representation for structural components
     *
     * @return array of ASCII art lines
     */
    @JsonIgnore
    public String[] getAsciiArt() {
        return new String[]{
                "                ",
                " STRUCTURAL M.  ",
                "                "
        };
    }

    /**
     * Indicates whether this component supports alien life
     *
     * @return false by default; override in AlienLifeSupport
     */
    public boolean isAlienLifeSupport() {
        return false;
    }

    /**
     * Returns the alien color if applicable
     *
     * @return AlienColor.NONE by default
     */

    public AlienColor getAlienColor() { return AlienColor.NONE; }

    /**
     * Indicates whether this is a structural component containing aliens
     *
     * @return false by default
     */

    public boolean isAlienStructural() {
        return false;
    }


}



