package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;

/**
 * The SlaversCard class represents a card in the game that introduces slavers encounter.
 * When this card is applied, the player either wins the encounter and gains resources
 * or loses and receives penalties.
 */
public class SlaversCard extends Card{
    @Serial
    private static final long serialVersionUID = -4318525824034411025L;
    private final int cannonStrength;
    private final int lostCrewMembers;
    private final int gainedCoins;
    private final int lostDays;
    private int timesCalled = 0;
    private boolean win;
    private double playerCannonStrength;

    public SlaversCard(@JsonProperty("id") int id,
                       @JsonProperty("level") int level,
                       @JsonProperty("name") String name,
                       @JsonProperty("testFlight") boolean testFlight,
                       @JsonProperty("cannonStrength") int cannonStrength,
                       @JsonProperty("lostCrewMembers") int lostCrewMembers,
                       @JsonProperty("gainedCoins") int gainedCoins,
                       @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.cannonStrength = cannonStrength;
        this.lostCrewMembers = lostCrewMembers;
        this.gainedCoins = gainedCoins;
        this.lostDays = lostDays;
    }


    public boolean isWin(){ return win; }

    @Override
    public EffectType getType() { return EffectType.SLAVERS; }
    @Override
    public int getIterations(){
        if(win) return 1;
        return lostCrewMembers;
    }

    /**
     * method used to do things that the player may/must do before applying the effect
     *
     * @param player setting their context
     * @param context map of parameters passed by the player, useful to set the context of the card
     *  In this case it's the coordinates of the cannon to activate and battery to remove
     */
    @Override
    public void setCardContext(Player player, Context context){
        if (context.getParameters("x1", Integer.class) == null ||
                context.getParameters("y1", Integer.class) == null ||
                context.getParameters("x2", Integer.class) == null ||
                context.getParameters("y2", Integer.class) == null)
            throw new IllegalArgumentException("Invalid parameters");
        player.getShip().activateCannon(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
    }



    /**
     * determines whether the player won or lost, adds coins and removes flight days if player won, removes crew members if player lost
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        if(timesCalled == 0) playerCannonStrength = player.getShip().getCannonPower();
        if(playerCannonStrength == cannonStrength) {
            timesCalled = 0;
            return true;
        }
        win = playerCannonStrength > cannonStrength;
        if (context == null && !win && player.getShip().getCrewNumber() == 0){
            timesCalled = 0;
            return true;
        }else if (context == null && win) {
            player.getRocket().changePosition(-lostDays);
            timesCalled = 0;
            return true;
        } else if (context == null && player.getShip().getCrewNumber() != 0){
            throw new RuntimeException("You still have crew members to remove");
        }else if (context != null &&
                 (context.getParameters("x", Integer.class) == null ||
                  context.getParameters("y", Integer.class) == null)) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        if(win) {
            player.addCosmicCredits(gainedCoins);
            player.getRocket().changePosition(-lostDays);
            return true;
        }
        Component component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));

        if(component == null){
            throw new RuntimeException("Selected position contains no components");
        } else if (!component.getName().equals("Cabin") && !component.getName().equals("MainCabin")) {
            throw new RuntimeException("Selected component is not a cabin");
        } else if(!component.modifyCrewMembers(-1)){
            throw new RuntimeException("There are no crew members to remove in this cabin");
        }

        timesCalled++;
        if (timesCalled == getIterations()) {
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
        return new String[]{
                "Carta: " + name,
                "Livello: " + level,
                "Facoltativa: No",
                "Effetto:",
                "- Incontri una nave di schiavisti con forza cannone " + cannonStrength,
                "- Se vinci, ottieni " + gainedCoins + " crediti cosmici",
                "- Se perdi, perdi " + lostCrewMembers + " membri dell'equipaggio",
                "- In ogni caso, perdi " + lostDays + " giorni di volo",
                "Comandi: 'set <x1> <y1> <x2> <y2>', dove",
                "x1 e y1 sono le coordinate del cannone doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "'set <x> <y>' che indica da quale cabina",
                "rimuovere i membri dell'equipaggio in caso di",
                "sconfitta. Se non hai più membri dell'equipaggio",
                "da rimuovere oppure se hai vinto o pareggiato",
                "scrivi 'apply' per terminare l'effetto."
        };
    }

}
