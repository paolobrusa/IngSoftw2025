package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.customerrors.PingErrorException;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to perform an increment operation,
 * used to keep track of clients' connections.
 */
public class IncrementCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -895265157334903549L;
    private final UUID clientId;

    /**
     * Constructs a new {@code IncrementCommand}.
     *
     * @param clientId The unique identifier of the client sending this command.
     */
    public IncrementCommand(UUID clientId) {
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
            server.increment(clientId);
        } catch (Exception e) {
            throw new PingErrorException("Connection error");
        }
    }
}