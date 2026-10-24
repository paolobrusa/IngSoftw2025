package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to lock the client's user interface and input.
 */
public class LockClientCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 6470070708586977984L;

    /**
     * Constructs a new {@code LockClientCommand}.
     */
    public LockClientCommand() {}

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to lock its user interface and prevent further input.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs while the client attempts to lock its interface.
     */
    public void execute(SocketClient client) throws Exception {
        client.lockClient();
    }
}