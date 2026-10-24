package it.polimi.ingsw.is25am31.network.socket.server;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.network.socket.clientServerMessages.ClientServerCommand;
import it.polimi.ingsw.is25am31.network.socket.serverClientMessages.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code SocketClientHandler} class is a server-side component responsible for managing
 * the communication with a single connected client in a socket-based network environment.
 * It implements the {@link VirtualView} interface, which means it acts as a listener
 * for the server, allowing the server's core logic to send updates and commands to a specific client
 * as if it were directly interacting with the client's view.
 */
public class SocketClientHandler implements VirtualView {
    private final SocketServer server;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    /**
     * Constructs a new {@code SocketClientHandler}.
     *
     * @param server The {@link SocketServer} instance that created this handler.
     * @param input  The {@link ObjectInputStream} connected to the client, used for receiving commands.
     * @param output The {@link ObjectOutputStream} connected to the client, used for sending updates and responses.
     */
    public SocketClientHandler(SocketServer server, ObjectInputStream input, ObjectOutputStream output) {
        this.server = server;
        this.in = input;
        this.out = output;
    }

    /**
     * The main execution loop for this client handler.
     * It continuously reads {@link ClientServerCommand} objects from the client's input stream.
     * Each received command is then executed on the {@link SocketServer}.
     * If an exception occurs during command execution, an {@link ExceptionCommand} is sent back to the client.
     *
     * @throws IOException If an I/O error occurs while reading objects, or if other unexpected errors occur.
     * @throws ClassNotFoundException If the class of the object to deserialize can't be found.
     */
    public void run() throws IOException, ClassNotFoundException {
        ClientServerCommand command;
        while ((command = (ClientServerCommand) in.readObject()) != null) {
            try {
                command.execute(server);
            } catch (Exception e) {
                send(new ExceptionCommand(e));
            }
        }
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
    private synchronized void send(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
        out.reset();
    }

    /**
     * Sends an {@link UpdateMessage} to the client.
     *
     * @param msg The {@link UpdateMessage} to be sent.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showUpdate(UpdateMessage msg) throws IOException {
        send(new ShowUpdateCommand(msg));
    }

    /**
     * Sends a {@link Card} object to the client for display.
     *
     * @param card The {@link Card} to be shown.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showCard(Card card) throws IOException {
        send(new ShowCardCommand(card));
    }

    /**
     * Sends a {@link Component} object to the client for display.
     *
     * @param component The {@link Component} to be shown.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showComponent(Component component) throws IOException {
        send(new ShowComponentCommand(component));
    }

    /**
     * Notifies the client that the game has started.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void startGame() throws IOException {
        send(new StartGameCommand());
    }

    /**
     * Commands the client to lock its input.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void lockClient() throws IOException {
        send(new LockClientCommand());
    }

    /**
     * Commands the client to prompt the user for a nickname.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setNickname() throws IOException {
        send(new SetNicknameCommand());
    }

    /**
     * Initiates the assembling phase on the client, providing player list and game level.
     *
     * @param players The list of {@link Player}s in the game.
     * @param level The current game level.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void startAssemblingPhase(List<Player> players, int level) throws IOException {
        send(new StartAssemblingPhaseCommand(players, level));
    }

    /**
     * Sends a {@link Ship} to the client for display.
     *
     * @param ship The {@link Ship} to be shown.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showShip(Ship ship) throws IOException {
        send(new ShowShipCommand(ship));
    }

    /**
     * Sends the current list of face-up {@link Component}s to the client for display.
     *
     * @param component The list of face-up {@link Component}s.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showFaceUpComponentList(List<Component> component) throws IOException {
        send(new ShowFaceUpComponentListCommand(component));
    }

    /**
     * Sends updated positions for game elements to the client.
     *
     * @param positions A map of integer keys to integer values representing updated positions.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void updatePositions(Map<Integer, Integer> positions) throws IOException {
        send(new UpdatePositionsCommand(positions));
    }

    /**
     * Notifies the client to initiate an effect resolution phase.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void effectResolution() throws IOException {
        send(new EffectResolutionCommand());
    }

    /**
     * Notifies the client to start the flight phase, providing the ship and its positions.
     *
     * @param ship The {@link Ship} that is flying.
     * @param positions A map of integer keys to integer values representing updated rockets positions on the game board.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void startFlightPhase(Ship ship, Map<Integer, Integer> positions) throws IOException {
        send(new FlightPhaseCommand(ship, positions));
    }

    /**
     * Sends a sub-deck of {@link Card}s to the client for display.
     *
     * @param subDeck The list of {@link Card}s in the sub-deck.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showSubDeck(List<Card> subDeck) throws IOException {
        send(new ShowSubDeckCommand(subDeck));
    }

    /**
     * Sends a ping command to the client to test connectivity.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void connectionTest() throws IOException {
        send(new PingCommand());
    }

    /**
     * Notifies the client to start the crew placing phase, providing the ship and its positions.
     *
     * @param ship The {@link Ship} for which crew is being placed.
     * @param positions A map of integer keys to integer values representing updated positions during crew placement.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void crewPlacingPhase(Ship ship, Map<Integer, Integer> positions) throws IOException {
        send(new CrewPlacingPhaseCommand(ship, positions));
    }

    /**
     * Sends the remaining seconds for an hourglass timer to the client.
     *
     * @param count The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showSecondsLeft(int count, int secondsLeft) throws IOException {
        send(new ShowSecondsLeftCommand(count, secondsLeft));
    }

    /**
     * Notifies the client that the final hourglass has finished.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void finishFinalHourglass() throws IOException {
        send(new FinishFinalHourglassCommand());
    }

    /**
     * Sends the remaining seconds for a GUI-specific timer to the client.
     *
     * @param counter The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showSecondsLeftForGui(int counter, int secondsLeft) throws IOException {
        send(new ShowSecondsLeftForGuiCommand(counter, secondsLeft));
    }

    /**
     * Sends the final ranking of players to the client.
     *
     * @param ranking A map where keys are {@link Player} objects and values are their final scores.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showFinalRanking(Map<Player, Integer> ranking) throws IOException {
        send(new ShowFinalRankingCommand(ranking));
    }

    /**
     * Notifies the client about a defeat condition, specifying the type of loss.
     *
     * @param loseType An integer representing the type of defeat.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void notifyDefeat(int loseType) throws IOException {
        send(new NotifyDefeatCommand(loseType));
    }

    /**
     * Commands the client to set the game level.
     *
     * @param level The current game level.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setGameLevel(int level) throws IOException {
        send(new SetGameLevelCommand(level));
    }

    /**
     * Sends the unique identifier (UUID) for this client.
     *
     * @param clientId The {@link UUID} to be set for the client.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void setUUID(UUID clientId) throws IOException {
        send(new SetUuidCommand(clientId));
    }

    /**
     * Sends a {@link ConnectionResponse} to the client after a connection attempt.
     *
     * @param connectionResponse The {@link ConnectionResponse}, indicating current game state.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void connectionResponse(ConnectionResponse connectionResponse) throws IOException {
        send(new ConnectionResponseCommand(connectionResponse));
    }

    /**
     * Notifies the client that a new player has joined the game, along with an updated list of all players.
     *
     * @param newPlayer The nickname of the new player who joined.
     * @param players The updated list of all player nicknames in the game.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void playerJoined(String newPlayer, List<String> players) throws IOException {
        send(new PlayerJoinedCommand(newPlayer, players));
    }

    /**
     * Notifies the client of the new game leader.
     *
     * @param isNewLeader A boolean indicating if the current client is the new leader.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void newLeader(boolean isNewLeader) throws IOException {
        send(new NewLeaderCommand(isNewLeader));
    }

    /**
     * Sends the result of a dice roll to the client.
     *
     * @param diceResult The integer result of the dice roll.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void diceResult(int diceResult) throws IOException {
        send(new DiceResultCommand(diceResult));
    }

    /**
     * Notifies the client of the result of a game round in specific card effects.
     *
     * @param roundWon A boolean indicating whether the round was won.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void roundResult(boolean roundWon) throws IOException {
        send(new RoundResultCommand(roundWon));
    }

    /**
     * Notifies the client about a change in turn, including whether it's their turn, the turn type, and the current player.
     *
     * @param isMyTurn A boolean indicating if it is the current client's turn.
     * @param turnType An integer representing the type of turn.
     * @param currentPlayer The ID of the current player whose turn it is.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void turnChange(boolean isMyTurn, int turnType, int currentPlayer) throws IOException {
        send(new TurnChangeCommand(isMyTurn, turnType, currentPlayer));
    }

    /**
     * Sends a list of available game IDs to the client.
     *
     * @param gameList A list of integers, where each integer represents a game ID.
     * @throws IOException If an I/O error occurs during communication.
     */
    @Override
    public void showGameList(List<Integer> gameList) throws IOException {
        send(new ShowGameListCommand(gameList));
    }
}