package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to set the maximum number of players for a game.
 */
public class SetMaxPlayersCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8736579632397449567L;
    private final UUID clientId;
    private final int maxPlayers;

    /**
     * Constructs a new {@code SetMaxPlayersCommand}.
     *
     * @param maxPlayers The maximum number of players allowed in the game.
     * @param clientId The unique identifier of the client setting the maximum players.
     */
    public SetMaxPlayersCommand(int maxPlayers, UUID clientId) {
        this.clientId = clientId;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to set the maximum number of players for the game associated with the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while setting the maximum players on the server.
     */
    @Override
    public void execute(SocketServer server) throws Exception {
        server.setMaxPlayers(clientId, maxPlayers);
    }
}