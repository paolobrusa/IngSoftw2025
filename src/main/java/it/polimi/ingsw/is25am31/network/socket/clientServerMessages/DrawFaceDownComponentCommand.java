package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to draw a face-down component.
 */
public class DrawFaceDownComponentCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -7171663444440261679L;
    private final UUID clientId;

    /**
     * Constructs a new {@code DrawFaceDownComponentCommand}.
     *
     * @param clientId The unique identifier of the client requesting to draw a face-down component.
     */
    public DrawFaceDownComponentCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to provide a face-down component to the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the drawing of a face-down component on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.drawFaceDownComponent(clientId);
    }
}