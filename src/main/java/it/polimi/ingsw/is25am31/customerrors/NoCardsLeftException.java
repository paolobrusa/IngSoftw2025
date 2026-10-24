package it.polimi.ingsw.is25am31.customerrors;

public class NoCardsLeftException extends RuntimeException {
    public NoCardsLeftException(String message) {
        super(message);
    }
}
