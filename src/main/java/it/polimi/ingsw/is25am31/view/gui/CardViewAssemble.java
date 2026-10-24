package it.polimi.ingsw.is25am31.view.gui;

import it.polimi.ingsw.is25am31.model.cards.Card;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.ListIterator;

/**
 * Controller for displaying a sub-deck of cards during the assembly phase.
 * Shows card images in a ListView for player reference.
 */
public class CardViewAssemble {

    @FXML
    private ListView <ImageView> faceUpListCard;

    /**
     * Displays a collection of cards in the face-up card list view.
     * Each card is loaded as an image from the resources folder and added to the ListView.
     *
     * @param subDeck the list of cards to display in the ListView
     */
    public void showSubDeck(List<Card> subDeck) {
        ListIterator<Card> iterator = subDeck.listIterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            try (var stream = getClass().getResourceAsStream(
                    "/it/polimi/ingsw/is25am31/image/cards/" + card.getId() + ".jpg")) {
                if (stream != null) {
                    faceUpListCard.getItems().add(new ImageView(new Image(stream)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
