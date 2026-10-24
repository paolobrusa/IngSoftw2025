package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Side;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing the selected component tile during the ship assembly phase.
 * Handles component display, rotation, placement confirmation, removal, and reservation.
 * Provides visual feedback and validation for component placement operations.
 */
public class SelectedTileController {

    public Button okButton;
    public Button rotateButton;
    @FXML
    private ImageView componentImageView;

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;

    private final Map<Integer, Image> componentImages = new HashMap<>();

    private Component currentComponent;
    private boolean confirmed = false;
    private int rotation = 0;

    private Gui gui;
    private Stage2Controller stage2Controller;


    /**
     * Initializes the controller by loading all component images.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        loadComponentImages();
    }

    private void loadComponentImages() {
        for (int id = 1; id <= 156; id++) {
            if (id == 33 || id == 34 || id == 52 || id == 61)
                continue;

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
     * Displays the component image and enables interaction buttons.
     *
     * @param id the component ID used to load the image
     * @param component the component to display
     */
    public void showComponentImage(int id, Component component) {
        Image image = componentImages.get(id);
        this.currentComponent = component;
        if (image != null) {
            componentImageView.setImage(image);
        } else {
            System.err.println("Nessuna immagine per componente ID " + id);
        }

        rotateButton.setDisable(false);
        removeButton.setDisable(false);
    }

    /**
     * Gets the current component being displayed.
     *
     * @return the current component, or null if no component is selected
     */
    public Component getCurrentComponent() {
        return currentComponent;
    }

    /**
     * Checks if the current component has been confirmed for placement.
     *
     * @return true if confirmed, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Clears the current component selection and resets the view.
     */
    public void clearComponent() {
        this.currentComponent = null;
        this.confirmed = false;
        this.componentImageView.setImage(null);
    }

    /**
     * Gets the component label for displaying messages.
     *
     * @return the reserve message label
     */
    public javafx.scene.control.Label getComponentLabel() {
        return reserveMessage;
    }

    /**
     * Sets the GUI instance for this controller.
     *
     * @param gui the GUI instance to set
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }

    /**
     * Sets the Stage2Controller instance for this controller.
     *
     * @param controller the Stage2Controller to set
     */
    public void setStage2Controller(Stage2Controller controller) {
        this.stage2Controller = controller;
    }

    private void rotateImageViewByOrientation(ImageView imageView, Side orientation) {
        switch (orientation) {
            case FRONT -> imageView.setRotate(0);
            case RIGHT -> imageView.setRotate(90);
            case BACK -> imageView.setRotate(180);
            case LEFT -> imageView.setRotate(270);
        }
    }

    @FXML
    private void rotateImage() {
        currentComponent.modifyOrientation(Side.RIGHT);
        rotateImageViewByOrientation(componentImageView, currentComponent.getOrientation());
        reserveMessage.setText("");
    }

    @FXML
    private Button removeButton;

    private Component lastConfirmedComponent;
    private int lastX, lastY;


    /**
     * Enables the rotate button using Platform.runLater for thread safety.
     */
    public void enableRotateButton() {
        Platform.runLater(() -> rotateButton.setDisable(false));
    }


    @FXML
    private void confirmComponent() {
        if (currentComponent == null) {
            reserveMessage.setText("No selected component");
            return;
        }

        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());

            int cordX = currentComponent.getX();
            int cordY = currentComponent.getY();

            gui.getServer().attachComponent(currentComponent, x-5, y-4, gui.getClient().getClientId());
            gui.getClient().getMyShip().addComponent( x-5, y-4, currentComponent);

            currentComponent.setX(x-5);
            currentComponent.setY(y-4);

            if (stage2Controller != null)
                stage2Controller.placeComponentImage(x, y, currentComponent);
            if (stage2Controller != null && cordX == 0 && (cordY == 5 || cordY == 6)) {
                stage2Controller.clearComponentImage(cordX+5, cordY+4);
            }

            lastConfirmedComponent = currentComponent;
            lastX = x-5;
            lastY = y-4;

