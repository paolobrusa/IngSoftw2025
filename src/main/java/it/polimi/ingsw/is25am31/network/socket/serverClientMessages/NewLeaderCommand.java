package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to inform about a change in game leader status.
 */
public class NewLeaderCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -4492004479238810013L;
    private final boolean isNewLeader;

    /**
     * Constructs a new {@code NewLeaderCommand}.
     *
     * @param isNewLeader A boolean value indicating whether the client is the new game leader ({@code true})
     * or not ({@code false}).
     */
    public NewLeaderCommand(boolean isNewLeader){
        this.isNewLeader = isNewLeader;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to update its internal state and its user interface
     * to reflect the new leader status.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs while the client processes the new leader information.
     */
    public void execute(SocketClient client) throws Exception{
        client.newLeader(isNewLeader);
    }
}