package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to display a specific card.
 */
public class ShowCardCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -1170472466704490241L;
    private final Card card;

    /**
     * Constructs a new {@code ShowCardCommand} with the specified card.
     * @param card The card to be displayed.
     */
    public ShowCardCommand(final Card card) {
        this.card = card;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided card.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during card display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showCard(card);
    }
}