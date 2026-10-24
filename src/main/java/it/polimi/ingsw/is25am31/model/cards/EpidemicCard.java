package it.polimi.ingsw.is25am31.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Connector;
import it.polimi.ingsw.is25am31.model.components.Side;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Epidemic card in the game. An Epidemic card triggers a special effect
 * that removes crew members from adjacent cabins.
 */
public class EpidemicCard extends Card{
    @Serial
    private static final long serialVersionUID = 6353419106884104201L;

    public EpidemicCard(@JsonProperty("id") int id,
                        @JsonProperty("level") int level,
                        @JsonProperty("name") String name,
                        @JsonProperty("testFlight") boolean testFlight) {
        super(id, level, name, testFlight);
    }
    
    public EffectType getType(){
        return EffectType.SPECIAL;
    }

    /**
     * if checked component is a cabin with at least one crew member, and it is adjacent to another cabin with at least one crew member, one crew member gets removed
     *
     * @param player on whom the effect is applied
     * @param context map of parameters passed by the player, used to apply the effect
     * @return {@code true} if effect has been completely resolved for passed player,
     *  {@code false} otherwise
     */
    @Override
    public boolean applyEffect(Player player, Context context) {
        Ship ship = player.getShip();
        List<Component> affectedCabins = new ArrayList<>();
        if( ship.getCrewNumber() <= 1)
            return true;
        for (int i = 0; i < 5; i++)
            for(int j = 0; j < 7; j++){
                Component component = ship.getComponent(i, j);
                if(ship.getComponent(i, j) != null && component.getCrewMembers() > 0 &&
                (((i + 1 < 5) && (ship.getComponent(i + 1, j) != null && (ship.getComponent(i + 1, j).getCrewMembers() > 0 || affectedCabins.contains(ship.getComponent(i + 1, j)))) && !ship.getComponent(i + 1, j).getConnectors().get(Side.FRONT).equals(Connector.NONE) && !component.getConnectors().get(Side.BACK).equals(Connector.NONE)) ||
                ((i - 1 >= 0) && (ship.getComponent(i - 1, j) != null && (ship.getComponent(i - 1, j).getCrewMembers() > 0 || affectedCabins.contains(ship.getComponent(i - 1, j)))) && !ship.getComponent(i - 1, j).getConnectors().get(Side.BACK).equals(Connector.NONE) && !component.getConnectors().get(Side.FRONT).equals(Connector.NONE))||
                ((j + 1 < 7) && (ship.getComponent(i, j + 1) != null && (ship.getComponent(i, j + 1).getCrewMembers() > 0 || affectedCabins.contains(ship.getComponent(i, j + 1)))) && !ship.getComponent(i, j + 1).getConnectors().get(Side.LEFT).equals(Connector.NONE) && !component.getConnectors().get(Side.RIGHT).equals(Connector.NONE)) ||
                ((j - 1 >= 0)&&(ship.getComponent(i, j - 1) != null && (ship.getComponent(i, j - 1).getCrewMembers() > 0 ||  affectedCabins.contains(ship.getComponent(i, j - 1)))) && !ship.getComponent(i, j - 1).getConnectors().get(Side.RIGHT).equals(Connector.NONE) && !component.getConnectors().get(Side.LEFT).equals(Connector.NONE)))){
                    component.modifyCrewMembers(-1);
                    affectedCabins.add(component);
                }
            }
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
                "- Se due cabine adiacenti hanno membri a bordo,",
                "  un membro viene rimosso da ciascuna",
                "- L'effetto si applica a tutta la nave",
                "- Nessun effetto se c'è un solo membro a bordo"
        };
    }

}
