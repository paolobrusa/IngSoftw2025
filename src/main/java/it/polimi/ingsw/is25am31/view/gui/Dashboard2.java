package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.view.ClientModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Controller for the main game dashboard during flight phase 2.
 * Displays the player's ship, rocket positions on the track, and manages
 * game actions such as drawing cards, viewing opponent ships, and handling
 * player elimination or abandonment.
 */
public class Dashboard2 {

    @FXML
    private AnchorPane anchorPane;

    private Ship ship;

    private final Map<String, ImageView> gridImages = new HashMap<>();
    private final Map<String, Label> gridLabels = new HashMap<>();
    private final Map<Integer, Image> componentImages = new HashMap<>();
    private VirtualServer server;
    private ClientModel client;
    private final Map<Integer, ImageView> rocketImages = new HashMap<>();


    /**
     * Sets the virtual server and client model for this dashboard.
     * Initializes the view ship buttons and loads rocket images.
     *
     * @param server the virtual server instance for network communication
     * @param client the client model containing game state information
     */
    public void setServerAndClient(VirtualServer server, ClientModel client) {
        this.server = server;
        this.client = client;


        viewShipButtons = List.of(viewShipButton1, viewShipButton2, viewShipButton3);

        updateViewShipButtons();
        loadRockets();
    }

    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof ImageView imageView && imageView.getId() != null) {
                String id = imageView.getId();


                gridImages.put(id, imageView);


                if (id.startsWith("rocket")) {
                    try {
                        int pos = Integer.parseInt(id.substring(6));
                        rocketImages.put(pos, imageView);
                    } catch (NumberFormatException e) {
                        System.err.println("ID razzo non valido: " + id);
                    }
                }

            } else if (node instanceof Label label && label.getId() != null) {
                gridLabels.put(label.getId(), label);
            }
        }

        loadComponentImages();
    }


    private void loadComponentImages() {
        for (int id = 1; id <= 156; id++) {
            String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
            try (var stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    componentImages.put(id, new Image(stream));
                }
            } catch (Exception e) {
                System.err.println("Errore caricando immagine ID " + id + ": " + e.getMessage());
            }
        }
    }

    /**
     * Displays the specified ship on the dashboard.
     *
     * @param ship the ship to display
     */
    public void showShip(Ship ship) {
        this.ship = ship;
        updateShipView();
    }

    private void updateShipView() {
        for (int row = 5; row <= 9; row++) {
            for (int col = 4; col <= 10; col++) {
                String id = row + "," + col;

                ImageView imageView = gridImages.get(id);
                Label label = gridLabels.get(id);

                if (imageView != null) {
                    imageView.setImage(null);
                    imageView.setVisible(false);
                }
                if (label != null) {
                    label.setText("");
                    label.setVisible(false);
                }

                Component c = ship.getComponent(row - 5, col - 4);
                if (c == null) continue;

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
                            System.err.println("⚠ Errore caricamento MainCabin: " + path);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore MainCabin ID " + mainCabinImageId + ": " + e.getMessage());
                    }

                    if (label != null) {
                        label.setText(String.valueOf(cabin.getCrewMembers()));
                        label.setVisible(true);
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
                    }
                }

                if (label != null) {
                    if (c instanceof BatteryComponent b) {
                        label.setText(b.getCapacity() + "/" + b.getMaxCapacity());
                        label.setVisible(true);

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
                        label.setVisible(true);

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
                        label.setText(String.valueOf(mainCabin.getCrewMembers()) + "/2");
                        label.setVisible(true);

                    } else {
                        label.setVisible(false);
                    }
                }
            }
        }
    }


    @FXML private Button viewShipButton1;
    @FXML private Button viewShipButton2;
    @FXML private Button viewShipButton3;

    private List<Button> viewShipButtons;
    private List<String> otherNicknames = new ArrayList<>();


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
     * Opens a new window displaying the opponent's ship.
     *
     * @param ship the opponent's ship to display
     */
    public void showOpponentShip(Ship ship) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am31/opponentShipView.fxml"));
            Parent root = loader.load();
            OpponentShipController controller = loader.getController();
            controller.showShip(ship, 2); //ho cambiato cosi con livello due dato che ancora manca la dashboard di livello 1
            Stage stage = new Stage();
            stage.setTitle("Opposing Ship");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays rocket images on the track based on each player's position.
     * Clears existing rocket images before loading new ones.
     */
    public void loadRockets() {

        for (ImageView view : rocketImages.values()) {
            view.setImage(null);
            view.setVisible(false);
        }



        List<Player> players = client.getPlayers();
        for (Player p : players) {
            int position = p.getRocket().getPosition();
            int pos = ((position % 24) + 24) % 24;
            ImageView view = rocketImages.get(pos);

            if (view != null) {
                String color = switch (p.getRocket().getColor()) {
                    case RED -> "rosso";
                    case BLUE -> "blu";
                    case GREEN -> "verde";
                    case YELLOW -> "giallo";
                };
                String path = "/it/polimi/ingsw/is25am31/image/rockets/razzo_" + color + ".jpeg";
                try (var stream = getClass().getResourceAsStream(path)) {
                    if (stream != null) {
                        view.setImage(new Image(stream));
                        view.setVisible(true);
                    }
                } catch (Exception e) {
                    System.err.println("Errore caricando immagine razzo: " + e.getMessage());
                }
            }
        }
    }


    @FXML
    private Button drawCardButton;

    private boolean isLeader;

    /**
     * Sets whether the current player is the leader and updates UI accordingly.
     * Enables/disables the draw card button and updates the notification label.
     *
     * @param isLeader true if the player is the leader, false otherwise
     */
    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;

        if (drawCardButton != null)
            drawCardButton.setDisable(!isLeader);

        if (notificationLabel != null) {
            if (isLeader) {
                notificationLabel.setText("You are the LEADER, draw a card.");
            } else {
                notificationLabel.setText("Wait for the LEADER to draw a card.");
            }
        }
    }


    @FXML
    private Label notificationLabel;

    @FXML
    private Button drawcardButton;

    @FXML
    private void handleDrawCard() {
        try {
            server.drawCard(client.getClientId());
            drawCardButton.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an elimination message and disables all interactive controls.
     *
     * @param reasonCode the code indicating the reason for elimination:
     * 1 - engine power is 0
     * 2 - no crew members left
     * 3 - lapped by another player
     * 4 - abandoned the race
     */
    public void showElimination(int reasonCode) {
        String reason = switch (reasonCode) {
            case 1 -> "You lost because your engine power is 0";
            case 2 -> "You lost because you don't have any crew members left";
            case 3 -> "You lost because someone lapped you";
            case 4 -> "You have abandoned the race";
            default -> throw new IllegalStateException("Unexpected value: " + reasonCode);
        };

        notificationLabel.setText(reason);

        for (Node node : anchorPane.lookupAll(".button")) {
            if (node instanceof Button btn) {
                String text = btn.getText();
                if (!text.contains("Ship")) {
                    btn.setDisable(true);
                }
            }
        }

        for (Node node : anchorPane.lookupAll(".text-field")) {
            node.setDisable(true);
        }
    }

    private Stage stage;

    /**
     * Sets the stage associated with this dashboard.
     *
     * @param stage the JavaFX stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Gets the stage associated with this dashboard.
     *
     * @return the JavaFX stage
     */
    public Stage getStage() {
        return stage;
    }

    @FXML
    private Button abandonButton;

    private Gui gui;

    /**
     * Sets the GUI instance for this dashboard.
     *
     * @param gui the GUI instance
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private void handleAbandonRace() {
        try {
            gui.getServer().abandonRace(gui.getClient().getClientId());
            notificationLabel.setText("You left the race. Now you are a spectator.");
            abandonButton.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
