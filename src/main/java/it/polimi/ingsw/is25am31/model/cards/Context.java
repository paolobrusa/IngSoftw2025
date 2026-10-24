package it.polimi.ingsw.is25am31.model.cards;

import java.util.Map;


/**
 * This class is used to make call to setCardContext and applyEffect uniform between all the specific card types.
 * It contains all the parameters to pass in order to apply the specific effects for the specific cards.
 * Only the useful parameters get assigned depending on the specific card's {@link EffectType}.
 * An instance of Context is then passed to the setCardContext or applyEffect method that can extract specific
 * information that it needs from it using {@code getParameters()}.
 */
public class Context {
    Map<String, Object> parameters;

    /**
     * Constructs a new {@code Context} object with the given parameters.
     *
     * @param parameters A map containing key-value pairs representing the parameters
     * required for the specific card's effects or other contextual data.
     */
    public Context(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Retrieves a parameter from the context's internal parameter map, casting it to the specified type.
     *
     * @param key The key associated with the parameter to retrieve.
     * @param type The {@link Class} representing the expected type of the parameter.
     * @param <T> The type to which the parameter will be cast.
     * @return The parameter corresponding to the given key, cast to the specified type, or {@code null} if the key is not present in the map.
     * @throws ClassCastException If the object associated with the key cannot be cast to the specified type.
     */
    public <T> T getParameters(String key, Class<T> type) {
        return type.cast(parameters.get(key));
    }
}
