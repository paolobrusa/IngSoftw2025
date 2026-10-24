package it.polimi.ingsw.is25am31.view.tui;

import it.polimi.ingsw.is25am31.model.cards.Card;
import it.polimi.ingsw.is25am31.model.cards.EffectType;
import it.polimi.ingsw.is25am31.model.components.Color;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Connector;
import it.polimi.ingsw.is25am31.model.components.Side;
import it.polimi.ingsw.is25am31.model.player.Player;
import it.polimi.ingsw.is25am31.model.player.Ship;
import it.polimi.ingsw.is25am31.network.*;
import it.polimi.ingsw.is25am31.view.tui.input.CommandParser;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import it.polimi.ingsw.is25am31.view.UiRenderer;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@code TuiRenderer} is an implementation of the {@link UiRenderer} interface that provides
 * a Text-based User Interface (TUI) for the game. It handles displaying game elements,
 * processing user input, and updating the console based on game state changes.
 * This class follows the Singleton design pattern.
 */
public class TuiRenderer implements UiRenderer {
    private static final int LARGHEZZA_CELLA = 20;
    private int effectCount = 0;
    private ClientModel clientModel;
    private static TuiRenderer instance;
    private CommandParser parser;
    private final AsyncRunner printer;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    /**
     * Private constructor. Initializes the {@link AsyncRunner}.
     */
    private TuiRenderer(){
        this.printer = new AsyncRunner();
    }

    /**
     * Returns the singleton instance of {@code TuiRenderer}. If no instance exists, it creates one.
     *
     * @return The singleton instance of {@code TuiRenderer}.
     */
    public static TuiRenderer getInstance(){
        if(instance == null){
            instance = new TuiRenderer();
        }
        return instance;
    }

