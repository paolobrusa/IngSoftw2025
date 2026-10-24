package it.polimi.ingsw.is25am31.controller;

import it.polimi.ingsw.is25am31.customerrors.NameErrorException;
import it.polimi.ingsw.is25am31.customerrors.NoCardsLeftException;
import it.polimi.ingsw.is25am31.model.Game;
import it.polimi.ingsw.is25am31.model.cards.*;
import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.gameboard.Phase;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.network.*;
import it.polimi.ingsw.is25am31.network.UpdateHandler;
import it.polimi.ingsw.is25am31.view.ClientState;

import java.util.*;


/**
 * The Controller class manages player interactions, communication
 * between clients and the server during the game, and some game logic.
 * It handles player connections, game phases, in-game actions,
 * and the asynchronous dispatcher for updates.
 */
public class Controller {
    private final Game model;
    private int lastPlayerId = 0;
    private Map<Player, ClientState> players = new LinkedHashMap<>();
    private List<Integer> ids = new LinkedList<>();
    private final List<Integer> playersApplied = new ArrayList<>();
    private final HourGlass hourGlass;
    private int hourglassCounter = 0;
    private final Map<UUID, ClientData> activeClients = new HashMap<>();
    private final Map<UUID, ClientData> passiveClients = new HashMap<>();
    private final AsyncRunner dispatcher;


    /**
     * Constructs a new Controller.
     * Initializes the game model, hourglass, and asynchronous dispatcher.
     */
    public Controller() {
        this.model = new Game();
        this.hourGlass = new HourGlass();
        this.dispatcher = new AsyncRunner();
    }

    /**
     * Gets the nickname of a player given their client ID.
     *
     * @param id The UUID of the client.
     * @return The nickname of the player, or null if not found.
     */
    public String getPlayerName(UUID id) {
        return activeClients.containsKey(id) ? activeClients.get(id).nickname : passiveClients.get(id).nickname;
    }

    /**
     * Gets the current number of active players in the game.
     *
     * @return The number of active clients.
     */
    public int numPlayers() {
        return activeClients.size();
    }

    /**
     * Handles a new client connection to the game.
     * Assigns a player ID and sends connection responses.
     *
     * @param client The VirtualView representing the client.
     * @param clientId The ID of the client.
     */
    public void newConnection(VirtualView client, UUID clientId) {
        synchronized (activeClients) {
            activeClients.put(clientId, new ClientData(client));
            if (ids.isEmpty()) {
                dispatcher.run(() -> client.connectionResponse(ConnectionResponse.FIRST_PLAYER));
                activeClients.get(clientId).playerId = addClient();
                dispatcher.run(client::startGame);
            } else {
                int id = addClient();
                if (id == -1) {
                    dispatcher.run(() -> client.connectionResponse(ConnectionResponse.IN_PROGRESS)).join();
                    activeClients.get(clientId).playerId = -1;
                    dispatcher.run(() -> activeClients.get(clientId).client.lockClient()).join();
                    activeClients.remove(clientId);
                    return;
                }
                activeClients.get(clientId).playerId = id;
                if (!model.isGameStarted()) {
                    dispatcher.run(() -> {
                        activeClients.get(clientId).client.lockClient();
                        client.connectionResponse(ConnectionResponse.WAIT_CREATION);
                    });
                } else {
                    dispatcher.run(() -> client.connectionResponse(ConnectionResponse.SET_NICKNAME));
                }
            }
        }
    }

    /**
     * Sets the maximum number of players for the game. This can only be done by the first player.
     *
     * @param clientId The ID of the client attempting to set the max players.
     * @param maxPlayers The desired maximum number of players (2-4).
     * @throws IllegalStateException if the client is not the first player.
     * @throws IllegalArgumentException if maxPlayers is out of the valid range.
     */
    public void setMaxPlayers(UUID clientId, int maxPlayers) {
        if (activeClients.get(clientId).playerId != ids.getFirst())
            throw new IllegalStateException("You can't use this command now");
        model.setMaxPlayers(maxPlayers);
        List<Integer> newIds = new LinkedList<>();

        for (int i = 0; i < Math.min(maxPlayers, ids.size()); i++)
            newIds.add(ids.get(i));

        ids = newIds;
    }

