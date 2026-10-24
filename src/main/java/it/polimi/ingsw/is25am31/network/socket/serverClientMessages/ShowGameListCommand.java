package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a command sent from the server to the client to display a list of available games.
 */
public class ShowGameListCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -6147901704800015398L;
    private final List<Integer> gameList;

    /**
     * Constructs a new {@code ShowGameListCommand} with the specified list of game identifiers.
     * @param gameList A list of integers, where each integer represents a game identifier.
     */
    public ShowGameListCommand(List<Integer> gameList){
        this.gameList = gameList;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided list of games.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during game list display.
     */
    public void execute(SocketClient client) throws Exception{
        client.showGameList(gameList);
    }
}