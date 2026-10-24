package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to communicate the result of a dice roll.
 */
public class DiceResultCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 6620001142311162991L;
    private final int diceResult;

    /**
     * Constructs a new {@code DiceResultCommand}.
     *
     * @param diceResult The integer value representing the outcome of the dice roll.
     */
    public DiceResultCommand(int diceResult){
        this.diceResult = diceResult;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to display and process the dice roll result.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs while the client processes the dice result.
     */
    public void execute(SocketClient client) throws Exception{
        client.diceResult(diceResult);
    }
}