    /**
     * Starts the game with the specified difficulty level. This can only be done by the first player.
     *
     * @param clientId The ID of the client attempting to start the game.
     * @param level The game difficulty level (1 or 2).
     * @throws IllegalArgumentException if the level is not 1 or 2.
     */
    public void startGame(UUID clientId, int level) {
        if (activeClients.get(clientId).playerId != ids.getFirst())
            throw new IllegalStateException("You can't use this command now");
        if (level != 1 && level != 2)
            throw new IllegalArgumentException("Level must be 1 or 2");
        List<UUID> removedClients = new ArrayList<>();
        for (UUID id : activeClients.keySet()) {
            if (!ids.contains(activeClients.get(id).playerId)) {
                dispatcher.run(() -> activeClients.get(id).client.showUpdate(UpdateMessage.GAME_FULL)).join();
                removedClients.add(id);
            } else {
                dispatcher.run(() -> activeClients.get(id).client.setNickname());
            }
        }
        for (UUID id : removedClients) {
            activeClients.remove(id);
        }
        model.setLevel(level);
    }

    /**
     * Handles a client leaving the game. Notifies other players and updates client lists.
     *
     * @param clientId The ID of the client leaving.
     */
    public void leaveGame(UUID clientId) {
        try {
            for (UUID idd : activeClients.keySet()) {
                if (!idd.equals(clientId)) {
                    dispatcher.run(() -> activeClients.get(idd).client.showUpdate(UpdateMessage.PLAYER_DISCONNECTED));
                }
            }
            dispatcher.run(() -> activeClients.get(clientId).client.showUpdate(UpdateMessage.QUIT_GAME));
        } catch (NullPointerException e) {
            // se un client di un giocatore che ha perso lascia la partita gli altri possono comunque continuare a giocare
            // in questo caso la partita non viene interrotta
            dispatcher.run(() -> passiveClients.get(clientId).client.showUpdate(UpdateMessage.QUIT_GAME));
            synchronized (passiveClients) {
                passiveClients.remove(clientId);
            }
        }
    }

    /**
     * @return The Game model instance.
     */
    public Game getModel() {
        return model;
    }

    /**
     * @return The ID of the leader player.
     */
    public int getLeader() {
        model.orderPlayers();
        return model.getLeader();
    }

    /**
     * Adds a new client to the game, assigning a player ID.
     *
     * @return The assigned player ID, or -1 if the maximum number of players has been reached.
     */
    public synchronized int addClient() {
        if (ids.size() >= Math.min(4, model.getMaxPlayers()))
            return -1;
        ids.add(lastPlayerId);
        return lastPlayerId++;
    }


    /**
     * Adds a new player to the game with a nickname.
     *
     * @param clientId The ID of the client.
     * @param nickname The desired nickname for the player.
     */
    public void addPlayer(UUID clientId, String nickname) throws NameErrorException {
        model.addPlayer(activeClients.get(clientId).playerId, nickname);
        players.put(model.getPlayer(activeClients.get(clientId).playerId), ClientState.LOBBY);

        if (model.getPlayers().size() == model.getMaxPlayers())
            players.replaceAll((p, v) -> ClientState.ASSEMBLE);

        activeClients.get(clientId).nickname = nickname;
        dispatcher.run(() -> activeClients.get(clientId).client.lockClient());

        // arriva solo a chi ha già settato il proprio nickname
        for (ClientData client : activeClients.values()) {
            if (ids.contains(client.playerId) && client.nickname != null) {
                dispatcher.run(() -> client.client.playerJoined(nickname, model.getPlayers().stream().map(Player::getNickname).toList()));
            }
        }

        if (model.getPlayers().size() == model.getMaxPlayers())
            for (ClientData client : activeClients.values()) {
                if (ids.contains(client.playerId)) {
                    dispatcher.run(() -> client.client.startAssemblingPhase(model.getPlayers(), model.getLevel()));
                }
            }
        dispatcher.run(() -> activeClients.get(clientId).client.setGameLevel(model.getLevel()));
    }

