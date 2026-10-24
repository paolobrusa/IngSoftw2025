package it.polimi.ingsw.is25am31.network.rmi.client;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.view.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * The {@code RmiClient} class represents the client-side implementation for RMI
 * communication in the application. It extends {@link UnicastRemoteObject} to enable remote object
 * functionality.
 * This class acts as a listener for server-side calls, receiving updates and
 * interactions from the RMI server. It serves as a bridge between the RMI server
 * and the client's local model ({@link ClientModel}), forwarding server-side calls to the
 * appropriate client-side logic for processing and UI rendering.
 */
public class RmiClient extends UnicastRemoteObject implements VirtualView {
    ClientModel clientModel;

    /**
     * Constructs a new {@code RmiClient} instance.
     *
     * @param server The {@link VirtualServer} remote object to communicate with.
     * @param renderer The {@link UiRenderer} responsible for rendering the user interface.
     * @throws RemoteException If a remote communication error occurs.
     */
    public RmiClient(VirtualServer server, UiRenderer renderer) throws RemoteException{
        super();
        clientModel = new ClientModel(renderer, server, this);
    }

    /**
     * Establishes a connection to the RMI server.
     * This static method looks up the remote server object in the RMI registry,
     * creates an {@code RmiClient} instance, and initiates the client's execution.
     *
     * @param serverIp The IP address of the RMI server.
     * @param renderer The {@link UiRenderer} to be used by the client.
     */
    public static void connect(String serverIp, UiRenderer renderer) throws Exception {
        final String serverName = "RmiServer";
        System.setProperty("java.rmi.client.hostname", serverIp);
        Registry registry = LocateRegistry.getRegistry(serverIp, 1234);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        RmiClient client = new RmiClient(server, renderer);
        client.run();
    }

    /**
     * Starts the client's main execution loop.
     * This method initiates the connection process within the {@link ClientModel}.
     *
     */
    private void run() throws Exception {
        clientModel.connect(this);
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
     * @param msg The {@link UpdateMessage} containing the type and payload of the update.
     */
    @Override
    public void showUpdate(UpdateMessage msg) throws Exception {
        clientModel.showUpdate(msg);
    }

    /**
     * Displays a given{@link Card} to the client.
     * The card display logic is delegated to the {@link ClientModel}.
     *
     * @param card The {@link Card} to be displayed.
     */
    @Override
    public void showCard(Card card) throws Exception{
        clientModel.showCard(card);
    }

    /**
     * Displays a given{@link Component} to the client.
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