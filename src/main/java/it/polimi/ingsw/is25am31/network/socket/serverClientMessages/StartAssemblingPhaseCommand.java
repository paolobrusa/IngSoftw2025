package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a command sent from the server to the client to start the assembling phase of the game.
 */
public class StartAssemblingPhaseCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -4423501256019095661L;
    private final List<Player> players;
    private final int level;

    /**
     * Constructs a new {@code StartAssemblingPhaseCommand}.
     * @param players A list of {@link Player}s participating in the game.
     * @param level The current game level.
     */
    public StartAssemblingPhaseCommand(final List<Player> players, final int level) {
        this.players = players;
        this.level = level;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to start the assembling phase
     * with the given players and game level.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during the start of the assembling phase.
     */
    public void execute(SocketClient client) throws Exception {
        client.startAssemblingPhase(players, level);
    }
}