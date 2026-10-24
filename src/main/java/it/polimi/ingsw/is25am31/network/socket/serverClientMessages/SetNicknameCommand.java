package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to prompt the client to set its nickname.
 */
public class SetNicknameCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8662456334018407863L;

    /**
     * Constructs a new {@code SetNicknameCommand}.
     */
    public SetNicknameCommand() {}

    /**
     * Executes the command on the {@link SocketClient}, instructing it to prompt for a nickname.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during nickname setting.
     */
    public void execute(SocketClient client) throws Exception {
        client.setNickname();
    }
}