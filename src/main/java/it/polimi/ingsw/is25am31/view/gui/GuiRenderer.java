package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import it.polimi.ingsw.is25am31.view.UiRenderer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Singleton implementation of the UiRenderer interface for JavaFX-based GUI rendering.
 * Serves as the bridge between the game logic and the JavaFX UI, handling all UI updates
 * and user interactions. Ensures thread-safe UI updates using Platform.runLater and
 * manages the various game phase transitions and visual updates.
 */
public class GuiRenderer implements UiRenderer {
    private static GuiRenderer instance;
    private Gui gui;
    private CardManagerController cardManagerController;
    private ClientModel clientModel;


    private GuiRenderer() {
    }

    /**
     * Gets the singleton instance of GuiRenderer.
     * Creates a new instance if one doesn't exist and launches the JavaFX application.
     *
     * @return the singleton instance of GuiRenderer
     */
    public static GuiRenderer getInstance() {
        if (instance == null) {
            instance = new GuiRenderer();
            new Thread(() -> {
                Application.launch(Gui.class);
            }).start();
        }
        return instance;
    }

    /**
     * Sets the GUI controller for this renderer.
     *
     * @param gui the GUI instance to set
     */
    public void setGuiController(Gui gui) {
        this.gui = gui;
    }

    /**
     * Initializes and runs the UI with the provided client, server, and model.
     *
     * @param client      the virtual view representing the client interface
     * @param server      the virtual server for network communication
     * @param clientModel the client model containing game state
     */
    @Override
    public void runUi(VirtualView client, VirtualServer server, ClientModel clientModel) {
        this.clientModel = clientModel;
        Gui.setClientModel(clientModel);
        Gui.setClient(client);
        Gui.setServer(server);
    }


    /**
     * Displays a drawn component in the selected tile view.
     *
     * @param component the component to display
     */
    @Override
    public void showsDrawnComponent(Component component) {
        Platform.runLater(() -> {
            gui.showComponentImageById(component.getId(), component);
        });
    }

    /**
     * Updates the face-up component list display.
     *
     * @param components the list of face-up components to display
     */
    @Override
    public void showFaceUpComponentList(List<Component> components) {
        Platform.runLater(() -> {
            if (gui != null) {
                gui.updateFaceUpList(components);
            }
        });
    }

