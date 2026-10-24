package it.polimi.ingsw.is25am31.view.tui.input;

import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import it.polimi.ingsw.is25am31.view.UiRenderer;

import java.util.UUID;

/**
 * Parses user input commands and dispatches them to the appropriate handler.
 * It validates commands based on the current {@link ClientState} to ensure
 * that only allowed actions are processed.
 */
public class CommandParser {
    private final CommandHandler commandHandler;
    private final ClientModel clientModel;
    private final UiRenderer renderer;


    public CommandParser(VirtualServer server, ClientModel model, UiRenderer renderer){
        this.clientModel = model;
        this.renderer = renderer;
        this.commandHandler = new CommandHandler(server, model, renderer);
    }

    /**
     * Sets the client identifier for the underlying {@link CommandHandler}.
     * @param clientId The {@link UUID} of the client.
     */
    public void setClientId(UUID clientId) {
        commandHandler.setClientId(clientId);
    }


    public void setNotified(){
        commandHandler.setNotified();
    }

    /**
     * Handles an input string from the user. It first validates the command,
     * then checks if it's allowed in the current client state, and finally
     * parses and dispatches it to the {@link CommandHandler}.
     * @param input The string input from the user.
     * @throws Exception if an error occurs during command parsing or handling.
     */
    public void handleInput(String input) throws Exception {
        //System.out.println(clientModel.getClientState());
        if(!isValidCommand(input)) {
            renderer.onError("Unknown command");
            Thread.sleep(50);
            renderer.printPromptSymbol();
            return;
        }
        if (!isAllowed(input)) {
            renderer.onError("You can't use this command now");
            renderer.printPromptSymbol();
            return;
        }
        parse(input);
        //System.out.println(clientModel.getClientState());
    }

    /**
     * Parses the command and dispatches it to the appropriate method in the {@link CommandHandler}.
     * This method assumes the command has already been validated and checked for allowance.
     * @param input The input string from the user.
     * @throws Exception if an error occurs during command handling.
     */
    private void parse(String input) throws Exception {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length == 0) return;

        String command = tokens[0];

