package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.model.components.Side;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code Meteor} class represents a meteor, which is
 * a game element characterized by specific side and size.
 */
public class Meteor implements Serializable {
    @Serial
    private static final long serialVersionUID = -4508807583733518611L;
    private Size size;
    private Side side;

    public Meteor(){}
    public Size getSize(){return size;}
    public Side getSide(){return side;}
}
