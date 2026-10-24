package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to set the game level or difficulty.
 */
public class SetGameLevelCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 2058257769059946982L;
    private final UUID clientId;
    private final int level;

    /**
     * Constructs a new {@code SetGameLevelCommand}.
     *
     * @param clientId The unique identifier of the client setting the game level.
     * @param level The integer value representing the desired game level or difficulty.
     */
    public SetGameLevelCommand(final UUID clientId, final int level) {
        this.clientId = clientId;
        this.level = level;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to set the game level for the game associated with the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs while setting the game level on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.setGameLevel(clientId, level);
    }
}