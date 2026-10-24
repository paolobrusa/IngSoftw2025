package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.MainCabin;
import it.polimi.ingsw.is25am31.model.components.Side;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.view.ClientModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the ship assembly phase (Stage 2) of the game.
 * Manages component selection from face-up cards or deck, placement on the ship grid,
 * viewing opponent ships, and timer management for level 2 games. Coordinates between
 * the main assembly view and the selected tile controller for component manipulation.
 */
public class Stage2Controller {

    public Button use2Button;
    public Button use1Button;
    @FXML
    public Button refresh;
    @FXML
    private Button drawCompButton;
    @FXML
    public Button endBuildButton;
    private int count;
    private VirtualServer server;
    private ClientModel client;

    private SelectedTileController selectedTileController;

    /**
     * Sets the SelectedTileController for this controller.
     *
     * @param selectedTileController the SelectedTileController to set
     */
    public void setSelectedTileController(SelectedTileController selectedTileController) {
        this.selectedTileController = selectedTileController;
    }


    /**
     * Sets the server and client model for this controller.
     * Initializes the view ship buttons.
     *
     * @param server the virtual server for network communication
     * @param clientModel the client model containing game state
     */
    public void setServerAndClient(VirtualServer server, ClientModel clientModel) {
        this.server = server;
        this.client = clientModel;


        viewShipButtons = List.of(viewShipButton1, viewShipButton2, viewShipButton3);

        updateViewShipButtons();
    }

    private final Map<String, ImageView> gridImages = new HashMap<>();
    private final Map<Integer, Image> componentImages = new HashMap<>();

    /**
     * Gets the virtual server instance.
     *
     * @return the current VirtualServer
     */
    public VirtualServer getServer() {
        return server;
    }

    /**
     * Gets the client model instance.
     *
     * @return the current ClientModel
     */
    public ClientModel getClient() {
        return client;
    }

    @FXML
    private ImageView ship1ImageView;

    @FXML
    private ImageView ship2ImageView;

    private int level;

    /**
     * Sets the game level for this controller.
     *
     * @param level the game level (1 or 2)
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Shows the level 2 ship image and hides the level 1 ship image.
     */
    public void showLevel2Ship() {
        if (ship1ImageView != null && ship2ImageView != null) {
            ship1ImageView.setVisible(false);
            ship2ImageView.setVisible(true);
        }
    }

