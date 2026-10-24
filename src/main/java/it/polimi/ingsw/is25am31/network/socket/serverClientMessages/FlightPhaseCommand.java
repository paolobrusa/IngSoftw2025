package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a command sent from the server to the client to start the flight phase of the game.
 */
public class FlightPhaseCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = 1687957447296780561L;
    private final Ship ship;
    private final Map<Integer, Integer> positions;

    /**
     * Constructs a new {@code FlightPhaseCommand}.
     *
     * @param ship The {@link Ship} object representing the current state of the player's ship.
     * @param positions A {@link Map} with as keys the players' ids and as values their updated positions on the game board.
     */
    public FlightPhaseCommand(Ship ship, Map<Integer, Integer> positions) {
        this.ship = ship;
        this.positions = positions;
    }

    /**
     * Executes the command on the {@link SocketClient},
     * instructing it to start the flight phase with the provided ship and position information.
     *
     * @param client The {@link SocketClient} instance on which the command will be executed.
     * @throws Exception if an error occurs during the flight phase on the client.
     */
    public void execute(SocketClient client) throws Exception {
        client.startFlightPhase(ship, positions);
    }
}