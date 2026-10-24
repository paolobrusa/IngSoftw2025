package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to set contextual information and to apply
 * the current card's effect.
 */
public class SetEffectContextCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 7879677002623581439L;
    private final UUID clientId;
    private final Map<String, Object> context;

    /**
     * Constructs a new {@code SetEffectContextCommand}.
     *
     * @param clientId The unique identifier of the client setting the effect context.
     * @param context A {@link Map} representing the effect-specific context data.
     */
    public SetEffectContextCommand(final UUID clientId, final Map<String, Object> context) {
        this.clientId = clientId;
        this.context = context;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to apply the current card's effect for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while setting the effect context on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.setEffectContext(clientId, context);
    }
}