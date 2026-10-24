package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.controller.ControllerManager;
import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.ConnectionResponse;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;
import it.polimi.ingsw.is25am31.view.ClientModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Main JavaFX application class that serves as the central controller for the GUI.
 * Manages the lifecycle of different game screens, coordinates between various controllers,
 * and handles the flow of game states from initial connection through final results.
 * Implements the Application interface to initialize the JavaFX runtime.
 */
public class Gui extends Application {

    private static VirtualServer server;
    private static VirtualView client;
    private static ClientModel clientModel;
    private Stage2Controller stage2Controller;
    private WaitingRoomController waitingRoomController;

    @FXML private Button drawCompButton;
    @FXML private Button okButton;
    @FXML private Button endBuildButton;

    @FXML private Label textPrompt;
    @FXML private TextField nicknameField;
    @FXML private ChoiceBox<Integer> playerNumberField;
    @FXML private ChoiceBox<Integer> levelField;

    private SelectedTileController selectedTileController;

    /**
     * Sets the Stage2Controller for this GUI instance.
     *
     * @param controller the Stage2Controller to set
     */
    public void setStage2Controller(Stage2Controller controller) {
        this.stage2Controller = controller;
    }

    /**
     * Sets the WaitingRoomController for this GUI instance.
     *
     * @param controller the WaitingRoomController to set
     */
    public void setWaitingRoomController(WaitingRoomController controller) {
        this.waitingRoomController = controller;
    }

    /**
     * Gets the WaitingRoomController instance.
     *
     * @return the current WaitingRoomController
     */
    public WaitingRoomController getWaitingRoomController() {
        return waitingRoomController;
    }

    /**
     * Sets the static VirtualServer instance.
     *
     * @param server the VirtualServer to set
     */
    public static void setServer(VirtualServer server) {
        Gui.server = server;
    }

    /**
     * Gets the static VirtualServer instance.
     *
     * @return the current VirtualServer
     */
    public static VirtualServer server() {
        return Gui.server;
    }

    /**
     * Sets the static ClientModel instance.
     *
     * @param client the ClientModel to set
     */
    public static void setClientModel(ClientModel client) {
        Gui.clientModel = client;
    }

    /**
     * Sets the static VirtualView client instance.
     *
     * @param client the VirtualView to set
     */
    public static void setClient(VirtualView client) {
        Gui.client = client;
    }

    /**
     * Starts the JavaFX application by loading the game selector view.
     *
     * @param stage the primary stage for the application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("/it/polimi/ingsw/is25am31/gameSelector.fxml"));
        Parent root = loader.load();

        GameSelectorController controller = loader.getController();
        controller.setClientAndServer(client, server, clientModel.getClientId());
        setGameSelectorController(controller);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Game Selection");
        stage.show();
        CloseStageManager.getCloseStageManager().addStage(stage);
        controller.setClient(client);
        GuiRenderer.getInstance().setGuiController(this);
    }

    /**
     * Handles the connection response from the server and updates the UI accordingly.
     *
     * @param response the ConnectionResponse indicating the connection status
     */
    public void handleConnectionEstablished(ConnectionResponse response) {
        switch (response) {
            case FIRST_PLAYER -> showFirstPlayerSetup();
            case WAIT_CREATION -> showWaitingMessage("Waiting for the first player to create the game...");
            case SET_NICKNAME -> showNicknameInput();
            case IN_PROGRESS -> showWaitingMessage("Game already started. You cannot join.");
        }
    }

    /**
     * Shows the first player setup UI with all fields enabled.
     * Allows setting player count, game level, and nickname.
     */
    public void showFirstPlayerSetup() {
        nicknameField.setDisable(false);
        playerNumberField.setDisable(false);
        playerNumberField.getItems().addAll(2, 3, 4);
        levelField.setDisable(false);
        levelField.getItems().addAll(1, 2);
        okButton.setDisable(false);
        textPrompt.setText("You are the first player. Set the number of players, level and nickname.");
    }

    /**
     * Shows a waiting message and disables all input fields.
     *
     * @param message the waiting message to display
     */
    public void showWaitingMessage(String message) {
        nicknameField.setDisable(true);
        playerNumberField.setDisable(true);
        levelField.setDisable(true);
        okButton.setDisable(true);
        textPrompt.setText(message);
    }

