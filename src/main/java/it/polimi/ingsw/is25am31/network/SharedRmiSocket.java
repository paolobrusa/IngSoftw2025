package it.polimi.ingsw.is25am31.network;

import it.polimi.ingsw.is25am31.controller.ControllerManager;
import it.polimi.ingsw.is25am31.network.rmi.server.RmiServer;
import it.polimi.ingsw.is25am31.network.socket.server.SocketServer;


/**
 * The SharedRmiSocket class is responsible for starting both RMI and Socket servers.
 * These servers work concurrently, sharing a single instance of {@link ControllerManager}
 * to handle client requests.
 */
public class SharedRmiSocket {

    /**
     * Starts both RMI and Socket servers concurrently.
     * This method creates a shared {@link ControllerManager} instance that
     * is used by both servers to handle client requests. Each server is started
     * in its own thread to allow for concurrent operation:
     * RMI Server: handles remote method invocation requests.
     * Socket Server: handles traditional socket-based communication
     * If either server fails to start, a
     * {@link RuntimeException} will be thrown from the respective thread
     *
     * @throws RuntimeException if either the RMI server or Socket server
     * fails to start properly
     *
     * @see ControllerManager
     * @see RmiServer#bind(ControllerManager)
     * @see SocketServer#start(ControllerManager)
     */
    public static void start() {
        ControllerManager controllerManager = new ControllerManager();
        new Thread(() -> {
            try {
                RmiServer.bind(controllerManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                SocketServer.start(controllerManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


}
