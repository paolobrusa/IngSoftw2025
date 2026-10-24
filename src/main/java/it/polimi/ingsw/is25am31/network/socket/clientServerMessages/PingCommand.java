package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.customerrors.PingErrorException;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to check the connection status.
 */
public class PingCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -295265457334603547L;
    private final UUID clientId;

    /**
     * Constructs a new {@code PingCommand}.
     *
     * @param clientId The unique identifier of the client sending the ping.
     */
    public PingCommand(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws PingErrorException if an error occurs while sending this command to the server.
     */
    public void execute(SocketServer server) throws Exception {
        try {
            server.ping(clientId);
        } catch (Exception e) {
            throw new PingErrorException("Connection error");
        }
    }
}