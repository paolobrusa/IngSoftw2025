package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to display a specific ship component.
 */
public class ShowComponentCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -7921952187108419807L;
    private final Component component;

    /**
     * Constructs a new {@code ShowComponentCommand} with the specified component.
     * @param component The component to be displayed.
     */
    public ShowComponentCommand(final Component component) {
        this.component = component;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided component.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during component display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showComponent(component);
    }
}