    /**
     * Draws a ship on the appropriate view based on the current context.
     * Shows opponent's ship in a separate window or updates the player's own ship view.
     *
     * @param ship the ship to draw
     * @throws Exception if drawing fails
     */
    @Override
    public void drawShip(Ship ship) throws Exception {
        Platform.runLater(() -> {
            if (clientModel.isViewingOpponentShip()) {
                if (gui.getStage2Controller() != null) {
                    gui.getStage2Controller().showOpponentShip(ship);
                } else if (gui.getDashboard2() != null) {
                    gui.getDashboard2().showOpponentShip(ship);
                } else {
                    System.err.println("Nessun controller disponibile per mostrare la nave avversaria.");
                }
                clientModel.setViewingOpponentShip(false);
            } else {
                clientModel.setMyShip(ship);
                if (gui.getCrewSelectionController() != null) {
                    gui.getCrewSelectionController().showShip(ship);
                }
                if (gui.getStage2Controller() != null) {
                    try {
                        gui.getStage2Controller().setShip(ship);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (gui.getDashboard2() != null) {
                    gui.getDashboard2().showShip(ship);
                }
                if (gui.getDashboard() != null) {
                    gui.getDashboard().showShip(ship);
                }
            }
        });
    }

    /**
     * Shows the rockets' positions on the track.
     * Currently not implemented for GUI.
     *
     * @param players the list of players
     * @param level   the game level
     */
    @Override
    public void showRocketsPosition(List<Player> players, int level) {
    }

    /**
     * Displays a card in the card manager window.
     * Creates a new card manager window if one doesn't exist.
     *
     * @param card the card to display
     */
    @Override
    public void showCard(Card card) {
        Platform.runLater(() -> {
            try {
                if (gui.getCurrentCardController() != null) {
                    gui.getCurrentCardController().setClientModel(clientModel);
                    gui.getCurrentCardController().setCard(card);
                    gui.getCurrentCardController().setTurnMessage();
                    return;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/cardManager.fxml"));
                Parent root = loader.load();
                cardManagerController = loader.getController();
                cardManagerController.setClientModel(clientModel);
                cardManagerController.setCard(card);
                gui.setCurrentCardController(cardManagerController);
                Stage stage = new Stage();
                CloseStageManager.getCloseStageManager().addStage(stage);
                stage.setTitle("Card Manager");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Starts the crew placement phase by loading the appropriate scene.
     *
     * @param ship      the player's ship
     * @param positions the component positions map
     */
    @Override
    public void startCrewPlacingPhase(Ship ship, Map<Integer, Integer> positions) {
        Platform.runLater(() -> {
            try {
                gui.loadCrewPhase(ship, positions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the end of the assembling phase.
     * Currently not implemented for GUI.
     */
    @Override
    public void endAssemblingPhase() {
    }

    /**
     * Starts the assembling phase by loading the stage 2 scene.
     */
    @Override
    public void startAssemblingPhase() {
        Platform.runLater(() -> {
            try {
                gui.loadStage2Scene();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Shows the final ranking screen with player scores.
     * Closes any open card manager or dashboard windows before showing rankings.
     *
     * @param ranking the final ranking map with players and their scores
     */
    @Override
    public void showFinalRanking(Map<Player, Integer> ranking) {
        Platform.runLater(() -> {
            if (gui.getCurrentCardController() != null && gui.getCurrentCardController().getStage() != null) {
                gui.getCurrentCardController().getStage().close();
            }
            if (gui.getDashboard2() != null && gui.getDashboard2().getStage() != null) {
                gui.getDashboard2().getStage().close();
            }
            gui.loadFinalStage(ranking);
        });
    }

    /**
     * Notifies the player of their defeat and switches to spectator mode.
     *
     * @param loseType the reason code for defeat
     */
    @Override
    public void notifyDefeat(int loseType) {
        Platform.runLater(() -> {
            clientModel.setClientState(ClientState.SPECTATOR);

            if (gui.getDashboard2() != null) {
                gui.getDashboard2().showElimination(loseType);
            }

            if (gui.getDashboard() != null) {
                gui.getDashboard().showElimination(loseType);
            }

            if (gui.getCurrentCardController() != null) {
                gui.getCurrentCardController().enterSpectatorMode();
            }
        });
    }

    /**
     * Handles custom card print for the player's turn.
     *
     * @param turnType the type of turn
     */
    @Override
    public void customCardPrintMyTurn(int turnType) {
        newTurn();
    }

    /**
     * Handles custom card print when it's not the player's turn.
     *
     * @param currentPlayer the nickname of the current active player
     */
    @Override
    public void customCardPrintNotMyTurn(String currentPlayer) {
        newTurn();
    }

    private void newTurn() {
        if (gui.getCurrentCardController() != null) {
            Platform.runLater(() -> gui.getCurrentCardController().setTurnMessage());
        }

        Ship ship = clientModel.getMyShip();
        if (gui.getDashboard2() != null && ship != null) {
            Platform.runLater(() -> {
                gui.getDashboard2().showShip(ship);
                gui.getDashboard2().loadRockets();
                gui.getDashboard2().setLeader(clientModel.getClientState() == ClientState.FLIGHT);
            });
        } else if (gui.getDashboard() != null && ship != null) {
            Platform.runLater(() -> {
                gui.getDashboard().showShip(ship);
                gui.getDashboard().loadRockets();
                gui.getDashboard().setLeader(clientModel.getClientState() == ClientState.FLIGHT);
            });
        }

    }


    private Label promptLabel;

    /**
     * Sets the prompt label for displaying messages.
     *
     * @param promptLabel the label to set
     */
    public void setPromptLabel(Label promptLabel) {
        this.promptLabel = promptLabel;
    }

    private Object guiController;

    /**
     * Sets the GUI controller object.
     *
     * @param controller the controller to set
     */
    public void setGuiController(Object controller) {
        this.guiController = controller;
    }

    private Label errorLabel;

    /**
     * Sets the error label for displaying error messages.
     *
     * @param errorLabel the label to set
     */
    public void setErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;
    }

    private Label messageLabel;

    /**
     * Sets the message label for general messages.
     *
     * @param label the label to set
     */
    public void setMessageLabel(Label label) {
        this.messageLabel = label;
    }
    private boolean lastCrewPlacementFailed = false;

    /**
     * Sets whether the last crew placement attempt failed.
     *
     * @param failed true if placement failed, false otherwise
     */
    public void setLastCrewPlacementFailed(boolean failed) {
        this.lastCrewPlacementFailed = failed;
    }

    /**
     * Checks if the last crew placement attempt failed.
     *
     * @return true if the last placement failed, false otherwise
     */
    public boolean hasCrewPlacementFailed() {
        return lastCrewPlacementFailed;
    }


    /**
     * Reports an exception to the appropriate UI component.
     * Displays error messages in various labels depending on the current context.
     *
     * @param e the exception to report
     */
    public void reportException(Exception e) {
        String message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
        Platform.runLater(() -> {
            if (cardManagerController != null) {
                cardManagerController.showErrorMessage(message);
            }
            if (promptLabel != null) {
                promptLabel.setText(message);
                gui.unlockNicknameField();
            }
            if (errorLabel != null) {
                errorLabel.setText(message);
                if (guiController instanceof WaitingRoomController controller) {
                    controller.unlockPositionField();
                }
            }
            if (messageLabel != null) {
                messageLabel.setText(message);
                lastCrewPlacementFailed = true;
            }
            if (gui.getStage2Controller() != null) {
                if (message.equals("No face down components left")) {
                    gui.getStage2Controller().finishComponentError(message);
                }
            }
        });
    }


    /**
     * Handles the connection response from the server.
     *
     * @param response the connection response to handle
     */
    @Override
    public void handleConnectionResponse(ConnectionResponse response) {
        Platform.runLater(() -> gui.handleConnectionEstablished(response));
    }

    /**
     * Shows the list of players in the game.
     * Currently not implemented for GUI.
     *
     * @param players the list of player nicknames
     */
    @Override
    public void showPlayersList(List<String> players) {

    }

    /**
     * Notifies when a new player joins the game.
     * Currently not implemented for GUI.
     *
     * @param nickname the nickname of the new player
     */
    @Override
    public void newPlayerJoined(String nickname) {

    }

    /**
     * Shows the result of a dice roll.
     *
     * @param diceResult the dice roll result (1-6)
     */
    @Override
    public void showDiceResult(int diceResult) {
        Platform.runLater(() -> {
            if (gui.getCurrentCardController() != null) {
                gui.getCurrentCardController().showDiceResult(diceResult);
            } else {
                System.out.println("🎲 Dice result: " + diceResult);
            }
        });
    }

    /**
     * Shows the result of a round (won or lost).
     *
     * @param roundWon true if the player won the round, false if lost
     */
    @Override
    public void roundResult(boolean roundWon) {
        Platform.runLater(() -> {
            if (gui.getCurrentCardController() != null) {
                if (!roundWon) {
                    gui.getCurrentCardController().showInfoMessage("❗You lost this phase, you can now apply the relative effect as explained in the card");
                } else {
                    gui.getCurrentCardController().showInfoMessage("✅ You didn't lose this phase. Wait for the player that lost to finish applying the relative effect.");
                }
            } else {
                System.out.println(!roundWon
                        ? "You lost this phase, you can now apply the relative effect as explained in the card"
                        : "You didn't lose this phase. Wait for the player that lost to finish applying the relative effect");
            }
        });
    }

    /**
     * Updates the UI when leadership changes.
     * Loads or updates the appropriate dashboard based on game level.
     *
     * @param isNewLeader true if the player is the new leader, false otherwise
     */
    @Override
    public void newLeader(boolean isNewLeader) {
        int level = clientModel.getLevel();
        Platform.runLater(() -> {
            if (level == 2) {
                if (gui.getDashboard2() == null) {
                    try {
                        gui.loadDashboard2Scene(isNewLeader, clientModel.getMyShip());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        gui.updateDashboard2Scene(isNewLeader, clientModel.getMyShip());
                        gui.getCurrentCardController().showInfoMessage("");
                        gui.getCurrentCardController().showDiceResult(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    gui.getDashboard2().loadRockets();
                }
                clientModel.setClientState(isNewLeader ? ClientState.FLIGHT : ClientState.LOCKED);
            }
            else {
                if (gui.getDashboard() == null) {
                    try {
                        CloseStageManager.getCloseStageManager().closeAllStages();
                        gui.loadDashboardScene(isNewLeader, clientModel.getMyShip());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        gui.updateDashboardScene(isNewLeader, clientModel.getMyShip());
                        gui.getCurrentCardController().showInfoMessage("");
                        gui.getCurrentCardController().showDiceResult(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    gui.getDashboard().loadRockets();
                }
                clientModel.setClientState(isNewLeader ? ClientState.FLIGHT : ClientState.LOCKED);
            }
        });
    }

    /**
     * Displays an informational message in the card manager.
     *
     * @param message the information message to display
     */
    @Override
    public void onInfo(String message) {
        if(cardManagerController != null)
            cardManagerController.onInfo(message);
    }

    /**
     * Displays an error message in the card manager.
     *
     * @param message the error message to display
     */
    @Override
    public void onError(String message) {
        cardManagerController.onError(message);
    }

    /**
     * Prompts the player to set their nickname.
     */
    @Override
    public void setNickname() {
        Platform.runLater(() -> gui.showNicknameInput());
    }

    /**
     * Shows the countdown timer in the GUI.
     *
     * @param count the current count
     * @param secondsLeft the seconds remaining
     */
    @Override
    public void showSecondsLeftForGui(int count, int secondsLeft) {
        Platform.runLater(() -> {
            gui.updateTimerStage(count, secondsLeft);
        });
    }

    /**
     * Shows the sub-deck of cards.
     *
     * @param subDeck the list of cards in the sub-deck
     */
    @Override
    public void showSubDeck(List<Card> subDeck) {
        Platform.runLater(() -> {
            if (gui != null) {
                gui.updateSubDeck(subDeck);
            }
        });
    }

    /**
     * Handles update messages from the server.
     *
     * @param msg the update message to handle
     */
    @Override
    public void handleUpdateMessage(UpdateMessage msg) {
        switch (msg) {
        }
    }

    /**
     * Prints the prompt symbol.
     * Not implemented for GUI.
     *
     * @throws InterruptedException if interrupted
     */
    @Override
    public void printPromptSymbol() throws InterruptedException {}

    /**
     * Sets the client UUID.
     * Not implemented for GUI.
     *
     * @param clientId the client UUID
     */
    @Override
    public void setUUID(UUID clientId) {}

    /**
     * Handles effect resolution phase by updating the turn message.
     */
    public void effectResolution() {
        Platform.runLater(() -> {
            gui.getCurrentCardController().setTurnMessage();
        });

    }

    /**
     * Shows the seconds left for a timer.
     * Not implemented for GUI.
     *
     * @param count the current count
     * @param secondsLeft the seconds remaining
     */
    @Override
    public void showSecondsLeft(int count, int secondsLeft) {

    }

    /**
     * Starts the flight phase by closing the crew selection window and updating positions.
     *
     * @param ship the player's ship
     * @param positions the rocket positions for level 1 games
     */
    @Override
    public void startFlightPhase(Ship ship, Map<Integer, Integer> positions){
        Platform.runLater(() -> {
            if (gui.getCrewSelectionController() != null) {
                Stage crewStage = gui.getCrewSelectionController().getStage();
                if (crewStage != null) {
                    crewStage.close();
                }
            }
            if (clientModel.getLevel() == 1) {
                if (positions != null) {
                    for (Player p : clientModel.getPlayers()) {
                        if (positions.containsKey(p.getId())) {
                            p.getRocket().setPosition(positions.get(p.getId()));
                        }
                    }
                }
            }
        });
    }


    /**
     * Shows the list of available games in the game selector.
     *
     * @param gameList the list of game IDs
     */
    @Override
    public void showGameList(List<Integer> gameList) {
        List<String> games = gameList.stream()
                .map(id -> "Game " + id)
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            if (gui.getGameSelectorController() != null) {
                gui.getGameSelectorController().setGames(games);
            } else {
                System.err.println("GameSelectorController è null, lista non aggiornata.");
            }
        });
    }
}