    /**
     * Updates the ship view based on the current game level.
     * Shows appropriate ship image and enables level 2 specific controls.
     */
    public void updateShipView() {
        if (ship1ImageView != null && ship2ImageView != null) {
            boolean isLevel2 = (level == 2);
            ship1ImageView.setVisible(!isLevel2);
            ship2ImageView.setVisible(isLevel2);
            timer.setVisible(isLevel2);
            deck1.setVisible(isLevel2);
            deck2.setVisible(isLevel2);
            deck3.setVisible(isLevel2);
        } else {
            System.err.println("ship ImageView non inizializzate");
        }
    }

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof ImageView imageView && imageView.getId() != null) {
                gridImages.put(imageView.getId(), imageView);
            }
        }
        loadComponentImages();
        faceUpListView.setOnMouseClicked(event -> {
            ImageView selectedImage = faceUpListView.getSelectionModel().getSelectedItem();
            if (selectedImage != null) {
                Integer id = (Integer) selectedImage.getUserData();
                try {
                    if (selectedTileController != null && selectedTileController.hasPendingComponent()) {
                        Component old = selectedTileController.getPendingComponent();
                        server.putDownComponent(old, client.getClientId());
                        selectedTileController.clearPendingComponent();
                    }
                    server.drawFaceUpComponent(id, client.getClientId());
                } catch (Exception e) {
                    selectedTileController.getComponentLabel().setText("Another player draw that component");
                }
            }
        });
    }

    private Ship ship;

    /**
     * Sets the ship for this controller and displays the main cabin.
     *
     * @param ship the ship to set
     * @throws IOException if the main cabin image cannot be loaded
     */
    public void setShip(Ship ship) throws IOException {
        this.ship = ship;
        showMainCabin();
    }


    private void showMainCabin() throws IOException {
        if (ship == null) return;

        Component comp = ship.getComponent(2, 3);
        if (!(comp instanceof MainCabin cabin) || cabin.getColor() == null) return;

        int id = switch (cabin.getColor()) {
            case RED -> 52;
            case BLUE -> 33;
            case GREEN -> 34;
            case YELLOW -> 61;
        };

        String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
        try (var stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                Image img = new Image(stream);
                ImageView imageView = gridImages.get("7,7");
                if (imageView != null) {
                    imageView.setImage(img);
                    imageView.setVisible(true);
                }
            }
        }
    }

    private void loadComponentImages() {
        for (int id = 1; id <= 156; id++) {
            if (id == 33 || id == 34 || id == 52 || id == 61) continue;
            String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
            try (var stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    Image image = new Image(stream);
                    componentImages.put(id, image);
                } else {
                    System.err.println("Immagine non trovata per ID " + id + ": " + path);
                }
            } catch (Exception e) {
                System.err.println("Errore caricando immagine ID " + id + ": " + e.getMessage());
            }
        }
    }

    /**
     * Registers an ImageView in the grid images map.
     *
     * @param key the grid position key (format: "x,y")
     * @param imageView the ImageView to register
     */
    public void registerGridImageView(String key, ImageView imageView) {
        gridImages.put(key, imageView);
    }

    /**
     * Places a component image at the specified grid position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param component the component to place
     */
    public void placeComponentImage(int x, int y, Component component) {
        String key = x + "," + y;
        ImageView view = gridImages.get(key);
        if (view != null && componentImages.containsKey(component.getId())) {
            view.setImage(componentImages.get(component.getId()));
            rotateImageViewByOrientation(view, component.getOrientation());
        } else {
            System.err.println("Impossibile posizionare componente in posizione: " + key);
        }
    }

    private GuiRenderer guiRenderer;

    /**
     * Sets the GUI renderer for this controller.
     *
     * @param guiRenderer the GuiRenderer to set
     */
    public void setGuiRenderer(GuiRenderer guiRenderer) {
        this.guiRenderer = guiRenderer;
    }

    @FXML
    private void handleDrawComponent(javafx.event.ActionEvent actionEvent) {
        if (server != null && client != null) {
            try {
                if (selectedTileController != null && selectedTileController.hasPendingComponent()) {
                    Component old = selectedTileController.getPendingComponent();
                    server.putDownComponent(old, client.getClientId());
                    selectedTileController.clearPendingComponent();
                }

                server.drawFaceDownComponent(client.getClientId());

                if (selectedTileController != null) {
                    selectedTileController.enableRotateButton();
                    selectedTileController.resetImage();
                }

            } catch (Exception e) {
                guiRenderer.reportException(e);
            }
        } else {
            System.err.println("Server o client non inizializzati");
        }
    }




    @FXML
    private void handleEndBuild(javafx.event.ActionEvent event) {
        if(gui.getWaitingRoomController() != null) {
            return;
        }
        try {
            server.endAssemblePhase(client.getClientId());
        } catch (Exception e) {
            System.err.println("Errore durante endAssemblePhase: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Stage stage2 = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage2.close();

        if (SelectedTileController.getStage() != null) {
            SelectedTileController.getStage().close();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/waitingRoom.fxml"));
            Parent root = loader.load();

            WaitingRoomController controller = loader.getController();
            controller.setServerAndClient(server, client);
            controller.setCount(count, secondsLeft);
            GuiRenderer.getInstance().setErrorLabel(controller.getErrorLabel());
            GuiRenderer.getInstance().setGuiController(controller);

            Platform.runLater(() -> {
                gui.setWaitingRoomController(controller);
            });
            Stage waitingStage = new Stage();
            CloseStageManager.getCloseStageManager().addStage(waitingStage);
            waitingStage.setTitle("Waiting Room");
            waitingStage.setScene(new Scene(root));
            waitingStage.show();
            gui.setWaitingRoomStage(waitingStage);
        } catch (IOException e) {
            System.err.println("Errore caricamento waitingRoom.fxml: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Enables the draw component button.
     */
    public void enableDrawButton() {
        if (drawCompButton != null)
            drawCompButton.setDisable(false);
    }

    private void rotateImageViewByOrientation(ImageView imageView, Side orientation) {
        switch (orientation) {
            case FRONT -> imageView.setRotate(0);
            case RIGHT -> imageView.setRotate(90);
            case BACK -> imageView.setRotate(180);
            case LEFT -> imageView.setRotate(270);
        }
    }

    /**
     * Clears the component image at the specified grid position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void clearComponentImage(int x, int y) {
        String key = x + "," + y;
        ImageView view = gridImages.get(key);
        if (view != null) {
            view.setImage(null);
            view.setRotate(0);
        } else {
            System.err.println("Nessuna ImageView trovata in posizione: " + key);
        }
    }

    @FXML
    private ListView<ImageView> faceUpListView;

    /**
     * Updates the face-up component list view with the provided components.
     *
     * @param components the list of face-up components to display
     */
    public void updateFaceUpComponentList(List<Component> components) {
        if (faceUpListView == null) {
            System.err.println("faceUpListView è null!");
            return;
        }
        faceUpListView.getItems().clear();
        for (Component c : components) {
            int id = c.getId();
            String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
            try {
                var stream = getClass().getResourceAsStream(path);
                if (stream == null) {
                    System.err.println("Immagine non trovata: " + path);
                    continue;
                }
                Image image = new Image(stream);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);

                imageView.setUserData(id);

                faceUpListView.getItems().add(imageView);

            } catch (Exception e) {
                System.err.println("Errore immagine ID " + id + ": " + e.getMessage());
            }
        }

    }
    @FXML
    private Button deck1;
    @FXML
    private Button deck2;
    @FXML
    private Button deck3;
    private int secondsLeft = 90;
    private Timeline timeline;
    @FXML
    private Button timer;

    private void initializeTimeline(int counter) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            timer.setText(secondsLeft + "s");
            if(secondsLeft <= 0) {
                if(counter == 3 || counter == 4) {
                    timer.setDisable(true);
                    timer.setText("N.T");
                    secondsLeft = 90;
                    if(counter == 4) {
                        endBuildButton.fire();
                    }
                    timeline.stop();
                }
                else{
                    timer.setDisable(false);
                    timer.setText("Timer " + (counter + 1));
                    secondsLeft = 90;
                    timeline.stop();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Updates the timer display and starts the countdown.
     *
     * @param counter the current timer count
     * @param seconds the seconds remaining
     */
    public void updateTimer(int counter, int seconds) {
        count = counter;
        timer.setDisable(true);
        initializeTimeline(counter);
        timeline.play();
    }

    /**
     * Starts the hourglass timer on the server.
     *
     * @throws Exception if the server request fails
     */
    public void startHourGlass() throws Exception {
        server.startHourGlass(client.getClientId());
    }

    @FXML
    private void handleUse1() {
        drawReservedComponent(0);
    }

    @FXML
    private void handleUse2() {
        drawReservedComponent(1);
    }

    private void drawReservedComponent(int index) {
        if (server != null && client != null) {
            try {
                if (selectedTileController != null && selectedTileController.hasPendingComponent()) {
                    Component old = selectedTileController.getPendingComponent();
                    server.putDownComponent(old, client.getClientId());
                    selectedTileController.clearPendingComponent();
                }
                server.drawFromReserved(index, client.getClientId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Client o server non inizializzati");
        }
    }
    private Gui gui;

    /**
     * Sets the GUI instance for this controller.
     *
     * @param gui the GUI instance to set
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }

    @FXML private Button viewShipButton1;
    @FXML private Button viewShipButton2;
    @FXML private Button viewShipButton3;

    private List<Button> viewShipButtons;
    private List<String> otherNicknames;

    private void updateViewShipButtons() {
        String myNick = client.getNickname();
        otherNicknames = client.getPlayers().stream()
                .map(p -> p.getNickname())
                .filter(nick -> !nick.equals(myNick))
                .toList();

        for (int i = 0; i < viewShipButtons.size(); i++) {
            if (i < otherNicknames.size()) {
                String nickname = otherNicknames.get(i);
                Button btn = viewShipButtons.get(i);
                btn.setText("View " + nickname + "'s ship");
                btn.setVisible(true);
            } else {
                viewShipButtons.get(i).setVisible(false);
            }
        }
    }

    @FXML
    private void handleViewShip1() {
        requestOpponentShip(0);
    }

    @FXML
    private void handleViewShip2() {
        requestOpponentShip(1);
    }

    @FXML
    private void handleViewShip3() {
        requestOpponentShip(2);
    }

    private void requestOpponentShip(int index) {
        if (index >= otherNicknames.size()) return;

        String opponent = otherNicknames.get(index);
        try {

            client.setViewingOpponentShip(true);


            server.showShip(client.getClientId(), opponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the face-up component list.
     *
     * @param actionEvent the action event
     */
    public void refreshListComponent(javafx.event.ActionEvent actionEvent) {
        try {
            server.getFaceUpComponentList(client.getClientId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows an opponent's ship in a new window.
     *
     * @param ship the opponent's ship to display
     */
    public void showOpponentShip(Ship ship) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/opponentShipView.fxml"));
            Parent root = loader.load();
            OpponentShipController controller = loader.getController();
            controller.showShip(ship, level);
            Stage stage = new Stage();
            stage.setTitle("Opposing Ship");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the sub-deck cards in a new window.
     * Disables all controls while viewing the sub-deck.
     *
     * @param subDeck the list of cards in the sub-deck
     */
    public void subDeck(List<Card> subDeck) {
        try {
            drawCompButton.setDisable(true);
            use1Button.setDisable(true);
            use2Button.setDisable(true);
            faceUpListView.setDisable(true);
            deck1.setDisable(true);
            deck2.setDisable(true);
            deck3.setDisable(true);
            endBuildButton.setDisable(true);
            selectedTileController.setDisableAll();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/cardViewAssemble.fxml"));
            Parent root = loader.load();
            CardViewAssemble cardViewAssemble = loader.getController();
            cardViewAssemble.showSubDeck(subDeck);
            Stage stage = new Stage();
            stage.setTitle("Sub Deck");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> {
                drawCompButton.setDisable(false);
                use1Button.setDisable(false);
                use2Button.setDisable(false);
                faceUpListView.setDisable(false);
                deck1.setDisable(false);
                deck2.setDisable(false);
                deck3.setDisable(false);
                endBuildButton.setDisable(false);
                selectedTileController.activateAll();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles deck 1 button click to view the first sub-deck.
     *
     * @param actionEvent the action event
     * @throws IOException if the request fails
     */
    public void deck1Button(ActionEvent actionEvent) throws IOException {
        try {
            Gui.server().getSubDeck(client.getClientId(), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles deck 2 button click to view the second sub-deck.
     *
     * @param actionEvent the action event
     * @throws IOException if the request fails
     */
    public void deck2Button(ActionEvent actionEvent) throws IOException {
        try {
            Gui.server().getSubDeck(client.getClientId(), 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles deck 3 button click to view the third sub-deck.
     *
     * @param actionEvent the action event
     * @throws IOException if the request fails
     */
    public void deck3Button(ActionEvent actionEvent) throws IOException {
        try {
            Gui.server().getSubDeck(client.getClientId(), 2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays an error message and disables the draw component button.
     *
     * @param message the error message to display
     */
    public void finishComponentError(String message) {
        selectedTileController.finishComponentError(message);
        drawCompButton.setDisable(true);
    }
}
