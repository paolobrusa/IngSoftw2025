package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a command sent from the server to the client to display a sub-deck of cards.
 */
public class ShowSubDeckCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8488786102054638113L;
    private final List<Card> subDeck;

    /**
     * Constructs a new {@code ShowSubDeckCommand} with the specified list of cards representing a sub-deck.
     * @param subDeck The list of cards to be displayed as a sub-deck.
     */
    public ShowSubDeckCommand(List<Card> subDeck) {
        this.subDeck = subDeck;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided sub-deck.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during sub-deck display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showSubDeck(subDeck);
    }
}