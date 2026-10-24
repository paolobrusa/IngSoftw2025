package it.polimi.ingsw.is25am31.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a cargo hold component capable of storing goods with specific constraints.
 * The cargo hold has a defined maximum capacity and can optionally accept special goods.
 */
public class CargoHold extends Component {
    @Serial
    private static final long serialVersionUID = -4824442075088666129L;
    private final int capacity;
    private final boolean special;
    private final List<Good> contains;

    /**
     * Constructs a cargo hold with specified properties
     *
     * @param id           unique component identifier
     * @param name         descriptive name of the cargo hold
     * @param connector    map of side-to-connector mappings
     * @param orientation  initial orientation of the component
     * @param capacity     maximum storage capacity
     * @param special      whether RED goods are accepted
     */

    public CargoHold(@JsonProperty("id") int id,
                     @JsonProperty("name") String name,
                     @JsonProperty("connector") Map<Side, Connector> connector,
                     @JsonProperty("orientation") Side orientation,
                     @JsonProperty("capacity") int capacity,
                     @JsonProperty("special") boolean special)
    {
        super(id, name, connector, orientation);
        this.capacity = capacity;
        this.special = special;
        this.contains = new ArrayList<>(); // Inizializziamo la lista vuota
    }

    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Attempts to add a good to the hold
     *
     * @param good the Good to add
     * @return true if the good was added, false otherwise
     */

    public boolean addContains(Good good) {
        if (good == Good.RED && !special) {
            return false;
        }
        if (contains.size() < capacity) {
            contains.add(good);
            return true;
        }
        return false;
    }

    /**
     * Removes a specific good from the hold
     *
     * @param good the Good to remove
     * @return true if the good was removed, false if it was not present
     */
    @Override
    public boolean removeContains(Good good) {
        return contains.remove(good);
    }

    /**
     * Returns a list of goods currently stored
     *
     * @return unmodifiable list of goods
     */

    public List<Good> getContains() {
        return contains;
    }

    /**
     * Returns an ASCII art representation of the cargo hold,
     * displaying its type, capacity, and stored goods
     *
     * @return array of ASCII art lines
     */

    @Override
    public String[] getAsciiArt() {
        String tipo = special ? "CARGO_S" : "CARGO";
        String header = tipo + "-" + capacity;

        List<String> goodsSymbols = contains.stream()
                .map(g -> switch (g) {
                    case RED -> "R";
                    case BLUE -> "B";
                    case GREEN -> "G";
                    case YELLOW -> "Y";
                })
                .collect(Collectors.toList());

        while (goodsSymbols.size() < capacity) {
            goodsSymbols.add("-");
        }

        String line = header + " [" + String.join(",", goodsSymbols) + "]";

        return new String[]{
                "           ",
                center(line, 17),
                "           "
        };
    }

    /**
     * Centers a string within a fixed width, padding with spaces
     *
     * @param str   input string
     * @param width target width
     * @return padded string of length width
     */

    private String center(String str, int width) {
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    @Override
    public int getMaxGoodValue() {
        return contains.stream()
                .mapToInt(Good::getValue)
                .max()
                .orElse(0);
    }



}