        switch (command) {
            case "create" -> commandHandler.handleCreate();
            case "join" -> commandHandler.handleJoin(tokens);
            case "view" -> commandHandler.handleViewGameList();
            case "set" -> {
                if(tokens[1].equals("nickname")){
                    commandHandler.handleSetNickname(tokens);
                } else if(tokens[1].equals("position")) {
                    commandHandler.handleSetPosition(tokens);
                }else {
                    commandHandler.handleSet(tokens);
                }
            }
            case "accept", "decline" -> commandHandler.handleSet(tokens);
            case "draw" -> {
                if (input.startsWith("draw face up component")) {
                    commandHandler.handleDrawFaceUpComponent(tokens);
                } else if (input.startsWith("draw card")) {
                    commandHandler.handleDrawCard(tokens);
                } else {
                    commandHandler.handleDrawComponent(tokens);
                }
            }
            case "attach" -> commandHandler.handleAttach(tokens);
            case "remove" -> commandHandler.handleRemoveComponent(tokens);
            case "show" -> commandHandler.handleShow(tokens);
            case "rotate" -> commandHandler.handleRotate(tokens);
            case "end" ->{
                if(tokens[1].equals("assembling")){
                    commandHandler.handleEndAssembling(tokens);
                } else if(tokens[1].equals("crew")){
                    commandHandler.handleEndCrewPlacing();
                }
            }

            case "roll" -> commandHandler.handleRoll();
            case "abandon" -> commandHandler.handleAbandon();
            case "apply" -> commandHandler.handleApply();
            case "quit" -> commandHandler.handleQuit();
            case "start" -> {
                if(tokens[1].equals("hourglass")){
                    commandHandler.handleStart();
                } else if(tokens[1].equals("game")){
                    commandHandler.handleStartGame(tokens);
                }
                else{
                    commandHandler.handleStartFinal();
                }
            }
            case "humans", "alien" -> commandHandler.handleAddCrewMember(tokens);
            default -> renderer.onError("Unknown command");
        }
    }


    /**
     * Checks if a given input string represents a syntactically valid command.
     * This performs a basic check on the command's format.
     * @param command The input string.
     * @return {@code true} if the command format is valid, {@code false} otherwise.
     */
    private boolean isValidCommand(String command) {
        return (command != null && ((command.startsWith("set") &&
                clientModel.getClientState() == ClientState.EFFECT_RESOLUTION) ||
                command.equals("create") ||
                command.equals("view list") ||
                command.startsWith("join") ||
                command.startsWith("set nickname") ||
                command.startsWith("start game") ||
                command.equals("quit") ||
                command.equals("apply") ||
                command.equals("draw card") ||
                command.startsWith("draw face up component") ||
                command.startsWith("draw reserved component") ||
                command.equals("draw component") ||
                command.startsWith("attach component") ||
                command.startsWith("remove component") ||
                command.equals("show ship")||
                command.equals("rotate") ||
                command.equals("show face up components")||
                command.equals("end assembling phase")||
                (command.startsWith("show") && command.endsWith("ship")) ||
                command.startsWith("show sub deck") ||
                command.equals("start hourglass") ||
                command.equals("start final hourglass") ||
                command.equals("accept") ||
                command.equals("decline") ||
                command.startsWith("alien") ||
                command.startsWith("humans") ||
                command.startsWith("set position") ||
                command.equals("end crew placing phase") ||
                command.equals("roll dice") ||
                command.equals("abandon race")));
    }


    /**
     * Checks if a command is allowed to be executed in the current {@link ClientState}.
     * This prevents users from issuing commands that are not relevant to the current phase of the game.
     * @param input The input string.
     * @return {@code true} if the command is allowed in the current state, {@code false} otherwise.
     */
    private boolean isAllowed(String input) {
        if(input.equals("quit")) return true;
        if(clientModel.getLevel() == 0 && input.startsWith("start game")) return true;
        if(clientModel.getLevel() == 2) {
            return switch (clientModel.getClientState()) {
                case LOCKED, LOBBY, ENDGAME -> false;
                case GAME_SELECTION -> input.equals("create") || input.equals("view list")|| input.startsWith("join");
                case NICKNAME_SETTING -> input.startsWith("set nickname");
                case INITIAL -> input.startsWith("start game");
                case ASSEMBLE -> input.startsWith("draw") || input.startsWith("attach") ||
                        input.startsWith("end assembling phase") || input.startsWith("show") || input.equals("rotate") ||
                        input.startsWith("show sub deck") || input.startsWith("start hourglass") || input.startsWith("remove component");
                case ENDED_ASSEMBLE -> input.equals("start hourglass") || input.equals("start final hourglass") || input.startsWith("show") && input.endsWith("ship") || input.startsWith("set position");
                case FLIGHT -> input.startsWith("draw card") || (input.startsWith("show") && input.endsWith("ship") ||
                        input.equals("abandon race"));
                case SPECTATOR ->  (input.startsWith("show") && input.endsWith("ship"));
                case EFFECT_RESOLUTION -> input.startsWith("set") ||
                        input.equals("apply") || input.equals("roll dice") || input.equals("accept") ||
                        input.equals("decline")  || (input.startsWith("show") && input.endsWith("ship"));
                case CREW_PLACING -> input.startsWith("humans") || input.startsWith("alien")  || (input.startsWith("show") && input.endsWith("ship")) ||
                        input.equals("end crew placing phase");
            };
        } else {
            return switch (clientModel.getClientState()) {
                case LOCKED, LOBBY, ENDGAME, CREW_PLACING -> false;
                case GAME_SELECTION -> input.equals("create") || input.equals("view list")|| input.startsWith("join");
                case NICKNAME_SETTING -> input.startsWith("set nickname");
                case INITIAL -> input.startsWith("start game");
                case ASSEMBLE -> input.startsWith("draw") || input.startsWith("attach") ||
                        input.startsWith("end assembling phase") || input.startsWith("show") ||
                        input.equals("rotate");
                case ENDED_ASSEMBLE, SPECTATOR ->  (input.startsWith("show") && input.endsWith("ship"));
                case FLIGHT -> input.startsWith("draw card") ||  (input.startsWith("show") && input.endsWith("ship") ||
                        input.equals("abandon race"));
                case EFFECT_RESOLUTION -> input.startsWith("set") ||
                        input.equals("apply") || input.equals("roll dice")  || input.equals("accept") ||
                        input.equals("decline")  || (input.startsWith("show") && input.endsWith("ship"));
            };
        }
    }
}

