package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;

/**
 * Represents an "Open Space" card in the game.
 * This card type involves moving forward a player's rocket based on their engine power.
 */
public class OpenSpaceCard extends Card{
    @Serial
    private static final long serialVersionUID = 3038862183947618479L;

    public OpenSpaceCard(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("name") String name,
                         @JsonProperty("testFlight") boolean testFlight) {
        super(id, level, name, testFlight);
    }

    @Override
    public EffectType getType() {
        return EffectType.OPENSPACE;
    }

    /**
     * things that the player may/must do before applying the effect
     *
     * @param player setting their context
     * @param context map of parameters passed by the player, useful to set the context of the card. In
     * this case it's the coordinates of the engine to activate and battery to remove
     */
    @Override
    public void setCardContext(Player player, Context context){
        if(context.getParameters("x1", Integer.class) == null ||
        context.getParameters("y1", Integer.class) == null ||
        context.getParameters("x2", Integer.class) == null ||
        context.getParameters("y2", Integer.class) == null)
            throw new IllegalArgumentException("Invalid parameters");
        player.getShip().activateEngine(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
    }

    /**
     * receives player with relative engine power p, player's rocket advances p squares
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context){
        player.getRocket().changePosition(player.getShip().getEnginePower());
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
                "- Sei nello Spazio Aperto!",
                "- Avanzi tanti giorni di volo quanti la tua potenza motore",
                "- Nessun effetto negativo",
                "Comandi: 'set <x1> <y1> <x2> <y2>'",
                "Dove x1 e y1 sono le coordinate del motore doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "una volta attivati i motori scrivi 'apply'"
        };
    }

}
