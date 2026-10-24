package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The {@code MeteorSwarmCard} class represents a card that reproduces the effect of
 * a meteor swarm in the game.
 */
public class MeteorSwarmCard extends Card{
    @Serial
    private static final long serialVersionUID = 5290423208532992259L;
    private final List<Meteor> meteorites;
    private final Map<Player, Integer> timesCalled;

    public MeteorSwarmCard(@JsonProperty("id") int id,
                           @JsonProperty("level") int level,
                           @JsonProperty("name") String name,
                           @JsonProperty("testFlight") boolean testFlight,
                           @JsonProperty("meteorites") List<Meteor> meteorites) {
        super(id, level, name, testFlight);
        this.meteorites = meteorites;
        timesCalled = new HashMap<>();
    }

    @Override
    public EffectType getType() { return EffectType.METEORSWARM; }
    @Override
    public int getIterations() { return meteorites.size(); }


    /**
     * method used to do things that the player may/must do before applying the effect
     *
     * @param player setting their context
     * @param context map of parameters passed by the player, useful to set the context of the card.
     * In this case it's the coordinates of the engine/shield to activate and battery to remove
     */
    @Override
    public void setCardContext(Player player, Context context) {
        timesCalled.putIfAbsent(player, 0);
        switch(meteorites.get(timesCalled.get(player)).getSize()){
            case SMALL -> player.getShip().activateShield(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
            case LARGE -> player.getShip().activateCannon(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
        }
    }

    /**
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        timesCalled.putIfAbsent(player, 0);
        player.getShip().hitByMeteor(context.getParameters("diceResult", Integer.class), meteorites.get(timesCalled.get(player)).getSide(), meteorites.get(timesCalled.get(player)).getSize());
        timesCalled.replace(player, timesCalled.get(player) + 1);
        if (timesCalled.get(player) == meteorites.size()) {
            timesCalled.put(player, 0);
            return true;
        }
        return false;
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
        List<String> lines = new ArrayList<>();
        lines.add("Carta: " + name);
        lines.add("Livello: " + level);
        lines.add("Facoltativa: No");
        lines.add("Effetto:");
        lines.add("- La nave viene colpita da " + meteorites.size() + " meteoriti:");
        for (int i = 0; i < meteorites.size(); i++) {
            Meteor m = meteorites.get(i);
            lines.add("  • Meteorite " + (i + 1) + ": lato " + m.getSide() + ", dimensione " + m.getSize());
        }
        lines.add("- I colpi si applicano uno alla volta");
        lines.add("Comandi: 'roll dice' per lanciare il dado,");
        lines.add("'set <x1> <y1> <x2> <y2>' per settare x e y");
        lines.add("dello scudo nel caso di meteorite piccolo");
        lines.add("o del cannone nel caso di meteorite grande");
        lines.add("e x e y del vano batteria da cui rimuovere");
        lines.add("un segnalino.");
        lines.add("scrivi 'apply' dopo roll dice per applicare l'effetto");
        return lines.toArray(new String[0]);
    }

}
