package it.polimi.ingsw.is25am31.model.cards;

import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code ContextValidator} class provides static methods to validate and manage
 * the context of card and effect activations within the game. It ensures that player actions
 * align with the rules and the current state of the game, especially concerning card effects
 * that may require specific parameters or involve multiple phases/players.
 * It also keeps track of which players have applied an effect in multi-phase scenarios.
 */
public class ContextValidator {
    private static final List<Integer> playersAppliedOnce = new ArrayList<>();
    private static final List<Integer> playersApplied = new ArrayList<>();
    private static int effectCalledCount = 0;
    private static int playerEffectCount = 0;

    /**
     * Returns the current count of how many times a multi-phase effect has been called.
     *
     * @return The current value of {@code effectCalledCount}.
     */
    public static int getEffectCalledCount() {
        return effectCalledCount;
    }


    /**
     * Resets the internal state of the {@code ContextValidator} class, clearing tracking data and counters.
     * This method is used to prepare the class for a new card effect application.
     */
    public static void reset(){
        playersAppliedOnce.clear();
        playersApplied.clear();
        effectCalledCount = 0;
        playerEffectCount = 0;
    }

    /**
     * Validates the parameters provided by a client before a card context is set.
     * This method checks if the parameters are valid for the current card type and game state.
     * It may also handle immediate game state changes like passing a turn.
     *
     * @param model The current {@link Game} instance.
     * @param playerId The ID of the player attempting to apply the card context.
     * @param parameters A {@link Map} containing the parameters for the card effect.
     * @return An integer indicating the result of the validation:
     * <ul>
     * <li>0: The player declined an action, turn has to be passed.</li>
     * <li>1: The context is valid, but it doesn't need to be passed to the card.</li>
     * <li>2: The context is valid and can be sent to the card.</li>
     * <li>4: The player declined to apply ABANDONED card effect, and the turn has to be passed.</li>
     * </ul>
     * @throws RuntimeException If it's not the player's turn or a prerequisite is missing.
     * @throws IllegalArgumentException If the provided parameters are invalid or missing.
     */
    public static int preCardContextValidation(Game model, int playerId, Map<String, Object> parameters) {
        switch(model.getCurrentCard().getType()){
            case PLANETS:
                if(model.getPlayer(playerId) != model.getCurrentPlayer())
                    throw new RuntimeException("It's not your turn");
                if(parameters.containsKey("decline") && (parameters.get("decline") instanceof Boolean)){
                    model.passTurn();
                    playersApplied.add(playerId);
                    return 0;
                }
                if(!parameters.containsKey("planetIndex") || !(parameters.get("planetIndex") instanceof Integer))
                    throw new IllegalArgumentException("Invalid parameters");
                break;
            case METEORSWARM:
                if(model.getDiceResult() == 0)
                    throw new RuntimeException("Dice must be rolled first");
                parameters.put("diceResult", model.getDiceResult());
                validateDoubleCoordinates(parameters);
                break;
            case PIRATES:
            case SMUGGLERS:
            case SLAVERS:
                if(model.getPlayer(playerId) != model.getCurrentPlayer())
                    throw new RuntimeException("It's not your turn");
            case COMBATZONE:
                validateDoubleCoordinates(parameters);
                break;
            case ABANDONED:
                if(model.getPlayer(playerId) != model.getCurrentPlayer())
                    throw new RuntimeException("It's not your turn");
                if(!parameters.containsKey("accept") || !(parameters.get("accept") instanceof Boolean))
                    throw new IllegalArgumentException("Invalid parameters");
                if(parameters.get("accept").equals(false)) {
                    model.passTurn();
                    return 4;
                }
                return 1;
        }
        // context validato
        return 2;
    }

    /**
     * Validates if the provided parameters contain the necessary double coordinate information (x1, y1, x2, y2).
     *
     * @param parameters A {@link Map} containing the parameters for the card effect.
     * @throws IllegalArgumentException If any of the required coordinate parameters are missing or not integers.
     */
    private static void validateDoubleCoordinates(Map<String, Object> parameters) {
        if(!parameters.containsKey("x1") || !(parameters.get("x1") instanceof Integer)
                || !parameters.containsKey("x2") || !(parameters.get("x2") instanceof Integer)
                || !parameters.containsKey("y1") || !(parameters.get("y1") instanceof Integer)
                || !parameters.containsKey("y2") || !(parameters.get("y2") instanceof Integer))
            throw new IllegalArgumentException("Invalid parameters");
    }

    /**
     * Performs post-card context validation, returning an indicator based on the card type.
     * This method primarily checks if the card type is one that requires other updates.
     *
     * @param model The current {@link Game} instance.
     * @return An integer indicating the type of post-validation needed:
     * <ul>
     * <li>3: For some card types, indicating the updated ship needs to be sent to the client.</li>
     * <li>2: Standard return for other card types, meaning no further actions are necessary.</li>
     * </ul>
     */
    public static int postCardContextValidation(Game model) {
        EffectType type = model.getCurrentCard().getType();
        return (type == EffectType.SLAVERS || type == EffectType.SMUGGLERS || type == EffectType.METEORSWARM || type == EffectType.PIRATES || type == EffectType.COMBATZONE) ? 3 : 2;
    }

