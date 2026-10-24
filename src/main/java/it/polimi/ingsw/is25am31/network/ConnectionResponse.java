package it.polimi.ingsw.is25am31.network;

import java.io.Serializable;

/**
 * Represents the possible responses to a connection attempt to a game.
 * This enum defines the states or outcomes that a client can encounter during the connection process.
 */
public enum ConnectionResponse implements Serializable {
    /**
     * Indicates that the client is the first player of the game.
     */
    FIRST_PLAYER,
    /**
     * Indicates that the game is already in progress.
     */
    IN_PROGRESS,
    /**
     * Indicates that the client has to wait for the creation of a new game session.
     */
    WAIT_CREATION,
    /**
     * Indicates that the client is connected to the game and required to set a nickname.
     */
    SET_NICKNAME,
}
