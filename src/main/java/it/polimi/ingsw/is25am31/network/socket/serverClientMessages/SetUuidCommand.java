package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the server to the client to set the client's UUID.
 */
public class SetUuidCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -8500805264957488795L;
    private final UUID uuid;

    /**
     * Constructs a new {@code SetUuidCommand} with the specified UUID.
     * @param uuid The UUID to be set on the client.
     */
    public SetUuidCommand(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to set its UUID.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during UUID setting.
     */
    public void execute(SocketClient client) throws Exception {
        client.setUUID(uuid);
    }
}