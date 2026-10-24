package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.customerrors.NoCardsLeftException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code Deck} class represents a collection of cards used within the game.
 * It contains both the full deck and the initial sub decks.
 */
public class Deck {
    private List<Card> deck;
    private List<List<Card>> subDecks;

    /**
     * Creates 4 sub decks of 2 level 2 cards and 1 level 1 card if the game level is 2,
     * otherwise creates a deck of 8 test flight cards.
     *
     * @param level Level of the game, used to create the right deck for a specified level.
     */
    public Deck(int level){
        CardFactory cf = new CardFactory();
        if(level == 2) {
            subDecks = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                List<Card> cards = new ArrayList<>();
                for (int j = 0; j < 2; j++) {
                    cards.add(cf.getRandomCard(2));
                }
                cards.add(cf.getRandomCard(1));
                subDecks.add(cards);
            }
        } else {
            deck = cf.getTestFlightCards();
            Collections.shuffle(deck);
        }
    }

    /**
     * Merges the sub decks in the main deck.
     */
    public void mergeSubDecks(){
        if(subDecks == null)
            throw new RuntimeException("Cannot merge sub decks in test flight mode");

        deck = new ArrayList<>();
        for(List<Card> cards : subDecks)
            deck.addAll(cards);

        Collections.shuffle(deck);
    }


    /**
     *
     * @return The first card of the deck.
     * @throws NoCardsLeftException if there are no cards left in the deck
     * (this condition should never occur in normal conditions).
     */
    public Card drawCard() throws NoCardsLeftException {
        if(deck == null)
            throw new RuntimeException("Deck not initialized");
        if (deck.isEmpty())
            throw new NoCardsLeftException("There are no cards left to draw");
        return deck.removeFirst();
    }

    /**
     *
     * @param index Index of the required sub deck.
     * @return Sub deck in the specified index.
     */
    public List<Card> getSubDeck(int index) {
        return subDecks.get(index);
    }

    /**
     *
     * @return {@code true} If deck has no more cards in it, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return deck.isEmpty();
    }
}