    /**
     * Shows the nickname input field while disabling other fields.
     * Checks if already waiting for other players to prevent re-enabling.
     */
    public void showNicknameInput() {
        if (!textPrompt.getText().equals("Waiting for other players...")) {
            textPrompt.setText("Enter your nickname");
            nicknameField.setDisable(false);
        } else {
            okButton.setDisable(true);
            playerNumberField.setDisable(true);
            levelField.setDisable(true);
            return;
        }
        playerNumberField.setDisable(true);
        levelField.setDisable(true);
        okButton.setDisable(false);
    }

    /**
     * Gets the text prompt label.
     *
     * @return the text prompt label
     */
    public Label getTextPrompt() {
        return textPrompt;
    }

    private GuiRenderer guiRenderer;

    /**
     * Sets the GUI renderer for this instance.
     *
     * @param guiRenderer the GuiRenderer to set
     */
    public void setGuiRenderer(GuiRenderer guiRenderer) {
        this.guiRenderer = guiRenderer;
    }

    /**
     * Unlocks the nickname field for input using Platform.runLater for thread safety.
     */
    public void unlockNicknameField() {
        Platform.runLater(() -> nicknameField.setDisable(false));
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        try {
            String nickname = nicknameField.getText();
            nicknameField.setDisable(true);
            textPrompt.setText("Waiting for other players...");
            clientModel.setNickname(nickname);
            if (!playerNumberField.isDisable() && !levelField.isDisable()) {
                int players = playerNumberField.getValue();
                int level = levelField.getValue();
                server.setMaxPlayers(clientModel.getClientId(), players);
                server.setGameLevel(clientModel.getClientId(), level);
            }
            server.setNickname(nickname, clientModel.getClientId());
        } catch (Exception e) {
            guiRenderer.reportException(e);
        }
    }


    private Stage selectedTileStage;

