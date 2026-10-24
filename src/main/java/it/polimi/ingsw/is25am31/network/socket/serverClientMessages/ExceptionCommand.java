package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.customerrors.PingErrorException;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;
import it.polimi.ingsw.is25am31.view.ClientModel;

import java.io.Serial;
import java.io.Serializable;
import java.net.Socket;

/**
 * Represents a command sent from the server to the client to communicate an exception that occurred on the server-side.
 */
public class ExceptionCommand  implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -2461843006443272077L;
    private final Exception exception;

    /**
     * Constructs a new {@code ExceptionCommand}.
     *
     * @param exception The {@link Exception} object that occurred on the server.
     */
    public ExceptionCommand(final Exception exception) {
        this.exception = exception;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to re-throw the received exception. This propagates the server-side error
     * to the client's execution context, allowing the client to handle it appropriately.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception the exception that was received from the server.
     */
    public void execute(SocketClient client) throws Exception {
        throw exception;
    }
}