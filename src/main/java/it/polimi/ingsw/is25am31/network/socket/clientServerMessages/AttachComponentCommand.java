package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to attach a component
 * to a specific position on the ship grid.
 */
public class AttachComponentCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 3049206584713440749L;
    private final Component component;
    private final int x;
    private final int y;
    private final UUID clientId;

    /**
     * Constructs a new {@code AttachComponentCommand}.
     *
     * @param component The {@link Component} to be attached.
     * @param x The row where the component is to be attached.
     * @param y The column where the component is to be attached.
     * @param clientId The unique identifier of the client sending this command.
     */
    public AttachComponentCommand(Component component, int x, int y, UUID clientId) {
        this.component = component;
        this.x = x;
        this.y = y;
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to attach the specified component at the given coordinates
     * for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during component attachment on the server.
     */
    public void execute(SocketServer server) throws Exception {
        server.attachComponent(component, x, y, clientId);
    }
}