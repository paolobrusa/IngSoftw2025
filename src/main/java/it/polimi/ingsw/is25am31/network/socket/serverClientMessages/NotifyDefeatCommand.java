package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to notify them of their defeat.
 */
public class NotifyDefeatCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -7366470740452671466L;
    private final int loseType;

    /**
     * Constructs a new {@code NotifyDefeatCommand}.
     *
     * @param loseType an integer representing the type of defeat.
     */
    public NotifyDefeatCommand(int loseType) {
        this.loseType = loseType;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to the defeat notification.
     *
     * @param client the {@link SocketClient} instance to execute the command on.
     */
    public void execute(SocketClient client) {
        client.notifyDefeat(loseType);
    }
}