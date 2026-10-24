package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serializable;

/**
 * Represents a command that can be sent from a client to the server.
 * All client-server commands must implement this interface to ensure
 * they can be executed by the {@link SocketServer}.
 */
public interface ClientServerCommand extends Serializable {
    /**
     * Executes the command on the provided {@link SocketServer} instance.
     * This method defines the action to be performed on the server side
     * when this command is received.
     *
     * @param server The {@link SocketServer} instance to execute the command on.
     * @throws Exception if an error occurs during command execution.
     */
    void execute(SocketServer server) throws Exception;
}