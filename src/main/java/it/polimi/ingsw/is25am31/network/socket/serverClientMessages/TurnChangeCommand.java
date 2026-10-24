package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to notify a change in turn.
 */
public class TurnChangeCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -124660684078719250L;
    private final boolean isMyTurn;
    private final int turnType;
    private final int currentPlayer;

    /**
     * Constructs a new {@code TurnChangeCommand}.
     * @param isMyTurn A boolean indicating if it's the recipient client's turn.
     * @param turnType An integer representing the type of turn.
     * @param currentPlayer An integer representing the identifier (playerId) of the current player.
     */
    public TurnChangeCommand(boolean isMyTurn, int turnType, int currentPlayer) {
        this.isMyTurn = isMyTurn;
        this.turnType = turnType;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to handle the turn change.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during turn change processing.
     */
    public void execute(SocketClient client) throws Exception{
        client.turnChange(isMyTurn, turnType, currentPlayer);
    }
}