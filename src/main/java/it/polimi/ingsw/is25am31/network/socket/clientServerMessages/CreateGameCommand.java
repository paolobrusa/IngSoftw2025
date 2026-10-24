package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from a client to the server to request the creation of a new game.
 * The client sending this command becomes the first player of the newly created game.
 */
public class CreateGameCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3494687967949849324L;
    private final UUID clientId;

    /**
     * Constructs a new {@code CreateGameCommand}.
     *
     * @param clientId The unique identifier of the client requesting to create a new game.
     */
    public CreateGameCommand(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to create a new game instance and associate it with the
     * client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during game creation on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.createGame(clientId);
    }
}