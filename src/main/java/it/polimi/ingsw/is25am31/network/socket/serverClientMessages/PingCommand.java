package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Represents a command sent from the server to the client to test the connection.
 */
public class PingCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8221217145251676953L;

    /**
     * Constructs a new {@code PingCommand}.
     */
    public PingCommand() {}

    /**
     * Executes the command on the {@link SocketClient},
     *
     * @param client the {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during the connection test.
     */
    public void execute(SocketClient client) throws Exception {
        client.connectionTest();
    }
}