package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Good;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.io.Serial;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a specific card in the game that triggers the "Combat Zone" scenario.
 */
public class CombatZoneCard extends Card{
    @Serial
    private static final long serialVersionUID = 3895321096611498168L;
    private final int lostDays;
    private final int lostGoods;
    private final int lostCrewMembers;
    private final List<CannonHit> cannonHits;
    private int timesCalled = 0;

    public CombatZoneCard(@JsonProperty("id") int id,
                          @JsonProperty("level") int level,
                          @JsonProperty("name") String name,
                          @JsonProperty("testFlight") boolean testFlight,
                          @JsonProperty("lostGoods") int lostGoods,
                          @JsonProperty("cannonHits") List<CannonHit> cannonHits,
                          @JsonProperty("lostDays") int lostDays,
                          @JsonProperty("lostCrewMembers") int lostCrewMembers) {
        super(id, level, name, testFlight);
        this.lostGoods = lostGoods;
        this.lostCrewMembers = lostCrewMembers;
        this.cannonHits = cannonHits;
        this.lostDays = lostDays;
    }

    @Override
    public EffectType getType() {
        return EffectType.COMBATZONE;
    }

    @Override
    public int getIterations(){
        return level == 1
            ? 1 + lostCrewMembers + cannonHits.size()
            : 1 + lostGoods + cannonHits.size();
    }

