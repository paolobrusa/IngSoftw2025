package it.polimi.ingsw.is25am31.network;


/**
 * Represents the client data within the system, managing the association between
 * a client represented by a VirtualView and its corresponding player information.
 */
public class ClientData {
    public VirtualView client;
    public int playerId = - 1;
    public String nickname = null;

    public ClientData(VirtualView client) {
        this.client = client;
    }
}
