package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to place a crew member or an alien
 * of a specific color on a cabin in a specified position.
 */
public class PlaceCrewMemberCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -384702066333543680L;
    private final UUID clientId;
    private final int x;
    private final int y;
    private final AlienColor type;

    /**
     * Constructs a new {@code PlaceCrewMemberCommand}.
     *
     * @param clientId The unique identifier of the client placing the crew member.
     * @param x The row where the crew member is to be placed.
     * @param y The column where the crew member is to be placed.
     * @param color The {@link AlienColor} of the crew member to be placed.
     */
    public PlaceCrewMemberCommand(final UUID clientId, final int x, final int y, final AlienColor color) {
        this.clientId = clientId;
        this.x = x;
        this.y = y;
        this.type = color;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to place the specified crew member at the given coordinates
     * for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the placement of the crew member on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.placeCrewMember(clientId, x, y, type);
    }
}