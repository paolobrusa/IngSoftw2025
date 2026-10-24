package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to request a sub-deck of cards.
 */
public class GetSubDeckCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 4974584416955191797L;
    private final UUID clientId;
    private final int index;

    /**
     * Constructs a new {@code GetSubDeckCommand}.
     *
     * @param clientId The unique identifier of the client requesting the sub-deck.
     * @param index The index of the specific sub-deck to be retrieved.
     */
    public GetSubDeckCommand(final UUID clientId, final int index) {
        this.clientId = clientId;
        this.index = index;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to retrieve the sub-deck at the specified index and provide it to the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the retrieval of the sub-deck on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.getSubDeck(clientId, index);
    }
}