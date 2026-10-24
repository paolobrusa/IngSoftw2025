package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to put down a component.
 */
public class PutDownComponentCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -1949528516955434788L;
    private final UUID clientId;
    private final Component component;

    /**
     * Constructs a new {@code PutDownComponentCommand}.
     *
     * @param clientId The unique identifier of the client putting down the component.
     * @param component The {@link Component} to be put down.
     */
    public PutDownComponentCommand(UUID clientId, Component component) {
        this.clientId = clientId;
        this.component = component;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to handle the logic for putting down the specified component
     * for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the component manipulation on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.putDownComponent(component, clientId);
    }
}