package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.model.player.Ship;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for displaying an opponent's ship in a separate window.
 * Shows the ship components with their current state including crew members,
 * cargo contents, battery levels, and aliens. Provides a read-only view of
 * another player's ship configuration.
 */
public class OpponentShipController {

    @FXML
    private ImageView ShipLevel1;
    @FXML
    private ImageView ShipLevel2;
    @FXML
    private AnchorPane anchorPane;
    private final Map<String, ImageView> gridImages = new HashMap<>();
    private final Map<String, Label> gridLabels = new HashMap<>();
    private final Map<Integer, Image> componentImages = new HashMap<>();
    private final Map<Integer, Image> mainCabinImages = new HashMap<>();

    private Ship ship;

    @FXML
    private void initialize() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof ImageView imageView && imageView.getId() != null) {
                gridImages.put(imageView.getId(), imageView);
            } else if (node instanceof Label label && label.getId() != null) {
                gridLabels.put(label.getId(), label);
            }
        }

        loadComponentImages();
        loadMainCabinImages();
    }

    private void loadComponentImages() {
        for (int id = 1; id <= 156; id++) {
            if (id == 33 || id == 34 || id == 52 || id == 61) continue;
            String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
            try (var stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    componentImages.put(id, new Image(stream));
                }
            } catch (Exception e) {
                System.err.println("Errore immagine componente ID " + id + ": " + e.getMessage());
            }
        }
    }

    private void loadMainCabinImages() {
        int[] ids = {33, 34, 52, 61};
        for (int id : ids) {
            String path = "/it/polimi/ingsw/is25am31/image/tiles/GT-new_tiles_16_for web" + id + ".jpg";
            try (var stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    mainCabinImages.put(id, new Image(stream));
                }
            } catch (Exception e) {
                System.err.println("Errore caricando main cabin ID " + id + ": " + e.getMessage());
            }
        }
    }

    /**
     * Displays the opponent's ship with the specified level.
     * Shows the appropriate ship level background and updates the ship view.
     *
     * @param ship the opponent's ship to display
     * @param level the ship level (1 or 2)
     */
    public void showShip(Ship ship, int level) {
        this.ship = ship;
        if(level == 1) {
            ShipLevel1.setVisible(true);
            ShipLevel2.setVisible(false);
        }
        updateShipView();
    }

    private void updateShipView() {
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
                                System.err.println("⚠Errore caricamento MainCabin: " + path);
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
}