    /**
     * Handles the completion of a player's ship assembly phase.
     * Updates player states and transitions to the flight phase if all players are ready for Level 1.
     *
     * @param clientId The ID of the client that has ended the assembling phase.
     */
    public void endAssemblePhase(UUID clientId) throws Exception {
        players.replace(model.getPlayer(activeClients.get(clientId).playerId), ClientState.ENDED_ASSEMBLE);
        Map<Player, ClientState> newPlayers = new LinkedHashMap<>();

        // riordina la lista mettendo prima chi ha finito
        for (Map.Entry<Player, ClientState> entry : players.entrySet())
            if (entry.getValue() == ClientState.ENDED_ASSEMBLE)
                newPlayers.put(entry.getKey(), ClientState.ENDED_ASSEMBLE);

        if (model.getLevel() == 1) {
            model.setPosition(model.getPlayer(activeClients.get(clientId).playerId), newPlayers.size());
        }

        for (Map.Entry<Player, ClientState> entry : players.entrySet())
            if (!newPlayers.containsKey(entry.getKey()))
                newPlayers.put(entry.getKey(), entry.getValue());

        if (model.getLevel() == 1 && players.values().stream().allMatch(c -> c == ClientState.ENDED_ASSEMBLE)) {
            players.replaceAll((p, v) -> ClientState.FLIGHT);
            Map<Integer, Integer> positions = model.getUpdatedPositions();
            for (Player p : model.getPlayers()) {
                p.getShip().validateShipNextPhase();
            }
            for (ClientData client : activeClients.values()) {
                model.initializeCrew(model.getPlayer(client.playerId));
                dispatcher.run(() -> {
                    client.client.startFlightPhase(model.getPlayer(client.playerId).getShip(), positions);
                    client.client.newLeader(client.playerId == getLeader());
                });
            }
            model.startFlightPhase();
            return;
        }

        players = newPlayers;
    }

    /**
     * Handles a player placing their rocket on the game board. This is only for Level 2 games.
     *
     * @param clientId The ID of the client placing the rocket.
     * @param position The desired starting position for the rocket.
     * @throws RuntimeException if the player cannot place their rocket at this time.
     */
    public void placeRocket(UUID clientId, int position) throws Exception {
        int playerId = activeClients.get(clientId).playerId;
        if (model.getLevel() == 1)
            throw new RuntimeException("You can't place rockets in test flight");
        if (playersApplied.contains(playerId))
            throw new RuntimeException("You have already placed your rocket");
        for (Map.Entry<Player, ClientState> entry : players.entrySet()) {
            if (!playersApplied.contains(entry.getKey().getId()) && entry.getKey().getId() == playerId) {
                model.setPosition(entry.getKey(), position);
                playersApplied.add(playerId);
                break;
            } else if (!playersApplied.contains(entry.getKey().getId()) && entry.getValue() == ClientState.ENDED_ASSEMBLE && entry.getKey().getId() != playerId) {
                throw new RuntimeException("Wait for players before you to place their rocket");
            }
        }
        for (Player p : model.getPlayers()) {
            if (p.getRocket().getPosition() == -1) {
                return;
            }
        }

        players.replaceAll((p, v) -> ClientState.CREW_PLACING);
        playersApplied.clear();
        Map<Integer, Integer> positions = model.getUpdatedPositions();
        for (Player p : model.getPlayers()) {
            p.getShip().validateShipNextPhase();
        }

        for (ClientData client : activeClients.values()) {
            dispatcher.run(() -> client.client.crewPlacingPhase(model.getPlayer(client.playerId).getShip(), positions));
        }
    }


    /**
     * Handles the completion of a player's crew placing phase. This is only for Level 2 games.
     * Updates player states and transitions to the flight phase if all players are ready.
     *
     * @param clientId The ID of the client that has ended the crew placing phase.
     */
    public void endCrewPlacingPhase(UUID clientId) throws Exception {
        if (model.getLevel() == 1)
            throw new RuntimeException("You can't place crew in test flight");
        players.replace(model.getPlayer(activeClients.get(clientId).playerId), ClientState.LOCKED);

        if (players.values().stream().allMatch(c -> c == ClientState.LOCKED)) {
            players.replaceAll((p, v) -> ClientState.FLIGHT);

            if (model.getLevel() == 2)
                model.shuffleCards();

            System.out.println("Starting flight phase");
            System.out.println("The leader is " + model.getPlayer(getLeader()).getNickname());
            for (ClientData client : activeClients.values()) {
                dispatcher.run(() -> {
                    client.client.startFlightPhase(model.getPlayer(client.playerId).getShip(), null);
                    client.client.newLeader(client.playerId == getLeader());
                });
            }
            return;
        }

        if (model.getCurrentPhase() != Phase.FLIGHT) {
            model.startFlightPhase();
        }
    }

