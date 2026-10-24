package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to request a certain player's updated ship.
 */
public class ShowShipCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3677875070287568823L;
    private final UUID clientId;
    private final String nickname;

    /**
     * Constructs a new {@code ShowShipCommand}.
     *
     * @param clientId The unique identifier of the client making the request.
     * @param nickname The nickname of the player whose ship is to be shown or updated.
     */
    public ShowShipCommand(final UUID clientId, final String nickname) {
        this.clientId = clientId;
        this.nickname = nickname;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to send the ship associated with the player with the given nickname
     * to the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the ship display/update operation on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.showShip(clientId, nickname);
    }
}