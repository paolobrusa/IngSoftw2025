package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to draw a component from the reserved components.
 */
public class DrawFromReservedCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 6305741701777472598L;
    private final UUID clientId;
    private final int index;

    /**
     * Constructs a new {@code DrawFromReservedCommand}.
     *
     * @param clientId The unique identifier of the client requesting to draw from the reserved area.
     * @param index The index of the item to be drawn from the reserved area.
     */
    public DrawFromReservedCommand(final UUID clientId, final int index) {
        this.clientId = clientId;
        this.index = index;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to retrieve the component at the specified index from the reserved area
     * and provide it to the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the drawing from the reserved area on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.drawFromReserved(index, clientId);
    }
}