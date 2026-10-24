package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to indicate that the final hourglass timer has finished.
 */
public class FinishFinalHourglassCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -1588297607100353991L;

    /**
     * Constructs a new {@code FinishFinalHourglassCommand}.
     */
    public FinishFinalHourglassCommand() {}

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to handle the completion of the final hourglass, and terminate assembling phase.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs while the client processes the completion of the final hourglass.
     */
    public void execute(SocketClient client) throws Exception {
        client.finishFinalHourglass();
    }
}