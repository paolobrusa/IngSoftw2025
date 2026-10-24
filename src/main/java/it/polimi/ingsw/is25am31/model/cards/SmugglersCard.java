package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Good;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.util.List;

/**
 * The PiratesCard class represents a card in the game that introduces smugglers encounter.
 * When this card is applied, the player either wins the encounter and gains resources
 * or loses and receives penalties.
 */
public class SmugglersCard extends Card{
    @Serial
    private static final long serialVersionUID = -7244181470887528574L;
    private final int cannonStrength;
    private final int lostGoods;
    private final List<Good> gainedGoods;
    private final int lostDays;
    private int timesCalled = 0;
    private Boolean win = null;
    private double playerCannonStrength;

    public SmugglersCard(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("name") String name,
                         @JsonProperty("testFlight") boolean testFlight,
                         @JsonProperty("cannonStrength") int cannonStrength,
                         @JsonProperty("lostGoods") int lostGoods,
                         @JsonProperty("gainedGoods") List<Good> gainedGoods,
                         @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.cannonStrength = cannonStrength;
        this.lostGoods = lostGoods;
        this.gainedGoods = gainedGoods;
        this.lostDays = lostDays;
    }

    public boolean isWin() {
        return win;
    }

    @Override
    public EffectType getType() { return EffectType.SMUGGLERS; }

    @Override
    public int getIterations() {
        if(win == null) return lostGoods;
        if(win) return gainedGoods.size();
        return lostGoods;
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
     * determines whether the player won or lost, adds goods and removes flight days if player won, removes goods if player lost
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
        if (context == null && !win && player.getShip().getGoodCount() + player.getShip().getBatteryCount() == 0){
            timesCalled = 0;
            return true;
        }else if (context == null && win) {
            player.getRocket().changePosition(-lostDays);
            timesCalled = 0;
            return true;
        } else if (context == null && player.getShip().getGoodCount() + player.getShip().getBatteryCount() != 0){
            throw new RuntimeException("You still have goods and/or batteries to remove");
        }else if (context != null &&
            (context.getParameters("Good", String.class) == null ||
            context.getParameters("x", Integer.class) == null ||
            context.getParameters("y", Integer.class) == null)){
                throw new IllegalArgumentException("Invalid parameters");
        }
        if(win){
            Component component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));

            if(!component.getName().equals("CargoHold"))
                throw new RuntimeException("Selected component is not a Cargo Hold");


            Good good = Good.parseGood(context.getParameters("Good", String.class));

            if(!gainedGoods.contains(good))
                throw new RuntimeException("Error in good selection");

            component.addContains(good);

        } else{
             if(player.getShip().getGoodCount() > 0)
                 player.getShip().removeGoods(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class), Good.parseGood(context.getParameters("Good", String.class)));
             else if(player.getShip().getBatteryCount() > 0)
                 player.getShip().removeBattery(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));
             else
                throw new RuntimeException("No goods or batteries to remove");
        }
        timesCalled++;
        if(timesCalled == getIterations()) {
            if(win) player.getRocket().changePosition(-lostDays);
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
        // merci ottenibili
        String merci = gainedGoods.stream()
                .map(g -> switch (g) {
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
                "Facoltativa: No",
                "Effetto:",
                "- Incontri una nave contrabbandiera con forza cannone " + cannonStrength,
                "- Se vinci, ottieni le seguenti merci: " + merci,
                "- Se perdi, scarta " + lostGoods + " merci o batterie",
                "- Se vinci perdi " + lostDays + " giorni di volo",
                "Comandi: 'set <x1> <y1> <x2> <y2>', dove",
                "x1 e y1 sono le coordinate del cannone doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "'set <color> <x> <y>' che indica quali merci",
                "aggiungere dalla carta alla nave nel caso di",
                "vittoria e quelle da rimuovere in caso di ",
                "sconfitta. Ricorda: le merci vanno rimosse in ",
                "ordine di valore.",
                "Se non hai merci da rimuovere devi rimuovere le ",
                "batterie con 'set battery <x> <y>'. Se non",
                "hai più merci o batterie da rimuovere oppure",
                "se hai vinto e non vuoi o non puoi aggiungere",
                "altre merci oppure se hai pareggiato",
                "scrivi 'apply' per terminare l'effetto."
        };
    }

}

