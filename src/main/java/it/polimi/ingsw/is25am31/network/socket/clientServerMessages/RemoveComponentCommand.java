package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to remove a component from a specific position.
 */
public class RemoveComponentCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3049206584713440750L;
    private final int x;
    private final int y;
    private final UUID clientId;

    /**
     * Constructs a new {@code RemoveComponentCommand}.
     *
     * @param x The row of the component to be removed.
     * @param y The column of the component to be removed.
     * @param clientId The unique identifier of the client requesting the component removal.
     */
    public RemoveComponentCommand(int x, int y, UUID clientId) {
        this.x = x;
        this.y = y;
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to remove the component at the specified coordinates
     * for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during component removal on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.removeComponent(clientId, x, y);
    }
}