package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.model.components.Side;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code CannonHit} class represents a cannon hit, which is
 * a game element characterized by specific side and size.
 */
public class CannonHit implements Serializable {
    @Serial
    private static final long serialVersionUID = 5348423224731415680L;
    private Size size;
    private Side side;

    public CannonHit(){}

    public CannonHit(Side side, Size size) {
        this.side = side;
        this.size = size;
    }

    public Size getSize(){ return this.size; }
    public Side getSide(){ return this.side; }
}
