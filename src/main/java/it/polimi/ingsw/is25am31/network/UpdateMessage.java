package it.polimi.ingsw.is25am31.network;

/**
 * The {@code UpdateMessage} enum represents a set of predefined messages
 * that are used to communicate the status of a game-related event to a client.
 * <br>
 * Enum Constants: <br>
 * - {@code GAME_FULL}: Indicates that the game is full and additional players
 *   cannot join at this time. <br>
 * - {@code QUIT_GAME}: Indicates that a player has chosen to quit the game. <br>
 * - {@code NOT_ENOUGH_CREWMEMBERS}: Indicates that there are not enough crew
 *   members to start ABANDONED cards' effect application. <br>
 * - {@code HOURGLASS_NOT_AVAILABLE}: Indicates that the standard hourglass
 *   is not available for use. <br>
 * - {@code FINAL_HOURGLASS_NOT_AVAILABLE}: Indicates that the final hourglass
 *   is not available for use. <br>
 * - {@code PLAYER_DISCONNECTED}: Indicates that a player has disconnected from
 *   the game unexpectedly. <br>
 * - {@code CREATE_OR_JOIN}: Indicates that the client should create a new game
 *   or join an existing one. <br>
 */
public enum UpdateMessage {
        GAME_FULL,
        QUIT_GAME,
        NOT_ENOUGH_CREWMEMBERS,
        HOURGLASS_NOT_AVAILABLE,
        FINAL_HOURGLASS_NOT_AVAILABLE,
        PLAYER_DISCONNECTED,
        CREATE_OR_JOIN,
}