package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a command sent from the server to the client to display a list of face-up components.
 */
public class ShowFaceUpComponentListCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -3414160073414988632L;
    private final List<Component> components;

    /**
     * Constructs a new {@code ShowFaceUpComponentListCommand} with the specified list of components.
     * @param components The list of face-up {@link Component}s to be displayed.
     */
    public ShowFaceUpComponentListCommand(List<Component> components) {
        this.components = components;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided list of face-up components.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during component list display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showFaceUpComponentList(components);
    }
}