package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to perform some action
 * before applying a card's effect.
 */
public class SetCardContextCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 6915526469918375842L;
    private final UUID clientId;
    private final Map<String, Object> context;

    /**
     * Constructs a new {@code SetCardContextCommand}.
     *
     * @param clientId The unique identifier of the client setting the card context.
     * @param context A {@link Map} representing the card-specific context data.
     */
    public SetCardContextCommand(final UUID clientId, final Map<String, Object> context) {
        this.clientId = clientId;
        this.context = context;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to set the provided card context for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while setting the card context on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.setCardContext(clientId, context);
    }
}