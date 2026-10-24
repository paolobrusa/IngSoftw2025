package it.polimi.ingsw.is25am31.network.rmi.server;

import it.polimi.ingsw.is25am31.controller.ControllerManager;
import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


/**
 * The RmiServer class is an implementation of the VirtualServer interface, extending UnicastRemoteObject.
 * It serves as a remote server that listens for client connections and processes requests
 * to interact with game mechanics.
 * The server communicates with a ControllerManager to delegate and manage client operations.
 */
public class RmiServer extends UnicastRemoteObject implements VirtualServer {
    private final ControllerManager controllerManager;

    public RmiServer(ControllerManager controllerManager) throws RemoteException {
        super();
        this.controllerManager = controllerManager;
    }

    public static void bind(ControllerManager controllerManager) throws RemoteException, UnknownHostException {
        String localIP = InetAddress.getLocalHost().getHostAddress();
        System.setProperty("java.rmi.server.hostname", localIP);
        final String serverName = "RmiServer";
        RmiServer server = new RmiServer(controllerManager);
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(serverName, server);
        System.out.println("Rmi Server bound");
    }


    /**
     * Handles a connection request
     *
     * @param client client to call the remote methods on
     */
    @Override
    public void connect(VirtualView client) throws Exception {
        System.out.println("New client connection request received from " + getClientHost());
        controllerManager.connectUUID(client);
    }

    @Override
    public void createGame(UUID clientId) throws Exception {
        System.out.println("New creation game request received from " + getClientHost());
        controllerManager.createNewGame(clientId);
    }

    @Override
    public void viewGame(UUID clientId) throws Exception {
        controllerManager.viewListGame(clientId);
    }

    @Override
    public void joinGame(UUID clientId, int game) throws Exception {
        System.out.println("New join game request received from " + getClientHost());
        controllerManager.connectToGame(game, clientId);
    }

    @Override
    public void ping(UUID clientId) throws RemoteException {
        controllerManager.getController(clientId).controllerPing();
    }

    @Override
    public void increment(UUID clientId) throws Exception {
        controllerManager.increment(clientId);
    }

    /**
     * Handles request to set max players
     *
     * @param clientId UUID of the client calling the method
     * @param userNumber number of players required to start the game
     */
    @Override
    public void setMaxPlayers(UUID clientId, int userNumber) throws Exception {
        System.out.println(getClientHost() + " requested to set max user number to " + userNumber);
        controllerManager.getController(clientId).setMaxPlayers(clientId, userNumber);
    }

    /**
     * Handles request to set game level
     *
     * @param clientId UUID of the client calling the method
     * @param level level of the game
     */
    @Override
    public void setGameLevel(UUID clientId, int level) throws Exception {
        System.out.println(getClientHost() + " requested to set level to " + level);
        controllerManager.getController(clientId).startGame(clientId, level);
    }



    /**
     * Handles request to leave game
     *
     * @param clientId id of the client leaving the game
     */
    @Override
    public void leaveGame(UUID clientId) throws Exception{
        System.out.println("Player leave game request received by " + getClientHost());
        controllerManager.getController(clientId).leaveGame(clientId);
    }

    /**
     * Handles request to set nickname.
     *
     * @param nickname nickname the player wants to set
     * @param clientId id of the player's client
     *
     */
    @Override
    public void setNickname(String nickname, UUID clientId) throws Exception{
        System.out.println("Nickname set to " + nickname + " by " + getClientHost() + " (" + clientId + ")");
        controllerManager.getController(clientId).addPlayer(clientId, nickname);
    }


