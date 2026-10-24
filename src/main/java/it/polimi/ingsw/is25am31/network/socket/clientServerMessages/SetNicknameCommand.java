package it.polimi.ingsw.is25am31.network.socket.clientServerMessages;

import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a command sent from the client to the server to set the client's nickname.
 */
public class SetNicknameCommand implements ClientServerCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 8331956855430845923L;
    private final String nickname;
    private final UUID clientId;

    /**
     * Constructs a new {@code SetNicknameCommand}.
     *
     * @param nickname The desired nickname for the client.
     * @param clientId The unique identifier of the client setting the nickname.
     */
    public SetNicknameCommand(String nickname, UUID clientId) {
        this.nickname = nickname;
        this.clientId = clientId;
    }

    /**
     * Executes the command on the {@link SocketServer},
     * instructing it to set the nickname for the client identified by {@code clientId}.
     *
     * @param server The {@link SocketServer} instance on which the command will be executed.
     * @throws Exception if an error occurs during the nickname setting process on the server (e.g., nickname already taken).
     */
    public void execute(SocketServer server) throws Exception {
        server.setNickname(nickname, clientId);
    }
}