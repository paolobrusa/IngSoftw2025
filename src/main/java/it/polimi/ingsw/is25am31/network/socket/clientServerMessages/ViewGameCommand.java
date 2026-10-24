package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to request to view the list of available games.
 */
public class ViewGameCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 2362694628728542043L;
    private final UUID clientId;

    /**
     * Constructs a new {@code ViewGameCommand}.
     *
     * @param clientId The unique identifier of the client requesting to view the game.
     */
    public ViewGameCommand(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to send the current list of available games back to the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the game list retrieval or sending on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.viewGame(clientId);
    }
}