    /**
     * Allows a client to view the ship of a specified player.
     *
     * @param clientId The ID of the client requesting to view a ship.
     * @param nickname The nickname of the player whose ship is to be viewed.
     * @throws RuntimeException if the specified player nickname is not found.
     */
    public void viewShip(UUID clientId, String nickname) throws Exception {
        ClientData targetClient = null;
        for (ClientData client : activeClients.values()) {
            if (client.nickname != null && client.nickname.equals(nickname)) {
                targetClient = client;
                break;
            }
        }
        if (targetClient == null) {
            for (ClientData client : passiveClients.values()) {
                if (client.nickname != null && client.nickname.equals(nickname)) {
                    targetClient = client;
                    break;
                }
            }
        }

        if (targetClient == null) {
            throw new RuntimeException("Player " + nickname + " not found");
        }

        final ClientData finalTargetClient = targetClient;

        showHourglassSecondsLeft(clientId);
        try {
            dispatcher.run(() -> activeClients.get(clientId).client.showShip(model.viewShip(finalTargetClient.playerId)));
        } catch (NullPointerException e) {
            dispatcher.run(() -> passiveClients.get(clientId).client.showShip(model.viewShip(finalTargetClient.playerId)));
        }
    }

    /**
     * Allows a client to draw a random face-down component.
     *
     * @param clientId The ID of the client drawing the component.
     */
    public void drawComponent(UUID clientId) throws Exception {
        Component component = model.drawComponent();
        showHourglassSecondsLeft(clientId);
        dispatcher.run(() -> activeClients.get(clientId).client.showComponent(component));
    }

    /**
     * Allows a client to view the list of face-up components on the game board.
     *
     * @param clientId The ID of the client requesting the list.
     */
    public void getFaceUpComponentList(UUID clientId) throws Exception {

        showHourglassSecondsLeft(clientId);
        dispatcher.run(() -> activeClients.get(clientId).client.showFaceUpComponentList(model.getFaceUpComponentList()));
    }

    /**
     * Allows a client to return a component to the board face-up.
     *
     * @param component The component to be returned.
     * @param clientId The ID of the client returning the component.
     */
    public void putComponentBack(Component component, UUID clientId) throws Exception {
        if (model.getPlayer(activeClients.get(clientId).playerId).getShip().getReservedComponentsList().stream().filter(Objects::nonNull).noneMatch(reservedComponent -> reservedComponent.getId() == component.getId())) {
            model.putComponentBack(component);
            showHourglassSecondsLeft(clientId);
        }
    }

    /**
     * Allows a client to draw a specific face-up component from the board.
     *
     * @param id The ID of the component to draw.
     * @param clientId The ID of the client drawing the component.
     */
    public void drawFaceUpComponent(int id, UUID clientId) throws Exception {
        model.drawFaceUpComponent(model.getComponent(id));
        showHourglassSecondsLeft(clientId);
        dispatcher.run(() -> activeClients.get(clientId).client.showComponent(model.getComponent(id)));
    }

    /**
     * Allows a client to draw a component from their reserved components. This is only for Level 2 games.
     *
     * @param index The index of the reserved component to draw.
     * @param clientId The ID of the client drawing the component.
     * @throws IllegalStateException if the game is Level 1.
     */
    public void drawFromReserved(int index, UUID clientId) throws Exception {
        if (model.getLevel() == 1)
            throw new IllegalStateException("You can't use this command now");
        showHourglassSecondsLeft(clientId);
        dispatcher.run(() -> activeClients.get(clientId).client.showComponent(model.getReservedComponent(model.getPlayer(activeClients.get(clientId).playerId), index)));
    }

    /**
     * Allows a client to remove a component from their ship.
     *
     * @param clientId The ID of the client removing the component.
     * @param line The row of the component to remove.
     * @param column The column of the component to remove.
     */
    public void removeComponent(UUID clientId, int line, int column) throws Exception {
        model.removeComponentFromShip(activeClients.get(clientId).playerId, line, column);
        dispatcher.run(() -> activeClients.get(clientId).client.showShip(model.getPlayer(activeClients.get(clientId).playerId).getShip()));
    }


