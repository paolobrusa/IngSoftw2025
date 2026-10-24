package it.polimi.ingsw.is25am31.network.socket.serverClientMessages;

import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a command sent from the server to the client to display a specific ship.
 */
public class ShowShipCommand implements ServerClientCommand, Serializable {
    @Serial
    private static final long serialVersionUID = -4342556605317606202L;
    private final Ship ship;

    /**
     * Constructs a new {@code ShowShipCommand} with the specified ship.
     * @param ship The {@link Ship} object to be displayed.
     */
    public ShowShipCommand(final Ship ship) {
        this.ship = ship;
    }

    /**
     * Executes the command on the {@link SocketClient}, instructing it to show the provided ship.
     * @param client The {@link SocketClient} instance to execute the command on.
     * @throws Exception if an error occurs during ship display.
     */
    public void execute(SocketClient client) throws Exception {
        client.showShip(ship);
    }
}