            confirmed = true;
            xField.clear();
            yField.clear();

            removeButton.setDisable(false);
            clearComponent();
            componentImageView.setRotate(0);
            reserveMessage.setText("");

            gui.getClient().setCurrentComponent(null);
            rotateButton.setDisable(true);

        } catch (Exception e) {
            reserveMessage.setText("Invalid position");
        }
    }


    @FXML
    private void removeComponent() {
        if (lastX < 0 || lastX >= 5 || lastY < 0 || lastY >= 7) {
            reserveMessage.setText("Invalid component position.");
            return;
        }

        try {
            UUID clientId = gui.getClient().getClientId();

            if (gui.getClient().getCurrentComponent() == null) {
                Component removed = gui.getClient().getMyShip().getComponent(lastX, lastY);
                if (removed == null) {
                    reserveMessage.setText("There is no component to remove in this position.");
                    return;
                }


                gui.getServer().removeComponent(clientId, lastX, lastY);
                gui.getClient().setCurrentComponent(removed);
                gui.getClient().getMyShip().removeComponent(lastX, lastY);


                int id = removed.getId();
                showComponentImage(id, removed);

                if (stage2Controller != null) {
                    stage2Controller.clearComponentImage(lastX + 5, lastY + 4);
                    stage2Controller.getServer().getFaceUpComponentList(clientId);
                }

                lastConfirmedComponent = null;
                removeButton.setDisable(true);
                reserveMessage.setText("Component removed successfully.");
            } else {
                reserveMessage.setText("You already have a drawn component.");
            }

        } catch (Exception e) {
            reserveMessage.setText("You can only remove the last placed component.");
            e.printStackTrace();
        }
    }





    @FXML
    private Button reserve1Button;

    @FXML
    private Button reserve2Button;

    @FXML
    private void handleReserve1() {

            reserveComponentTo(5, 9);

    }


    @FXML
    private void handleReserve2() {

            reserveComponentTo(5, 10);


    }


    private void reserveComponentTo(int x, int y) {
        if (currentComponent == null) {
            System.err.println("Nessuna componente da riservare.");
            return;
        }

        try {
            //gui.getServer().reserveComponent(currentComponent, gui.getClient().getClientId());
            if (stage2Controller != null)
                stage2Controller.placeComponentImage(x, y, currentComponent);

            componentImageView.setImage(null);
            currentComponent = null;
            System.out.println("Componente riservata in " + x + "," + y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label reserveMessage;

    /**
     * Checks if there is a pending component that hasn't been confirmed.
     *
     * @return true if there's an unconfirmed component, false otherwise
     */
    public boolean hasPendingComponent() {
        return currentComponent != null && !confirmed;
    }

    /**
     * Gets the pending component that hasn't been confirmed yet.
     *
     * @return the pending component, or null if none exists
     */
    public Component getPendingComponent() {
        return currentComponent;
    }

    /**
     * Clears the pending component and resets the image view.
     */
    public void clearPendingComponent() {
        componentImageView.setImage(null);
        currentComponent = null;
    }
    private static Stage stage;

    /**
     * Sets the static stage for this controller.
     *
     * @param s the stage to set
     */
    public static void setStage(Stage s) {
        stage = s;
    }

    /**
     * Gets the static stage for this controller.
     *
     * @return the current stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Disables all interactive buttons in the view.
     */
    public void setDisableAll(){
        removeButton.setDisable(true);
        okButton.setDisable(true);
        rotateButton.setDisable(true);
    }

    /**
     * Enables all interactive buttons in the view.
     */
    public void activateAll(){
        removeButton.setDisable(false);
        okButton.setDisable(false);
        rotateButton.setDisable(false);
    }

    /**
     * Resets the component image rotation to 0 degrees.
     */
    public void resetImage(){
        componentImageView.setRotate(0);
    }

    /**
     * Displays an error message when component placement fails.
     *
     * @param message the error message to display
     */
    public void finishComponentError(String message) {
        reserveMessage.setText(message);
    }
}


