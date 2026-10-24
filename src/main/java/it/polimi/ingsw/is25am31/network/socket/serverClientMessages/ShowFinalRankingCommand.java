package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a command sent from the server to the client to display the final ranking of players.
 */
public class ShowFinalRankingCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 452244747906505121L;
    private final Map<Player, Integer> ranking;

    /**
     * Constructs a new {@code ShowFinalRankingCommand} with the specified map of player rankings.
     * @param ranking A map where keys are {@link Player}s and values are their final scores, representing the ranking.
     */
    public ShowFinalRankingCommand(Map<Player, Integer> ranking) {
        this.ranking = ranking;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the final ranking.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during ranking display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showFinalRanking(ranking);
    }
}