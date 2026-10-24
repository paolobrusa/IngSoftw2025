package it.polimi.ingsw.is25am31.model;

import java.util.*;

import it.polimi.ingsw.is25am31.model.cards.*;
import it.polimi.ingsw.is25am31.model.components.*;
import it.polimi.ingsw.is25am31.customerrors.NameErrorException;
import it.polimi.ingsw.is25am31.model.gameboard.CurrentPhase;
import it.polimi.ingsw.is25am31.model.gameboard.Gameboard;
import it.polimi.ingsw.is25am31.model.gameboard.Phase;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Rocket;
import it.polimi.ingsw.is25am31.model.player.Ship;

/**
 * Represents a Galaxy Trucker game session. Manages game state, players, components,
 * and game flow according to Galaxy Trucker rules. Handles initialization, ship building,
 * flight phase, card effects, and scoring
 */

public class Game {

    private List<Player> players;
    private Deck deck;
    private Tiles tiles;
    CurrentPhase currentPhase;
    private Gameboard gameboard;
    private int level;
    private int maxPlayers;
    private final List<Player> finishingOrder = new ArrayList<>();
    private Card currentCard;
    private Player currentPlayer;
    private int diceResult;
    private List<Player> remainingPlayers = new ArrayList<>();
    private List<Player> eliminatedPlayers = new ArrayList<>();

    /**
     * Constructs a new Game with default settings
     * Initializes player list and default max players to 5
     */

    public Game(){
        this.currentPlayer = null;
        this.players = new LinkedList<>();
        this.currentPhase = new CurrentPhase(Phase.NEWGAME);
        this.maxPlayers = 5;
    }

    /**
     * Sets the game difficulty level and initializes game components
     *
     * @param level Game difficulty level (1 or 2)
     */

    public void setLevel(int level) {
        this.level = level;
        this.gameboard = new Gameboard(level);
        this.deck = new Deck(level);
        this.tiles = new Tiles(level);
        diceResult = 0;
        currentPhase.setPhase(Phase.INITIALIZENICKNAME);
    }

    /**
     * Checks if the game has started by verifying maxPlayers has changed from default
     *
     * @return true if the game started; false otherwise
     */

    public boolean isGameStarted() {
        return getMaxPlayers() != 5;
    }

    /**
     * Gets players still actively participating in the race
     *
     * @return list of players still active in the race
     */
    public List<Player> getRemainingPlayers() {
        return remainingPlayers;
    }

    /**
     * Gets players eliminated from the current race
     *
     * @return list of players who have been eliminated
     */

    public List<Player> getEliminatedPlayers() {
        return eliminatedPlayers;
    }

    /**
     * Gets current game difficulty level
     *
     * @return Current game level (1 or 2)
     */

    public int getLevel(){
        return level;
    }

    /**
     * Sets the current event card in play
     *
     * @param card the card to set as current
     */

    public void setCurrentCard(Card card){
        currentCard = card;
    }

    /**
     * Changes a player's rocket position with collision avoidance logic
     *
     * @param player the player whose rocket to move
     * @param change the amount to move the rocket (positive or negative)
     */

    public void changeRocketPosition(Player player, int change){
        int count = 0;
        if(change == 0)
            return;
        for (Player p : players){
            if (player.getRocket().getPosition() + change == p.getRocket().getPosition()){
                change += change > 0 ? 1 : -1;
                count = 1;
            }
        }
        if(count == 1){
            count = 0;
            changeRocketPosition(player, change);
        }
        else {
            player.getRocket().changePosition(change);
        }
    }

    /**
     * Sets a player's rocket starting position based on their finishing order
     *
     * @param player the player to position
     * @param position the finishing position (1–4)
     * @throws IllegalArgumentException if position is out of range
     * @throws RuntimeException if the chosen position is already occupied
     */
    public synchronized void setPosition(Player player, int position){
        if(position < 0 || position > getPlayers().size())
            throw new IllegalArgumentException("Invalid position");

        switch (position){
            case 1 -> {
                position = level == 2 ? 7 : 5;
                setCurrentPlayer(player);
            }
            case 2 -> position = level == 2 ? 4 : 3;
            case 3 -> position = 2;
            case 4 -> position = 1;
        }

        for (Player p : players){
            if (p.getRocket().getPosition() == position)
                throw new RuntimeException("Position has already been chosen by another player");
        }

        player.getRocket().setPosition(position);
    }

