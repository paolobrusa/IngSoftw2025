package it.polimi.ingsw.is25am31.network;

import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;

import java.rmi.Remote;
import java.util.Map;
import java.util.UUID;

/**
 * This interface defines the methods for server-side operations that can be invoked remotely by a client.
 * It serves as a generic representation of the server,
 * allowing different underlying communication technologies (RMI, Socket) to implement it.
 * <p>
 * Methods declared in this interface are expected to throw {@link Exception} to signal
 * any issues that prevent the successful completion of the operation. These exceptions
 * can arise from low-level network communication failures or from application-level
 * logical errors encountered during the execution of the requested operation on the server.
 */
public interface VirtualServer extends Remote {
    /**
     * Connects a new client's virtual view to the server.
     * @param client The {@link VirtualView} instance representing the client's view.
     */
    void connect(VirtualView client) throws Exception;

    /**
     * Notifies the server that a client is leaving the game.
     * @param clientId The ID of the client.
     */
    void leaveGame(UUID clientId) throws Exception;

    /**
     * Requests the server to draw a card for the specified client.
     * @param clientId The ID of the client.
     */
    void drawCard(UUID clientId) throws Exception;

    /**
     * Requests the server to draw a face-down component for the specified client.
     * @param clientId The ID of the client.
     */
    void drawFaceDownComponent(UUID clientId) throws Exception;

    /**
     * Requests the server to draw a face-up component at a specific index for the specified client.
     * @param index The index of the face-up component to draw from the display.
     * @param clientId The ID of the client.
     */
    void drawFaceUpComponent(int index, UUID clientId) throws Exception;

    /**
     * Requests the server to attach a component to the client's ship at specified coordinates.
     * @param component The {@link Component} to attach.
     * @param x The row on the ship's grid where the component should be placed.
     * @param y The column on the ship's grid where the component should be placed.
     * @param clientId The ID of the client.
     */
    void attachComponent(Component component, int x, int y, UUID clientId) throws Exception;

    /**
     * Sets the nickname for the specified client on the server.
     * @param nickname The nickname to set.
     * @param clientId The ID of the client.
     */
    void setNickname(String nickname, UUID clientId) throws Exception;

    /**
     * Sets the context for a card effect on the server. This method is used to perform an action
     * that may/must be performed before the card's effect takes place.
     * @param clientId The ID of the client.
     * @param context A map containing key-value pairs relevant to the card effect's resolution.
     */
    void setCardContext(UUID clientId, Map<String, Object> context) throws Exception;

    /**
     * Sets the context for a card effect on the server. This method is used to provide
     * additional parameters needed to resolve a card's effect.
     * @param clientId The ID of the client.
     * @param context A map containing key-value pairs relevant to the effect's resolution.
     */
    void setEffectContext(UUID clientId, Map<String, Object> context) throws Exception;

    /**
     * Notifies the server that the assembling phase for the specified client has ended.
     * This signals that the client has finished arranging components on their ship.
     * @param clientId The ID of the client.
     */
    void endAssemblePhase(UUID clientId) throws Exception;

    /**
     * Sets the maximum number of players allowed for a game on the server.
     * This method is typically called by the game creator.
     * @param clientId The ID of the client setting the maximum players.
     * @param maxPlayers The maximum number of players for the game.
     */
    void setMaxPlayers(UUID clientId, int maxPlayers) throws Exception;

    /**
     * Sets the game level on the server.
     * @param clientId The ID of the client setting the game level.
     * @param level The game level to set.
     */
    void setGameLevel(UUID clientId, int level) throws Exception;

    /**
     * Requests the server to send the ship of a specific player to the requesting client.
     * @param clientId The ID of the client requesting to view the ship.
     * @param nickname The nickname of the player whose ship is to be shown.
     */
    void showShip(UUID clientId, String nickname) throws Exception;

    /**
     * Requests the server to roll the dice.
     * @param clientId The ID of the client.
     */
    void rollDice(UUID clientId) throws Exception;

    /**
     * Requests the server to put down a component.
     * @param component The {@link Component} to put down.
     * @param clientId The ID of the client.
     */
    void putDownComponent(Component component, UUID clientId) throws Exception;

    /**
     * Requests the server to send the current list of face-up components.
     * @param clientId The ID of the client.
     */
    void getFaceUpComponentList(UUID clientId) throws Exception;

    /**
     * Requests the server to draw a component from the reserved pile at a specific index.
     * @param index The index of the component in the reserved pile to draw.
     * @param clientId The ID of the client.
     */
    void drawFromReserved(int index, UUID clientId) throws Exception;

    /**
     * Requests the server to send a specific sub-deck of cards.
     * @param clientId The ID of the client.
     * @param index The index of the sub-deck to retrieve.
     */
    void getSubDeck(UUID clientId, int index) throws Exception;

    /**
     * Requests the server to place the client's rocket at a specific position on the track.
     * @param clientId The ID of the client.
     * @param position The position on the game track where the rocket should be placed.
     */
    void placeRocket(UUID clientId, int position) throws Exception;

    /**
     * Notifies the server that the crew placing phase for the specified client has ended.
     * This signals that the client has finished placing crew members.
     * @param clientId The ID of the client.
     */
    void endCrewPlacingPhase(UUID clientId) throws Exception;

    /**
     * Requests the server to place a crew member or an alien of a given color at specified coordinates on the ship.
     * @param clientId The ID of the client.
     * @param x The x-coordinate (row) on the ship's gameboard where the crew member should be placed.
     * @param y The y-coordinate (column) on the ship's gameboard where the crew member should be placed.
     * @param a The {@link AlienColor} of the crew member to place.
     */
    void placeCrewMember(UUID clientId, int x, int y, AlienColor a) throws Exception;

    /**
     * Requests the server to start a standard hourglass timer.
     * @param clientId The ID of the client.
     */
    void startHourGlass(UUID clientId) throws Exception;

    /**
     * Requests the server to start the final hourglass timer.
     * @param clientId The ID of the client.
     */
    void startFinalHourGlass(UUID clientId) throws Exception;

    /**
     * Notifies the server that a client is abandoning the race, removing them from the active game.
     * @param clientId The ID of the client abandoning the race.
     */
    void abandonRace(UUID clientId) throws Exception;

    /**
     * Sends a ping to the server for connection testing.
     * @param clientId The ID of the client sending the ping.
     * @throws Exception if a remote communication error occurs.
     */
    void ping(UUID clientId) throws Exception;

    /**
     * Requests the server to create a new game instance.
     * @param id The ID of the client creating the game.
     */
    void createGame(UUID id) throws Exception;

    /**
     * Requests the server to send the list of available games that the client can join.
     * @param clientId The ID of the client.
     */
    void viewGame(UUID clientId) throws Exception;

    /**
     * Requests the server to join an existing game.
     * @param id The ID of the client joining the game.
     * @param game The ID of the game to join.
     */
    void joinGame(UUID id, int game) throws Exception;

    /**
     * Requests the server to remove a component from the client's ship at specified coordinates.
     * @param clientId The ID of the client.
     * @param x The row of the component to remove.
     * @param y The column of the component to remove.
     */
    void removeComponent(UUID clientId, int x, int y) throws Exception;

    /**
     * Increments a counter used to trace clients' disconnections.
     *
     * @param clientId the ID of the client whose value needs to be incremented
     * @throws Exception if the increment operation fails or if the client ID is invalid
     */
    void increment(UUID clientId) throws Exception;
}