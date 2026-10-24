package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a command sent from the server to the client to update players' positions.
 */
public class UpdatePositionsCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3061629119167373280L;
    private final Map<Integer, Integer> positions;

    /**
     * Constructs a new {@code UpdatePositionsCommand} with the specified map of positions.
     * @param positions A map where keys represent player identifiers and values represent their updated positions.
     */
    public UpdatePositionsCommand(final Map<Integer, Integer> positions) {
        this.positions = positions;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to update the displayed positions.
     * @param client The {@link SocketClient} instance to execute the command on.
     */
    public void execute(SocketClient client) {
        client.updatePositions(positions);
    }
}