    /**
     * Rolls dice for card effect resolution
     *
     * @param playerId ID of rolling player
     * @return Dice roll result (2-12)
     * @throws IllegalStateException If not permitted to roll
     */

    public int rollDice(int playerId){
        if (!getCurrentCard().getType().equals(EffectType.METEORSWARM) &&
            !getCurrentCard().getType().equals(EffectType.PIRATES) &&
            !getCurrentCard().getType().equals(EffectType.COMBATZONE)) {
            throw new IllegalStateException("You can't roll dice now");
        }
        if(!getPlayer(playerId).equals(currentPlayer)){
            throw new IllegalStateException("You can't roll dice now");
        }
        if(diceResult != 0){
            throw new IllegalStateException("Dice has already been rolled");
        }
        diceResult = (int)(Math.random() * 11 + 2);

        return diceResult;
    }

    /**
     * Gets last dice roll result
     *
     * @return Current dice result
     */

    public int getDiceResult(){
        return diceResult;
    }

    /**
     * Resets dice result to 0.
     */

    public void resetDiceResult(){
        this.diceResult = 0;
    }

    /**
     * Sets current game phase
     *
     * @param phase New game phase
     */

    public void setCurrentPhase(Phase phase) {
        currentPhase.setPhase(phase);
    }

    /**
     * Gets the game board
     *
     * @return Current game board
     */


    public Gameboard getGameboard() {
        return gameboard;
    }

    /**
     * Sets allowed number of players (2-4)
     *
     * @param maxPlayers number of players
     * @throws IllegalArgumentException if out of range
     */

    public void setMaxPlayers(int maxPlayers) {
        if(maxPlayers < 2 || maxPlayers > 4)
            throw new IllegalArgumentException("number of players must be between 2 and 4");
        this.maxPlayers = maxPlayers;
    }

    /**
     * Gets maximum allowed players.
     *
     * @return Max player count
     */

    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Adds a player with unique id and nickname to the game
     *
     * @param id unique player id
     * @param name desired nickname
     * @throws NameErrorException if name already taken
     * @throws IllegalStateException if id already used
     * @throws IllegalArgumentException if max players reached
     */

    public void addPlayer(int id, String name) throws NameErrorException {
        if(getPlayer(id) != null && getPlayer(id).getNickname() != null)
            throw new IllegalStateException("You've already set your nickname");
        for (Player player : players) {
            if (player.getNickname().equalsIgnoreCase(name)) {
                throw new NameErrorException("This username is already taken");
            }
        }
        if(players.size() >= maxPlayers){
            throw new IllegalArgumentException("Max player reached");
        }
        players.add(new Player(id, name, gameboard));
        remainingPlayers.add(players.getLast());
        if(players.size() == maxPlayers){
            List<Color> colors = new ArrayList<>(List.of(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW));
            for(Player player : players){
                Color color = colors.removeFirst();
                player.getRocket().setColor(color);
                player.getShip().setColor(color);
            }
        }
    }


    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the Ship object for given player id
     *
     * @param id player id
     * @return Ship or null if not found
     */

