package it.polimi.ingsw.is25am31.network.socket.client;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.network.socket.serverClientMessages.ServerClientCommand;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import it.polimi.ingsw.is25am31.view.UiRenderer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code SocketClient} class represents the client-side implementation for Socket
 * communication in the application.
 * This class acts as a bridge between the Socket server and the client's local model ({@link ClientModel}),
 * forwarding server-side calls to the appropriate client-side logic.
 */
public class SocketClient implements VirtualView {
    private final ObjectInputStream in;
    private final ClientModel clientModel;

    /**
     * Constructs a new instance of the SocketClient.
     *
     * @param input The {@link ObjectInputStream} used to receive data from the server.
     * @param output The {@link ObjectOutputStream} used to send data to the server.
     * @param renderer The {@link UiRenderer} responsible for rendering the client user interface.
     * @throws IOException If an I/O error occurs while initializing the client.
     */
    public SocketClient(ObjectInputStream input, ObjectOutputStream output, UiRenderer renderer) throws IOException {
        this.in = input;
        this.clientModel = new ClientModel(renderer, new SocketServerHandler(output), this);
    }

    /**
     * Executes the main process of the {@link SocketClient}.
     *
     * This method runs the client-side loop for receiving and processing commands
     * from the server. It first establishes the connection with the server and
     * listens for incoming {@link ServerClientCommand} objects. Each command received
     * is executed using the {@link ServerClientCommand#execute} method.
     *
     * If an exception occurs during the execution of a command, it is reported
     * using the {@link ClientModel#reportException(Exception)} method.
     *
     * @throws Exception If an error occurs during the connection, reading from the
     * input stream, or processing commands.
     */
    public void run() throws Exception {
        ServerClientCommand command;
        clientModel.connect(this);
        try {
            while ((command = (ServerClientCommand) in.readObject()) != null) {
                try {
                    command.execute(this);
                } catch (Exception e) {
                    clientModel.reportException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("\nSocket closed, game finished");
            ClientModel.closeClient();
        }
    }

    /**
     * Establishes a connection to the server and starts the SocketClient.
     *
     * @param host The hostname or IP address of the server to connect to.
     * @param port The port number on which the server is listening.
     * @param renderer The {@link UiRenderer} responsible for rendering the client user interface.
     * @throws Exception If an error occurs during the connection setup or while starting the client.
     */
    public static void start(String host, String port, UiRenderer renderer) throws Exception {

        Socket serverSocket = new Socket(host, Integer.parseInt(port));

        ObjectOutputStream socketTx = new ObjectOutputStream(serverSocket.getOutputStream());
        socketTx.flush();
        ObjectInputStream socketRx = new ObjectInputStream(serverSocket.getInputStream());

        new SocketClient(socketRx, socketTx, renderer).run();
    }


    /**
     * A method to test the connection.
     *
     */
    @Override
    public void connectionTest() throws Exception {}

    /**
     * Handles update messages received from the server.
     * These messages represent "simpler" actions or state changes that don't
     * require full method calls. The update is forwarded to the {@link ClientModel}.
     *
     * @param msg The {@link UpdateMessage} containing the type of the update.
     */
    @Override
    public void showUpdate(UpdateMessage msg) throws Exception {
        clientModel.showUpdate(msg);
    }

    /**
     * Displays a given {@link Card} to the client.
     * The card display logic is delegated to the {@link ClientModel}.
     *
     * @param card The {@link Card} to be displayed.
     */
    @Override
    public void showCard(Card card) throws Exception{
        clientModel.showCard(card);
    }

    /**
     * Displays a given {@link Component} to the client.
     * The component display logic is delegated to the {@link ClientModel}.
     *
     * @param component The {@link Component} to be displayed.
     */
    @Override
    public void showComponent(Component component) throws Exception{
        clientModel.showComponent(component);
    }

    /**
     * Locks the client's interaction.
     *
     */
    @Override
    public void lockClient() throws Exception {
        clientModel.lockClient();
    }

    /**
     * Prompts the client to set their nickname.
     * The nickname setting logic is delegated to the {@link ClientModel}.
     *
     */
    @Override
    public void setNickname() throws InterruptedException {
        clientModel.setNickname();
    }

    /**
     * Starts the assembling phase of the game.
     * This method provides the list of players and the current game level to the client model
     * to begin the assembling phase.
     *
     * @param players The list of {@link Player}s in the game.
     * @param level The current game level.
     */
    @Override
    public void startAssemblingPhase(List<Player> players, int level) throws Exception {
        clientModel.startAssemblingPhase(players, level);
    }

    /**
     * Notifies the client that the game has started.
     */
    @Override
    public void startGame(){
        clientModel.startGame();
    }

    /**
     * Displays his own or another player's {@link Ship} to the client.
     *
     * @param ship The {@link Ship} to be displayed.
     */
    @Override
    public void showShip(Ship ship) throws InterruptedException {
        clientModel.showShip(ship);
    }

    /**
     * Displays a list of face-up {@link Component}s to the client.
     *
     * @param components The list of face-up {@link Component}s.
     */
    @Override
    public void showFaceUpComponentList(List<Component> components){
        clientModel.showFaceUpComponentList(components);
    }

    /**
     * Displays the countdown for the hourglass timer (GUI).
     *
     * @param counter The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     */
    @Override
    public void showSecondsLeftForGui (int counter, int secondsLeft) throws Exception{
        clientModel.showSecondsLeftForGui(counter, secondsLeft);
    }

    /**
     * Displays the countdown for the hourglass timer (TUI).
     *
     * @param count The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     */
    @Override
    public void showSecondsLeft(int count, int secondsLeft) throws Exception{
        clientModel.showSecondsLeft(count, secondsLeft);
    }

    /**
     * Notifies the client that the final hourglass has finished.
     *
     */
    @Override
    public void finishFinalHourglass() throws Exception{
        clientModel.finishFinalHourglass();
    }

    /**
     * Updates the positions of players on the game board.
     *
     * @param positions A map of integer keys to integer values representing updated positions.
     */
    @Override
    public void updatePositions(Map<Integer, Integer> positions){
        clientModel.updatePositions(positions);
    }

    /**
     * Starts the effect resolution phase.
     *
     */
    @Override
    public void effectResolution(){
        clientModel.effectResolution();
    }

    /**
     * Starts the flight phase client-side.
     *
     * @param ship The player's {@link Ship}.
     * @param positions A map with player ids as keys and their respective positions as values.
     */
    @Override
    public void startFlightPhase(Ship ship, Map<Integer, Integer> positions) throws Exception {
        clientModel.startFlightPhase(ship, positions);
    }

    /**
     * Initiates the crew placing phase for a ship.
     *
     * @param ship      The {@link Ship} for which crew is being placed.
     * @param positions A map of integer keys to integer values representing updated positions during crew placement.
     */
    @Override
    public void crewPlacingPhase(Ship ship, Map<Integer, Integer> positions) throws Exception{
        clientModel.crewPlacingPhase(ship, positions);
    }

    /**
     * Displays a sub-deck of {@link Card}s to the client.
     *
     * @param subDeck The list of {@link Card}s in the sub-deck.
     */
    @Override
    public void showSubDeck(List<Card> subDeck) throws Exception{
        clientModel.showSubDeck(subDeck);
    }

    /**
     * Displays the final ranking of players at the end of the game.
     *
     * @param ranking A map where keys are {@link Player}s and values are their final scores.
     */
    @Override
    public void showFinalRanking(Map<Player, Integer> ranking) throws Exception{
        clientModel.showFinalRanking(ranking);
    }

    /**
     * Notifies the client of his defeat and the reason.
     *
     * @param loseType An integer representing the type of defeat.
     */
    @Override
    public void notifyDefeat(int loseType){
        clientModel.notifyDefeat(loseType);
    }

    /**
     * Sets the game level on the client side.
     *
     * @param level The current game level.
     */
    @Override
    public void setGameLevel(int level){
        clientModel.setGameLevel(level);
    }

    /**
     * Sets the unique identifier (UUID) for this client.
     *
     * @param clientId The {@link UUID} assigned to this client.
     */
    @Override
    public void setUUID(UUID clientId) throws Exception {
        clientModel.setUUID(clientId);
    }

    /**
     * Handles the response received after a connection attempt.
     *
     * @param response The {@link ConnectionResponse} indicating the current game state.
     */
    @Override
    public void connectionResponse(ConnectionResponse response) throws Exception{
        clientModel.connectionResponse(response);
    }

    /**
     * Notifies the client when a new player has joined the game.
     *
     * @param newPlayer The nickname of the new player who joined.
     * @param players   The updated list of all player nicknames in the game.
     */
    @Override
    public void playerJoined(String newPlayer, List<String> players) throws Exception{
        clientModel.playerJoined(newPlayer, players);
    }

    /**
     * Notifies the client if they have become the new leader of the game.
     *
     * @param isNewLeader A boolean indicating if the current client is the new leader.
     */
    @Override
    public void newLeader(boolean isNewLeader) throws Exception{
        clientModel.newLeader(isNewLeader);
    }

    /**
     * Displays the result of a dice roll to the client.
     *
     * @param diceResult The integer result of the dice roll.
     */
    @Override
    public void diceResult(int diceResult) throws Exception{
        clientModel.diceResult(diceResult);
    }

    /**
     * Notifies the client of the result of a game round during effect application of certain cards.
     *
     * @param roundWon A boolean indicating whether the round was won.
     */
    @Override
    public void roundResult(boolean roundWon) throws Exception{
        clientModel.roundResult(roundWon);
    }

    /**
     * Notifies the client about a change in turn.
     *
     * @param isMyTurn     A boolean indicating if it is the current client's turn.
     * @param turnType     An integer representing the type of turn (e.g., specific phase).
     * @param currentPlayer The ID of the current player whose turn it is.
     */
    @Override
    public void turnChange(boolean isMyTurn, int turnType, int currentPlayer) throws Exception {
        clientModel.turnChange(isMyTurn, turnType, currentPlayer);
    }

    /**
     * Displays a list of available games to the client.
     *
     * @param gameList A list of integers, where each integer represents a game ID.
     */
    @Override
    public void showGameList(List<Integer> gameList) throws Exception{
        clientModel.showGameList(gameList);
    }
}