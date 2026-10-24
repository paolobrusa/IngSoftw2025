package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.Map;

/**
 * Represents a specialized component which, if attached to the ship
 * next to a cabin, ensures it can contain alien inhabitants.
 */
public class AlienLifeSupport extends Component {
    @Serial
    private static final long serialVersionUID = -3965470497925054011L;
    private AlienColor color;
    private boolean isStructural;

    public AlienLifeSupport(@JsonProperty("id") int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("connector") Map<Side, Connector> connector,
                            @JsonProperty("orientation") Side orientation,
                            @JsonProperty("color")AlienColor color) {
        super(id, name, connector, orientation);
        this.color = color;
        this.isStructural = false;
    }

    public void setAsStructural() {
        isStructural = true;
        color = null; // Rimuove il colore perché ora è un modulo strutturale
    }

    public boolean isStructural() {
        return isStructural;
    }

    @Override
    public String[] getAsciiArt() {
        return new String[]{
                "            ",
                "ALIEN LIFE " + (color.equals(AlienColor.BROWN) ? "B" : "P"),
                "            "
        };
    }

    @Override
    public boolean isAlienLifeSupport() {
        return true;
    }

    @Override
    public AlienColor getAlienColor() {
        return this.color;
    }

    @Override
    public boolean isAlienStructural() {
        return this.isStructural;
    }


}
