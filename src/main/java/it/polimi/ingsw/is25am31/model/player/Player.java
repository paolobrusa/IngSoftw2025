package it.polimi.ingsw.is25am31.model.player;

import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a player in a game, identified by a unique ID and nickname, with
 * associated game-specific objects like a rocket and a ship.
 *<br>
 * This class contains methods for manipulating and accessing the state of the player,
 * including score-related properties, game objects, and player status.
 *<br>
 * Implements the Serializable interface to allow player data to be serialized.
 */
public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 4111318711157814265L;
    private int id;
    private String nickname;
    private int cosmicCredits;
    private int lostComponents;
    private int cargoCredits;
    private Rocket rocket;
    private Ship ship;
    private boolean hasAbandoned = false;
    private int crew;

    /**
     * Constructs a new player
     *
     * @param id         unique player identifier
     * @param nickname   chosen nickname
     * @param gameboard  reference to the game board
     */

    public Player(int id, String nickname, Gameboard gameboard) {
        this.id = id;
        this.nickname = nickname;
        this.rocket = new Rocket(gameboard);
        this.ship = new Ship(gameboard);
        this.cosmicCredits = 0;
        this.lostComponents = 0;
        this.cargoCredits = 0;
    }

    /**
     * Returns the player's identifier
     *
     * @return unique player ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the player's nickname
     *
     * @return nickname string
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the current cosmic credits
     *
     * @return amount of cosmic credits
     */
    public int getCosmicCredits() {
        return cosmicCredits;
    }

    /**
     * Adds cosmic credits to the current total
     *
     * @param cosmicCredits number of credits to add
     */
    public void addCosmicCredits(int cosmicCredits) {
        this.cosmicCredits += cosmicCredits;
    }

    /**
     * Returns the player's current ship
     *
     * @return Ship instance
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Sets a new ship for the player
     *
     * @param ship new Ship instance
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * Returns the player's rocket
     *
     * @return Rocket instance
     */
    public Rocket getRocket() {
        return rocket;
    }

    /**
     * Sets the player's rocket
     *
     * @param rocket new Rocket instance
     */
    public void setRocket(Rocket rocket) {
        if (this.rocket == null) {
            this.rocket = rocket;
        }
    }

    /**
     * Returns the number of lost components
     *
     * @return penalty count for lost components
     */
    public int getLostComponents() {
        return lostComponents;
    }

    /**
     * Increments the lost components
     */
    public void incrementLostComponents() {
        lostComponents++;
    }

    /**
     * Returns the credits obtained from cargo sales
     *
     * @return cargo sale credits
     */
    public int getCargoCredits() {
        return cargoCredits;
    }

    /**
     * Adds credits from cargo sales to the total
     *
     * @param amount credits amount to add
     */
    public void addCargoCredits(int amount) {
        this.cargoCredits += amount;
    }

    /**
     * Sets the number of crew members at the player level
     *
     * @param crew number of crew members
     */
    public void setCrew(int crew) {
        this.crew = crew;
    }

    /**
     * Returns the number of crew members
     *
     * @return number of crew members
     */
    public int getCrew() {
        return crew;
    }

    /**
     * Indicates whether the player has abandoned the game
     *
     * @return true if abandoned, false otherwise
     */
    public boolean hasAbandoned() {
        return hasAbandoned;
    }

    /**
     * Sets the player's abandonment status
     *
     * @param hasAbandoned true if the player abandons, false otherwise
     */
    public void setAbandoned(boolean hasAbandoned) {
        this.hasAbandoned = hasAbandoned;
    }
}