    /**
     * Allows a client to add a component to their ship at a specified position.
     *
     * @param clientId The ID of the client adding the component.
     * @param x The row where the component will be added.
     * @param y The column where the component will be added.
     * @param component The component to add.
     * @throws IllegalStateException if the component cannot be added at this time.
     */
    public void addComponentToShip(UUID clientId, int x, int y, Component component) throws Exception {
        showHourglassSecondsLeft(clientId);
        if (!model.addComponentToShip(activeClients.get(clientId).playerId, x, y, component))
            throw new IllegalStateException("You can't use this command now");
    }

    /**
     * Allows a client to view a sub-deck of cards.
     *
     * @param clientId The ID of the client viewing the sub-deck.
     * @param index The index of the sub-deck.
     */
    public void drawSubDeck(UUID clientId, int index) {
        dispatcher.run(() -> activeClients.get(clientId).client.showSubDeck(model.drawSubDeck(index)));
    }

    /**
     * Helper method to show hourglass seconds left to a client.
     *
     * @param clientId The ID of the client to update.
     */
    public void showHourglassSecondsLeft(UUID clientId) throws Exception {
        if (hourGlass.isRunning()) {
            dispatcher.run(() -> activeClients.get(clientId).client.showSecondsLeft(hourglassCounter, hourGlass.getSecondsLeft()));
        }
    }

    public int runFinalHourglass() {
        if (hourglassCounter == 3 && !hourGlass.isRunning()) {
            hourGlass.start();
            hourglassCounter++;
            return 0;
        } else {
            return 1;
            //throw new RuntimeException("already running hourglass");
        }
    }

    public int runHourglass() {
        if (hourglassCounter < 3 && !hourGlass.isRunning()) {
            hourGlass.start();
            hourglassCounter++;
            return 0;
        } else {
            return 1;
        }
    }

    public void stopHourglass() {
        if (hourGlass.isRunning()) {
            hourGlass.stop();
        }
    }

    int c = 0;

