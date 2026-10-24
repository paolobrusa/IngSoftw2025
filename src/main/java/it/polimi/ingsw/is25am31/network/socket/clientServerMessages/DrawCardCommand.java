package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to draw a new card.
 */
public class DrawCardCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3339892809258773333L;
    private final UUID clientId;

    /**
     * Constructs a new {@code DrawCardCommand}.
     *
     * @param clientId The unique identifier of the client requesting to draw a card.
     */
    public DrawCardCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to handle the card drawing logic for the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during card drawing on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.drawCard(clientId);
    }
}