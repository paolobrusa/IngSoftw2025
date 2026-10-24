package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.components.Good;

import java.io.Serial;
import java.util.List;

/**
 * Represents a card that allows players to interact with an abandoned station in the game.
 */
public class AbandonedStationCard extends Card {
    @Serial
    private static final long serialVersionUID = -3611686521088705397L;
    private final int crewMembers;
    private final List<Good> gainedGoods;
    private final int lostDays;
    private int timesCalled = 0;

    public AbandonedStationCard(@JsonProperty("id") int id,
                                @JsonProperty("level") int level,
                                @JsonProperty("name") String name,
                                @JsonProperty("testFlight") boolean testFlight,
                                @JsonProperty("crewMembers") int crewMembers,
                                @JsonProperty("gainedGoods") List<Good> gainedGoods,
                                @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.crewMembers = crewMembers;
        this.gainedGoods = gainedGoods;
        this.lostDays = lostDays;
    }

    public int getCrewMembers() { return crewMembers; }


    @Override
    public int getIterations() { return gainedGoods.size(); }
    @Override
    public EffectType getType(){ return EffectType.ABANDONED; }

    /**
     * Method used to do things that the player may/must do before applying the effect.
     *
     * @param player Player setting their context.
     * @param context Map of parameters passed by the player, useful to set the context of the card.
     */
    @Override
    public void setCardContext(Player player, Context context){
        if(player.getShip().getCrewNumber() < crewMembers)
            throw new RuntimeException("Your ship doesn't have enough crew members");
    }

    /**
     * @param player Player on whom the effect is applied.
     * @param context Map of parameters passed by the player, used to apply the effect.
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise.
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        if(context == null) {
            player.getRocket().changePosition(-lostDays);
            return true;
        }
        if(context.getParameters("Good", String.class) == null ||
        context.getParameters("x", Integer.class) == null ||
        context.getParameters("y", Integer.class) == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        boolean flag = false;
        Component component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));
        // adds specified good to specified cargo hold
        if(!component.getName().equals("CargoHold"))
            throw new RuntimeException("Selected component isn't a cargo hold");

        Good requested = Good.parseGood(context.getParameters("Good", String.class));

        for(Good g : gainedGoods)
            if(g.equals(requested)) {
                flag = true;
                break;
            }

        if (!flag)
            throw new RuntimeException("Requested good is not in the list of available goods");

        if(!component.addContains(requested))
            throw new RuntimeException("Selected cargo hold can't hold selected good");

        gainedGoods.remove(requested);
        timesCalled++;
        // on last call, removes flight days from the player
        if(gainedGoods.isEmpty()) {
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

        String listaMerci = gainedGoods.stream()
                .map(good -> switch (good) {
                    case RED -> "Rosso";
                    case BLUE -> "Blu";
                    case GREEN -> "Verde";
                    case YELLOW -> "Giallo";
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("Nessuna");

        return new String[]{
                "Carta: " + name,
                "Livello: " + level,
                "Facoltativa: Sì",
                "Effetto:",
                "- Ottieni le seguenti merci: " + listaMerci,
                "- Da posizionare nei Cargo Hold (una per turno)",
                "- Richiede " + crewMembers + " membri dell'equipaggio",
                "- Perdi " + lostDays + " giorni di volo al termine",
                "Comandi: 'accept' per accettare la carta, 'decline'",
                "per rifiutarla e passarla al prossimo giocatore,",
                "'set <color> <x> <y>' per settare il colore della",
                "merce da caricare e le coordinate della stiva in",
                "cui caricarla. 'apply' se le merci non sono finite",
                "per resitituire le rimanenti alla banca."
        };
    }

}

