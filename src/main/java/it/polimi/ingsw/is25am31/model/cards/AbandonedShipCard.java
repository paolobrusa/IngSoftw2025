package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;

/**
 * Represents a card that allows players to interact with an abandoned ship in the game.
 */
public class AbandonedShipCard extends Card {
    @Serial
    private static final long serialVersionUID = 977481406698157353L;
    private final int gainedCoins;
    private final int lostCrewMembers;
    private final int lostDays;
    private int timesCalled = 0;

    public AbandonedShipCard(@JsonProperty("id") int id,
                             @JsonProperty("level") int level,
                             @JsonProperty("name") String name,
                             @JsonProperty("testFlight") boolean testFlight,
                             @JsonProperty("gainedCoins") int gainedCoins,
                             @JsonProperty("lostCrewMembers") int lostCrewMembers,
                             @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.gainedCoins = gainedCoins;
        this.lostCrewMembers = lostCrewMembers;
        this.lostDays = lostDays;
    }
    
    public int getCrewMembers() {
        return lostCrewMembers;
    }

    @Override
    public EffectType getType() { return EffectType.ABANDONED; }

    @Override
    public int getIterations() { return this.lostCrewMembers; }

    /**
     * @param player Player on whom the effect is applied
     * @param context Map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        if (context.getParameters("x", Integer.class) == null ||
            context.getParameters("y", Integer.class) == null)
            throw new IllegalArgumentException("Invalid parameters");

        //removes crew member from given cabin
        Component component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));

        if(component.getCrewMembers() == -1)
            throw new RuntimeException("Selected component isn't a cabin");
        else if (component.getCrewMembers() == 0)
            throw new RuntimeException("Selected cabin has no crew members inside");

        component.modifyCrewMembers(-1);
        timesCalled++;
        if(timesCalled == getIterations()) {
            //adds credits to player balance
            player.addCosmicCredits(gainedCoins);
            //removes flight days
            player.getRocket().changePosition(-lostDays);
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
                "Facoltativa: Sì",
                "Effetto:",
                "- Scarica " + lostCrewMembers + " membri da cabine a scelta",
                "- Guadagna " + gainedCoins + " crediti",
                "- Perde " + lostDays + " giorni di volo",
                "Comandi: 'accept' per accettare, 'decline'",
                "per rifutare e passare la carta al prossimo",
                "giocatore, 'set <x> <y>' per settare la ",
                "posizione della cabina da cui rimovere 1 membro ",
                "dell'equipaggio (da fare tante volte quanti",
                "sono i membri totali da rimuovere)"
        };
    }

}
