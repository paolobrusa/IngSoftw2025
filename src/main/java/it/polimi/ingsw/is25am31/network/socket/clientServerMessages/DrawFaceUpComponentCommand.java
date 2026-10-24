package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to draw a specific face-up component.
 */
public class DrawFaceUpComponentCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -2871100334587987718L;
    private final UUID clientId;
    private final int id;

    /**
     * Constructs a new {@code DrawFaceUpComponentCommand}.
     *
     * @param clientId The unique identifier of the client requesting to draw a face-up component.
     * @param id The identifier of the specific face-up component to be drawn.
     */
    public DrawFaceUpComponentCommand(final UUID clientId, final int id) {
        this.clientId = clientId;
        this.id = id;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to provide the specified face-up component to the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the drawing of the face-up component on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.drawFaceUpComponent(id, clientId);
    }
}