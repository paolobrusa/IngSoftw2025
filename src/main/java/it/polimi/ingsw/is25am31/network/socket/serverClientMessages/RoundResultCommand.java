package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to notify the result of a round
 * during specific cards' effect application.
 */
public class RoundResultCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 1619013438581375038L;
    private final boolean roundWon;

    /**
     * Constructs a new {@code RoundResultCommand}.
     * @param roundWon A boolean indicating whether the round was won (true) or lost (false).
     */
    public RoundResultCommand(boolean roundWon){
        this.roundWon = roundWon;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to display the round result.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during round result notification.
     */
    public void execute(SocketClient client) throws Exception{
        client.roundResult(roundWon);
    }
}