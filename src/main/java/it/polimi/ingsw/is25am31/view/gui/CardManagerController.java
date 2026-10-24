package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.view.tui.input.CommandParser;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Controller for managing card display and player interactions during card resolution phases.
 * Handles the display of card information, player commands, dice results, and manages
 * the state transitions between active player turns and spectator mode.
 */
public class CardManagerController{

    @FXML private ImageView cardImageView;
    @FXML private Label descriptionLabel;
    @FXML private TextField commandInputField;
    @FXML private Label errorLabel;
    @FXML private Label infoLabel;
    @FXML private Label diceLabel;
    @FXML private Button sendButton;

    private Card card;
    private ClientModel clientModel;


    /**
     * Sets the client model for this controller and updates the turn message.
     *
     * @param clientModel the client model containing game state information
     */
    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
        setTurnMessage();
    }

    /**
     * Sets the card to be displayed and managed by this controller.
     * Loads the card image and displays its description.
     *
     * @param card the card to be displayed
     */
    public void setCard(Card card) {
        this.card = card;
        loadCardImage();
        showCardDescription();
        if (clientModel != null)
            setTurnMessage();
    }

    private void loadCardImage() {
        try (InputStream stream = getClass().getResourceAsStream(
                "/it/polimi/ingsw/is25am31/image/cards/" + card.getId() + ".jpg")) {
            if (stream != null) {
                cardImageView.setImage(new Image(stream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the turn message and input field state based on the current client state.
     * Enables input for the active player during effect resolution, disables it otherwise.
     */
    public void setTurnMessage() {
        if (clientModel.getClientState() == ClientState.EFFECT_RESOLUTION) {
            commandInputField.setDisable(false);
            errorLabel.setText("You are the active player. Resolve the card.");
        } else {
            commandInputField.setDisable(true);
            errorLabel.setText("Wait for the active player.");
        }

    }

    private void showCardDescription() {
        String[] ascii = card.getAsciiArt();
        descriptionLabel.setText(String.join("\n", ascii));
    }

    @FXML
    private void onSendCommand() {
        String input = commandInputField.getText().trim();
        if (input.isEmpty()) {
            errorLabel.setText("Please enter a command.");
            return;
        }

        try {
            CommandParser parser = new CommandParser(Gui.server(), clientModel, GuiRenderer.getInstance());
            parser.setClientId(clientModel.getClientId());
            parser.handleInput(input);
        } catch (Exception e) {
            onError("❌ " + e.getMessage());
            e.printStackTrace();
        }
        commandInputField.clear();
    }

    /**
     * Displays an informational message in the info label.
     *
     * @param message the information message to display
     */
    public void onInfo(String message) {
        infoLabel.setText(message);
    }

    /**
     * Displays an error message in the error label with an error icon.
     *
     * @param message the error message to display
     */
    public void onError(String message) {
        errorLabel.setText("❌ " + message);
    }

    /**
     * Switches the controller to spectator mode, disabling all input controls.
     */
    public void enterSpectatorMode() {
        sendButton.setDisable(true);
        commandInputField.setDisable(true);
    }

    private Stage stage;

    /**
     * Sets the stage associated with this controller.
     *
     * @param stage the JavaFX stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Gets the stage associated with this controller.
     *
     * @return the JavaFX stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Displays the result of a dice roll in the dice label.
     * Shows an empty string if the result is 0, otherwise shows the dice emoji with the result.
     *
     * @param result the dice roll result (0 to hide, 1-6 to display)
     */
    public void showDiceResult(int result) {
        if (result == 0) {
            diceLabel.setText("");
            diceLabel.setVisible(true);
        } else {
            diceLabel.setText("🎲 Roll dice result: " + result);
            diceLabel.setVisible(true);
        }
    }

    /**
     * Displays an error message in the error label and makes it visible.
     *
     * @param message the error message to display
     */
    public void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Displays an informational message in the info label and makes it visible.
     *
     * @param message the information message to display
     */
    public void showInfoMessage(String message) {
        infoLabel.setText(message);
        infoLabel.setVisible(true);
    }





}