    /**
     * Method used to do things that the player may/must do before applying the effect.
     *
     * @param player Player setting their context
     * @param context Map of parameters passed by the player, useful to set the context of the card
     */
    @Override
    public void setCardContext(Player player, Context context) {
        if(level == 1){
            if(timesCalled > 0 && timesCalled < 3){
                player.getShip().activateEngine(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
            } else if (timesCalled >= 3) {
                player.getShip().activateCannon(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
            }
        }else if(level == 2){
            if(timesCalled == 0) {
                player.getShip().activateCannon(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
            }else if (timesCalled  > 0 & timesCalled < 4) {
                player.getShip().activateEngine(context.getParameters("x1", Integer.class), context.getParameters("y1", Integer.class), context.getParameters("x2", Integer.class), context.getParameters("y2", Integer.class));
            }
        }
    }

    /**
     * Dispatches between the level 1 and level 2 methods.
     *
     * @param player Player on whom the effect is applied
     * @param context Map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context){
        boolean res;
        if(level == 1)
            res =  applyEffectLevel1(player, context);
        else
            res = applyEffectLevel2(player, context);

        timesCalled++;
        System.out.println("carta chiamata: " + timesCalled + " volte");
        System.out.println("getiterations: " + getIterations());
        if(timesCalled == getIterations()) {
            timesCalled = 0;
            res = true;
        }
        return res;
    }

    /**
     * Applies effect of the specific level 1 instance of the card.
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    private boolean applyEffectLevel1(Player player, Context context){
        Component component;
        if(timesCalled == 0) {
            player.getRocket().changePosition(-lostDays);
            return true;
        }
        else if (timesCalled <= 2) {
            if(context == null) {
                if (player.getShip().getCrewNumber() == 0) {
                    timesCalled = 2;
                    return true;
                } else
                    throw new RuntimeException("You still have crew members to remove");
            }
            if (context.getParameters("x", Integer.class) == null ||
                context.getParameters("y", Integer.class) == null)
                throw new IllegalArgumentException("Invalid parameters");
            component = player.getShip().getComponent(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));

            if (component.getCrewMembers() == -1)
                throw new RuntimeException("Selected component isn't a cabin");
            else if (component.getCrewMembers() == 0)
                throw new RuntimeException("Selected cabin has no crew members inside");

            //removes crew member from given cabin
            component.modifyCrewMembers(-1);
            if(timesCalled == 2) {
                return true;
            }
        } else{
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
            player.getShip().hitByCannon(context.getParameters("diceResult", Integer.class), cannonHits.get(timesCalled - 3).getSide(), cannonHits.get(timesCalled - 3).getSize());
        }
        return false;
    }

    /**
     * Applies effect of the specific level 2 instance of the card.
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    private boolean applyEffectLevel2(Player player, Context context){
        if(timesCalled == 0) {
            player.getRocket().changePosition(-lostDays);
            System.out.println("Perdita di giorni di volo");
            return true;
        }
        else if(timesCalled <= 3) {
            if(context == null) {
                if (player.getShip().getBatteryCount() + player.getShip().getGoodCount() == 0) {
                    timesCalled = 3;
                    System.out.println("Fine effetto merci");
                    return true;
                } else
                    throw new RuntimeException("You still have goods and/or batteries to remove");
            }
            if (context.getParameters("x", Integer.class) == null ||
                context.getParameters("y", Integer.class) == null ||
                context.getParameters("Good", String.class) == null)
                throw new IllegalArgumentException("Invalid parameters");
            if(player.getShip().getGoodCount() > 0) {
                player.getShip().removeGoods(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class), Good.parseGood(context.getParameters("Good", String.class)));
            } else if(player.getShip().getBatteryCount() > 0) {
                player.getShip().removeBattery(context.getParameters("x", Integer.class), context.getParameters("y", Integer.class));
            } else {
                throw new RuntimeException("No goods or batteries to remove");
            }
            if(timesCalled == 3) {
                 return true;
            }
        } else{
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
            System.out.println("Dadi " + timesCalled);
            System.out.println(context.getParameters("diceResult", Integer.class) + " " + cannonHits.get(timesCalled - 4).getSide() + " " + cannonHits.get(timesCalled - 4).getSize() + " " + (timesCalled - 4));
            player.getShip().hitByCannon(context.getParameters("diceResult", Integer.class), cannonHits.get(timesCalled - 4).getSide(), cannonHits.get(timesCalled - 4).getSize());
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
        String[] effettiLivello1 = {
                "Carta: " + name,
                "Livello: " + level,
                "Facoltativa: No",
                "Effetto Livello 1:",
                "- Il giocatore con meno equipaggio perde " + lostDays,
                " giorni di volo",
                "- Il giocatore con meno potenza motore perde " + lostCrewMembers,
                " membri dell'equipaggio da cabine",
                "- Il giocatore con meno potenza cannoni subisce ",
                cannonHits.size() + " colpi di cannone",
                "Comandi : prima fase: 'apply' per applicare",
                "l'effetto. Seconda fase: 'set <x1> <y1> <x2> <y2>'",
                "Dove x1 e y1 sono le coordinate del motore doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "una volta attivati i motori scrivi 'apply'",
                "se sei il giocatore con la minore potenza motore,",
                "dovrai usare 'set <x> <y>' per scegliere da quale",
                "cabina rimuovere i membri dell'equipaggio.",
                "terza fase: 'set <x1> <y1> <x2> <y2>'",
                "Dove x1 e y1 sono le coordinate del cannone doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "una volta attivati i cannoni scrivi 'apply'.",
                "Se sei il giocatore con meno potenza cannoni",
                "dovrai scrivere 'roll dice' per tirare i dadi per",
                "stabilire dove verrai colpito dalle cannonate,",
                "e 'apply' oppure 'set shield <x1> <y1> <x2> <y2>'",
                "per essere colpito dalla cannonata.",
                "x1 e y1 sono le coordinate dello scudo mentre",
                "x2 e y2 sono le coordinate della batteria",
                "CANNONATA 1: Small Back",
                "CANNONATA 2: Large Back"
        };

        String[] effettiLivello2 = {
                "Carta: " + name,
                "Livello: " + level,
                "Facoltativa: No",
                "Effetto Livello 2:",
                "- Il giocatore con meno potenza cannoni perde " + lostDays,
                " giorni di volo",
                "- Il giocatore con meno potenza motore perde " + lostGoods,
                " merci o batterie",
                "- Il giocatore con meno equipaggio subisce ",
                cannonHits.size() + " colpi di cannone",
                "Comandi: prima fase: 'set <x1> <y1> <x2> <y2>'",
                "Dove x1 e y1 sono le coordinate del cannone doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "una volta attivati i cannoni scrivi 'apply'",
                "seconda fase: 'set <x1> <y1> <x2> <y2>'",
                "Dove x1 e y1 sono le coordinate del motore doppio",
                "da attivare e x2 e y2 sono le coordinate del vano",
                "batterie da cui rimuovere il segnalino batteria,",
                "una volta attivati i motori scrivi 'apply'",
                "se sei il giocatore con la minore potenza motore,",
                "dovrai usare 'set <color> <x> <y>' se richiesto per",
                "scegliere da quale stiva rimuovere le merci.",
                "terza fase: 'apply' per applicare.",
                "se sei il giocatore con meno membri dell'equipaggio",
                "dovrai scrivere 'roll dice' per tirare i dadi per",
                "stabilire dove verrai colpito dalle cannonate",
                "CANNONATA 1: Small Front",
                "CANNONATA 2: Small Left",
                "CANNONATA 3: Large Back",
                "CANNONATA 4: Small Right"
        };

        return level == 1 ? effettiLivello1 : effettiLivello2;
    }

    /**
     *
     * @param players List of active players.
     * @return ID of the player allowed to apply the card effect (i.e., the player that lost the round).
     */
    public int getAllowedPlayer(List<Player> players) {
        return switch (timesCalled) {
            case 0 -> isTestFlight()
                    ? players.stream().min(Comparator.comparingInt(p -> p.getShip().getCrewNumber())).map(Player::getId).orElseThrow()
                    : players.stream().min(Comparator.comparingDouble(p -> p.getShip().getCannonPower())).map(Player::getId).orElseThrow();
            case 1,2 ->
                    players.stream().min(Comparator.comparingInt(p -> p.getShip().getEnginePower())).map(Player::getId).orElseThrow();
            case 3 -> isTestFlight()
                    ? players.stream().min(Comparator.comparingDouble(p -> p.getShip().getCannonPower())).map(Player::getId).orElseThrow()
                    : players.stream().min(Comparator.comparingInt(p -> p.getShip().getEnginePower())).map(Player::getId).orElseThrow();
            default -> isTestFlight()
                    ? players.stream().min(Comparator.comparingDouble(p -> p.getShip().getCannonPower())).map(Player::getId).orElseThrow()
                    : players.stream().min(Comparator.comparingInt(p -> p.getShip().getCrewNumber())).map(Player::getId).orElseThrow();
        };
    }

}
