package it.polimi.ingsw.is25am31.view;

import it.polimi.ingsw.is25am31.customerrors.PingErrorException;
import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Manages the client-side model, handling game state, player data, and
 * interactions with the UI and server.
 */
public class ClientModel {
    private int level;
    private UUID clientId;
    private Component currentComponent = null;
    private Ship myShip;
    private Card currentCard;
    private ClientState clientState;
    private List<Component> faceUpComponents;
    private List<Player> players;
    private String nickname;
    private boolean isViewingOpponentShip = false;
    private final UiRenderer renderer;
    private final VirtualServer server;
    private final VirtualView client;

    /**
     * Constructs a new ClientModel.
     * @param renderer The UI renderer responsible for displaying game information.
     * @param server The virtual server for network communication.
     * @param client The virtual view representing the client.
     */
    public ClientModel(UiRenderer renderer, VirtualServer server, VirtualView client) {
        clientState = ClientState.GAME_SELECTION;
        this.renderer = renderer;
        this.server = server;
        this.client = client;
    }

    /**
     * @param nickname The nickname to set.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return The client's nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * @param players The list of players.
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * @return The players in the game.
     */
    public List<Player> getPlayers(){
        return players;
    }

    /**
     * @return The list of face-up components.
     */
    public List<Component> getFaceUpComponents() {
        return faceUpComponents;
    }

    /**
     * @param faceUpComponents The list of face-up components.
     */
    public void setFaceUpComponents(List<Component> faceUpComponents) {
        this.faceUpComponents = faceUpComponents;
    }

    /**
     * @return The game level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return The {@link UUID} of the client.
     */
    public UUID getClientId() {
        return clientId;
    }

