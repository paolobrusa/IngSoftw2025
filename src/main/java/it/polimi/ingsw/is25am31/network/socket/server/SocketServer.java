package it.polimi.ingsw.is25am31.network.socket.server;

import it.polimi.ingsw.is25am31.controller.ControllerManager;
import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.network.VirtualView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * The SocketServer class represents a server that listens for client connections,
 * manages them, and delegates their requests to the ControllerManager for processing.
 * It implements the VirtualServer interface.
 */
public class SocketServer implements VirtualServer {
    private final ServerSocket listenSocket;
    private final ControllerManager controllerManager;

    /**
     * Initializes a SocketServer instance with the specified listen socket
     * and controller manager.
     *
     * @param listenSocket the server socket that will listen for incoming connections
     * @param controllerManager the controller manager responsible for handling proper games management logic.
     */
    public SocketServer(ServerSocket listenSocket,  ControllerManager controllerManager) {
        this.listenSocket = listenSocket;
        this.controllerManager = controllerManager;
    }

    /**
     * Starts the server by creating a listening server socket and initializing
     * the socket server with the provided controller manager.
     *
     * @param controllerManager the controller manager responsible for handling proper games management logic.
     * @throws Exception if an error occurs while starting the server
     */
    public static void start(ControllerManager controllerManager) throws Exception {
        ServerSocket listenSocket = new ServerSocket(1235);
        new SocketServer(listenSocket, controllerManager).runServer();
    }

    /**
     * Starts the server socket to listen for incoming client connections and handles
     * each connection by delegating it to a dedicated thread.
     *
     * The server continuously accepts incoming connections through the listening socket.
     * For each connection, it initializes input and output streams for communication
     * with the client and creates a new instance of {@code SocketClientHandler} to manage
     * the client.
     *
     * @throws Exception if an error occurs while initializing the server or handling client connections
     */
    private void runServer() throws Exception {
        Socket clientSocket;
        System.out.println("Socket server listening on port " + listenSocket.getLocalPort());
        while ((clientSocket = this.listenSocket.accept()) != null) {
            ObjectOutputStream socketTx = new ObjectOutputStream(clientSocket.getOutputStream());
            socketTx.flush();
            ObjectInputStream socketRx = new ObjectInputStream(clientSocket.getInputStream());

            SocketClientHandler handler = new SocketClientHandler(this, socketRx, socketTx);

            new Thread(() -> {
                try {
                    handler.run();
                } catch (Exception e) {
                    System.out.println("Client disconnected");
                }
            }).start();
            connect(handler);
            System.out.println("New client connection request received from " + clientSocket.getInetAddress().getHostAddress());
        }
    }

    /**
     * Handles a connection request
     *
     * @param client client to call the remote methods on
     */
    public void connect(VirtualView client) throws Exception {
        controllerManager.connectUUID(client);
    }


    /**
     * Handles a request to create a new game.
     *
     * @param clientId the unique identifier of the client requesting the game creation
     * @throws Exception if an error occurs during the game creation process
     */
    public void createGame(UUID clientId) throws Exception {
        System.out.println("New creation game request received from " + clientId);
        controllerManager.createNewGame(clientId);
    }

    /**
     * Handles a request to view the list of currently available games.
     *
     * @param clientId the UUID of the client making the request
     * @throws Exception if an error occurs while processing the request
     */
    public void viewGame(UUID clientId) throws Exception {
        controllerManager.viewListGame(clientId);
    }

    /**
     * Handles a request for a client to join an existing game.
     *
     * @param clientId the UUID of the client requesting to join the game
     * @param game the identifier of the game that the client wishes to join
     * @throws Exception if an error occurs during the join operation
     */
    public void joinGame(UUID clientId, int game) throws Exception {
        System.out.println("New join game request received from " + clientId);
        controllerManager.connectToGame(game, clientId);
    }

    /**
     * Sends a ping request to the controller associated with the specified client.
     *
     * @param clientId the UUID of the client to be pinged.
     * @throws Exception if an error occurs during the ping operation.
     */
    public void ping(UUID clientId) throws Exception {
        controllerManager.getController(clientId).controllerPing();
    }

    /**
     * Handles a request to increment the connection counter for a specific client.
     *
     * @param clientId the UUID of the client whose connection counter should be incremented
     * @throws Exception if an error occurs during the operation
     */
    public void increment(UUID clientId) throws Exception {
        controllerManager.increment(clientId);
    }

    /**
     * Handles request to set max players.
     *
     * @param clientId UUID of the client calling the method
     * @param userNumber number of players required to start the game
     */
    public void setMaxPlayers(UUID clientId, int userNumber) throws Exception {
        controllerManager.getController(clientId).setMaxPlayers(clientId, userNumber);
    }

    /**
     * Handles request to set game level.
     *
     * @param clientId UUID of the client calling the method
     * @param level level of the game
     */
    public void setGameLevel(UUID clientId, int level) throws Exception {
        controllerManager.getController(clientId).startGame(clientId, level);
    }



    /**
     * Handles request to leave game.
     *
     * @param clientId id of the client leaving the game
     */
    public void leaveGame(UUID clientId) throws Exception{
        controllerManager.getController(clientId).leaveGame(clientId);
    }

