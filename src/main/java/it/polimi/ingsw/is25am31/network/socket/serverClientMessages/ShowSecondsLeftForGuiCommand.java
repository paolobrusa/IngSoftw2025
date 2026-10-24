package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client (specifically for GUI)
 * to display the remaining seconds for an hourglass timer.
 */
public class ShowSecondsLeftForGuiCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 7813132257398171383L;
    private final int count;
    private final int secondsLeft;

    /**
     * Constructs a new {@code ShowSecondsLeftForGuiCommand}.
     * @param count The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     */
    public ShowSecondsLeftForGuiCommand(int count, int secondsLeft) {
        this.count = count;
        this.secondsLeft = secondsLeft;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the remaining seconds for the GUI.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during the display of seconds left.
     */
    public void execute(SocketClient client) throws Exception{
        client.showSecondsLeftForGui(count, secondsLeft);
    }
}