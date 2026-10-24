package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to put the client in effect resolution state.
 */
public class EffectResolutionCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -634564649394245049L;

    /**
     * Constructs a new {@code EffectResolutionCommand}.
     */
    public EffectResolutionCommand() {}

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to switch to effect resolution state.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     */
    public void execute(SocketClient client) {
        client.effectResolution();
    }
}