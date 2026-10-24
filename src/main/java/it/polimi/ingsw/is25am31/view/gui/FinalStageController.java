package it.polimi.ingsw.is25am31.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.util.Map;

/**
 * Controller for displaying the final game results and player rankings.
 * Shows the final scores in a sorted order and provides an option to close
 * the application.
 */
public class FinalStageController {

    @FXML
    private TextArea rankingTextArea;

    /**
     * Sets and displays the final game ranking in the text area.
     * Players are sorted by their ranking value in ascending order (best rank first).
     *
     * @param ranking a map containing players and their final ranking positions
     */
    public void setRanking(Map<Player, Integer> ranking) {
        StringBuilder sb = new StringBuilder();
        ranking.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> sb.append(entry.getKey().getNickname())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n"));
        rankingTextArea.setText(sb.toString());
    }

    /**
     * Handles the close button click event.
     * Closes the ranking window and terminates the entire application.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) rankingTextArea.getScene().getWindow();
        stage.close();
        System.out.println("Disconnecting in progress");
        Platform.exit();
        System.exit(0);
    }
}
