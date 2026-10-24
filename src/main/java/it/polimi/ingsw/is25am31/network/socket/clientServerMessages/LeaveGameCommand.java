package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from a client to the server to request to leave the current game.
 */
public class LeaveGameCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -4776542624709680676L;
    private final UUID clientId;

    /**
     * Constructs a new {@code LeaveGameCommand}.
     *
     * @param clientId The unique identifier of the client requesting to leave the game.
     */
    public LeaveGameCommand(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to remove the client identified by {@code clientId} from their current game.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the process of leaving the game on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.leaveGame(clientId);
    }
}