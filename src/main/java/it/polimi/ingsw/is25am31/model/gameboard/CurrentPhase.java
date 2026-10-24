package it.polimi.ingsw.is25am31.model.gameboard;

/**
 * The CurrentPhase class represents the current phase of a game.
 * It allows getting and setting the active phase, which is defined
 * using the {@link Phase} enumeration.
 */
public class CurrentPhase {
    private Phase phase;

    /**
     * Constructs a new CurrentPhase with the specified initial phase.
     *
     * @param phase the initial phase to set for the game
     */
    public CurrentPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Returns the current phase of the game.
     *
     * @return the current phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Sets the current phase of the game.
     *
     * @param phase the new phase to set
     */
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
