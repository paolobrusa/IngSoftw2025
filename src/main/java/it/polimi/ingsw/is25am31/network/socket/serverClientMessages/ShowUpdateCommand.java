package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to display a general update message.
 */
public class ShowUpdateCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -8491037160947930527L;
    private final UpdateMessage updateMessage;

    /**
     * Constructs a new {@code ShowUpdateCommand} with the specified update message.
     * @param updateMessage The {@link UpdateMessage} to be displayed.
     */
    public ShowUpdateCommand(UpdateMessage updateMessage) {
        this.updateMessage = updateMessage;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided update message.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during update message display.
     */
    @Override
    public void execute(SocketClient client) throws Exception {
        client.showUpdate(updateMessage);
    }
}