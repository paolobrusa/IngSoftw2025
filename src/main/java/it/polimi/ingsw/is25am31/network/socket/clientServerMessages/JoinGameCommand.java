package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from a client to the server to request to join an existing game.
 */
public class JoinGameCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 7604293774551134092L;
    private final int game;
    private final UUID clientId;

    /**
     * Constructs a new {@code JoinGameCommand}.
     *
     * @param game The identifier of the game to join.
     * @param clientId The unique identifier of the client requesting to join the game.
     */
    public JoinGameCommand(int game, UUID clientId) {
        this.game = game;
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to add the client identified by {@code clientId} to the game
     * specified by {@code game}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the game joining process on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.joinGame(clientId, game);
    }
}