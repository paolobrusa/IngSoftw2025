package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to signal the end of the assemble phase.
 */
public class EndAssemblePhaseCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -6371364322606264896L;
    private final UUID clientId;

    /**
     * Constructs a new {@code EndAssemblePhaseCommand}.
     *
     * @param clientId The unique identifier of the client signaling the end of their assemble phase.
     */
    public EndAssemblePhaseCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to process the end of the assemble phase for the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while ending the assemble phase on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.endAssemblePhase(clientId);
    }
}