    public Ship viewShip(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p.getShip();
            }
        }
        return null;
    }

    /**
     * Finds a player by id among active and eliminated
     *
     * @param id player id
     * @return Player or null if none
     */

    public Player getPlayer(int id) {
        for (Player p : players) {
            if (p.getId() == id)  return p;
        }
        for (Player p : eliminatedPlayers) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    /**
     * Initializes ships for all players and assigns initial crew
     */

    public void initializeShip() {
        for (Player p : players) {
            Ship ship = new Ship(gameboard);
            p.setShip(ship);
            p.setRocket(null);
            p.setCrew(p.getCrew() + 2);
        }
    }

    /**
     * Retrieves a reserved component from a player's ship
     *
     * @param player the Player
     * @param index the component index
     * @return the Component reserved
     */

    public Component getReservedComponent(Player player, int index) {
        return player.getShip().getReservedComponent(index);
    }

    /**
     * Gets the current phase of the game
     *
     * @return the Phase enum
     */

    public Phase getCurrentPhase() {
        return currentPhase.getPhase();
    }

    //FASE 3: Assemblaggio della nave

    /**
     * Draws a random face-down component
     *
     * @return Drawn component
     */

    public Component drawComponent() {
        return tiles.getFaceDownComponent();
    }

    /**
     * Gets component by ID
     *
     * @param id Component ID
     * @return Requested component
     */

    public Component getComponent(int id) {
        return tiles.getComponent(id);
    }

    /**
     * Lists all face-up components
     *
     * @return List of face-up components
     */

    public List<Component> getFaceUpComponentList(){
        return tiles.getFaceUpComponentsList();
    }

    /**
     * Returns a component to the board face-up
     *
     * @param component Component to return
     */
    public void putComponentBack(Component component) {
        tiles.putDownComponent(component);
    }

    /**
     * Draws a specific face-up component
     *
     * @param component Component to draw
     */

    public void drawFaceUpComponent(Component component) {
        tiles.getFaceUpComponent(component);
    }

    /**
     * Adds component to player's ship
     *
     * @param playerId Player ID
     * @param line Grid row
     * @param column Grid column
     * @param component Component to add
     * @return true if successful, false otherwise
     */

    public boolean addComponentToShip(int playerId, int line, int column, Component component) {
        Player player = getPlayer(playerId);
        if (player != null && player.getShip() != null) {
            return player.getShip().addComponent(line, column, component);
        }
        return false;
    }

    /**
     * Removes component from player's ship
     *
     * @param playerId Player ID
     * @param line Component row
     * @param column Component column
     * @return true if successful, false otherwise
     */

    public boolean removeComponentFromShip(int playerId, int line, int column) {
        Player player = getPlayer(playerId);
        if (player != null && player.getShip() != null) {
            boolean removed = player.getShip().removeComponent(line, column);
            return true;
        }
        return false;
    }

    /**
     * Creates rocket for player
     *
     * @param playerId Player ID
     */

    public void instantiateRocketForPlayer(int playerId) {
        Player player = getPlayer(playerId);
        if (player != null && player.getRocket() == null) {
            player.setRocket(new Rocket(gameboard)); //crea il razzo solo se non esiste
        }
    }

    /**
     * Completes ship assembly and starts flight if all ready
     *
     * @param playerId Player ID
     */

    public void completeAssembly(int playerId) {
        Player player = getPlayer(playerId);
        if (player != null && player.getShip() != null) {
            instantiateRocketForPlayer(playerId); //crea il razzo se non esiste
            if (!finishingOrder.contains(player)) {
                finishingOrder.add(player); //aggiunge il giocatore alla lista
            }
            // assegna le posizioni e avvia la fase di volo se tutti hanno completato
            if (finishingOrder.size() == players.size()) {
                assignRocketPositionsToFinishedPlayers(finishingOrder);
                startFlightPhase();
            }
        }
    }

    /**
     * Gets ship completion order
     *
     * @return Players in build finish order
     */

    public List<Player> getFinishingOrder() {
        return finishingOrder;
    }

    /**
     * Assigns starting positions based on build completion order
     *
     * @param finishingOrder Players in build completion order
     */

    public void assignRocketPositionsToFinishedPlayers(List<Player> finishingOrder) {
        int position = 1;
        for (Player p : finishingOrder) {
            if (p.getRocket() != null) {
                p.getRocket().setPosition(position);
            }
            position++;
        }
    }

    /**
     * Views a sub-deck of cards
     *
     * @param index Sub-deck index (0-2)
     * @return Cards in sub-deck
     * @throws RuntimeException If invalid index or wrong game level
     */

    public List<Card> drawSubDeck(int index) {
        if(index < 0 || index > 2)
            throw new RuntimeException("Invalid index");
        if(getLevel() != 2)
            throw new RuntimeException("Can't use this command in test flight");
       return deck.getSubDeck(index);
    }

    /**
     * Checks if player can continue racing
     *
     * @param player Player to check
     * @return true if player can continue, false otherwise
     */

    public boolean canContinuePlaying(Player player) {
        if (player == null || player.hasAbandoned()) { return false;}
        Ship ship = player.getShip();
        if (ship == null) { return false;}

        Rocket rocket = player.getRocket();
        if (rocket == null) { return false;}

        if (ship.getCrewNumber() <= 0) { return false;}
        if (ship.getEnginePower() <= 0) { return false;}

        List<Player> routeRanking = getRouteRankingForRemainingPlayers();
        int trackLength = gameboard.getBoxes();

        if (!routeRanking.isEmpty()) {
            Player leader = routeRanking.get(routeRanking.size() - 1);
            int leaderPosition = leader.getRocket().getPosition();
            int currentPosition = rocket.getPosition();

            return leaderPosition < currentPosition + trackLength;
        }
        return true;
    }

    public void startFlightPhase() {
        currentPhase.setPhase(Phase.FLIGHT);
    }

    /**
     * Shuffles all card decks together
     */

    public void shuffleCards() {
        deck.mergeSubDecks();
    }

    /**
     * Checks if deck is empty (ends flight phase)
     *
     * @return Final scores if deck empty, null otherwise
     */

    public Map<Player, Integer> checkDeck(){
        return deck.isEmpty() ? endFlightPhase() : null;
    }

    /**
     * Initializes crew members for player
     *
     * @param player Player to initialize
     */

    public void initializeCrew(Player player) {
            Ship ship = player.getShip();
            if (ship == null) return;
            for (Component comp : ship.getComponents()) {
                if (!comp.getName().equals("Cabin")) continue;
                Cabin cabin = (Cabin) comp;
                if (!cabin.hasBrownAlienLifeSupport() && !cabin.hasPurpleAlienLifeSupport()) {
                    cabin.modifyCrewMembers(2);
                    player.setCrew(player.getCrew() + 2);
                }
            }

    }

    /**
     * Handles crew/alien placement request
     *
     * @param playerId Player ID
     * @param x Cabin row
     * @param y Cabin column
     * @param alienColor Alien color (NONE for crew)
     * @throws IllegalArgumentException For invalid player or position
     * @throws RuntimeException For invalid placement rules
     */

    public void handleCrewRequest(int playerId, int x, int y, AlienColor alienColor) {
        Player player = getPlayer(playerId);
        if (player == null)
            throw new IllegalArgumentException("Giocatore non trovato");

        Ship ship = player.getShip();
        Component targetComponent = ship.getComponent(x, y);
        if (targetComponent == null || !targetComponent.getName().equals("Cabin") || targetComponent.getName().equals("MainCabin"))
            throw new IllegalArgumentException("Posizione non valida o non è una cabina");

        Cabin cabin = (Cabin) targetComponent;
        if (alienColor != AlienColor.NONE) {
            handleAlienAddition(player, cabin, alienColor);
        } else {
            handleCrewAddition(player, cabin);
        }
    }

    private void handleAlienAddition(Player player, Cabin cabin, AlienColor color) {
        if (!hasProperLifeSupport(player.getShip(), cabin, color))
            throw new RuntimeException("Nessun modulo di supporto vitale " + color + " adiacente");

        if (player.getShip().getAlienCount(color) >= 1)
            throw new RuntimeException("È già presente un alieno " + color + " sulla nave");

        if (!cabin.addAlien(color))
            throw new RuntimeException("Impossibile aggiungere alieno: cabina non idonea");

        player.setCrew(player.getCrew() + 1);
    }

    private void handleCrewAddition(Player player, Cabin cabin) {
        if (cabin.getCurrentAlien() != AlienColor.NONE)
            throw new RuntimeException("La cabina è occupata da un alieno");

        if (!cabin.modifyCrewMembers(2))
            throw new RuntimeException("Spazio insufficiente nella cabina");

        player.setCrew(player.getCrew() + 2);
    }

    private boolean hasProperLifeSupport(Ship ship, Cabin cabin, AlienColor color) {
        int x = cabin.getX(); //riga
        int y = cabin.getY(); //colonna

        //controlla tutte e 4 le direzioni
        return checkAdjacent(ship, x-1, y, color) ||
                checkAdjacent(ship, x+1, y, color) ||
                checkAdjacent(ship, x, y-1, color) ||
                checkAdjacent(ship, x, y+1, color);
    }

    private boolean checkAdjacent(Ship ship, int x, int y, AlienColor targetColor) {
        if(x < 0 || x >= ship.getGrid().length) return false;
        if(y < 0 || y >= ship.getGrid()[0].length) return false;

        Component adj = ship.getComponent(x, y);
        return adj != null &&
                adj.isAlienLifeSupport() &&
                adj.getAlienColor() == targetColor &&
                !adj.isAlienStructural();
    }

    /**
     * Removes alien from cabin
     *
     * @param playerId Player ID
     * @param x Cabin row
     * @param y Cabin column
     * @throws IllegalArgumentException For invalid cabin
     */

    public void removeAlien(int playerId, int x, int y) {
        Player player = getPlayer(playerId);
        Ship ship = player.getShip();
        Component component = ship.getComponent(x, y);
        if (component == null || !component.getName().equals("Cabin") || component.getName().equals("MainCabin")) {throw new IllegalArgumentException("Invalid cabin");}
        Cabin cabin = (Cabin) component;
        if (cabin.getCurrentAlien() != AlienColor.NONE) {
            cabin.removeAlien();
            int newCrew = player.getCrew() - 1;
            player.setCrew(Math.max(newCrew, 0)); //equipaggio non può essere negativo
        }
    }

    /**
     * Orders players by rocket position (descending)
     */

    public void orderPlayers() {
        players.sort(Comparator.comparingInt((Player p) -> p.getRocket().getPosition()).reversed());
    }

    /**
     * Orders players by rocket position (ascending)
     */

    public void orderPlayersReverse() {
        players.sort(Comparator.comparingInt((Player p) -> p.getRocket().getPosition()));
    }

    /**
     * Gets current race leader ID
     *
     * @return Leader player ID
     */

    public int getLeader(){
        return players.getFirst().getId();
    }

    /**
     * Draws next card for flight phase
     *
     * @param playerId Drawing player ID
     * @return Drawn card
     * @throws RuntimeException If not leader's turn
     */

    public Card drawCard(int playerId){
        ContextValidator.reset();
        currentPlayer = players.getFirst();
        if(currentPlayer.getId() != playerId){
            throw new RuntimeException("You are not the leader, you can't draw a card");
        }

        do {
            currentCard = deck.drawCard();
        // se c'è un solo giocatore la carta zona di guerra viene saltata
        } while(players.size() == 1 && currentCard.getType() == EffectType.COMBATZONE);

        if(currentCard.getType() == EffectType.COMBATZONE && currentCard.isTestFlight()) {
            for (Player player : getPlayers()) {
                if (player.getId() == getPlayers().getLast().getId()) {
                    applyCardEffect(player, null);
                    continue;
                }
                applyCardEffect(player, null);
                passTurn();
            }
            applyCardEffect(getCurrentPlayer(), null);
        }

        return currentCard;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Passes turn to next player
     */

    public void passTurn(){
        try {
            setCurrentPlayer(players.get(players.indexOf(getCurrentPlayer()) + 1));
        }catch (IndexOutOfBoundsException e){
            currentPlayer = players.getFirst();
            throw new IndexOutOfBoundsException(e.getMessage());
        }
    }

    /**
     * Applies current card's effect to player
     *
     * @param player Target player
     * @param parameters Effect parameters
     * @return Application result code
     * @throws IllegalStateException If no card or wrong turn
     */

    public int applyCardEffect(Player player, Map<String, Object> parameters) {
        if(getCurrentCard() == null) {throw new IllegalStateException("No current card selected");} //controlla se la carta è null
        parameters = ContextValidator.preEffectValidation(player.getId(), parameters, this);
        if(ContextValidator.handleMultiPhaseEffect(player.getId(), this)) return 0;
        if(player != getCurrentPlayer() && getCurrentCard().getType() != EffectType.METEORSWARM)
            throw new IllegalStateException("It's not your turn");
        Context context = parameters == null ? null : new Context(parameters);
        return currentCard.applyEffect(getPlayer(player.getId()), context) ? ContextValidator.isEffectResolved(player.getId(), this) : 3;
    }

    /**
     * Sets context for current card
     *
     * @param player Acting player
     * @param parameters Context parameters
     * @return Validation result code
     */

    public int setCardContext(Player player, Map<String, Object> parameters){
        int res = ContextValidator.preCardContextValidation(this, player.getId(), parameters);

        if(res != 2)
            return res;

        Context context = new Context(parameters);
        currentCard.setCardContext(player, context);

        return ContextValidator.postCardContextValidation(this);
    }

    /**
     * Gets current active card
     *
     * @return Current card
     */
    public Card getCurrentCard(){
        return currentCard;
    }

    /**
     * Removes player from current race
     *
     * @param playerId Player to abandon
     */
    public void abandonRace(int playerId) {
        Player player = getPlayer(playerId);
        if (player == null || !remainingPlayers.contains(player)) { return; }
        int totalGoods = calculateTotalGoods(player);
        int halfValue;
        if (totalGoods % 2 == 0) {
            halfValue = totalGoods / 2;
        } else {
            halfValue = totalGoods / 2 + 1;
        }
        player.addCargoCredits(halfValue);
        clearGoods(player);
        int penalty = player.getLostComponents();
        player.addCosmicCredits(-penalty);
        players.remove(player);
        remainingPlayers.remove(player);
        eliminatedPlayers.add(player);
        player.setAbandoned(true);
        gameboard.removeRockets(player.getRocket());
    }

    private int calculateTotalGoods(Player player) {
        int total = 0;
        Ship ship = player.getShip();
        if (ship != null) {
            for (Component comp : ship.getComponents()) {
                if (comp.getClass().equals(CargoHold.class)) {
                    CargoHold cargo = (CargoHold) comp;
                    for (Good good : cargo.getContains()) {
                        total += good.getValue();
                    }
                }
            }
        }
        return total;
    }

    private void clearGoods(Player player) {
        Ship ship = player.getShip();
        if (ship != null) {
            for (Component comp : ship.getComponents()) {
                if (comp.getClass().equals(CargoHold.class)) {
                    CargoHold cargo = (CargoHold) comp;
                    cargo.getContains().clear();
                }
            }
        }
    }

    /**
     * Converts all goods to credits for remaining players
     */

    public void sellGoodsPhase() {
        for (Player p : remainingPlayers) {
            if (p.getShip() != null) {
                int totalGoodsValue = 0; // variabile per accumulare il valore totale delle merci
                List<Component> comps = p.getShip().getComponents();
                for (Component comp : comps) {
                    if (comp.getClass().equals(CargoHold.class)) {
                        CargoHold cargo = (CargoHold) comp;
                        List<Good> goods = cargo.getContains();
                        for (Good good : goods) {totalGoodsValue += good.getValue();}
                        cargo.getContains().clear();
                    }
                }
                p.addCargoCredits(totalGoodsValue);
            }
        }

    }

    /**
     * Ends flight phase and calculates scores
     *
     * @return Final player scores
     */

    public Map<Player, Integer> endFlightPhase() {
        List<Player> remainingCopy = new ArrayList<>(remainingPlayers);
        for (Player p : remainingCopy) {
            if (!canContinuePlaying(p)) { abandonRace(p.getId()); }
        }
        sellGoodsPhase();
        List<Player> routeRanking = getRouteRankingForRemainingPlayers();
        int minExposed = calculateMinExposedForRemainingPlayers();
        Map<Player, Integer> scores = new HashMap<>();
        for (Player p : remainingPlayers) {
            int beautyBonus = (p.getShip().getExposedConnectors() == minExposed) ? 2 : 0;
            int routeBonus = getRouteBonus(p, routeRanking);
            int finalScore = p.getCosmicCredits() + p.getCargoCredits() + beautyBonus + routeBonus - p.getLostComponents();
            scores.put(p, finalScore);
        }
        for (Player p : eliminatedPlayers) {
            int finalScore = p.getCosmicCredits() + p.getCargoCredits();
            scores.put(p, finalScore);
        }
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(remainingPlayers);
        allPlayers.addAll(eliminatedPlayers);
        List<Player> ranking = new ArrayList<>(allPlayers);
        ranking.sort((p1, p2) -> scores.get(p2) - scores.get(p1));
        Map<Player, Integer> scoresByPlayer = new HashMap<>();
        for (Player p : ranking) {
            int finalScore = scores.get(p);
            scoresByPlayer.put(p, finalScore);
        }
        currentPhase.setPhase(Phase.FLIGHT_ENDED);
        return scoresByPlayer;
    }

    //ricompensa per l'ordine di arrivo
    private int getRouteBonus(Player p, List<Player> routeRanking) {
        int rank = routeRanking.indexOf(p) + 1; // rank parte da 1
        return switch (rank) {
            case 1 -> 4;
            case 2 -> 3;
            case 3 -> 2;
            case 4 -> 1;
            default -> 0;
        };
    }

    //per giocatori attivi
    List<Player> getRouteRankingForRemainingPlayers() {
        List<Player> routeRanking = new ArrayList<>(remainingPlayers);
        routeRanking.sort((p1, p2) -> {
            int pos1 = (p1.getRocket() != null) ? p1.getRocket().getPosition() : Integer.MIN_VALUE;
            int pos2 = (p2.getRocket() != null) ? p2.getRocket().getPosition() : Integer.MIN_VALUE;
            return Integer.compare(pos2, pos1);
        });
        return routeRanking;
    }

    //bonus bellezza per rimanenti
    private int calculateMinExposedForRemainingPlayers() {
        int minExposed = Integer.MAX_VALUE;
        for (Player p : remainingPlayers) {
            if (p.getShip() != null) {
                int exposed = p.getShip().getExposedConnectors();
                if (exposed < minExposed) {
                    minExposed = exposed;
                }
            }
        }
        return (minExposed == Integer.MAX_VALUE) ? 0 : minExposed;
    }

    /**
     * Automatically eliminates player if conditions met
     *
     * @param playerId Player to check
     * @return Status code (0=no action, 2=no crew, 3=too far behind)
     */
    public int automaticLost (int playerId){
        int rocketPositionMax = 0;
        int rocketPos = 0;
        if (getPlayer(playerId).getShip().getCrewNumberWithoutAlien() == 0) {
            try {
                abandonRace(playerId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return 2;
        }
        for (Player player : getPlayers()) {
            rocketPos = player.getRocket().getPosition();
            if (rocketPositionMax < rocketPos && player.getId() != playerId) {
                rocketPositionMax = rocketPos;
            }
        }
        if (getPlayer(playerId).getRocket().getPosition() + (getLevel() == 2 ? 24 : 18) < rocketPositionMax) {
            try {
                abandonRace(playerId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return 3;
        }
        return 0;
    }

    /**
     * Checks and handles engine loss elimination
     *
     * @param playerId Player to check
     * @return true if eliminated, false otherwise
     */

    public boolean automaticEngineLost (int playerId){
        if(getPlayer(playerId).getShip().getComponents().stream().filter(c -> c.getName().equals("Engine")).noneMatch(Component::isActive)) {
            passTurn();
            abandonRace(playerId);
            return true;
        }
        return false;
    }

    /**
     * Gets current rocket positions
     *
     * @return Map of player IDs to positions
     */

    public Map<Integer, Integer> getUpdatedPositions(){
        Map<Integer, Integer> positions = new HashMap<>();
        for(Player p : getPlayers()) {
            positions.put(p.getId(), p.getRocket().getPosition());
        }
        return positions;
    }
}


