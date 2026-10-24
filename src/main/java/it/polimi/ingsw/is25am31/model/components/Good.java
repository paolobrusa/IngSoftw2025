package it.polimi.ingsw.is25am31.model.components;

/**
 * Enumeration representing different types of goods with their associated values.
 * This enum defines the various goods that can be collected, traded, or
 * used within the game system. Each good has both a color identifier and
 * a numerical value that represents its worth, rarity, or importance in
 * the game mechanics.
 * The goods are ordered by their value in descending order, with RED
 * being the most valuable and BLUE being the least valuable. This value
 * system can be used for scoring, trading calculations, or determining
 * the relative importance of different goods.
 * The available goods and their values are:
 * {@link #RED} - Value: 4 (highest value),
 * {@link #YELLOW} - Value: 3,
 * {@link #GREEN} - Value: 2,
 * {@link #BLUE} - Value: 1 (lowest value).
 * @see Color
 */
public enum Good {
    RED(4),
    YELLOW(3),
    GREEN(2),
    BLUE(1);

    private final int value;

    /**
     * Constructs a Good with the specified value.
     *
     * @param value the numerical value of this good
     */
    Good(int value) {
        this.value = value;
    }

    /**
     * Returns the numerical value of this good.
     *
     * @return the numerical value of this good
     */
    public int getValue() {
        return value;
    }

    /**
     * Parses a string representation of a good and returns the corresponding Good enum.
     * Supported string values:
     * "red" -> {@link #RED},
     * "yellow" -> {@link #YELLOW},
     * "green" -> {@link #GREEN},
     * "blue" -> {@link #BLUE}.
     *
     * @param good the string representation of the good (must be lowercase)
     * @return the Good enum constant corresponding to the input string
     * @throws RuntimeException if the input string does not match any valid good
     * @see #RED
     * @see #YELLOW
     * @see #GREEN
     * @see #BLUE
     */
    public static Good parseGood(String good) {
        Good requested;
        switch (good) {
            case "yellow" -> requested = Good.YELLOW;
            case "red" -> requested = Good.RED;
            case "green" -> requested = Good.GREEN;
            case "blue" -> requested = Good.BLUE;
            default -> throw new RuntimeException("Invalid color");
        }
        return requested;
    }
}
