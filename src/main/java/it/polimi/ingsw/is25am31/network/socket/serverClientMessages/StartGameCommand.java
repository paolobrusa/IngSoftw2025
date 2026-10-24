package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to signal the start of the game.
 */
public class StartGameCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8944185830975764952L;

    /**
     * Constructs a new {@code StartGameCommand}.
     */
    public StartGameCommand() {}

    /**
     * Executes the command on the {@link SocketClient}, instructing it to initiate the game.
     * @param client The {@link SocketClient} instance to execute the command on.
     */
    public void execute(SocketClient client) {
        client.startGame();
    }
}