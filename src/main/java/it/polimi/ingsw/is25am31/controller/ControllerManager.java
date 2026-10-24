package it.polimi.ingsw.is25am31.controller;

import it.polimi.ingsw.is25am31.network.AsyncRunner;
import it.polimi.ingsw.is25am31.network.ClientData;
import it.polimi.ingsw.is25am31.network.UpdateMessage;
import it.polimi.ingsw.is25am31.network.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * The ControllerManager class handles the management of game controllers, client connections,
 * managing the starting and closing of games. It is used to create new games, connect clients
 * to games, monitor client connections, and close games.
 */
public class ControllerManager {
    private int gameNumber = 0;
    private final ConcurrentMap<UUID, VirtualView> clients = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Controller> controllers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Controller> game = new ConcurrentHashMap<>();
    //private final ConcurrentMap<Controller, UUID> controllerManagement = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Integer> connections = new ConcurrentHashMap<>();
    private  final AsyncRunner dispatcher = new AsyncRunner();

    /**
     * Retrieves the controller of the specified client ID
     *
     * @param id UUID of the client
     * @return Controller associated with the client, or null if not found
     */

    public Controller getController(UUID id) {
        return controllers.get(id);
    }

    /**
     * Creates a new game and assigns the specified client as the first player.
     * Initializes a new controller, starts connection monitoring, and increments the game counter.
     *
     * @param id UUID of the client creating the game
     */
    public void createNewGame(UUID id) {
        gameNumber++;
        Controller controller = new Controller();
        controller.newConnection(clients.get(id), id);
        controllers.put(id, controller);
        game.put(gameNumber, controller);
        connections.put(id, 5);
        new Thread(() -> {
            try {
                checkPlayersConnection(controller);
            } catch (Exception e) {
                System.out.println("Game closed");
            }
        }).start();
    }

    /**
     * Sends a list of available games to the specified client
     * Only includes games that have available slots
     *
     * @param id UUID of the client requesting the game list
     */
    public void viewListGame(UUID id) {
        List<Integer> gameNumb = game.entrySet().stream()
                .filter(e->e.getValue().getModel().getMaxPlayers() > e.getValue().numPlayers())
                .map(Map.Entry::getKey).toList();
        dispatcher.run(() -> clients.get(id).showGameList(new ArrayList<>(gameNumb)));
        //semplicemente a video si vedra game1, game2 e via dicendo
    }

    /**
     * Connects a client to an existing game by game number.
     * Validates that the game exists and has available slots before connecting.
     *
     * @param gameNumber number identifier of the game to join
     * @param id UUID of the client attempting to connect
     * @throws RuntimeException if the game is full or doesn't exist
     */
    public void connectToGame(int gameNumber, UUID id) {
        Controller controller = game.get(gameNumber);
        if (controller == null || controller.getModel().getMaxPlayers() == controller.numPlayers()) {
            throw new RuntimeException("Game is full or not exists, create or join another game");
        }
        controller.newConnection(clients.get(id), id);
        controllers.put(id, controller);
        connections.put(id, 5);
    }

    /**
     * Registers a new client connection by generating a unique UUID and initializing the client
     * Sets up the client's virtual view and sends initial connection messages
     *
     * @param client VirtualView representing the client connection
     */
    public void connectUUID(VirtualView client)  {
        synchronized (clients) {
            UUID id = UUID.randomUUID();
            dispatcher.run(() -> {
                client.setUUID(id);
            });
            clients.put(id, client);
            dispatcher.run(() -> client.showUpdate(UpdateMessage.CREATE_OR_JOIN));
        }
    }

    /**
     * Removes a game from the active games map and logs the closure.
     *
     * @param gameNumber the number identifier of the game to remove
     */
    public void removeGame(int gameNumber) {
        game.remove(gameNumber);
        System.out.println("Closing game " + gameNumber);
    }

    /**
     * Increments the connection counter for a specific client.
     * Used as part of the connection monitoring mechanism.
     *
     * @param clientId the UUID of the client whose counter should be incremented
     */
    public synchronized void increment(UUID clientId) {
        connections.replace(clientId, connections.get(clientId) + 1);
    }

    /**
     * Continuously monitors player connections for a specific game controller.
     * Tests client connections periodically and removes disconnected clients.
     * Runs in a separate thread and handles connection timeouts.
     *
     * @param controller the Controller whose players' connections should be monitored
     * @throws Exception if there's an error during connection monitoring
     */
    public void checkPlayersConnection(Controller controller) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        while(true) {
            for (UUID id : controllers.keySet()) {
                Controller c = controllers.get(id);
                if (c == controller) {
                    try {
                        Future<?> future = executor.submit(() -> {
                            try {
                                clients.get(id).connectionTest();
                                synchronized (connections) {
                                    connections.replace(id, connections.get(id) - 1);
                                }
                                if (connections.get(id) <= 0) {
                                    support(controller, id);
                                    executor.shutdown();
                                    Thread.currentThread().interrupt();
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        future.get(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        support(controller, id);
                        executor.shutdown();
                        Thread.currentThread().interrupt();
                    }
                }
            }
            Thread.sleep(3000);
        }
    }

    /**
     * Support for methods checkPlayersConnection
     *
     * @param controller the Controller associated with the disconnected client
     * @param id the UUID of the disconnected client
     */
    private void support(Controller controller, UUID id) {
        synchronized (this) {
            for(int i : game.keySet()) {
                if(game.get(i) == controller) {
                    removeGame(i);
                    for(UUID clientId : controllers.keySet()) {
                        if(clientId != id && controllers.get(clientId) == controller) {
                            controllers.remove(clientId);
                        }
                    }
                }
            }
        }
    }

}
