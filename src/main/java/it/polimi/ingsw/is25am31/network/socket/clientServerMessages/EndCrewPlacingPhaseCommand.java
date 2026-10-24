package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to signal the end of the crew placing phase
 * for the specified client.
 */
public class EndCrewPlacingPhaseCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -5495033015668341343L;
    private final UUID clientId;

    /**
     * Constructs a new {@code EndCrewPlacingPhaseCommand}.
     *
     * @param clientId The unique identifier of the client signaling the end of their crew placing phase.
     */
    public EndCrewPlacingPhaseCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to process the end of the crew placing phase for the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while ending the crew placing phase on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.endCrewPlacingPhase(clientId);
    }
}