    /**
     * Handles request to draw a card from the deck.
     *
     */
    @Override
    public void drawCard(UUID clientId) throws Exception{
        System.out.println("Draw card request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawCard(clientId);
    }

    /**
     * Handles a request to draw a component from face down components,
     *
     * @param clientId UUID of the client calling the method
     */
    @Override
    public void drawFaceDownComponent(UUID clientId) throws Exception{
        System.out.println("Draw face down component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawComponent(clientId);
    }

    /**
     * Handles a request to put down a component
     *
     * @param component component to be put back
     * @param clientId UUID of the client calling the method
     */

    @Override
    public void putDownComponent(Component component, UUID clientId) throws Exception{
        System.out.println("Put down component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).putComponentBack(component, clientId);
    }

    /**
     * Handles a request to see face up components list
     *
     * @param clientId UUID of the client calling the method
     */

    @Override
    public void getFaceUpComponentList(UUID clientId) throws Exception{
        System.out.println("Get face up components list request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).getFaceUpComponentList(clientId);
    }

    /**
     * Handles a request to draw a face up component.
     *
     * @param id component's id
     * @param clientId UUID of the client calling the method
     * @throws Exception if either specified component is not in face up components list or someone else already took it
     */
    @Override
    public void drawFaceUpComponent(int id, UUID clientId) throws Exception{
        System.out.println("Draw face up component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawFaceUpComponent(id, clientId);
    }

    @Override
    public void startHourGlass(UUID clientId) throws Exception{
        controllerManager.getController(clientId).startHourGlass(clientId);
    }

    @Override
    public void startFinalHourGlass(UUID clientId) throws Exception{
        controllerManager.getController(clientId).startFinalHourGlass(clientId);
    }

    /**
     * Handles a request to draw a component from reserved components
     *
     * @param index index of the component that the player wants to draw
     * @param clientId UUID of the client calling the method
     */
    @Override
    public void drawFromReserved(int index, UUID clientId) throws Exception{
        System.out.println("Draw from reserved components request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawFromReserved(index, clientId);
    }

    /**
     * Handles a request to attach specified component on player's ship at specified coordinates
     *
     * @param component component to be attached to the ship
     * @param x row to place the component on
     * @param y column to place the component on
     * @param clientId UUID of the client calling the method
     */

    @Override
    public void attachComponent(Component component, int x, int y, UUID clientId) throws Exception{
        System.out.println("Attach component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).addComponentToShip(clientId, x, y, component);
    }

    @Override
    public void removeComponent(UUID clientId, int x, int y) throws Exception{
        System.out.println("Remove component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).removeComponent(clientId, x, y);
    }


    /**
     * Handles request to set card context (performing an operation that the player may/must do right before card effect is applied)
     *
     * @param clientId UUID of the client calling the method
     * @param context parameters used to set card context (actions done immediately before card effect resolution)
     */
    @Override
    public void setCardContext(UUID clientId, Map<String, Object> context) throws Exception{
        controllerManager.getController(clientId).setCardContext(clientId, context);
    }

    /**
     * Handles a request to apply card effect
     *
     * @param clientId UUID of the client calling the method
     * @param context parameters used for card effect resolution
     */
    @Override
    public void setEffectContext(UUID clientId, Map<String, Object> context) throws Exception{
        System.out.println("Effect application request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).setEffectContext(clientId, context);
    }

    /**
     * Handles a request to end assembling phase
     *
     * @param clientId UUID of the client calling the method
     */

    @Override
    public void endAssemblePhase(UUID clientId) throws Exception {
        System.out.println(controllerManager.getController(clientId).getPlayerName(clientId) + " has ended assembling phase");
        controllerManager.getController(clientId).endAssemblePhase(clientId);
    }

    /**
     * Handles a request to place player's rocket in specified position.
     *
     * @param clientId UUID of the client calling the method
     * @param position integer representing the position from which the player wants to start
     */
    @Override
    public void placeRocket(UUID clientId, int position) throws Exception{
        System.out.println(controllerManager.getController(clientId).getPlayerName(clientId) + " placed rocket at position " + position);
        controllerManager.getController(clientId).placeRocket(clientId, position);
    }

    /**
     * Handles a request to end crew placing phase
     *
     * @param clientId UUID of the client calling the method
     */
    @Override
    public void endCrewPlacingPhase(UUID clientId) throws Exception {
        System.out.println(controllerManager.getController(clientId).getPlayerName(clientId) + " ended crew placing phase");
        controllerManager.getController(clientId).endCrewPlacingPhase(clientId);
    }


    /**
     * Handles a request to place crew members/alien of specified type in cabin in specified coordinates of the ship of the player
     *
     * @param clientId UUID of the client calling the method
     * @param x row of the cabin
     * @param y column of the cabin
     * @param type 1 for humans, 2 for brown alien, 3 for purple alien
     */
    @Override
    public void placeCrewMember(UUID clientId, int x, int y, AlienColor type) throws Exception {
        System.out.println("Place crew members request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).handleCrewRequest(clientId, x, y, type);
    }

    @Override
    public void showShip(UUID clientId, String nickname) throws Exception {
        System.out.println(controllerManager.getController(clientId).getPlayerName(clientId) +  " requested to see " + nickname + " ship ");
        controllerManager.getController(clientId).viewShip(clientId, nickname);
    }

    /**
     * Handles a request to roll dice.
     *
     * @param clientId UUID of the client calling the method
     * @throws Exception if player can't roll dice
     */
    @Override
    public void rollDice(UUID clientId) throws Exception{
        System.out.println("Roll dice request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).rollDice(clientId);
    }

    /**
     * Handles a request to see sub deck in specified index.
     *
     * @param clientId UUID of the client calling the method
     * @param index index of sub deck requested
     */
    @Override
    public void getSubDeck(UUID clientId, int index) throws Exception {
        System.out.println("Get sub deck " + index + " request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawSubDeck(clientId, index);
    }

    /**
     * Handles a request to abandon the race.
     *
     * @param clientId UUID of the client calling the method
     */
    @Override
    public void abandonRace(UUID clientId) throws Exception {
        System.out.println("Abandon race request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).abandonRace(clientId);
    }
}