    /**
     * Allows the leader player to draw a card from the deck.
     *
     * @param clientId The ID of the client drawing the card.
     */
    public void drawCard(UUID clientId) throws Exception {
        model.resetDiceResult();
        Card card;
        try {
            card = model.drawCard(activeClients.get(clientId).playerId);
        } catch (NoCardsLeftException e){
            // le carte sono finite, la partita è conclusa
            // può essere tirata solo nello specifico caso che sia rimasto un solo giocatore
            // e l'ultima carta del mazzo sia di tipo combat zone
            isGameOver(clientId);
            return;
        }
        model.setCurrentCard(card);
        for (ClientData client : activeClients.values()) {
            dispatcher.run(() -> client.client.showCard(card)).join();
        }

        model.setCurrentPlayer(model.getPlayer(activeClients.get(clientId).playerId));

        if (UpdateHandler.drawCardTurnHandler(card, this, activeClients, passiveClients, model, dispatcher)) return;

        for (ClientData client : activeClients.values()) {
            dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));
        }
    }

    /**
     * Handles a request to place a crew member or alien in a cabin on a player's ship.
     *
     * @param clientId The ID of the client making the request.
     * @param x The row of the target cabin.
     * @param y The column of the target cabin.
     * @param type The color of the alien to place (NONE for a crew member).
     */
    public void handleCrewRequest(UUID clientId, int x, int y, AlienColor type) throws Exception {
        model.handleCrewRequest(activeClients.get(clientId).playerId, x, y, type);
        dispatcher.run(() -> activeClients.get(clientId).client.showShip(model.getPlayer(activeClients.get(clientId).playerId).getShip()));
    }


    /**
     * Sets the context for the current card's pre-effect action.
     *
     * @param clientId The ID of the player calling the method.
     * @param parameters A map with parameters useful to set card context.
     */
    public void setCardContext(UUID clientId, Map<String, Object> parameters) throws Exception {
        try {
            int res = model.setCardContext(model.getPlayer(activeClients.get(clientId).playerId), parameters);
            if (res == 0) {
                for (ClientData client : activeClients.values()) {
                    dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));
                }
            } else if (res == 3) {
                dispatcher.run(() -> activeClients.get(clientId).client.showShip(model.getPlayer(activeClients.get(clientId).playerId).getShip()));
            } else if (res == 4){
                // ritorno di abandoned se un giocatore ha rifiutato la carta
                // controllo se quello dopo può giocarla (è come se l'avesse pescata)
                if (model.getPlayer(model.getCurrentPlayer().getId()).getShip().getCrewNumber() < model.getCurrentCard().getCrewMembers()) {
                    UpdateHandler.drawCardTurnHandler(model.getCurrentCard(), this, activeClients, passiveClients, model, dispatcher);
                } else {
                    for (ClientData client : activeClients.values()) {
                        dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            UpdateHandler.newLeader(activeClients, passiveClients, this, dispatcher, model);
        }
    }

    /**
     * Checks if the game is over by determining if there are any active clients left.
     * If no active clients remain, the flight phase ends and final rankings are displayed.
     *
     * @param clientId The ID of the client that triggered the check.
     * @return true if the game is over, false otherwise.
     */
    private boolean isGameOver(UUID clientId) {
        synchronized (passiveClients) {
            synchronized (activeClients) {
                passiveClients.put(clientId, activeClients.get(clientId));
                activeClients.remove(clientId);
                if (activeClients.isEmpty()) {
                    Map<Player, Integer> ranking = model.endFlightPhase();
                    for (ClientData client : passiveClients.values()) {
                        dispatcher.run(() -> client.client.showFinalRanking(ranking));
                    }
                    return true;
                }
                return false;
            }
        }
    }


    /**
     * Applies the current card's effect based on provided parameters.
     * Handles player eliminations.
     *
     * @param clientId The ID of the player applying the effect.
     * @param parameters Parameters required by the card's effect.
     */
    public void setEffectContext(UUID clientId, Map<String, Object> parameters) throws Exception {
        if(activeClients.get(clientId) == null){
            dispatcher.run(() -> passiveClients.get(clientId).client.lockClient());
            throw new RuntimeException("You can't use this command now");
        }
        // controllo se il giocatore ha potenza motore 0, in quel caso ha perso.
        try {
            if (model.getCurrentCard().getType() == EffectType.OPENSPACE && model.automaticEngineLost(activeClients.get(clientId).playerId)) {
                dispatcher.run(() -> activeClients.get(clientId).client.notifyDefeat(1)).join();
                if (isGameOver(clientId)) return;
                for (ClientData client : activeClients.values()) {
                    dispatcher.run(() -> client.client.turnChange(client.playerId == model.getCurrentPlayer().getId(), -1, model.getCurrentPlayer().getId()));
                }
                return;
            }
            // se viene lanciata vuol dire che il giocatore era l'ultimo
        } catch (IndexOutOfBoundsException e) {
            dispatcher.run(() -> activeClients.get(clientId).client.notifyDefeat(1)).join();
            if (isGameOver(clientId)) return;
            UpdateHandler.newLeader(activeClients, passiveClients, this, dispatcher, model);
            return;
        }

        int res;
        try {
            res = model.applyCardEffect(model.getPlayer(activeClients.get(clientId).playerId), parameters);
        } catch (IndexOutOfBoundsException e) {
            // effetto finito per tutti
            res = 2;
        }

        if (res == 2) {
            model.orderPlayers();
        } else if (res == 1) {
            dispatcher.run(() -> activeClients.get(clientId).client.lockClient());
        } else if (res == 3) {
            //combat zone o pirates ha finito un effetto intermedio
            if(model.getCurrentCard().getType() == EffectType.COMBATZONE || model.getCurrentCard().getType() == EffectType.PIRATES) {
                model.resetDiceResult();
            }
        }

        UpdateHandler.effectContextTurnHandler(clientId, res, activeClients, passiveClients, model, dispatcher);

        if (res != 2) return;
        // if the effect is over for everyone
        for (ClientData client : passiveClients.values()) {
            dispatcher.run(() -> client.client.updatePositions(model.getUpdatedPositions()));
        }
        // controllo se ogni giocatore ha perso e nel caso glielo segnalo
        List<UUID> removedClients = new ArrayList<>();
        synchronized (activeClients) {
            for (UUID id : activeClients.keySet()) {
                int loseType = model.automaticLost(activeClients.get(id).playerId);
                if (loseType == 0) continue;
                dispatcher.run(() -> activeClients.get(id).client.notifyDefeat(loseType)).join();
                passiveClients.put(id, activeClients.get(id));
                removedClients.add(id);
                if (removedClients.size() == activeClients.size()) {
                    Map<Player, Integer> ranking = model.endFlightPhase();
                    for (ClientData c : passiveClients.values()) {
                        dispatcher.run(() -> c.client.showFinalRanking(ranking));
                    }
                    return;
                }
                model.setCurrentPlayer(model.getPlayer(getLeader()));
            }
            for (UUID client : removedClients) {
                activeClients.remove(client);
            }
        }
        UpdateHandler.newLeader(activeClients, passiveClients, this, dispatcher, model);
    }

    /**
     * Handles a player abandoning the race.
     * Updates player status and checks if the game is over.
     *
     * @param clientId The ID of the client abandoning the race.
     */
    public void abandonRace(UUID clientId) throws Exception {
        boolean isLeader = activeClients.get(clientId).playerId == model.getLeader();
        if(isLeader) {
            try {
                model.passTurn();
                for (ClientData client : activeClients.values()) {
                    if(client.playerId != model.getCurrentPlayer().getId())
                        continue;
                    dispatcher.run(() -> client.client.newLeader(true));
                    break;
                }
            }catch(IndexOutOfBoundsException _){}
        }
        if(isGameOver(clientId)) return;
        model.abandonRace(passiveClients.get(clientId).playerId);
    }


    /**
     * Rolls dice for card effect resolution.
     *
     * @param clientId The ID of the client rolling the dice.
     */
    public void rollDice(UUID clientId) throws Exception {
        int diceResult = model.rollDice(activeClients.get(clientId).playerId);
        if (model.getCurrentCard().getType() == EffectType.METEORSWARM) {
            for (ClientData client : activeClients.values()) {
                dispatcher.run(() -> {
                    client.client.diceResult(diceResult);
                    client.client.effectResolution();
                });
            }
        } else {
            dispatcher.run(() -> {
                activeClients.get(clientId).client.diceResult(diceResult);
                activeClients.get(clientId).client.effectResolution();
            });
        }
    }

    public void controllerPing(){}


    public void startHourGlass(UUID clientId) throws Exception{
        System.out.println("Start hour glass request received by " + activeClients.get(clientId).nickname);
        synchronized (activeClients) {
            if(runHourglass() == 0){
                dispatcher.run(() -> activeClients.get(clientId).client.showSecondsLeft(hourglassCounter, hourGlass.getSecondsLeft()));
                for(UUID id : activeClients.keySet()) {
                    dispatcher.run(() -> activeClients.get(id).client.showSecondsLeftForGui(hourglassCounter, hourGlass.getSecondsLeft()));
                }
            }
            else {
                dispatcher.run(() -> activeClients.get(clientId).client.showUpdate(UpdateMessage.HOURGLASS_NOT_AVAILABLE));
            }
        }
    }

    public void startFinalHourGlass(UUID clientId) throws Exception{
        System.out.println("Start hour glass request received by " + activeClients.get(clientId).nickname);
        synchronized (activeClients) {
            if(runFinalHourglass() != 0) {
                dispatcher.run(() -> activeClients.get(clientId).client.showUpdate(UpdateMessage.FINAL_HOURGLASS_NOT_AVAILABLE));
                return;
            }
            dispatcher.run(() -> activeClients.get(clientId).client.showSecondsLeft(hourglassCounter, hourGlass.getSecondsLeft()));
            for(UUID id : activeClients.keySet()) {
                dispatcher.run(() -> activeClients.get(id).client.showSecondsLeftForGui(hourglassCounter, hourGlass.getSecondsLeft()));
            }
            new Thread(() -> {
                try {
                    while(true){
                        if(hourGlass.getSecondsLeft() != 1) {
                            Thread.sleep(500);
                            continue;
                        }
                        for(UUID id : activeClients.keySet()) {
                            if(activeClients.get(id) != activeClients.get(clientId)){
                                dispatcher.run(() -> activeClients.get(id).client.finishFinalHourglass());
                            }
                        }
                        Thread.currentThread().interrupt();
                    }
                } catch (Exception e) {
                    System.out.println("Hourglass finished");
                }
            }).start();
        }
    }
}
