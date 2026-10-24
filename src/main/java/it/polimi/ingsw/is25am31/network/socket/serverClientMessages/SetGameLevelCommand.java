package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to set the game level.
 */
public class SetGameLevelCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3883498588891235550L;
    private final int level;

    /**
     * Constructs a new {@code SetGameLevelCommand} with the specified game level.
     * @param level The integer representing the game level to be set.
     */
    public SetGameLevelCommand(int level) {
        this.level = level;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to set the game level
     * in the local client model.
     * @param client The {@link SocketClient} instance to execute the command on.
     */
    public void execute(SocketClient client) {
        client.setGameLevel(level);
    }
}