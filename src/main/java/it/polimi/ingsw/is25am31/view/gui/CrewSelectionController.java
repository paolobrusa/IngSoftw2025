package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.view.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the crew selection phase of the game.
 * Manages the placement of crew members and aliens on the spaceship grid,
 * displays the ship components with their current state, and handles user interactions
 * for placing different types of crew members.
 */
public class CrewSelectionController {

    private Gui gui;

    /**
     * Sets the GUI instance for this controller.
     *
     * @param gui the GUI instance to set
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }

    private VirtualServer server;
    private ClientModel client;

    /**
     * Sets the virtual server and client model for this controller.
     *
     * @param server the virtual server instance for network communication
     * @param client the client model containing game state information
     */
    public void setServerAndClient(VirtualServer server, ClientModel client) {
        this.server = server;
        this.client = client;
    }

    private final Map<String, Label> gridLabels = new HashMap<>();

    private final Map<String, ImageView> gridImages = new HashMap<>();


    /**
     * Initializes the controller by populating the grid label and image maps.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof ImageView imageView && imageView.getId() != null) {
                gridImages.put(imageView.getId(), imageView);
            }
        }

        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Label label && label.getId() != null) {
                gridLabels.put(label.getId(), label);
            }
        }

    }


    /**
     * Displays the ship on the UI by loading component images and updating the view.
     *
     * @param ship the ship to be displayed
     */
    public void showShip(Ship ship) {
        this.ship = ship;
        loadComponentImages();
        updateShipView();
    }



    private int level;

    /**
     * Sets the ship level for display purposes.
     *
     * @param level the ship level (1 or 2)
     */
    public void setLevel(int level) {
        this.level = level;
    }

    private void updateShipView() {
        ShipLevel1.setVisible(level == 1);
        ShipLevel2.setVisible(level == 2);

        for (int row = 5; row <= 9; row++) {
            for (int col = 4; col <= 10; col++) {
                Component c = ship.getComponent(row - 5, col - 4);
                if (c != null) {
                    String id = row + "," + col;

                    ImageView imageView = gridImages.get(id);
                    Label label = gridLabels.get(id);


                    if (c instanceof MainCabin cabin && id.equals("7,7")) {
                        int mainCabinImageId = switch (cabin.getColor()) {
                            case RED -> 52;
                            case BLUE -> 33;
                            case GREEN -> 34;
                            case YELLOW -> 61;
                        };

                        String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + mainCabinImageId + ".jpg";
                        try (var stream = getClass().getResourceAsStream(path)) {
                            if (stream != null && imageView != null) {
                                Image img = new Image(stream);
                                imageView.setImage(img);
                                imageView.setRotate(0);
                                imageView.setVisible(true);
                            } else {
                                System.err.println("Immagine MainCabin non trovata: " + path);
                            }
                        } catch (Exception e) {
                            System.err.println("Errore MainCabin ID " + mainCabinImageId + ": " + e.getMessage());
                        }

                    }


                    if (imageView != null) {
                        Image img = componentImages.get(c.getId());
                        if (img != null) {
                            imageView.setImage(img);
                            imageView.setRotate(switch (c.getOrientation()) {
                                case LEFT -> 270;
                                case RIGHT -> 90;
                                case BACK -> 180;
                                case FRONT -> 0;
                            });
                            imageView.setVisible(true);
                        } else {
                            if (c.getId() != 0) {
                                System.err.println("Nessuna immagine per componente ID " + c.getId());
                            }
                        }
                    }

                    if (label != null) {
                        label.setVisible(true);

                        if (c instanceof BatteryComponent b) {
                            label.setText(b.getCapacity() + "/" + b.getMaxCapacity());

                        } else if (c instanceof CargoHold ch) {
                            StringBuilder sb = new StringBuilder();
                            for (Good g : ch.getContains()) {
                                sb.append(switch (g) {
                                    case RED -> "R";
                                    case BLUE -> "B";
                                    case GREEN -> "G";
                                    case YELLOW -> "Y";
                                });
                            }
                            while (sb.length() < ch.getCapacity()) {
                                sb.append("-");
                            }
                            label.setText(sb.toString());

                        } else if (c instanceof Cabin cabin) {
                            AlienColor alien = cabin.getCurrentAlien();

                            if (alien == AlienColor.BROWN) {
                                label.setText("BA");
                            } else if (alien == AlienColor.PURPLE) {
                                label.setText("PA");
                            } else if (cabin.getCrewMembers() > 0) {
                                label.setText(cabin.getCrewMembers() + "/2");
                            } else {
                                label.setText("0/2");
                            }

                            label.setVisible(true);
                        }

                        else if (c instanceof MainCabin mainCabin) {
                            int n = mainCabin.getCrewMembers();
                            label.setText(String.valueOf(n) + "/2");


                        } else {
                            label.setVisible(false);
                        }
                    }
                }
            }
        }
    }





    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView ShipLevel1, ShipLevel2;

    private Ship ship;




    private Map<Integer, Image> componentImages = new HashMap<>();

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

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;


    private int parseCoord(TextField field) throws NumberFormatException {
        return Integer.parseInt(field.getText().trim());
    }

    /**
     * Gets the message label component.
     *
     * @return the message label
     */
    public Label getMessageLabel() {
        return messageLabel;
    }

    private GuiRenderer guiRenderer;

    /**
     * Sets the GUI renderer for this controller.
     *
     * @param guiRenderer the GUI renderer instance
     */
    public void setGuiRenderer(GuiRenderer guiRenderer) {
        this.guiRenderer = guiRenderer;
    }


    private void placeCrew(AlienColor type) {
        int x = 0;
        int y = 0;
        GuiRenderer.getInstance().setLastCrewPlacementFailed(false); // resetto il flag

        try {
            x = parseCoord(xField);
            y = parseCoord(yField);
            server.placeCrewMember(client.getClientId(), x - 5, y - 4, type);
        } catch (NumberFormatException e) {
            showMessage("Coordinate non valide", true);
            return;
        } catch (Exception e) {
            GuiRenderer.getInstance().reportException(e);
            return;
        }


        if (GuiRenderer.getInstance().hasCrewPlacementFailed()) {
            return;
        }

        showMessage("Posizionato a (" + x + ", " + y + "), tipo: " + type, false);

        updateShipView();
    }

    @FXML
    private void handleAddCrew() {
        placeCrew(AlienColor.NONE);
    }

    @FXML
    private void handleAddBrownAlien() {
        placeCrew(AlienColor.BROWN);
    }

    @FXML
    private void handleAddPurpleAlien() {
        placeCrew(AlienColor.PURPLE);
    }

    @FXML
    private Label messageLabel;

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    @FXML
    private Button finishButton, addCrewButton, addBrownButton, addPurpleButton;

    @FXML
    private void handleFinish() {
        disableAllInputs();
        showMessage("In attesa degli altri giocatori...", false);

        try {
            server.endCrewPlacingPhase(client.getClientId());
        } catch (Exception e) {
            showMessage("Errore: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void disableAllInputs() {
        xField.setDisable(true);
        yField.setDisable(true);
        finishButton.setDisable(true);
        addCrewButton.setDisable(true);
        addBrownButton.setDisable(true);
        addPurpleButton.setDisable(true);
    }

    private Stage crewStage;

    /**
     * Sets the stage associated with this controller.
     *
     * @param stage the JavaFX stage
     */
    public void setStage(Stage stage) {
        this.crewStage = stage;
    }

    /**
     * Gets the stage associated with this controller.
     *
     * @return the JavaFX stage
     */
    public Stage getStage() {
        return crewStage;
    }

}
