package it.polimi.ingsw.is25am31.network;

import it.polimi.ingsw.is25am31.controller.Controller;
import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.cards.ContextValidator;
import it.polimi.ingsw.is25am31.model.player.Player;

import java.util.Map;
import java.util.UUID;

/**
 * The {@code UpdateHandler} class provides static methods to manage and dispatch game updates
 * to connected clients. It handles various scenarios
 * related to drawing cards and resolving card effects, ensuring that clients receive
 * the correct information and turn changes.
 */
public class UpdateHandler {

    /**
     * Handles the turn logic after a card has been drawn. This method dictates later
     * actions based on the type of card drawn, such as updating positions, changing turns.
     *
     * @param card The {@link Card} that was drawn.
     * @param controller The game {@link Controller} instance.
     * @param clients A map of active client UUIDs to their {@link ClientData}.
     * @param passiveClients A map of passive client UUIDs to their {@link ClientData}.
     * @param model The current {@link Game} model.
     * @param dispatcher The {@link AsyncRunner} used for asynchronous task execution to clients.
     * @return {@code true} if the turn handling logic concludes successfully and the turn can proceed,
     * {@code false} if the current player's turn needs to be passed.
     * @throws Exception If an error occurs during client communication or model manipulation.
     */
    public static boolean drawCardTurnHandler(Card card, Controller controller, Map<UUID, ClientData> clients, Map<UUID, ClientData> passiveClients, Game model, AsyncRunner dispatcher) throws Exception {
        switch(card.getType()) {
            case COMBATZONE -> {
                for (ClientData client : clients.values()) {
                    if(card.isTestFlight()) dispatcher.run(() -> client.client.updatePositions(model.getUpdatedPositions())).join();
                    dispatcher.run(() -> client.client.turnChange(true, model.getCurrentCard().isTestFlight() ? 1 : 2, -1));
                }
                return true;
            }
            case SPECIAL -> {
                model.orderPlayersReverse();
                model.setCurrentPlayer(model.getPlayers().getFirst());
                for (Player p : model.getPlayers())
                    for (UUID id : clients.keySet())
                        if(clients.get(id).playerId == p.getId())
                            controller.setEffectContext(id, null);
                return true;
            }
            case ABANDONED -> {
                // check if the current player has enough crew members for the abandoned card
                if (model.getPlayer(model.getCurrentPlayer().getId()).getShip().getCrewNumber() >= model.getCurrentCard().getCrewMembers())
                    break; // if yes, proceed with the default flow after the switch
                // if not enough crew members, notify the player and try to pass the turn
                for (Player player : model.getPlayers()) {
                    if (player.getId() != model.getCurrentPlayer().getId())
                        continue;
                    for (ClientData client : clients.values()) {
                        if (client.playerId != player.getId())
                            continue;
                        dispatcher.run(() -> client.client.showUpdate(UpdateMessage.NOT_ENOUGH_CREWMEMBERS));
                        try {
                            model.passTurn();
                            return false;
                        } catch (IndexOutOfBoundsException e) {
                            newLeader(clients, passiveClients, controller, dispatcher, model);
                            return true;
                        }
                    }
                }
            }
            case METEORSWARM, PLANETS, SMUGGLERS, SLAVERS, PIRATES -> {
                try {
                    model.setCurrentPlayer(model.getPlayer(model.getLeader()));
                } catch (IndexOutOfBoundsException e) {
                    newLeader(clients, passiveClients, controller, dispatcher, model);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles turn logic based on the resolution of a card effect context. This method dispatches
     * specific updates to clients depending on the current card type and the result of the effect.
     * This includes updating ship positions, showing ships, and managing turn changes.
     *
     * @param clientId The {@link UUID} of the client whose effect context is being resolved.
     * @param res An integer representing the result or choice made during the effect resolution.
     * @param clients A map of active client UUIDs to their {@link ClientData}.
     * @param passiveClients A map of passive client UUIDs to their {@link ClientData}.
     * @param model The current {@link Game} model.
     * @param dispatcher The {@link AsyncRunner} used for asynchronous task execution to clients.
     * @throws Exception If an error occurs during client communication or model manipulation.
     */
    public static void effectContextTurnHandler(UUID clientId, int res, Map<UUID, ClientData> clients, Map<UUID, ClientData> passiveClients, Game model, AsyncRunner dispatcher) throws Exception {
        Map<Integer, Integer> positions = model.getUpdatedPositions();
        switch(model.getCurrentCard().getType()){
            case METEORSWARM:
                if(clients.get(clientId).playerId != model.getLeader())
                    dispatcher.run(() -> clients.get(clientId).client.lockClient()).join();
                dispatcher.run(() -> {
                    clients.get(clientId).client.updatePositions(positions);
                    clients.get(clientId).client.showShip(model.getPlayer(clients.get(clientId).playerId).getShip());
                }).join();
                break;
            case ABANDONED:
                for (ClientData client : clients.values()) {
                    dispatcher.run(() -> {
                        client.client.updatePositions(positions);
                        client.client.showShip(model.getPlayer(client.playerId).getShip());
                    });
                }
                break;
            case OPENSPACE:
                for (ClientData client : clients.values()) {
                    dispatcher.run(() -> client.client.updatePositions(positions));
                    if(res == 2)
                        dispatcher.run(() -> client.client.showShip(model.getPlayer(client.playerId).getShip()));
                    else if(res == 1) {
                        dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));

                    }
                }
                break;
            case PLANETS, SMUGGLERS, SLAVERS, PIRATES:
                dispatcher.run(() -> {
                    clients.get(clientId).client.updatePositions(positions);
                    clients.get(clientId).client.showShip(model.getPlayer(clients.get(clientId).playerId).getShip());
                });
                if(res == 1) {
                    for (ClientData client : clients.values()) {
                        dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));
                    }
                }
                break;
            case SPECIAL:
                if(res == 2) {
                    for (ClientData client : clients.values()) {
                        dispatcher.run(() -> {
                            client.client.updatePositions(positions);
                            client.client.showShip(model.getPlayer(client.playerId).getShip());
                        });
                    }
                }
                break;
            case COMBATZONE:
                dispatcher.run(() -> {
                    clients.get(clientId).client.updatePositions(model.getUpdatedPositions());
                    clients.get(clientId).client.showShip(model.getPlayer(clients.get(clientId).playerId).getShip());
                }).join();
                if(res == 1){
                    for (ClientData client : clients.values()) {
                        dispatcher.run(() -> client.client.turnChange(true, 0, -1));
                    }
                } else if (res == 0 && ContextValidator.getEffectCalledCount() == 1){
                    for (ClientData client : clients.values()) {
                        dispatcher.run(() -> client.client.roundResult(client.playerId != model.getCurrentPlayer().getId()));
                    }
                }
                break;
        }
    }

