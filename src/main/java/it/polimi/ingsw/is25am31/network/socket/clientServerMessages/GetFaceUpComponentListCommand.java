package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to request the list of available face-up components.
 */
public class GetFaceUpComponentListCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -5888268957232976006L;
    private final UUID clientId;

    /**
     * Constructs a new {@code GetFaceUpComponentListCommand}.
     *
     * @param clientId The unique identifier of the client requesting the list of face-up components.
     */
    public GetFaceUpComponentListCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to retrieve send back the list of face-up components to the client
     * identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the retrieval or sending of the component list on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.getFaceUpComponentList(clientId);
    }
}