package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am31.model.components.Side;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * The PiratesCard class represents a card in the game that introduces a pirate encounter.
 * When this card is applied, the player either wins the encounter and gains resources
 * or loses and receives penalties.
 */
public class PiratesCard extends Card{
    @Serial
    private static final long serialVersionUID = -6292380365781125507L;
    private final int cannonStrength;
    private final List<Size> cannonHits;
    private final int gainedCoins;
    private final int lostDays;
    private int timesCalled = 0;
    private boolean win;

    public PiratesCard(@JsonProperty("id") int id,
                       @JsonProperty("level") int level,
                       @JsonProperty("name") String name,
                       @JsonProperty("testFlight") boolean testFlight,
                       @JsonProperty("cannonStrength") int cannonStrength,
                       @JsonProperty("cannonHits") List<Size> cannonHits,
                       @JsonProperty("gainedCoins") int gainedCoins,
                       @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.cannonStrength = cannonStrength;
        this.cannonHits = cannonHits;
        this.gainedCoins = gainedCoins;
        this.lostDays = lostDays;
    }

    @JsonIgnore
    public void setWin(Player player) {
        win = player.getShip().getCannonPower() > cannonStrength;
    }

    public boolean isWin() { return win; }

    @Override
    public EffectType getType(){ return EffectType.PIRATES; }
    @Override
    public int getIterations(){
        if(win) return 1;
        return cannonHits.size();
    }

    /**
     * Method used to do things that the player may/must do before applying the effect.
     *
     * @param player Player setting their context.
     * @param context Map of parameters passed by the player, useful to set the context of the card.
     *  In this case it's the coordinates of the cannon to activate and battery to remove.
     */
    @Override
    public void setCardContext(Player player, Context context){
        player.getShip().activateCannon(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
    }

    /**
     * Determines whether the player won or lost, gives them coins (win) or shoots cannon hits at them (defeat).
     *
     * @param player Player on whom the effect is applied.
     * @param context Map of parameters passed by the player, used to apply the effect.
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise.
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        if(win) {
            player.addCosmicCredits(gainedCoins);
            player.getRocket().changePosition(-lostDays);
            return true;
        } else {
            try {
                if (context.getParameters("diceResult", Integer.class) == 0)
                    throw new RuntimeException("Dice must be rolled first");
            }catch (NullPointerException e){
                throw new RuntimeException("Dice must be rolled first");
            }
            Integer x1 = context.getParameters("x1", Integer.class);
            Integer y1 = context.getParameters("y1", Integer.class);
            Integer x2 = context.getParameters("x2", Integer.class);
            Integer y2 = context.getParameters("y2", Integer.class);
            if(x1 != null && y1 != null && x2 != null && y2 != null) {
                player.getShip().activateShield(x1, y1, x2, y2);
            }
            player.getShip().hitByCannon(context.getParameters("diceResult", Integer.class), Side.FRONT, cannonHits.get(timesCalled));
        }
        timesCalled++;
        if(timesCalled == getIterations()){
            timesCalled = 0;
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
        lines.add("- Incontri una nave di pirati con forza cannone " + cannonStrength);
        lines.add("- Se vinci, ottieni " + gainedCoins + " crediti cosmici");
        lines.add("- Se perdi, subisci " + cannonHits.size() + " colpi di cannone");
        lines.add("- Inoltre perdi " + lostDays + " giorni di volo");
        lines.add("Comandi: 'set <x1> <y1> <x2> <y2>', dove");
        lines.add("x1 e y1 sono le coordinate del cannone doppio");
        lines.add("da attivare e x2 e y2 sono le coordinate del vano");
        lines.add("batterie da cui rimuovere il segnalino batteria.");
        lines.add("Se hai vinto scrivi 'apply' per riscuotere i ");
        lines.add("crediti, se hai perso invece usa 'roll dice'");
        lines.add("per tirare il dado e 'apply' per applicare");
        lines.add("l'effetto del meteorite, oppure ");
        lines.add("'set shield <x1> <y1> <x2> <y2>' per attivare uno");
        lines.add("scudo con x1 e x2 linea e colonna dello scudo e ");
        lines.add("y1 e y2 coordinate della batteria da rimuovere.");
        if (level==1){
            lines.add("CANNONATA 1: Small Front");
            lines.add("CANNONATA 2: Large Front");
            lines.add("CANNONATA 3: Small Front");
        } else {
            lines.add("CANNONATA 1: Large Front");
            lines.add("CANNONATA 2: Small Front");
            lines.add("CANNONATA 3: Large Front");
        }

        return lines.toArray(new String[0]);
    }

}