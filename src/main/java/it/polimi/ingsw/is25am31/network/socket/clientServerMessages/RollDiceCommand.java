package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to request a dice roll.
 */
public class RollDiceCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 4587213312626038011L;
    private final UUID clientId;

    /**
     * Constructs a new {@code RollDiceCommand}.
     *
     * @param clientId The unique identifier of the client requesting the dice roll.
     */
    public RollDiceCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to perform a dice roll for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the dice roll operation on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.rollDice(clientId);
    }
}