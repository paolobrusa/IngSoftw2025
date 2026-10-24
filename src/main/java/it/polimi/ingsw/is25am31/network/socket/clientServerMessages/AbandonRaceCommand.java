package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to abandon the race.
 * This command is serializable and can be transmitted over a network.
 */
public class AbandonRaceCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -890568427532151364L;
    private final UUID clientId;

    /**
     * Constructs an {@code AbandonRaceCommand} with the specified client ID.
     * @param clientId The unique identifier of the client abandoning the race.
     */
    public AbandonRaceCommand(final UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Executes the abandon race action on the provided {@code SocketServer}.
     * This method calls the {@code abandonRace} method of the server,
     * passing the client's UUID.
     * @param server The {@code SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the execution on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.abandonRace(clientId);
    }
}