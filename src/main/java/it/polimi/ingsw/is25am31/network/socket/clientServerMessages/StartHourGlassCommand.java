package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to initiate an hourglass timer.
 */
public class StartHourGlassCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 6667532346189561193L;
    private final UUID clientId;

    /**
     * Constructs a new {@code StartHourGlassCommand}.
     *
     * @param clientId The unique identifier of the client initiating the hourglass.
     */
    public StartHourGlassCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to start an hourglass timer for the game associated with the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while starting the hourglass on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.startHourGlass(clientId);
    }
}