    /**
     * @param level The level to set.
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @param clientState The {@link ClientState} to set.
     */
    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
    }

    /**
     * @return The current {@link ClientState}.
     */
    public ClientState getClientState() {
        return clientState;
    }

    /**
     * @param currentCard The {@link Card} to set.
     */
    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    /**
     * @return The current {@link Card}.
     */
    public Card getCurrentCard() {
        return currentCard;
    }

    /**
     * @param currentComponent The component to set.
     */
    public void setCurrentComponent(Component currentComponent) {
        this.currentComponent = currentComponent;
    }

    /**
     * @return The current component.
     */
    public Component getCurrentComponent() {
        return currentComponent;
    }

    /**
     * @param level The level of the gameboard for the ship.
     */
    public void setMyShip(int level) {
        this.myShip = new Ship(new Gameboard(level));
    }

    /**
     * Sets the client's ship.
     * @param myShip The {@link Ship} to set.
     */
    public void setMyShip(Ship myShip) { this.myShip = myShip; }

    /**
     * Retrieves the client's ship.
     * @return The client's {@link Ship}.
     */
    public Ship getMyShip() {
        return myShip;
    }

    /**
     * Adds the current component to the client's ship at the specified coordinates.
     * Throws a RuntimeException if {@code currentComponent} is null.
     * @param x The x-coordinate on the ship's gameboard.
     * @param y The y-coordinate on the ship's gameboard.
     * @throws RuntimeException If {@code currentComponent} is null.
     */
    public void addComponentToMyShip(int x, int y) {
        if(currentComponent == null)
            throw new RuntimeException("Component is null");
        myShip.addComponent(x,y, currentComponent);
        currentComponent = null;
    }

    /**
     * Checks if the client is currently viewing an opponent's ship.
     * @return True if viewing an opponent's ship, false otherwise.
     */
    public boolean isViewingOpponentShip() {
        return isViewingOpponentShip;
    }

    /**
     * Sets whether the client is viewing an opponent's ship.
     * @param flag True to indicate viewing an opponent's ship, false otherwise.
     */
    public void setViewingOpponentShip(boolean flag) {
        isViewingOpponentShip = flag;
    }

    /**
     * Closes the client application after a short delay.
     */
    public static void closeClient() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException _) {}
            System.exit(0);
        }).start();
    }

    /**
     * Connects the client to the server and starts the UI.
     * @param client The {@link VirtualView} representing the client.
     * @throws Exception If an error occurs during connection or UI rendering.
     */
    public void connect(VirtualView client) throws Exception {
        renderer.runUi(client, server, this);
        server.connect(client);
    }

    /**
     * Periodically pings the server to check for connection status.
     * If the server connection is lost, the client closes.
     */
    private void pingServer() {
        new Thread(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            while (true) {
                if(clientState == ClientState.GAME_SELECTION) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                try {
                    Future<?> future = executor.submit(() -> {
                        try {
                            server.increment(clientId);
                            server.ping(clientId);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    future.get(2, TimeUnit.SECONDS);
                } catch (Exception e) {
                    System.out.println("\nGame finished, one or more players disconnected");
                    ClientModel.closeClient();
                    executor.shutdown();
                }
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Displays an update message received from the server.
     * @param msg The {@link UpdateMessage} to display.
     * @throws Exception If an error occurs during message handling by the renderer.
     */
    public void showUpdate(UpdateMessage msg) throws Exception {
        renderer.handleUpdateMessage(msg);
    }

    /**
     * Shows a drawn card to the client.
     * @param card The {@link Card} to show.
     * @throws Exception If an error occurs during rendering.
     */
    public void showCard(Card card) throws Exception{
        setCurrentCard(card);
        renderer.showCard(card);
    }

    /**
     * Shows a drawn component to the client and prints a prompt symbol.
     * @param component The {@link Component} to show.
     * @throws Exception If an error occurs during rendering.
     */
    public void showComponent(Component component) throws Exception{
        setCurrentComponent(component);
        renderer.showsDrawnComponent(component);
        renderer.printPromptSymbol();
    }

    /**
     * Locks the client's state, preventing further interaction.
     * @throws Exception If an error occurs during state transition.
     */
    public void lockClient() throws Exception {
        setClientState(ClientState.LOCKED);
    }

    /**
     * Initiates the nickname setting phase for the client.
     * @throws InterruptedException If the thread is interrupted during sleep.
     */
    public void setNickname() throws InterruptedException {
        setClientState(ClientState.NICKNAME_SETTING);
        renderer.setNickname();
        renderer.printPromptSymbol();
    }

    /**
     * Starts the assembling phase of the game.
     * @param players The list of players in the game.
     * @param level The current game level.
     * @throws Exception If an error occurs during setup or rendering.
     */
    public void startAssemblingPhase(List<Player> players, int level) throws Exception {
        setLevel(level);
        setMyShip(level);
        setClientState(ClientState.ASSEMBLE);
        setPlayers(players);
        renderer.startAssemblingPhase();
        renderer.printPromptSymbol();
    }

    /**
     * Starts the game by setting the client state to initial.
     */
    public void startGame(){
        setClientState(ClientState.INITIAL);
    }

    /**
     * Displays a ship to the client.
     * @param ship The {@link Ship} to display.
     * @throws InterruptedException If the thread is interrupted during rendering.
     */
    public void showShip(Ship ship) throws InterruptedException {
        try {
            renderer.drawShip(ship);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ClientState s = getClientState();
        if (s != ClientState.FLIGHT && s != ClientState.LOCKED && s != ClientState.CREW_PLACING)
            renderer.printPromptSymbol();
    }

    /**
     * Displays the list of face-up components to the client.
     * @param components The list of components to display.
     */
    public void showFaceUpComponentList(List<Component> components){
        setFaceUpComponents(components);
        renderer.showFaceUpComponentList(components);
    }

    /**
     * Displays the seconds left for GUI updates.
     * @param counter The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     * @throws Exception If an error occurs during rendering.
     */
    public void showSecondsLeftForGui (int counter, int secondsLeft) throws Exception{
        renderer.showSecondsLeftForGui(counter, secondsLeft);
    }

    /**
     * Displays the seconds left (for TUI).
     * @param count The current hourglass number.
     * @param secondsLeft The number of seconds remaining.
     * @throws Exception If an error occurs during rendering.
     */
    public void showSecondsLeft(int count, int secondsLeft) throws Exception{
        renderer.showSecondsLeft(count, secondsLeft);
    }

    /**
     * Finishes the final hourglass phase. If in ASSEMBLE state,
     * it ends the assembling phase and updates the client state.
     * @throws Exception If an error occurs during phase transition or server interaction.
     */
    public void finishFinalHourglass() throws Exception{
        if (getClientState() == ClientState.ASSEMBLE) {
            System.out.println("\nTime is over");
            renderer.endAssemblingPhase();
            if (getLevel() == 2) {
                renderer.printPromptSymbol();
                setClientState(ClientState.ENDED_ASSEMBLE);
            } else {
                setClientState(ClientState.LOCKED);
            }
            server.endAssemblePhase(clientId);
        }
    }

    /**
     * Updates the positions of players' rockets and displays them.
     * @param positions A map where keys are player IDs and values are their new positions.
     */
    public void updatePositions(Map<Integer, Integer> positions){
        for (Player p : getPlayers()) {
            if (!positions.containsKey(p.getId())) {
                removePlayer(p);
            }
        }
        for (Player p : getPlayers()) {
            p.getRocket().setPosition(positions.get(p.getId()));
        }
        renderer.showRocketsPosition(getPlayers(), getLevel());
    }

    /**
     * Starts the effect resolution phase.
     */
    public void effectResolution(){
        setClientState(ClientState.EFFECT_RESOLUTION);
        try {
            renderer.effectResolution();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the flight phase of the game.
     * @param ship The client's {@link Ship}.
     * @param positions A map of player IDs to their rocket positions.
     * @throws Exception If an error occurs during rendering or setup.
     */
    public void startFlightPhase(Ship ship, Map<Integer, Integer> positions) throws Exception {
        setMyShip(ship);
        if(getLevel() == 1){
            updatePositions(positions);
        }
        renderer.startFlightPhase(ship, positions);
    }

    /**
     * Initiates the crew placing phase.
     * @param ship The client's {@link Ship}.
     * @param positions A map of player IDs to their rocket positions.
     * @throws Exception If an error occurs during rendering or setup.
     */
    public void crewPlacingPhase(Ship ship, Map<Integer, Integer> positions) throws Exception{
        setClientState(ClientState.CREW_PLACING);
        renderer.onInfo("Your ship has been fixed according to game rules");
        updatePositions(positions);
        setMyShip(ship);

        renderer.drawShip(ship);
        renderer.startCrewPlacingPhase(ship, positions);
        renderer.printPromptSymbol();
    }

    /**
     * Displays the sub-deck of cards.
     * @param subDeck The list of cards in the sub-deck.
     * @throws Exception If an error occurs during rendering.
     */
    public void showSubDeck(List<Card> subDeck) throws Exception{
        renderer.showSubDeck(subDeck);
        renderer.printPromptSymbol();
    }

    /**
     * Displays the final ranking of players.
     * @param ranking A map where keys are {@link Player}s and values are their final scores.
     * @throws Exception If an error occurs during rendering.
     */
    public void showFinalRanking(Map<Player, Integer> ranking) throws Exception{
        renderer.showFinalRanking(ranking);
    }

    /**
     * Notifies the client of the defeat and sets the client state to SPECTATOR.
     * @param loseType An integer representing the type of defeat:
     * 1 - lost due to engines
     * 2 - lost due to crew
     * 3 - lost because lapped
     * 4 - abandonment
     */
    public void notifyDefeat(int loseType){
        renderer.notifyDefeat(loseType);
        setClientState(ClientState.SPECTATOR);
    }

    /**
     * Sets the game level and initializes the client's ship accordingly.
     * @param level The game level to set.
     */
    public void setGameLevel(int level){
        setLevel(level);
        setMyShip(getLevel());
    }

    /**
     * Sets the client's ID and starts the pinging process to the server.
     * @param clientId The {@link UUID} of the client.
     * @throws Exception If an error occurs during UUID setting or pinging.
     */
    public void setUUID(UUID clientId) throws Exception {
        this.clientId = clientId;
        pingServer();
        renderer.setUUID(clientId);
    }

    /**
     * Handles a connection response from the server. If the response is SET_NICKNAME,
     * the client state is updated.
     * @param response The {@link ConnectionResponse} from the server.
     */
    public void connectionResponse(ConnectionResponse response){
        renderer.handleConnectionResponse(response);
        if(response == ConnectionResponse.SET_NICKNAME)
            setClientState(ClientState.NICKNAME_SETTING);
    }

    /**
     * Notifies the client that a new player has joined and displays the updated player list.
     * @param newPlayer The nickname of the new player.
     * @param players The updated list of player nicknames.
     */
    public void playerJoined(String newPlayer, List<String> players){
        renderer.newPlayerJoined(newPlayer);
        renderer.showPlayersList(players);
    }

    /**
     * Notifies the client if they are the new leader and updates the client state.
     * @param isNewLeader True if the client is the new leader, false otherwise.
     * @throws Exception If an error occurs during rendering.
     */
    public void newLeader(boolean isNewLeader) throws Exception{
        renderer.newLeader(isNewLeader);
        setClientState(ClientState.FLIGHT);
        if(isNewLeader) {
            renderer.printPromptSymbol();
        }
    }

    /**
     * Displays the result of a dice roll and updates the client state.
     * @param diceResult The result of the dice roll.
     * @throws Exception If an error occurs during rendering.
     */
    public void diceResult(int diceResult) throws Exception{
        setClientState(ClientState.EFFECT_RESOLUTION);
        renderer.showDiceResult(diceResult);
        renderer.printPromptSymbol();
    }

    /**
     * Displays the result of a round (won or lost).
     * @param roundWon True if the round was won, false otherwise.
     * @throws Exception If an error occurs during rendering.
     */
    public void roundResult(boolean roundWon) throws Exception{
        renderer.roundResult(roundWon);
        if(!roundWon){
            renderer.printPromptSymbol();
        }
    }

    /**
     * Handles a turn change, indicating whether it's the client's turn and the turn type.
     * @param isMyTurn True if it's the client's turn, false otherwise.
     * @param turnType The type of turn.
     * @param currentPlayer The index of the current player in the players list.
     * @throws Exception If an error occurs during rendering.
     */
    public void turnChange(boolean isMyTurn, int turnType, int currentPlayer) throws Exception {
        if(isMyTurn){
            renderer.customCardPrintMyTurn(turnType);
            setClientState(ClientState.EFFECT_RESOLUTION);
            renderer.printPromptSymbol();
        } else {
            for(int i = 0; i < getPlayers().size(); i++){
                if (getPlayers().get(i).getId() != currentPlayer)
                    continue;
                renderer.customCardPrintNotMyTurn(getPlayers().get(i).getNickname());
            }
            setClientState(ClientState.LOCKED);
        }
    }

    /**
     * Reports an exception to the renderer. If the exception is a {@link PingErrorException},
     * it indicates a disconnection and closes the client.
     * @param e The exception to report.
     */
    public void reportException(Exception e) {
        renderer.reportException(e);
        try{
            throw e;
        } catch (PingErrorException _){
            System.out.println("\nGame finished, one or more players disconnected");
            closeClient();
        } catch(Exception _){}
    }

    /**
     * Displays the list of available games.
     * @param gameList The list of game IDs.
     * @throws Exception If an error occurs during rendering.
     */
    public void showGameList(List<Integer> gameList) throws Exception{
        renderer.showGameList(gameList);
    }
}