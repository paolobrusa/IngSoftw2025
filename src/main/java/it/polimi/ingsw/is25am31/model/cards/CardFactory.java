package it.polimi.ingsw.is25am31.model.cards;

import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

/**
 * {@code CardFactory} is responsible for managing and handling a collection of cards
 * used in the game. It reads card data from a predefined JSON file
 * and provides functionality to retrieve specific cards based on
 * certain criteria, including randomness and test flight availability.
 */
public class CardFactory {
    private final List<Card> allCards;
    private final Set<Card> usedCards;

    /**
     *
     * Reads the JSON file with cards info, puts it into a stream and then creates a list of all the Cards
     */
    public CardFactory(){
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cards.json");
        try {
            allCards = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        usedCards = new HashSet<>();
    }


    /**
     *
     * @param level Level of the required card.
     * @return Random card of a specified level that hasn't already been returned.
     */
    public Card getRandomCard(int level){
        int randomIndex;
        do{
           randomIndex = (int)(Math.random() * 20);
            if(level == 2)
                randomIndex += 20;
        }while(usedCards.contains(allCards.get(randomIndex)));

        usedCards.add(allCards.get(randomIndex));
        return allCards.get(randomIndex);
    }

    /**
     *
     * @return All 8 test flight cards.
     */
    public List<Card> getTestFlightCards(){
        return new ArrayList<>(allCards.stream().filter(Card::isTestFlight).toList());
    }
}
