package it.polimi.ingsw.is25am31.model.components;

import java.io.Serializable;

/**
 * Enumeration representing the different types of connectors available in the game.
 * This enum defines the various connector types that can be used to link,
 * attach, or interface different game components. Connectors determine how
 * game elements can be combined, connected, or interact with each other
 * within the game system.
 * This enum implements {@link Serializable} to support serialization
 * of connector data for game state persistence, network communication,
 * or other data storage requirements.
 * The available connector types are:
 * {@link #SINGLE} - Single connection point,
 * {@link #DOUBLE} - Double connection point,
 * {@link #UNIVERSAL} - Universal connector (compatible with all types),
 * {@link #NONE} - No connector present,
 * {@link #ENGINE} - Engine-specific connector,
 * {@link #CANNON} - Cannon-specific connector.
 *
 */
public enum Connector implements Serializable {
    SINGLE, DOUBLE, UNIVERSAL, NONE, ENGINE, CANNON;
}
