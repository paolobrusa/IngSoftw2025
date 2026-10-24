package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serializable;

/**
 * Represents a command that can be sent from the server to a client.
 * All server-client commands must implement this interface to ensure
 * they can be executed by a {@link SocketClient}.
 */
public interface ServerClientCommand extends Serializable {
    /**
     * Executes the command on the provided {@link SocketClient} instance.
     * This method defines the action to be performed on the client side
     * when this command is received.
     *
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during command execution.
     */
    void execute(SocketClient client) throws Exception;
}