    /**
     * Loads the Stage 2 scene (ship assembly) and selected tile view.
     *
     * @throws IOException if FXML files cannot be loaded
     */
    @FXML
    public void loadStage2Scene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource("/it/polimi/ingsw/is25am31/stage2.fxml"));
        Parent root = fxmlLoader.load();
        Stage2Controller stage2Controller = fxmlLoader.getController();
        this.setStage2Controller(stage2Controller);

        stage2Controller.setServerAndClient(server, clientModel);
        int level = clientModel.getLevel();
        stage2Controller.setLevel(level);

        try {
            server.showShip(clientModel.getClientId(), clientModel.getNickname());
        } catch (Exception e) {
            System.err.println("Errore durante showShip: " + e.getMessage());
            e.printStackTrace();
        }

        Stage stage = new Stage();
        CloseStageManager.getCloseStageManager().addStage(stage);
        stage.setTitle("Ship Assembly");
        stage.setScene(new Scene(root));
        stage.show();
        this.stage2Stage = stage;
        stage2Controller.setGui(this);
        stage2Controller.updateShipView();
        Stage oldStage = (Stage) textPrompt.getScene().getWindow();
        oldStage.close();
        FXMLLoader fxmlLoader2 = new FXMLLoader(Gui.class.getResource("/it/polimi/ingsw/is25am31/selectedTile.fxml"));
        Parent root2 = fxmlLoader2.load();
        SelectedTileController tileController = fxmlLoader2.getController();
        tileController.setStage2Controller(stage2Controller);
        tileController.setGui(this);
        selectedTileController = tileController;
        stage2Controller.setSelectedTileController(tileController);
        selectedTileStage = new Stage();
        CloseStageManager.getCloseStageManager().addStage(selectedTileStage);
        selectedTileStage.setTitle("Selected Tile");
        selectedTileStage.setScene(new Scene(root2));
        selectedTileStage.initOwner(stage);
        selectedTileStage.setX(stage.getX() + 1000);
        selectedTileStage.show();
    }

    /**
     * Shows a component image in the selected tile view.
     *
     * @param id the component ID
     * @param component the component to display
     */
    public void showComponentImageById(int id, Component component) {
        if (selectedTileController != null) {
            selectedTileController.showComponentImage(id, component);
            if (selectedTileStage != null && !selectedTileStage.isShowing()) {
                selectedTileStage.show();
            } else if (selectedTileStage != null) {
                selectedTileStage.toFront();
            }
        } else {
            System.err.println("selectedTileController è null");
        }
    }

    /**
     * Gets the selected tile stage.
     *
     * @return the selected tile stage
     */
    public Stage getSelectedTileStage() {
        return selectedTileStage;
    }

    /**
     * Sets the selected tile stage.
     *
     * @param stage the stage to set
     */
    public void setSelectedTileStage(Stage stage) {
        this.selectedTileStage = stage;
    }



    /**
     * Gets the VirtualServer instance.
     *
     * @return the current VirtualServer
     */
    public VirtualServer getServer() {
        return server;
    }

    /**
     * Gets the ClientModel instance.
     *
     * @return the current ClientModel
     */
    public ClientModel getClient() {
        return clientModel;
    }

    /**
     * Updates the face-up component list in Stage2Controller.
     *
     * @param components the list of components to display
     */
    public void updateFaceUpList(List<Component> components) {
        if (stage2Controller != null) {
            //System.out.println("updateFaceUpList chiamata con " + components.size() + " elementi");
            stage2Controller.updateFaceUpComponentList(components);
        } else {
            System.err.println("stage2Controller è null");
        }
    }

    /**
     * Updates the timer display in the appropriate controller.
     *
     * @param counter the current count
     * @param seconds the total seconds
     */
    public void updateTimerStage(int counter, int seconds) {
        if (waitingRoomController != null) {
            waitingRoomController.updateTimer(counter, seconds);
        }
        else if (stage2Controller != null) {
            stage2Controller.updateTimer(counter, seconds);
        }
    }




    @FXML
    private void openCardManager(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource("/it/polimi/ingsw/is25am31/cardManager.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            CloseStageManager.getCloseStageManager().addStage(stage);
            stage.setTitle("Gestione Carte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage stage2Stage;

    /**
     * Sets the Stage2 stage.
     *
     * @param stage the stage to set
     */
    public void setStage2Stage(Stage stage) {
        this.stage2Stage = stage;
    }

    /**
     * Gets the Stage2 stage.
     *
     * @return the Stage2 stage
     */
    public Stage getStage2Stage() {
        return stage2Stage;
    }

    private Stage waitingRoomStage;

    /**
     * Sets the waiting room stage.
     *
     * @param stage the stage to set
     */
    public void setWaitingRoomStage(Stage stage) {
        this.waitingRoomStage = stage;
    }

    /**
     * Loads the crew placement phase scene.
     *
     * @param ship the player's ship
     * @param positions the component positions map
     * @throws IOException if FXML file cannot be loaded
     */
    public void loadCrewPhase(Ship ship, Map<Integer, Integer> positions) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/crewSelection.fxml"));
        Parent root = loader.load();

        CrewSelectionController controller = loader.getController();
        this.setCrewSelectionController(controller);
        controller.setGui(this);
        controller.setServerAndClient(server, clientModel);
        int level = clientModel.getLevel();
        crewSelectionController.setLevel(level);
        controller.setGuiRenderer(GuiRenderer.getInstance());
        GuiRenderer.getInstance().setGuiController(controller);
        GuiRenderer.getInstance().setMessageLabel(controller.getMessageLabel());

        controller.showShip(ship);

        Stage stage = new Stage();
        CloseStageManager.getCloseStageManager().addStage(stage);
        stage.setTitle("Crew Selection");
        stage.setScene(new Scene(root));
        stage.show();
        controller.setStage(stage);

        if (waitingRoomStage != null) {
            waitingRoomStage.close();
        }
    }


    private CrewSelectionController crewSelectionController;

    /**
     * Sets the CrewSelectionController.
     *
     * @param controller the controller to set
     */
    public void setCrewSelectionController(CrewSelectionController controller) {
        this.crewSelectionController = controller;
    }

    /**
     * Gets the CrewSelectionController.
     *
     * @return the current CrewSelectionController
     */
    public CrewSelectionController getCrewSelectionController() {
        return crewSelectionController;
    }


    /**
     * Gets the Stage2Controller.
     *
     * @return the current Stage2Controller
     */
    public Stage2Controller getStage2Controller() {
        return stage2Controller;
    }



    /**
     * Loads the dashboard scene for flight phase 1.
     *
     * @param isLeader whether the player is the current leader
     * @param ship the player's ship
     * @throws IOException if FXML file cannot be loaded
     */
    public void loadDashboardScene(boolean isLeader, Ship ship) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/dashboard.fxml"));
        Parent root = fxmlLoader.load();

        Dashboard controller = fxmlLoader.getController();
        controller.setServerAndClient(server, clientModel);
        controller.setLeader(isLeader);
        controller.setGui(this);
        controller.showShip(ship);

        setDashboard(controller);

        Stage stage = new Stage();
        controller.setStage(stage);

        CloseStageManager.getCloseStageManager().addStage(stage);
        stage.setTitle("FLIGHT 1");
        stage.setScene(new Scene(root));
        stage.show();
    }


    /**
     * Updates the dashboard scene with new leader status and ship state.
     *
     * @param isLeader whether the player is the current leader
     * @param ship the player's ship (can be null)
     * @throws IOException if update fails
     */
    public void updateDashboardScene(boolean isLeader, Ship ship) throws IOException {
        Dashboard controller = getDashboard();
        controller.setLeader(isLeader);
        if(ship != null) {
            controller.showShip(ship);
        }
    }

    /**
     * Gets the waiting room stage.
     *
     * @return the waiting room stage
     */
    public Stage getWaitingRoomStage() {
        return waitingRoomStage;
    }

    /**
     * Updates the dashboard2 scene with new leader status and ship state.
     *
     * @param isLeader whether the player is the current leader
     * @param ship the player's ship (can be null)
     * @throws IOException if update fails
     */
    public void updateDashboard2Scene(boolean isLeader, Ship ship) throws IOException {
        Dashboard2 controller = getDashboard2();
        controller.setLeader(isLeader);
        if(ship != null) {
            controller.showShip(ship);
        }
    }

    /**
     * Loads the dashboard scene for flight phase 2.
     *
     * @param isLeader whether the player is the current leader
     * @param ship the player's ship
     * @throws IOException if FXML file cannot be loaded
     */
    public void loadDashboard2Scene(boolean isLeader, Ship ship) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/dashboard2.fxml"));
        Parent root = fxmlLoader.load();

        Dashboard2 controller = fxmlLoader.getController();
        controller.setServerAndClient(server, clientModel);
        controller.setLeader(isLeader);
        controller.setGui(this);
        controller.showShip(ship);

        setDashboard2(controller);

        Stage stage = new Stage();
        controller.setStage(stage);

        CloseStageManager.getCloseStageManager().addStage(stage);
        stage.setTitle("FLIGHT 2");
        stage.setScene(new Scene(root));
        stage.show();
    }


    /**
     * Updates the sub-deck display in Stage2Controller.
     *
     * @param subDeck the list of cards in the sub-deck
     */
    public void updateSubDeck(List<Card> subDeck) {
        if (stage2Controller != null) {
            //System.out.println("updateFaceUpList chiamata con " + components.size() + " elementi");
            stage2Controller.subDeck(subDeck);
        } else {
            System.err.println("stage2Controller è null");
        }
    }

    private Dashboard2 dashboard2;
    private Dashboard dashboard;
    private CardManagerController cardManagerController;

    /**
     * Sets the Dashboard2 controller.
     *
     * @param dashboard2 the controller to set
     */
    public void setDashboard2(Dashboard2 dashboard2) {
        this.dashboard2 = dashboard2;
    }

    /**
     * Sets the Dashboard controller.
     *
     * @param dashboard the controller to set
     */
    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    /**
     * Gets the Dashboard2 controller.
     *
     * @return the current Dashboard2 controller
     */
    public Dashboard2 getDashboard2() {
        return dashboard2;
    }

    /**
     * Gets the Dashboard controller.
     *
     * @return the current Dashboard controller
     */
    public Dashboard getDashboard() {
        return dashboard;
    }

    private CardManagerController currentCardController;

    /**
     * Sets the current CardManagerController.
     *
     * @param controller the controller to set
     */
    public void setCurrentCardController(CardManagerController controller) {
        this.currentCardController = controller;
    }

    /**
     * Gets the current CardManagerController.
     *
     * @return the current CardManagerController
     */
    public CardManagerController getCurrentCardController() {
        return currentCardController;
    }

    /**
     * Loads the final stage scene showing game results.
     *
     * @param ranking the final ranking of all players
     */
    public void loadFinalStage(Map<Player, Integer> ranking) {
        try {
            CloseStageManager.getCloseStageManager().closeAllStages();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/finalStage.fxml"));
            Parent root = loader.load();

            FinalStageController controller = loader.getController();
            controller.setRanking(ranking);

            Stage stage = new Stage();
            CloseStageManager.getCloseStageManager().addStage(stage);
            stage.setTitle("Final Score");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private GameSelectorController gameSelectorController;
    private Stage gameSelectorStage;


    /**
     * Sets the GameSelectorController.
     *
     * @param controller the controller to set
     */
    public void setGameSelectorController(GameSelectorController controller) {
        this.gameSelectorController = controller;
    }

    /**
     * Gets the GameSelectorController.
     *
     * @return the current GameSelectorController
     */
    public GameSelectorController getGameSelectorController() {
        return gameSelectorController;
    }



}
