package it.polimi.ingsw.is25am31.view;

/**
 * Enumeration representing the different states a client can be in during the game.
 * Each state corresponds to a specific phase or condition of the client's interaction
 * with the game, from initial connection through game completion.
 * The client state determines what actions are available to the player and
 * how the user interface should be presented
 */
public enum ClientState {
    GAME_SELECTION,
    LOCKED,
    NICKNAME_SETTING,
    INITIAL,
    LOBBY,
    ASSEMBLE,
    ENDED_ASSEMBLE,
    CREW_PLACING,
    FLIGHT,
    EFFECT_RESOLUTION,
    ENDGAME,
    SPECTATOR
}