    /**
     * Handles request to set nickname.
     *
     * @param nickname nickname the player wants to set
     * @param clientId id of the player's client
     *
     */
    public void setNickname(String nickname, UUID clientId) throws Exception{
        controllerManager.getController(clientId).addPlayer(clientId, nickname);
    }


    /**
     * Handles a request to draw a card for the specified client.
     *
     * @param clientId UUID of the client making the request
     * @throws Exception if an error occurs during the card draw operation
     */
    public void drawCard(UUID clientId) throws Exception{
        System.out.println("Draw card request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawCard(clientId);
    }

    /**
     * Handles a request to draw a component from face down components,
     *
     * @param clientId UUID of the client calling the method
     */
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
    public void putDownComponent(Component component, UUID clientId) throws Exception{
        System.out.println("Put down component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).putComponentBack(component, clientId);
    }

    /**
     * Handles a request to see face up components list
     *
     * @param clientId UUID of the client calling the method
     */
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
    public void drawFaceUpComponent(int id, UUID clientId) throws Exception{
        System.out.println("Draw face up component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawFaceUpComponent(id, clientId);
    }

    
    /**
     * Handles a request to start the hourglass timer for the specified client.
     *
     * @param clientId UUID of the client requesting to start the hourglass
     * @throws Exception if an error occurs during the hourglass start process
     */
    public void startHourGlass(UUID clientId) throws Exception{
        controllerManager.getController(clientId).startHourGlass(clientId);
    }

    
    /**
     * Handles a request to start the final hourglass for the specified client.
     *
     * @param clientId UUID of the client calling the method
     * @throws Exception if an error occurs during the execution of the final hourglass process
     */
    public void startFinalHourGlass(UUID clientId) throws Exception{
        controllerManager.getController(clientId).startFinalHourGlass(clientId);
    }

    /**
     * Handles a request to draw a component from reserved components
     *
     * @param index index of the component that the player wants to draw
     * @param clientId UUID of the client calling the method
     */
    public void drawFromReserved(int index, UUID clientId) throws Exception{
        System.out.println("Draw from reserved components request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawFromReserved(index, clientId);
    }

    /**
     * Handles a request to remove a component from the player's ship at the specified coordinates.
     *
     * @param clientId UUID of the client making the request.
     * @param x The row of the component to be removed.
     * @param y The column of the component to be removed.
     * @throws Exception if an error occurs during the removal process.
     */
    public void removeComponent(UUID clientId, int x, int y) throws Exception{
        System.out.println("Remove component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).removeComponent(clientId, x, y);
    }

    /**
     * Handles a request to attach specified component on player's ship at specified coordinates
     *
     * @param component component to be attached to the ship
     * @param x row to place the component on
     * @param y column to place the component on
     * @param clientId UUID of the client calling the method
     */
    public void attachComponent(Component component, int x, int y, UUID clientId) throws Exception{
        System.out.println("Attach component request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).addComponentToShip(clientId, x, y, component);
    }


    /**
     * Handles request to set card context (performing an operation that the player may/must do right before card effect is applied)
     *
     * @param clientId UUID of the client calling the method
     * @param context parameters used to set card context (actions done immediately before card effect resolution)
     */
    public void setCardContext(UUID clientId, Map<String, Object> context) throws Exception{
        controllerManager.getController(clientId).setCardContext(clientId, context);
    }

    /**
     * Handles a request to apply card effect
     *
     * @param clientId UUID of the client calling the method
     * @param context parameters used for card effect resolution
     */
    public void setEffectContext(UUID clientId, Map<String, Object> context) throws Exception{
        System.out.println("Effect application request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).setEffectContext(clientId, context);
    }

    /**
     * Handles a request to end assembling phase
     *
     * @param clientId UUID of the client calling the method
     */
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
    public void placeRocket(UUID clientId, int position) throws Exception{
        System.out.println(controllerManager.getController(clientId).getPlayerName(clientId) + " placed rocket at position " + position);
        controllerManager.getController(clientId).placeRocket(clientId, position);
    }

    /**
     * Handles a request to end crew placing phase
     *
     * @param clientId UUID of the client calling the method
     */
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
     * @param type color of the alien to place
     */
    public void placeCrewMember(UUID clientId, int x, int y, AlienColor type) throws Exception {
        System.out.println("Place crew members request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).handleCrewRequest(clientId, x, y, type);
    }

    
    /**
     * Handles a request to view a specific player's ship.
     *
     * @param clientId UUID of the client making the request
     * @param nickname Nickname of the player whose ship is being requested to view
     * @throws Exception if an error occurs during the operation
     */
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
    public void getSubDeck(UUID clientId, int index) throws Exception {
        System.out.println("Get sub deck " + index + " request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).drawSubDeck(clientId, index);
    }

    /**
     * Handles a request to abandon the race.
     *
     * @param clientId UUID of the client calling the method
     */
    public void abandonRace(UUID clientId) throws Exception {
        System.out.println("Abandon race request received by " + controllerManager.getController(clientId).getPlayerName(clientId));
        controllerManager.getController(clientId).abandonRace(clientId);
    }
}
