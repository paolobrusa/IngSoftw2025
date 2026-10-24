package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;

/**
 * Represents the stardust card in the game, a specific type of card that applies
 * an effect where a player's rocket moves back based on the number of exposed connectors
 * on their ship.
 */
public class StardustCard extends Card{
    @Serial
    private static final long serialVersionUID = 7979323507700618331L;

    public StardustCard(@JsonProperty("id") int id,
                        @JsonProperty("level") int level,
                        @JsonProperty("name") String name,
                        @JsonProperty("testFlight") boolean testFlight) {
        super(id, level, name, testFlight);
    }

    public EffectType getType(){
        return EffectType.SPECIAL;
    }

    /**
     * player rocket moves back one square for each exposed connector
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        player.getRocket().changePosition(-player.getShip().getExposedConnectors());
        return true;
    }

    /**
     * Generates an ASCII art representation of the card containing details such as
     * the card name, level, optional status, and its effects. This representation
     * serves as a textual description of the card, formatted line by line for display.
     *
     * @return an array of strings containing the ASCII art representation of the card
     */
    @Override
    public String[] getAsciiArt() {
        return new String[]{
                "Carta: " + name,
                "Livello: " + level,
                "Facoltativa: No",
                "Effetto:",
                "- Polvere di stelle attraversa il tuo percorso",
                "- Perdi 1 giorno di volo per ogni connettore esposto",
                "- Più la tua nave è scoperta, peggiore sarà l'effetto",
                "Comandi: 'apply' per applicare l'effetto"
        };
    }

}
