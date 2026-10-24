package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a generic card used in the game. This class is abstract and defines the common properties
 * and methods that all specific card types must implement or inherit.
 * Instances of this class are deserialized from JSON using Jackson.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlanetsCard.class, name = "Planets"),
        @JsonSubTypes.Type(value = MeteorSwarmCard.class, name = "MeteorSwarm"),
        @JsonSubTypes.Type(value = OpenSpaceCard.class, name = "OpenSpace"),
        @JsonSubTypes.Type(value = PiratesCard.class, name = "Pirates"),
        @JsonSubTypes.Type(value = SlaversCard.class, name = "Slavers"),
        @JsonSubTypes.Type(value = SmugglersCard.class, name = "Smugglers"),
        @JsonSubTypes.Type(value = AbandonedShipCard.class, name = "AbandonedShip"),
        @JsonSubTypes.Type(value = AbandonedStationCard.class, name = "AbandonedStation"),
        @JsonSubTypes.Type(value = CombatZoneCard.class, name = "CombatZone"),
        @JsonSubTypes.Type(value = StardustCard.class, name = "Stardust"),
        @JsonSubTypes.Type(value = EpidemicCard.class, name = "Epidemic"),
})

public abstract class Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 8168688518262807371L;
    protected final int id;
    protected final int level;
    protected final String name;
    protected final boolean testFlight;


    /**
     * the constructor is called by the Jackson library, and the values are read from the JSON file
     *
     * @param id card's id
     * @param level card's level
     * @param name card's name
     * @param testFlight {@code true} if the card is available in test flight mode, {@code false} otherwise
     */
    public Card(@JsonProperty("id") int id,
                @JsonProperty("level") int level,
                @JsonProperty("name") String name,
                @JsonProperty("testFlight") boolean testFlight) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.testFlight = testFlight;
    }

    /**
     * Retrieves the number of crew members associated with the card.
     *
     * @return the number of crew members for the card
     * @throws UnsupportedOperationException if the card does not have crew members
     */
    public int getCrewMembers(){
        throw new UnsupportedOperationException("This card does not have crew members");
    }


    /**
     * Determines if the card is available in test flight mode.
     *
     * @return {@code true} if the card is a test flight card, {@code false} otherwise
     */
    public boolean isTestFlight() { return testFlight; }


    /**
     *
     * @return card type
     */
    public abstract  EffectType getType();

    /**
     * @return Number of times the applyEffect method needs to be called in order to fully
     * resolve the effect of the specific Card subclass.
     */
    public int getIterations() { return 1; }

    /**
     * Method used to do things that the player may/must do before applying the effect.
     *
     * @param player Player setting their context.
     * @param context map of parameters passed by the player, useful to set the context of the card
     *
     */
    public void setCardContext(Player player, Context context){}


    /**
     * Applies the specific card effect relative to the current game state and the passed parameters.
     *
     * @param player Player on whom the effect is applied.
     * @param context Map of parameters passed by the player, used to apply the effect.
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    public abstract boolean applyEffect(Player player, Context context);


    /**
     * Generates an ASCII art representation of the card containing details such as
     * the card name, level, optional status, and its effects. This representation
     * serves as a textual description of the card, formatted line by line for display.
     *
     * @return an array of strings containing the ASCII art representation of the card
     */
    public abstract String[] getAsciiArt();

    public int getId() {
        return id;
    }
}
