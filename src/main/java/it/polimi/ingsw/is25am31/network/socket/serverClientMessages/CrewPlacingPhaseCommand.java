package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a command sent from the server to the client to start the crew placing phase.
 */
public class CrewPlacingPhaseCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -1942592928231940305L;
    private final Ship ship;
    private final Map<Integer, Integer> positions;

    /**
     * Constructs a new {@code CrewPlacingPhaseCommand}.
     *
     * @param ship The {@link Ship} object representing the current state of the player's ship.
     * @param positions A {@link Map} where keys represent players' ids and values represent
     * their updated positions on the gameboard.
     */
    public CrewPlacingPhaseCommand(final Ship ship, final Map<Integer, Integer> positions) {
        this.ship = ship;
        this.positions = positions;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to enter the crew placing phase with the provided ship and position information.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs during the initiation of the crew placing phase on the client.
     */
    public void execute(SocketClient client) throws Exception{
        client.crewPlacingPhase(ship, positions);
    }
}