    /**
     * Performs pre-effect validation for specific card types before their effects are applied.
     * This method prepares the effect parameters, such as incorporating dice results, and
     * checks for multi-phase effect constraints.
     *
     * @param playerId The ID of the player whose effect is being validated.
     * @param parameters A {@link Map} containing the updated parameters for the effect.
     * @param model The current {@link Game} instance.
     * @return The updated {@link Map} of parameters, including necessary values like dice results.
     * @throws RuntimeException If a dice roll is required but hasn't occurred.
     * @throws IllegalStateException If a player attempts to apply a multi-phase effect out of order.
     */
    public static Map<String, Object> preEffectValidation(int playerId, Map<String, Object> parameters, Game model) {
        switch(model.getCurrentCard().getType()){
            case COMBATZONE:
                if(parameters != null && parameters.containsKey("diceResult") && parameters.size() == 1) {
                    parameters = null;
                }
                if(parameters == null && model.getDiceResult() != 0) {
                    parameters = new HashMap<>();
                    parameters.put("diceResult", model.getDiceResult());
                } else if (parameters != null && parameters.containsKey("shield") && model.getDiceResult() != 0){
                    parameters.put("diceResult", model.getDiceResult());
                }
                break;
            case PIRATES:
                ((PiratesCard) model.getCurrentCard()).setWin(model.getPlayer(playerId));
                if(((PiratesCard) model.getCurrentCard()).isWin())
                    break;
                if(parameters == null && model.getDiceResult() != 0) {
                    parameters = new HashMap<>();
                    parameters.put("diceResult", model.getDiceResult());
                } else if (parameters != null && parameters.containsKey("shield") && model.getDiceResult() != 0){
                    parameters.put("diceResult", model.getDiceResult());
                }
                if(model.getDiceResult() == 0)
                    throw new RuntimeException("Dice must be rolled first");
                if (parameters == null || !parameters.containsKey("diceResult")) {
                    parameters = new HashMap<>(Map.of("diceResult", model.getDiceResult()));
                }
                break;
            case METEORSWARM:
                if(model.getDiceResult() == 0)
                    throw new RuntimeException("Dice must be rolled first");
                if (parameters == null || !parameters.containsKey("diceResult")) {
                    parameters = new HashMap<>(Map.of("diceResult", model.getDiceResult()));
                }
                if(playersAppliedOnce.contains(playerId))
                    throw new IllegalStateException("You have to wait until everybody finishes their turn for this effect.");
                break;
        }
        return parameters;
    }

    /**
     * Handles logic for multi-phase card effects, managing player turns and effect counts.
     * This method is made for cards that have multiple phases and not just context setting and application.
     *
     * @param playerId The ID of the player who just performed an action for the effect.
     * @param model The current {@link Game} instance.
     * @return {@code true} if actual card effect doesn't have to be called because
     * it's just an effect setup.
     * {@code false} if the actual card effect has to be called.
     */
    public static boolean handleMultiPhaseEffect(int playerId, Game model) {
        EffectType cardEffect = model.getCurrentCard().getType();
        if(!playersAppliedOnce.contains(playerId))
            playersAppliedOnce.add(playerId);
        switch (cardEffect) {
            case COMBATZONE:
                if(effectCalledCount == 0){
                    if(playersAppliedOnce.size() == model.getPlayers().size()){
                        model.orderPlayers();
                        model.setCurrentPlayer(model.getPlayer(((CombatZoneCard)model.getCurrentCard()).getAllowedPlayer(model.getPlayers())));
                        effectCalledCount++;
                    }
                    return true;
                }
                break;
            case METEORSWARM:
                if (playersAppliedOnce.size() == model.getPlayers().size()) {
                    model.resetDiceResult();
                    playersAppliedOnce.clear();
                }
                break;
        }
        return false;
    }

    /**
     * Checks if a card effect has been fully resolved for all relevant players or conditions.
     * This method is called after an effect has been applied by a player and determines
     * if the turn should pass or if the overall effect is concluded.
     *
     * @param playerId The ID of the player who just applied an effect.
     * @param model The current {@link Game} instance.
     * @return An integer indicating the resolution status:
     * <ul>
     * <li>0: Effect not fully resolved for all necessary players, continue current phase.</li>
     * <li>1: Effect partially resolved, turn passes to the next player.</li>
     * <li>2: Effect fully resolved for all players/conditions, clear tracking and proceed.</li>
     * </ul>
     */
    public static int isEffectResolved(int playerId, Game model) {
        EffectType cardEffect = model.getCurrentCard().getType();
        if(cardEffect == EffectType.COMBATZONE || cardEffect == EffectType.PIRATES) {
            model.resetDiceResult();
        }
        if (cardEffect == EffectType.COMBATZONE) {
            model.setCurrentPlayer(model.getPlayer(model.getLeader()));
            playerEffectCount++;
            effectCalledCount = 0;
            playersAppliedOnce.clear();
            if(playerEffectCount == 3) {
                playerEffectCount = 0;
                return 2;
            } else if(playerEffectCount == 2 && !model.getCurrentCard().isTestFlight()) {
                for (Player player : model.getPlayers()) {
                    model.applyCardEffect(player, null);
                }
                return 0;
            }
            return 1;
        }
        if (!playersApplied.contains(playerId)) {
            playersApplied.add(playerId);

            // se la carta nave o stazione abbandonata è stata usata 1 volta l'effetto è risolto per tutti
            if (cardEffect == EffectType.ABANDONED) {
                return 2;
            }
            // se un giocatore vince contro i nemici l'effetto è risolto per tutti
            else if (cardEffect == EffectType.SMUGGLERS && ((SmugglersCard) model.getCurrentCard()).isWin() ||
                    cardEffect == EffectType.SLAVERS && ((SlaversCard) model.getCurrentCard()).isWin() ||
                    cardEffect == EffectType.PIRATES && ((PiratesCard) model.getCurrentCard()).isWin()) {
                return 2;
            }
            // ritorno standard per effetto concluso per qualche giocatore ma non per tutti
            if (playersApplied.size() != model.getPlayers().size()) {
                model.passTurn();
                return 1;
            }

            // ritorno standard per effetto concluso
            playersApplied.clear();
            playersAppliedOnce.clear();
            return 2;
        }
        return 0;
    }
}