    /**
     * Handles the logic for a new leader scenario, or concludes the game if a final ranking is available.
     * This method is called when the game needs to determine a new leader or if the game has ended
     * due to the deck being empty. It dispatches updates to both active and passive clients.
     *
     * @param activeClients A map of active client UUIDs to their {@link ClientData}.
     * @param passiveClients A map of passive client UUIDs to their {@link ClientData}.
     * @param controller The game {@link Controller} instance.
     * @param dispatcher The {@link AsyncRunner} used for asynchronous task execution to clients.
     * @param model The current {@link Game} model.
     * @throws Exception If an error occurs during client communication or model manipulation.
     */
    public static void newLeader(Map<UUID, ClientData> activeClients, Map<UUID, ClientData> passiveClients, Controller controller, AsyncRunner dispatcher, Game model) throws Exception {
        Map<Player, Integer> ranking = model.checkDeck();
        if (ranking != null) {
            // if a final ranking exists, the game has ended. Send final ranking to all clients
            for (ClientData c : activeClients.values()) {
                dispatcher.run(() -> c.client.showFinalRanking(ranking));
            }
            for (ClientData c : passiveClients.values()) {
                dispatcher.run(() -> c.client.showFinalRanking(ranking));
            }
            return;
        }
        // if no final ranking, update positions and notify clients about the new leader
        for (ClientData client : activeClients.values()) {
            dispatcher.run(() -> {
                client.client.updatePositions(model.getUpdatedPositions());
                client.client.newLeader(client.playerId ==  controller.getLeader());
            });
        }
    }
}
