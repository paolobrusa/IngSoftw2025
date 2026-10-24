package it.polimi.ingsw.is25am31.view.gui;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton manager for handling the closing of all application stages.
 * Ensures proper cleanup and termination of the JavaFX application when any stage is closed.
 */
public class CloseStageManager {
    private static CloseStageManager closeStageManager;
    private List<Stage> allStages = new ArrayList<>();

    private CloseStageManager() {}

    /**
     * Gets the singleton instance of CloseStageManager.
     * Creates a new instance if one doesn't exist.
     *
     * @return the singleton instance of CloseStageManager
     */
    public static CloseStageManager getCloseStageManager() {
        if (closeStageManager == null) {
            closeStageManager = new CloseStageManager();
        }
        return closeStageManager;
    }

    /**
     * Registers a stage with the manager and sets up its close request handler.
     * When the stage is closed, it will trigger the application exit process.
     *
     * @param stage the JavaFX stage to be managed
     */
    public void addStage(Stage stage) {
        allStages.add(stage);
        stage.setOnCloseRequest(event -> {
            exitApplication();
        });
    }

    /**
     * Terminates the entire application.
     * Shuts down the JavaFX platform and exits the JVM with status code 0.
     */
    public void exitApplication() {
        System.out.println("Disconnecting in progress");
        Platform.exit();
        System.exit(0);
    }

    /**
     * Closes all managed stages that are currently showing.
     * Clears the list of managed stages after closing them.
     */
    public void closeAllStages() {
        for (Stage stage : allStages) {
            if (stage.isShowing()) {
                Platform.runLater(stage::close);
            }
        }
        allStages.clear();
    }

}
