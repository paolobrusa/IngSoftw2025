package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to place the player's rocket at a specific position.
 */
public class PlaceRocketCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8500593663809898805L;
    private final UUID clientId;
    private final int position;

    /**
     * Constructs a new {@code PlaceRocketCommand}.
     *
     * @param clientId The unique identifier of the client placing the rocket.
     * @param position The specific position where the rocket is to be placed.
     */
    public PlaceRocketCommand(final UUID clientId, final int position) {
        this.clientId = clientId;
        this.position = position;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to place the rocket at the specified position for the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the placement of the rocket on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.placeRocket(clientId, position);
    }
}