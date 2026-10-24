package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

/**
 * Controller for the game selection screen.
 * Allows players to view available game lobbies, create new games, or join existing ones.
 * Manages the transition between game selection and the main game interface.
 */
public class GameSelectorController {
    private VirtualView client;
    private VirtualServer server;
    private UUID clientId;

    /**
     * Sets the client view, server, and client ID for this controller.
     *
     * @param client the virtual view representing the client interface
     * @param server the virtual server for network communication
     * @param clientId the unique identifier of the client
     */
    public void setClientAndServer(VirtualView client, VirtualServer server, UUID clientId) {
        this.client = client;
        this.server = server;
        this.clientId = clientId;
    }

    @FXML
    private void handleShowLobbies() {
        try {
            server.viewGame(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleCreateNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/hello-view.fxml"));
            Parent root = loader.load();

            Gui gui = loader.getController();
            gui.setGuiRenderer(GuiRenderer.getInstance());
            Gui.setServer(server);
            Gui.setClient(client);
            GuiRenderer.getInstance().setPromptLabel(gui.getTextPrompt());

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            CloseStageManager.getCloseStageManager().addStage(stage);
            stage.setTitle("CREATE GAME");
            stage.setScene(scene);
            stage.show();
            GuiRenderer.getInstance().setGuiController(gui);

            Stage oldStage = (Stage) gameListView.getScene().getWindow();
            oldStage.close();


            server.createGame(clientId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML private ListView<String> gameListView;

    @FXML
    private void handleJoin() {
        String selected = gameListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int gameId = extractId(selected);
            try {
                server.joinGame(clientId, gameId);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/hello-view.fxml"));
                Parent root = loader.load();

                Gui guiController = loader.getController();
                guiController.setGuiRenderer(GuiRenderer.getInstance());
                Gui.setClient(client);
                Gui.setServer(server);
                GuiRenderer.getInstance().setPromptLabel(guiController.getTextPrompt());

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("JOIN GAME");
                stage.setScene(scene);
                stage.show();


                GuiRenderer.getInstance().setGuiController(guiController);

                Stage oldStage = (Stage) gameListView.getScene().getWindow();
                oldStage.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the game list view with the provided list of available games.
     * Uses Platform.runLater to ensure thread-safe UI updates.
     *
     * @param games the list of game descriptions to display
     */
    public void setGames(List<String> games) {
        Platform.runLater(() -> {
            gameListView.getItems().clear();
            gameListView.getItems().addAll(games);
        });
    }


    private int extractId(String s) {
        try {
            return Integer.parseInt(s.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Sets the client view for this controller.
     *
     * @param client the virtual view representing the client interface
     */
    public void setClient(VirtualView client) {
        this.client = client;
    }




}

