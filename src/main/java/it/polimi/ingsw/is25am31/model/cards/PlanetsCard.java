package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Good;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * The PlanetsCard class represents a card in the game that allows a player to interact with
 * different planets and collect goods from them while losing some days of travel.
 */
public class PlanetsCard extends Card{
    @Serial
    private static final long serialVersionUID = -7144919529908890204L;
    private final int planets;
    private final List<List<Good>> goods;
    private final int lostDays;
    private int timesCalled = 0;
    public int currentPlanet = -1;

    public PlanetsCard(@JsonProperty("id") int id,
                       @JsonProperty("level") int level,
                       @JsonProperty("name") String name,
                       @JsonProperty("testFlight") boolean testFlight,
                       @JsonProperty("planets") int planets,
                       @JsonProperty("goods")  List<List<Good>> goods,
                       @JsonProperty("lostDays") int lostDays) {
        super(id, level, name, testFlight);
        this.planets = planets;
        this.goods = goods;
        this.lostDays = lostDays;
    }


    @Override
    public EffectType getType() { return EffectType.PLANETS; }

    //returns number of goods in selected planet
    @Override
    public int getIterations(){ return goods.get(currentPlanet).size(); }

    /**
     * Method used to do things that the player may/must do before applying the effect
     *
     * @param player setting their context
     * @param context map of parameters passed by the player, useful to set the context of the card.
     * In this case it's the index of the planet the user wants to land on
     */
    @Override
    public void setCardContext(Player player, Context context){
        int planet = context.getParameters("planetIndex", Integer.class);
        if(currentPlanet != -1)
            throw new RuntimeException("You have already selected a planet");
        if(planet >= planets || planet < 0)
            throw new RuntimeException("Planet index out of bounds");
        if(goods.get(planet) == null){
            throw new RuntimeException("Planet has already been looted by another player");
        }

        currentPlanet = planet;
    }


    /**
     * Receives a cargo hold selected by the player, puts the good relative to the iteration belonging to the selected planet into the cargo hold
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context){
        if(currentPlanet == -1)
            throw new RuntimeException("You have to select a planet first");
        if(context == null){
            player.getRocket().changePosition(-lostDays);
            timesCalled = 0;
            goods.set(currentPlanet, null);
            currentPlanet = -1;
            return true;
        }
        if(context.getParameters("Good", String.class) == null ||
                context.getParameters("x", Integer.class) == null ||
                context.getParameters("y", Integer.class) == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        boolean flag = false;
        Component component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));

        if(component == null)
            throw new RuntimeException("Selected spot has no component");
        if(!component.getName().equals("CargoHold"))
            throw new RuntimeException("Selected component is not a Cargo Hold");

        Good requested = Good.parseGood(context.getParameters("Good", String.class));

        for(Good g : goods.get(currentPlanet)){
            if(g.equals(requested)){
                flag = true;
                break;
            }
        }
        if (!flag)
            throw new RuntimeException("Requested good is not in the list of available goods");

        if(!component.addContains(requested))
            throw new RuntimeException("Selected cargo hold can't hold selected good");

        goods.get(currentPlanet).remove(requested);
        timesCalled++;
        if(goods.get(currentPlanet).isEmpty()){
            player.getRocket().changePosition(-lostDays);
            goods.set(currentPlanet, null);
            timesCalled = 0;
            currentPlanet = -1;
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
        lines.add("Facoltativa: Sì");
        lines.add("Effetto:");
        lines.add("- Ci sono " + planets + " pianeti disponibili.");
        lines.add("- Ogni pianeta offre merci diverse:");

        // per ogni pianeta, elenca le merci
        for (int i = 0; i < planets; i++) {
            String merci = goods.get(i).stream()
                    .map(g -> switch (g) {
                        case RED -> "Rosso";
                        case BLUE -> "Blu";
                        case GREEN -> "Verde";
                        case YELLOW -> "Giallo";
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Nessuna");

            lines.add("  • Pianeta " + (i + 1) + ": " + merci);
        }

        lines.add("- Il giocatore sceglie un pianeta su cui");
        lines.add("atterrare.");
        lines.add("- Al termine, perdi " + lostDays + " giorni di volo");
        lines.add("Comandi: 'set <planet index>' (1 - " + planets + "),");
        lines.add("per scegliere il pianeta su cui atterrare, oppure");
        lines.add("'decline' per passare il turno senza atterrare.");
        lines.add("'set <color> <x> <y>' per indicare la merce da");
        lines.add("caricare e la posizione della stiva in cui ");
        lines.add("caricarla. 'apply' se le merci non sono  ");
        lines.add("finite per resitituire le rimanenti alla banca.");

        return lines.toArray(new String[0]);
    }

}
