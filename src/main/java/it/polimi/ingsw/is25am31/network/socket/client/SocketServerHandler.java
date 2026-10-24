package it.polimi.ingsw.is25am31.network.socket.client;

import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.network.socket.clientServerMessages.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code SocketServerHandler} class acts as a client-side proxy for the server in a socket-based
 * communication. It implements the {@link VirtualServer} interface, allowing the client
 * to invoke "remote" methods that are then serialized as command objects and sent
 * to the actual server via an {@link ObjectOutputStream}.
 * This class handles the serialization and sending of various client-to-server messages.
 */
public class SocketServerHandler implements VirtualServer {
    private final ObjectOutputStream out;

    /**
     * Constructs a new {@code SocketServerHandler}.
     *
     * @param out The {@link ObjectOutputStream} used to send command objects to the server.
     */
    public SocketServerHandler(ObjectOutputStream out) {
        this.out = out;
    }

    /**
     * Sends an object through the {@link ObjectOutputStream}, flushes the stream, and then resets it.
     * This synchronized method ensures thread-safe writing and guarantees that each object
     * is sent with its current state. Resetting the stream prevents issues with object caching
     * and ensures updated objects are correctly transmitted.
     *
     * @param obj The object to be sent.
     * @throws IOException If an I/O error occurs during {@code writeObject}, {@code flush}, or {@code reset}.
     */
    private synchronized void send(ClientServerCommand obj) throws IOException {
        out.writeObject(obj);
        out.flush();
        out.reset();
    }

    /**
     * This method is part of the {@link VirtualServer} interface but is not directly used
     * in the current socket communication model for connecting.
     * 
     * @param client The {@link VirtualView} instance of the client. (Not used for sending a command)
     */
    @Override
    public void connect(VirtualView client) {}

