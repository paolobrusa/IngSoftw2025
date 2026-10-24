package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client as a response to a connection attempt.
 */
public class ConnectionResponseCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -969522664046539118L;
    private final ConnectionResponse connectionResponse;

    /**
     * Constructs a new {@code ConnectionResponseCommand}.
     *
     * @param connectionResponse The {@link ConnectionResponse} object containing details about the connection outcome.
     */
    public ConnectionResponseCommand(ConnectionResponse connectionResponse) {
        this.connectionResponse = connectionResponse;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to process the connection response.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs while the client processes the connection response.
     */
    public void execute(SocketClient client) throws Exception{
        client.connectionResponse(connectionResponse);
    }
}