package it.polimi.ingsw.is25am31.view;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This interface defines the methods for any User Interface (UI) in the application.
 * It provides methods for displaying various game elements, handling game phases, and managing
 * user interactions and notifications.
 */
public interface UiRenderer {
    /**
     * Starts and runs the UI.
     *
     * @param client The virtual view representing the client.
     * @param server The virtual server representing the server.
     * @param clientModel The client-side model containing the current state of the game for this client.
     */
    void runUi(VirtualView client, VirtualServer server, ClientModel clientModel);

    /**
     * Displays a newly drawn game component to the user.
     *
     * @param component The component that was drawn.
     */
    void showsDrawnComponent(Component component);

    /**
     * Displays a list of face-up components available in the game.
     *
     * @param componentList A list of components to be displayed.
     */
    void showFaceUpComponentList(List<Component> componentList);

    /**
     * Renders a ship on the UI.
     *
     * @param ship The ship to be drawn.
     * @throws Exception if an error occurs during drawing the ship.
     */
    void drawShip(Ship ship) throws Exception;

    /**
     * Shows the current positions of rockets for all players.
     *
     * @param players The list of players in the game.
     * @param level The current level of the game.
     */
    void showRocketsPosition(List<Player> players, int level);

    /**
     * Displays a specific game card to the user.
     *
     * @param card The card to be displayed.
     */
    void showCard(Card card);

    /**
     * Displays the flight phase information.
     *
     * @param ship The player's ship.
     * @param positions Updated positions of the player's rockets on the game board, received from the server.
     */
    void startFlightPhase(Ship ship, Map<Integer, Integer> positions) throws Exception;

    /**
     * Displays the crew placing phase information.
     *
     * @param ship The ship for which crew is being placed.
     * @param positions A map indicating possible positions for placing crew members.
     */
    void startCrewPlacingPhase(Ship ship, Map<Integer, Integer> positions);

    /**
     * Ends the assembling phase in the UI.
     */
    void endAssemblingPhase();

    /**
     * Starts and displays the assembling phase in the UI.
     */
    void startAssemblingPhase();

    /**
     * Displays the final ranking of players at the end of the game.
     *
     * @param ranking A map where keys are players and values are their final scores.
     */
    void showFinalRanking(Map<Player, Integer> ranking);

    /**
     * Notifies the user of a defeat, specifying the type of loss.
     *
     * @param loseType An integer representing the type of defeat.
     */
    void notifyDefeat(int loseType);

    /**
     * Prints a custom message related to a card when it's the current player's turn.
     *
     * @param turnType An integer representing the type of turn or card action.
     */
    void customCardPrintMyTurn(int turnType);

    /**
     * Prints a custom message related to a card when it's not the current player's turn.
     *
     * @param currentPlayer The nickname of the player whose turn it currently is.
     */
    void customCardPrintNotMyTurn(String currentPlayer);

    /**
     * Reports an exception to the user interface.
     *
     * @param e The exception to be reported.
     */
    void reportException(Exception e);

    /**
     * Handles and displays the response received after a connection attempt.
     *
     * @param response The connection response to be handled.
     */
    void handleConnectionResponse(ConnectionResponse response);

    /**
     * Displays the list of players currently in the game.
     *
     * @param players A list of player nicknames.
     */
    void showPlayersList(List<String> players);

    /**
     * Notifies the UI that a new player has joined the game.
     *
     * @param nickname The nickname of the player who joined.
     */
    void newPlayerJoined(String nickname);

    /**
     * Displays the result of a dice roll to the user.
     *
     * @param diceResult The integer result of the dice roll.
     */
    void showDiceResult(int diceResult);

    /**
     * Displays the result of a game round, indicating whether it was won or lost.
     *
     * @param roundWon A boolean indicating if the round was won (true) or lost (false).
     */
    void roundResult(boolean roundWon);

    /**
     * Notifies the UI if the current player has become the new leader.
     *
     * @param isNewLeader A boolean indicating if the current player is the new leader.
     */
    void newLeader(boolean isNewLeader);

    /**
     * Displays a general informational message to the user.
     *
     * @param message The informational message to display.
     */
    void onInfo(String message);

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     * @throws InterruptedException if the thread is interrupted while waiting for an error display.
     */
    void onError(String message) throws InterruptedException;

    /**
     * Prompts the user to set their nickname.
     */
    void setNickname();

    /**
     * Shows a countdown of seconds left, specifically for GUI updates.
     *
     * @param count The current hourglass number.
     * @param secondsLeft The total seconds left.
     */
    void showSecondsLeftForGui(int count, int secondsLeft);

    /**
     * Displays a sub-deck of cards to the user.
     *
     * @param subDeck A list of cards representing the sub-deck.
     */
    void showSubDeck(List<Card> subDeck);

    /**
     * Handles and processes an update message received from the server.
     *
     * @param msg The update message to be handled.
     */
    void handleUpdateMessage(UpdateMessage msg);

    /**
     * Prints the prompt symbol in the UI, indicating readiness for user input.
     *
     * @throws InterruptedException if the thread is interrupted while printing the prompt symbol.
     */
    void printPromptSymbol() throws InterruptedException;

    /**
     * Sets the ID for the client in the UI.
     *
     * @param clientId The UUID to be set.
     * @throws InterruptedException if the thread is interrupted while setting the UUID.
     */
    void setUUID(UUID clientId) throws InterruptedException;

    /**
     * Displays effect resolution information.
     *
     */
    void effectResolution();

    /**
     * Shows a countdown of seconds left.
     *
     * @param count The current hourglass number.
     * @param secondsLeft The total seconds left.
     */
    void showSecondsLeft(int count, int secondsLeft);

    /**
     * Displays a list of available games that the user can join.
     *
     * @param gameList A list of game IDs.
     */
    void showGameList(List<Integer> gameList);
}