    /**
     * Sends a command to the server for a client to leave the current game.
     *
     * @param clientId The {@link UUID} of the client requesting to leave.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void leaveGame(UUID clientId) throws IOException {
        send(new LeaveGameCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to draw a card.
     *
     * @param clientId The {@link UUID} of the client requesting to draw a card.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void drawCard(UUID clientId) throws IOException {
        send(new DrawCardCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to draw a face-down component.
     *
     * @param clientId The {@link UUID} of the client requesting to draw a face-down component.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void drawFaceDownComponent(UUID clientId) throws IOException {
        send(new DrawFaceDownComponentCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to draw a face-up component at a specific index.
     *
     * @param index    The index of the face-up component to draw.
     * @param clientId The {@link UUID} of the client requesting to draw the component.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void drawFaceUpComponent(int index, UUID clientId) throws IOException {
        send(new DrawFaceUpComponentCommand(clientId, index));
    }

    /**
     * Sends a command to the server for a client to attach a component at specified coordinates.
     *
     * @param component The {@link Component} to be attached.
     * @param x The line to attach the component on.
     * @param y The column to attach the component on.
     * @param clientId  The {@link UUID} of the client performing the action.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void attachComponent(Component component, int x, int y, UUID clientId) throws IOException {
        send(new AttachComponentCommand(component, x, y, clientId));
    }

    /**
     * Sends a command to the server to set a client's nickname.
     *
     * @param nickname The desired nickname for the client.
     * @param clientId The {@link UUID} of the client setting the nickname.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setNickname(String nickname, UUID clientId) throws IOException {
        send(new SetNicknameCommand(nickname, clientId));
    }

    /**
     * Sends a command to the server to set the context for a card operation.
     * This context can contain various parameters needed for a card's effect.
     *
     * @param clientId The {@link UUID} of the client providing the context.
     * @param context  A {@link Map} containing key-value pairs representing the card context.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setCardContext(UUID clientId, Map<String, Object> context) throws IOException {
        send(new SetCardContextCommand(clientId, context));
    }

    /**
     * Sends a command to the server to set the context for an effect resolution.
     * This context can contain various parameters needed for resolving an effect.
     *
     * @param clientId The {@link UUID} of the client providing the context.
     * @param context A {@link Map} containing key-value pairs representing the effect context.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setEffectContext(UUID clientId, Map<String, Object> context) throws IOException {
        send(new SetEffectContextCommand(clientId, context));
    }

    /**
     * Sends a command to the server to signal the end of the assemble phase for a client.
     *
     * @param clientId The {@link UUID} of the client ending the assemble phase.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void endAssemblePhase(UUID clientId) throws IOException {
        send(new EndAssemblePhaseCommand(clientId));
    }

    /**
     * Sends a command to the server to set the maximum number of players for a game.
     * This is typically done by the game creator.
     *
     * @param clientId   The {@link UUID} of the client setting the maximum players.
     * @param maxPlayers The maximum number of players allowed in the game.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setMaxPlayers(UUID clientId, int maxPlayers) throws IOException {
        send(new SetMaxPlayersCommand(maxPlayers, clientId));
    }

    /**
     * Sends a command to the server to set the game level.
     * This is typically done by the game creator.
     *
     * @param clientId The {@link UUID} of the client setting the game level.
     * @param level    The desired game level.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setGameLevel(UUID clientId, int level) throws IOException {
        send(new SetGameLevelCommand(clientId, level));
    }

    /**
     * Sends a command to the server to request showing a ship belonging to a specific player.
     *
     * @param clientId The {@link UUID} of the client requesting the action.
     * @param nickname The nickname of the player whose ship is to be shown.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showShip(UUID clientId, String nickname) throws IOException {
        send(new ShowShipCommand(clientId, nickname));
    }

    /**
     * Sends a command to the server for a client to roll the dice.
     *
     * @param clientId The {@link UUID} of the client requesting to roll the dice.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void rollDice(UUID clientId) throws IOException {
        send(new RollDiceCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to put down a component.
     *
     * @param component The {@link Component} to be put down.
     * @param clientId  The {@link UUID} of the client performing the action.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void putDownComponent(Component component, UUID clientId) throws IOException {
        send(new PutDownComponentCommand(clientId, component));
    }

    /**
     * Sends a command to the server to request the list of face-up components.
     *
     * @param clientId The {@link UUID} of the client requesting the list.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void getFaceUpComponentList(UUID clientId) throws IOException {
        send(new GetFaceUpComponentListCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to draw from the reserved components at a specific index.
     *
     * @param index    The index in the reserved components from which to draw.
     * @param clientId The {@link UUID} of the client performing the action.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void drawFromReserved(int index, UUID clientId) throws IOException {
        send(new DrawFromReservedCommand(clientId, index));
    }

    /**
     * Sends a command to the server to remove a component at specified coordinates.
     *
     * @param clientId The {@link UUID} of the client requesting the action.
     * @param x        The X-coordinate of the component to remove.
     * @param y        The Y-coordinate of the component to remove.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void removeComponent(UUID clientId, int x, int y) throws IOException {
        send(new RemoveComponentCommand(x, y, clientId));
    }

    /**
     * Sends a command to the server to get a sub-deck from the game.
     *
     * @param clientId The {@link UUID} of the client requesting the sub-deck.
     * @param index    The index of the sub-deck to retrieve.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void getSubDeck(UUID clientId, int index) throws IOException {
        send(new GetSubDeckCommand(clientId, index));
    }

    /**
     * Sends a command to the server for a client to place a rocket at a specific position.
     *
     * @param clientId The {@link UUID} of the client performing the action.
     * @param position The position where the rocket is to be placed.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void placeRocket(UUID clientId, int position) throws IOException {
        send(new PlaceRocketCommand(clientId, position));
    }

    /**
     * Sends a command to the server to signal the end of the crew placing phase for a client.
     *
     * @param clientId The {@link UUID} of the client ending the phase.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void endCrewPlacingPhase(UUID clientId) throws IOException {
        send(new EndCrewPlacingPhaseCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to place a crew member at specified coordinates with a specific color
     * (if it's an alien).
     *
     * @param clientId The {@link UUID} of the client performing the action.
     * @param x The row where the crew member is placed.
     * @param y The column where the crew member is placed.
     * @param a The {@link AlienColor} of the crew member.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void placeCrewMember(UUID clientId, int x, int y, AlienColor a) throws IOException {
        send(new PlaceCrewMemberCommand(clientId, x, y, a));
    }

    /**
     * Sends a command to the server to start an hourglass timer.
     *
     * @param clientId The {@link UUID} of the client initiating the hourglass.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void startHourGlass(UUID clientId) throws IOException {
        send(new StartHourGlassCommand(clientId));
    }

    /**
     * Sends a command to the server to start the final hourglass timer.
     *
     * @param clientId The {@link UUID} of the client initiating the final hourglass.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void startFinalHourGlass(UUID clientId) throws IOException {
        send(new StartFinalHourGlassCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to abandon the race.
     *
     * @param clientId The {@link UUID} of the client abandoning the race.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void abandonRace(UUID clientId) throws IOException {
        send(new AbandonRaceCommand(clientId));
    }

    /**
     * Sends a ping command to the server to test connectivity.
     *
     * @param clientId The {@link UUID} of the client sending the ping.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void ping(UUID clientId) throws IOException {
        send(new PingCommand(clientId));
    }

    /**
     * Sends an increment command to the server. (Specific use case for a game mechanic or test).
     *
     * @param clientId The {@link UUID} of the client requesting the increment.
     * @throws IOException If an I/O error occurs during communication.
     */
    public void increment(UUID clientId) throws IOException {
        send(new IncrementCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to create a new game.
     *
     * @param clientId The {@link UUID} of the client creating the game.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void createGame(UUID clientId) throws IOException {
        send(new CreateGameCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to view available games.
     *
     * @param clientId The {@link UUID} of the client requesting to view games.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void viewGame(UUID clientId) throws IOException {
        send(new ViewGameCommand(clientId));
    }

    /**
     * Sends a command to the server for a client to join a specific game.
     *
     * @param clientId The {@link UUID} of the client requesting to join the game.
     * @param game     The integer ID of the game to join.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void joinGame(UUID clientId, int game) throws IOException {
        send(new JoinGameCommand(game, clientId));
    }
}