    /**
     * Starts and runs the TUI. It initializes the {@link CommandParser} in a new thread
     * to continuously read and handle user input from the console.
     *
     * @param client The virtual view representing the client's connection to the server.
     * @param server The virtual server instance.
     * @param clientModel The client-side model containing the current state of the game for this client.
     */
    @Override
    public void runUi(VirtualView client, VirtualServer server, ClientModel clientModel)  {
        new Thread(() -> {
            Scanner scan = new Scanner(System.in);
            this.clientModel = clientModel;
            lock.lock();
            try {
                parser = new CommandParser(server, clientModel, this);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
            while (true) {
                String command = scan.nextLine();
                try {
                    parser.handleInput(command);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Prints the prompt symbol ("> ") to the console, indicating that the UI is ready for user input.
     * This is typically called after an action or message.
     */
    public void printPromptSymbol() {
        printer.run(() -> {
            System.out.print("> ");
        });
    }

    /**
     * Sets the unique client ID (UUID) for the {@link CommandParser}. It waits until the
     * parser is initialized before setting the ID to avoid NullPointerExceptions.
     *
     * @param clientId The {@link UUID} to be set for this client.
     * @throws InterruptedException if the current thread is interrupted while waiting for the parser to be initialized.
     */
    @Override
    public void setUUID(UUID clientId) throws InterruptedException {
        lock.lock();
        try {
            while (parser == null) {
                condition.await();
            }
            parser.setClientId(clientId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Displays a drawn game component in a formatted ASCII art representation.
     *
     * @param component The {@link Component} to be displayed.
     */
    public void showsDrawnComponent(Component component) {
        printer.run(() -> {
            Connector top = component.getConnector(Side.FRONT);
            String sopra = formatTopBottom(top);

            System.out.println("+--------------------+");
            System.out.printf("|%-20s|\n", sopra);

            Connector left = component.getConnector(Side.LEFT);
            Connector right = component.getConnector(Side.RIGHT);
            String[] art = component.getAsciiArt();
            String center = art[1].trim();

            String contenuto = formatSide(left) + center + formatSide(right);
            contenuto = padCenter(contenuto, LARGHEZZA_CELLA);
            System.out.printf("|%-20s|\n", contenuto);

            Connector bottom = component.getConnector(Side.BACK);
            String sotto = formatTopBottom(bottom);
            System.out.printf("|%-20s|\n", sotto);

            System.out.println("+--------------------+");
        });
    }

    /**
     * Displays a list of face-up components, showing each component's ID and its ASCII art.
     *
     * @param components A list of components to be displayed.
     */
    @Override
    public void showFaceUpComponentList(List<Component> components) {
        printer.run(() -> {
            for (Component c : components) {
                printer.run(() ->  System.out.println("id: " + c.getId()));
                showsDrawnComponent(c);
            }
            printer.run(this::printPromptSymbol);
        });
    }

    /**
     * Draws the entire ship grid in a formatted ASCII art representation.
     *
     * @param ship The {@link Ship} object to be drawn.
     */
    public void drawShip(Ship ship)  {
        Component[][] grid = ship.getGrid();
        printer.run(() -> {
            System.out.print("   ");
            for (int c = 0; c < 7; c++) {
                System.out.print("          " + (c + 4) + "          ");
            }
            System.out.println();
            System.out.print("   ");
            for (int c = 0; c < 7; c++) {
                System.out.print("+--------------------");
            }
            System.out.println("+");

            for (int r = 0; r < 5; r++) {

                for (int c = 0; c < 7; c++) {
                    Component comp = grid[r][c];

                    if (r == 0 && (c == 5 || c == 6) && comp != null)
                        comp = ship.getReservedComponent(c - 5);

                    String top = " ".repeat(LARGHEZZA_CELLA);
                    if (comp != null) {
                        Connector conn = comp.getConnector(Side.FRONT);
                        if (conn != null && conn != Connector.NONE) {
                            top = formatTopBottom(conn);
                        }
                    }
                    if (c == 0)
                        System.out.print("   ");
                    System.out.printf("|%-20s", top);
                }
                System.out.println("|");


                for (int c = 0; c < 7; c++) {
                    Component comp = grid[r][c];
                    String content = " ".repeat(LARGHEZZA_CELLA);
                    if (comp != null) {
                        String center = comp.getAsciiArt()[1].trim();
                        Connector left = comp.getConnector(Side.LEFT);
                        Connector right = comp.getConnector(Side.RIGHT);
                        content = formatSide(left) + center + formatSide(right);
                        content = padCenter(content, LARGHEZZA_CELLA);
                    } else if (ship.getUsableSlot(r, c)) {
                        content = padCenter("....", LARGHEZZA_CELLA);
                    }
                    if (c == 0)
                        System.out.print(r + 5 + "  ");
                    System.out.printf("|%-20s", content);
                }
                System.out.println("|");

                for (int c = 0; c < 7; c++) {
                    Component comp = grid[r][c];
                    String bottom = " ".repeat(LARGHEZZA_CELLA);
                    if (comp != null) {
                        Connector conn = comp.getConnector(Side.BACK);
                        if (conn != null && conn != Connector.NONE) {
                            bottom = formatTopBottom(conn);
                        }
                    }
                    if (c == 0)
                        System.out.print("   ");
                    System.out.printf("|%-20s", bottom);
                }
                System.out.println("|");

            }

            System.out.print("   ");
            for (int c = 0; c < 7; c++) {
                System.out.print("+--------------------");
            }
            System.out.println("+");
        });
    }


    private String formatTopBottom(Connector c) {
        if (c == null || c == Connector.NONE) return " ".repeat(LARGHEZZA_CELLA);
        String code = shortCode(c);
        if (code.isBlank()) return " ".repeat(LARGHEZZA_CELLA);
        return padCenter("[" + code + "]", LARGHEZZA_CELLA);
    }


    private String formatSide(Connector c) {
        if (c == null || c == Connector.NONE) return "";
        String code = shortCode(c);
        if (code.isBlank()) return "";
        return "[" + code + "]";
    }

    /**
     * Returns a single-character short code for a given {@link Connector} type.
     *
     * @param c The {@link Connector} to get the short code for.
     * @return "S" for SINGLE, "D" for DOUBLE, "U" for UNIVERSAL, or a space for others/NONE.
     */
    private String shortCode(Connector c) {
        return switch (c) {
            case SINGLE -> "S";
            case DOUBLE -> "D";
            case UNIVERSAL -> "U";
            default -> " ";
        };
    }

    private String padCenter(String str, int width) {
        if (str.length() >= width) return str;
        int pad = width - str.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + str + " ".repeat(right);
    }

    /**
     * Displays the current positions of all players' rockets on the TUI.
     *
     * @param players The list of players in the game.
     * @param level The current level of the game, which determines the track length.
     */
    public void showRocketsPosition(List<Player> players, int level) {
        printer.run(() -> {
            Map<Color, String> colorEmoji = Map.of(
                    Color.RED, "R ",
                    Color.BLUE, "B ",
                    Color.GREEN, "G ",
                    Color.YELLOW, "Y "
            );

            int caselle = level == 1 ? 18 : 24;
            String[] board = new String[caselle];
            Arrays.fill(board, "     ");


            for (Player p : players) {
                int pos = p.getRocket().getPosition() >= 1 ? (p.getRocket().getPosition() - 1) % caselle : (caselle - 1 + ((p.getRocket().getPosition()) % (caselle)));
                String symbol = colorEmoji.getOrDefault(p.getRocket().getColor(), " ? ");
                board[pos] = " " + symbol + " ";
            }

            System.out.println("\nPosizione dei razzi:");
            System.out.print("|");
            for (String cell : board) {
                System.out.print(cell + "|");
            }
            System.out.println();

            System.out.print(" ");
            for (int i = 1; i <= caselle; i++) {
                String num = String.valueOf(i);
                int totalWidth = 6;
                int padding = totalWidth - num.length();
                int left = padding / 2;
                int right = padding - left;
                System.out.print(" ".repeat(left) + num + " ".repeat(right));
            }
            System.out.println();

            System.out.println("\nGiocatori:");
            for (Player p : players) {
                String symbol = colorEmoji.getOrDefault(p.getRocket().getColor(), " ? ");
                int lap = (p.getRocket().getPosition() >= 1 ? (((p.getRocket().getPosition() - 1) / caselle) + 1) : -((p.getRocket().getPosition() - 1) / caselle));
                System.out.printf("%s %s (posizione: %d) (giro: %d)\n", symbol, p.getNickname(), p.getRocket().getPosition(), lap);
            }
        });
    }

    /**
     * Displays a game card in a formatted ASCII art frame.
     *
     * @param card The {@link Card} to be displayed.
     */
    public void showCard(Card card) {
        printer.run(() -> {
            String[] description = card.getAsciiArt();
            int width = 50;

            System.out.println("╔" + "═".repeat(width) + "╗");

            for (String line : description) {
                System.out.printf("║ %-" + (width - 1) + "s║%n", line);
            }

            System.out.println("╚" + "═".repeat(width) + "╝");
        });
    }

    /**
     * Informs the user that they are in the crew placing phase and lists the available commands.
     *
     * @param ship The ship for which crew is being placed.
     * @param positions A map indicating possible positions for placing crew members.
     */
    public void startCrewPlacingPhase(Ship ship, Map<Integer, Integer> positions){
        printer.run(() -> {
            System.out.print("""
                    You are now in crew placing phase
                    Commands:
                    - 'alien <color> <x> <y>' (brown, purple)
                    - 'humans <x> <y>'
                    - 'end crew placing phase'
                    """);
        });
    }

    /**
     * Notifies the user that the assembling phase has ended and provides relevant commands or waiting messages.
     */
    public void endAssemblingPhase(){
        printer.run(() -> {
            System.out.println("You have ended the assembling phase");
            if (clientModel.getLevel() == 2) {
                System.out.print("""
                        Commands:
                        - 'start hourglass' (optional)
                        - 'start final hourglass' (optional)
                        """);
                System.out.print("- 'set position <position>' (" + 1);
                for (Player p : clientModel.getPlayers().subList(1, clientModel.getPlayers().size()))
                    System.out.print(", " + (clientModel.getPlayers().indexOf(p) + 1));
                System.out.println(")");
            } else {
                System.out.println("Wait for other players to end the assembling phase to move on");
            }
        });
    }

    /**
     * Informs the user that the assembling phase has started, displays their ship,
     * and lists all commands available during this phase.
     */
    public void startAssemblingPhase()  {
        printer.run(() -> {
            System.out.println("Target player number reached, starting game...\n");
            System.out.println("Your ship:");
        });
        drawShip(clientModel.getMyShip());
        printer.run(() -> {
            System.out.print("""
                    You are now in ship assembling phase
                    Commands:
                    - 'draw component'
                    - 'rotate'
                    - 'attach component <row> <column>'
                    - 'remove component <row> <column>'
                    - 'show ship'
                    - 'show face up components'
                    - 'draw face up component <id>'
                    - 'show <nickname> ship'
                    - 'end assembling phase'
                    """);
            if (clientModel.getLevel() == 2)
                System.out.print("""
                        - 'draw reserved component <index>' (1 or 2)
                        - 'show sub deck <index>' (1 - 3)
                        - 'start hourglass'
                        """);
        });
    }

    /**
     * Displays the final ranking of players at the end of the game, including their scores
     * and announcing the winner.
     *
     * @param ranking A map where keys are {@link Player}s and values are their final scores.
     */
    public void showFinalRanking(Map<Player, Integer> ranking){
        printer.run(() -> {
            System.out.println("\n\nThe game is over");
            System.out.println("Final ranking:");
            Player winner = null;
            for (Player p : ranking.keySet()) {
                System.out.println(p.getNickname() + ": " + ranking.get(p) + " credits");
                if (winner == null || ranking.get(p).equals(ranking.values().stream().max(Integer::compare).orElse(null)))
                    winner = p;
            }

            System.out.println("The winner is " + winner.getNickname());
        });
    }

    /**
     * Handles the start of the flight phase. For level 1, it updates rocket positions and displays them.
     * It also redraws the player's ship.
     *
     * @param ship The current player's ship.
     * @param positions Updated rocket's positions on the game board, received by the server.
     */
    @Override
    public void startFlightPhase(Ship ship, Map<Integer, Integer> positions)  {
        if(clientModel.getLevel() == 1){
            for(Player p : clientModel.getPlayers()){
                p.getRocket().setPosition(positions.get(p.getId()));
            }
            showRocketsPosition(clientModel.getPlayers(), clientModel.getLevel());
        }
        drawShip(clientModel.getMyShip());
    }

    /**
     * Notifies the user about the reason for their defeat.
     *
     * @param loseType An integer representing the type of defeat:
     * 1 - lost due to engine power.
     * 2 - lost due to no crew members left.
     * 3 - lost because someone lapped them.
     * 4 - voluntarily abandoned the race.
     */
    public void notifyDefeat(int loseType){
        printer.run(() -> {
            switch (loseType) {
                case 1 -> System.out.println("You lost because your engine power is 0");
                case 2 -> System.out.println("You lost because you don't have any crew members left");
                case 3 -> System.out.println("You lost because someone lapped you");
                case 4 -> System.out.println("You have abandoned the race");
            }
            System.out.println("You will now spectate the game until it's over");
        });
    }

    /**
     * Provides custom messages for the current player's turn.
     *
     * @param turnType An integer indicating the specific phase or context of the turn
     * for a given card effect.
     */
    public void customCardPrintMyTurn(int turnType){
        printer.run(() -> {
            if (clientModel.getCurrentCard().getType() == EffectType.COMBATZONE) {
                switch (turnType) {
                    case 1:
                        System.out.println("First phase effect applied, you can now set second phase context (engines), write 'apply' when you are done setting to apply the effect");
                        effectCount += 2;
                        break;
                    case 2:
                        System.out.println("You can now set first phase context (cannons), write 'apply' when you are done setting to apply the effect");
                        effectCount++;
                        break;
                    case 0:
                        switch (effectCount) {
                            case 1:
                                System.out.println("First phase effect applied, you can now set second phase context (engines), write 'apply' when you are done setting to apply the effect");
                                effectCount += 2;
                                break;
                            case 2:
                                System.out.println("Second phase effect applied, you can now set third phase context (cannons), write 'apply' when you are done setting to apply the effect");
                                break;
                            case 3:
                                System.out.println("Second phase effect applied, you will now see third phase result");
                                break;
                        }
                }
                return;
            } else if (clientModel.getCurrentCard().getType() == EffectType.METEORSWARM) {
                System.out.println("You can roll the dice with 'roll dice'");
                return;
            }
            System.out.println("It's your turn");
        });
    }

    /**
     * Provides custom messages when it's not the current player's turn,
     * specifically for cards where another player needs to perform an action.
     *
     * @param currentPlayer The nickname of the player whose turn it currently is.
     */
    public void customCardPrintNotMyTurn(String currentPlayer){
        printer.run(() -> {
            if (clientModel.getCurrentCard().getType() == EffectType.METEORSWARM) {
                System.out.println("Wait for " + currentPlayer + " to roll the dice");
                return;
            }
            System.out.println("It's " + currentPlayer + " turn, wait for them to complete their turn");
        });
    }

    /**
     * Reports an exception to the console, printing an error message.
     *
     * @param e The {@link Exception} to be reported.
     */
    public void reportException(Exception e) {
            printer.run(() -> {
                synchronized (this) {
                    System.err.println(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
                    Thread.sleep(50);
                    printPromptSymbol();
                    parser.setNotified();
                    notifyAll();
                }
            });

    }

    /**
     * Handles and displays the response received after a connection attempt, informing the user
     * about the game's current state.
     *
     * @param response The {@link ConnectionResponse} to be handled.
     */
    public void handleConnectionResponse(ConnectionResponse response){
        printer.run(() -> {
            switch (response) {
                case FIRST_PLAYER ->{
                    System.out.println("You are the first player, you can start the game via 'start game <players> <level>' (players: 2 - 4, level: 1 - 2)");
                    printPromptSymbol();
                }
                case IN_PROGRESS -> System.out.println("Game is already in progress");
                case WAIT_CREATION -> System.out.println("Game is being created by another player");
                case SET_NICKNAME -> {
                    System.out.println("Game has already started, you can join by setting your nickname via 'set nickname <nickname>'");
                    printPromptSymbol();
                }
            }
        });
    }

    /**
     * Displays the list of players currently in the lobby or game.
     *
     * @param players A {@link List} of player nicknames.
     */
    public void showPlayersList(List<String> players){
        printer.run(() -> {
            System.out.print("Players in the lobby: " + players.getFirst());
            for (int i = 1; i < players.size(); i++) {
                System.out.print(", " + players.get(i));
            }
            System.out.println();
        });
    }

    /**
     * Notifies the user that a new player has joined the game.
     *
     * @param nickname The nickname of the player who joined.
     */
    public void newPlayerJoined(String nickname){
        printer.run(() -> {
            System.out.println(nickname + " joined the game");
        });
    }

    /**
     * Displays the result of a dice roll.
     *
     * @param diceResult The integer result of the dice roll.
     */
    public void showDiceResult(int diceResult){
        printer.run(() -> {
            System.out.println("Dice result: " + diceResult);
        });
    }

    /**
     * Displays the result of a game round, informing the player if they won or lost the phase
     * and what action to take next.
     *
     * @param roundWon A boolean indicating if the round was won ({@code true}) or lost ({@code false}) by the player.
     */
    public void roundResult(boolean roundWon){
        printer.run(() -> {
            if (!roundWon) {
                System.out.println("You lost this phase, you can now apply the relative effect as explained in the card");
            } else {
                System.out.println("You didn't lose this phase");
                System.out.println("Wait for the player that lost to finish applying the relative effect");
            }
        });
    }

    /**
     * Notifies the player about the new leader for the next round. If the current player is the leader,
     * they are prompted to draw a card. Otherwise, they wait for the leader.
     *
     * @param isNewLeader A boolean indicating if the current player is the new leader.
     */
    public void newLeader(boolean isNewLeader) {
        printer.run(() -> {
            System.out.println("New card has to be drawn");
            System.out.println(isNewLeader
                    ? "You are the leader, you can draw a card"
                    : "You are not the leader, wait for the leader to draw a card");
        });
    }

    /**
     * Displays a general informational message to the user.
     *
     * @param message The informational message to display.
     */
    @Override
    public void onInfo(String message) {
        printer.run(() -> System.out.println(message));
    }

    /**
     * Displays an error message to the user, prints it to System.err.
     *
     * @param message The error message to display.
     */
    @Override
    public void onError(String message) {
        printer.run(() -> {
            synchronized (this) {
                System.err.println(message);
                parser.setNotified();
                notifyAll();
            }
        });
    }

    /**
     * Prompts the user to set their nickname after successfully joining a game.
     */
    @Override
    public void setNickname() {
        printer.run(() -> {
            System.out.println("You have joined the game");
            System.out.println("You can now set your nickname via 'set nickname <nickname>'");
        });
    }

    /**
     * This method is a placeholder for GUI-specific timer updates and does nothing in the TUI.
     *
     * @param count The current hourglass number.
     * @param secondsLeft The total seconds left.
     */
    @Override
    public void showSecondsLeftForGui(int count, int secondsLeft) {}

    /**
     * Displays the remaining seconds for an hourglass timer.
     *
     * @param count The current hourglass number.
     * @param secondsLeft The total seconds left for the hourglass.
     */
    @Override
    public void showSecondsLeft(int count, int secondsLeft) {
        synchronized (this) {
            printer.run(() -> {
                System.out.println("The " + count + " hourglass seconds left: " + secondsLeft);
            });
            this.notifyAll();
        }
    }

    /**
     * Displays a sub-deck of cards by showing each card's ASCII art representation.
     *
     * @param subDeck A {@link List} of {@link Card} objects representing the sub-deck.
     */
    @Override
    public void showSubDeck(List<Card> subDeck) {
        for (Card c : subDeck){
            showCard(c);
        }
        printPromptSymbol();
    }

    /**
     * Handles and processes various update messages received from the server,
     * displaying appropriate messages to the user.
     *
     * @param msg The {@link UpdateMessage} to be handled.
     */
    @Override
    public void handleUpdateMessage(UpdateMessage msg) {
        printer.run(() -> {
            switch (msg) {
                case GAME_FULL:
                    System.out.println("Lobby is already full");
                    break;
                case QUIT_GAME:
                    System.out.println("You are leaving the game");
                    ClientModel.closeClient();
                    break;
                case NOT_ENOUGH_CREWMEMBERS:
                    System.out.println("You don't have enough crew members to use this card, turn is passed to the next player");
                    clientModel.setClientState(ClientState.LOCKED);
                    break;
                case HOURGLASS_NOT_AVAILABLE:
                    System.out.println("Finished hourglasses or it is running");
                    break;
                case FINAL_HOURGLASS_NOT_AVAILABLE:
                    System.out.println("Final hourglass is not available or it is running");
                    break;
                case PLAYER_DISCONNECTED:
                    System.out.println("\nOne player disconnected");
                    System.out.println("Game is over");
                    ClientModel.closeClient();
                    break;
                case CREATE_OR_JOIN:
                    System.out.println("Create or join a game?");
                    System.out.println("Commands:");
                    System.out.println("- 'create'");
                    System.out.println("- 'view list'");
                    printPromptSymbol();
                    break;
                default:
            }
        });
    }

    /**
     * This method is a placeholder for effect resolution in GUI and does nothing in the TUI.
     */
    @Override
    public void effectResolution()  {}

    /**
     * Displays a list of available games that the user can join, along with the command to join one.
     *
     * @param gameList A list of game IDs to be displayed.
     */
    @Override
    public void showGameList(List<Integer> gameList) {
        printer.run(() -> {
            System.out.println("Game list: " + gameList);
            System.out.println("To join a game, write 'join <index>'");
            printPromptSymbol();
        });
    }
}