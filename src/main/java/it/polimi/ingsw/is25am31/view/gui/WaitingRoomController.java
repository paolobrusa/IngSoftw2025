package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.view.ClientModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller for the waiting room between game phases.
 * Manages rocket position selection for level 2 games and displays/controls
 * the hourglass timer system. Handles the transition between assembly phase
 * and flight phase while players wait for others to complete their actions.
 */
public class WaitingRoomController {

    @FXML private TextField positionField;
    @FXML private Label availablePositionsLabel;
    @FXML private Label errorLabel;
    @FXML private Button setPositionButton;
    @FXML private Button timerButton;
    private int count;
    private Timeline timeline;
    private Timeline timelineEnt;
    private int secondsL = 90;

    private VirtualServer server;
    private ClientModel client;

    /**
     * Sets the server and client model for this controller.
     * Configures the UI based on the game level (shows position selection for level 2).
     *
     * @param server the virtual server for network communication
     * @param client the client model containing game state
     */
    public void setServerAndClient(VirtualServer server, ClientModel client) {
        this.server = server;
        this.client = client;

        int level = client.getLevel();

        if (level == 2) {

            positionField.setVisible(true);
            setPositionButton.setVisible(true);
            availablePositionsLabel.setVisible(true);
            updateAvailablePositions();
        } else {

            positionField.setVisible(false);
            setPositionButton.setVisible(false);
            availablePositionsLabel.setVisible(false);
            timerButton.setVisible(false);
        }
    }

    /**
     * Sets the timer count and configures the timer button behavior.
     * Handles different timer phases and continues countdown if already in progress.
     *
     * @param count the current timer count (0-4)
     * @param seconds the remaining seconds if timer is already running
     */
    public void setCount(int count, int seconds) {
        this.count = count;
        if(count < 3) {
            timerButton.setText("Timer " + (count+1));
            timerButton.setOnAction(this::startHourGlass);
        }
        else {
            timerButton.setText("Timer " + (count+1));
            timerButton.setOnAction(this::startFinalHourGlass);
            if(count == 4){
                timerButton.setDisable(true);
                timerButton.setText("N.T");
            }
        }
        if (seconds != 90){
            timerButton.setDisable(true);
            AtomicInteger sec = new AtomicInteger((seconds-1));
            timelineEnt = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                int s = sec.getAndDecrement();
                timerButton.setText(s + "s");
                if(s <= 0) {
                    timerButton.setDisable(false);
                    timerButton.setText("Timer " + (count + 1));
                    timelineEnt.stop();
                }
            }));
            timelineEnt.setCycleCount(Timeline.INDEFINITE);
            timelineEnt.play();
        }
    }

    private void startFinalHourGlass(ActionEvent event){
        try {
            server.startFinalHourGlass(client.getClientId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHourGlass(ActionEvent actionEvent){
        try {
            server.startHourGlass(client.getClientId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTimeline(int counter) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsL--;
            timerButton.setText(secondsL + "s");
            if(secondsL <= 0) {
                if(counter == 4) {
                    timerButton.setDisable(true);
                    timerButton.setText("N.T");
                    secondsL = 90;
                    timeline.stop();
                }
                else{
                    timerButton.setDisable(false);
                    timerButton.setText("Timer " + (counter + 1));
                    secondsL = 90;
                    if(counter == 3) {
                        timerButton.setOnAction(this::startFinalHourGlass);
                    }
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
     * @param seconds the seconds remaining (not used in this implementation)
     */
    public void updateTimer(int counter, int seconds) {
        count = counter;
        timerButton.setDisable(true);
        initializeTimeline(counter);
        timeline.play();
    }

    private void updateAvailablePositions() {
        List<Player> players = client.getPlayers();
        StringBuilder sb = new StringBuilder("Available rockets positions: ");
        for (int i = 1; i <= players.size(); i++) {
            int finalI = i;
            boolean occupied = players.stream().anyMatch(p -> p.getRocket().getPosition() == finalI);
            if (!occupied) sb.append(i).append(" ");
        }
        availablePositionsLabel.setText(sb.toString());
    }


    /**
     * Gets the error label component.
     *
     * @return the error label
     */
    public Label getErrorLabel() {
        return errorLabel;
    }

    /**
     * Unlocks the position input field and button.
     * Uses Platform.runLater for thread-safe UI updates.
     */
    public void unlockPositionField() {
        Platform.runLater(() -> {
            positionField.setDisable(false);
            setPositionButton.setDisable(false);
            positionField.requestFocus();
        });
    }



    @FXML
    private void handleSetPosition() {
        String input = positionField.getText();
        errorLabel.setText("");

        try {
            int position = Integer.parseInt(input);
            int max = client.getPlayers().size();

            if (position < 1 || position > max) {
                errorLabel.setText("Position out of range (1 - " + max + ")");
                return;
            }

            boolean occupied = client.getPlayers().stream()
                    .anyMatch(p -> p.getRocket().getPosition() == position);

            if (occupied) {
                errorLabel.setText("Position already occupied");
                return;
            }

            try {
                server.placeRocket(client.getClientId(), position);
            } catch (Exception e) {
                GuiRenderer.getInstance().reportException(e);
                return;
            }

            errorLabel.setText("✅ Position assigned");
            positionField.setDisable(true);
            setPositionButton.setDisable(true);
            updateAvailablePositions();

        } catch (NumberFormatException e) {
            errorLabel.setText("Enter a number");
        } catch (RuntimeException e) {
            errorLabel.setText(" " + e.getMessage());
        }
    }



}

