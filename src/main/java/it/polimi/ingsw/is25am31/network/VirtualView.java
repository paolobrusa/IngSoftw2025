package it.polimi.ingsw.is25am31.network;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This interface defines the methods for client-side display and input operations
 * that can be remotely invoked by the server. It represents a generic view accessible by the server,
 * allowing different underlying communication technologies (RMI, Socket) to implement it.
 * <p>
 * Methods declared in this interface are expected to throw {@link Exception} to signal
 * any issues that prevent the successful completion of the operation. These exceptions
 * can arise from low-level network communication failures or from application-level
 * logical errors encountered during the display or processing of the information on the client.
 */
public interface VirtualView extends Remote {
    /**
     * Displays an update message received from the server to the client's UI.
     * @param msg The {@link UpdateMessage} to display.
     */
    void showUpdate(UpdateMessage msg) throws Exception;

    /**
     * Displays a drawn card to the client. This occurs when a player draws a card
     * from a deck.
     * @param card The {@link Card} to display.
     */
    void showCard(Card card) throws Exception;

    /**
     * Displays a drawn component to the client.
     * @param component The {@link Component} object to display.
     */
    void showComponent(Component component) throws Exception;

    /**
     * Notifies the client that the game has started.
     */
    void startGame() throws Exception;

    /**
     * Locks the client's UI, preventing user input.
     */
    void lockClient() throws Exception;

    /**
     * Prompts the client to set their nickname.
     */
    void setNickname() throws Exception;

    /**
     * Initiates the assembling phase for the client, providing the initial list of players
     * and the game level.
     * @param players The list of {@link Player} objects currently in the game.
     * @param level The current game level.
     */
    void startAssemblingPhase(List<Player> players, int level) throws Exception;

    /**
     * Displays a ship to the client. This can be the client's own ship or an opponent's ship.
     * @param ship The {@link Ship} object to display.
     */
    void showShip(Ship ship) throws Exception;

    /**
     * Displays the current list of face-up components available on the gameboard to the client.
     * @param components The list of components that are currently face-up.
     */
    void showFaceUpComponentList(List<Component> components) throws Exception;

    /**
     * Updates the positions of players' rockets on the client's game board display.
     * @param positions A map where keys are player IDs and values are their updated rocket positions.
     */
    void updatePositions(Map<Integer, Integer> positions) throws Exception;

    /**
     * Starts the effect resolution phase on the client's side.
     */
    void effectResolution() throws Exception;

    /**
     * Starts the flight phase for the client, providing the client's updated ship and
     * the current rocket positions.
     * @param ship The client's {@link Ship}.
     * @param positions A map of player IDs to their rocket positions.
     */
    void startFlightPhase(Ship ship, Map<Integer, Integer> positions) throws Exception;

    /**
     * Displays a sub-deck of cards to the client.
     * @param subDeck The list of {@link Card} objects in the sub-deck.
     */
    void showSubDeck(List<Card> subDeck) throws Exception;

    /**
     * Performs a connection test.
     */
    void connectionTest() throws Exception;

    /**
     * Starts the crew placing phase for the client, providing the client's updated ship
     * and current rocket positions.
     * @param ship The client's {@link Ship} object.
     * @param positions A map of player IDs to their rocket positions.
     */
    void crewPlacingPhase(Ship ship, Map<Integer, Integer> positions) throws Exception;

    /**
     * Displays a countdown of seconds left.
     * @param count The current hourglass number.
     * @param secondsLeft The total number of seconds remaining.
     */
    void showSecondsLeft(int count, int secondsLeft) throws Exception;

    /**
     * Notifies the client that the final hourglass timer has finished.
     */
    void finishFinalHourglass() throws Exception;

    /**
     * Displays a countdown of seconds left specifically for GUI updates.
     * @param counter The current hourglass number.
     * @param secondsLeft The total seconds remaining.
     */
    void showSecondsLeftForGui(int counter, int secondsLeft) throws Exception;

    /**
     * Displays the final ranking of players at the end of the game.
     * @param ranking A map where keys are {@link Player} objects and values are their final scores.
     */
    void showFinalRanking(Map<Player, Integer> ranking) throws Exception;

    /**
     * Notifies the client of a defeat in the game, along with the reason.
     * @param loseType An integer indicating the type of defeat.
     */
    void notifyDefeat(int loseType) throws Exception;

    /**
     * Sets the current game level on the client's view.
     * @param level The game level to set.
     */
    void setGameLevel(int level) throws Exception;

    /**
     * Sets the unique identifier (UUID) for the client.
     * @param clientId The {@link UUID} to set for the client.
     */
    void setUUID(UUID clientId) throws Exception;

    /**
     * Handles a connection response from the server, indicating the outcome of a connection attempt
     * or a change in connection status.
     * @param connectionResponse The {@link ConnectionResponse} enum received from the server.
     */
    void connectionResponse(ConnectionResponse connectionResponse) throws Exception;

    /**
     * Notifies the client that a new player has joined the game and provides the updated list
     * of all current players.
     * @param newPlayer The nickname of the new player who joined.
     * @param players The updated list of all player nicknames in the game.
     */
    void playerJoined(String newPlayer, List<String> players) throws Exception;

    /**
     * Notifies the client if they have become the new leader of the game.
     * @param isNewLeader True if the client is the new leader, false otherwise.
     */
    void newLeader(boolean isNewLeader) throws Exception;

    /**
     * Displays the result of a dice roll to the client.
     * @param diceResult The integer result of the dice roll.
     */
    void diceResult(int diceResult) throws Exception;

    /**
     * Notifies the client whether the current round was won or lost.
     * @param roundWon True if the round was won, false if lost.
     */
    void roundResult(boolean roundWon) throws Exception;

    /**
     * Notifies the client about a turn change, indicating if it's their turn, the type of turn,
     * and the index of the current player.
     * @param isMyTurn True if it's the client's turn, false otherwise.
     * @param turnType An integer representing the specific type or phase of the turn.
     * @param currentPlayer The index of the current player in the game's player list.
     */
    void turnChange(boolean isMyTurn, int turnType, int currentPlayer) throws Exception;

    /**
     * Displays the list of available games that the client can join or observe.
     * @param gameList A list of integers representing the IDs of available games.
     */
    void showGameList(List<Integer> gameList) throws Exception;
}