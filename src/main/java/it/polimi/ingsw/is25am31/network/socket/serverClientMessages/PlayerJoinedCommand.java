package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a command sent from the server to the client to notify about a new player joining the game.
 */
public class PlayerJoinedCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 1332646096888767912L;
    private final String newPlayer;
    private final List<String> players;

    /**
     * Constructs a new {@code PlayerJoinedCommand}.
     * @param newPlayer The nickname of the player who just joined.
     * @param players A list of all current player nicknames in the game.
     */
    public PlayerJoinedCommand(String newPlayer, List<String> players){
        this.newPlayer = newPlayer;
        this.players = players;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to update its view
     * with the new player and the current list of players.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during player joined notification.
     */
    public void execute(SocketClient client) throws Exception{
        client.playerJoined(newPlayer, players);
    }
}