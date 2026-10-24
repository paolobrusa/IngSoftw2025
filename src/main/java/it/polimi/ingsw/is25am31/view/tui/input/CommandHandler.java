package it.polimi.ingsw.is25am31.view.tui.input;

import it.polimi.ingsw.is25am31.model.cards.EffectType;
import it.polimi.ingsw.is25am31.model.components.AlienColor;
import it.polimi.ingsw.is25am31.model.components.Component;
import it.polimi.ingsw.is25am31.model.components.Side;
import it.polimi.ingsw.is25am31.network.VirtualServer;
import it.polimi.ingsw.is25am31.view.ClientModel;
import it.polimi.ingsw.is25am31.view.ClientState;
import it.polimi.ingsw.is25am31.view.UiRenderer;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the execution of commands received from the {@link CommandParser}.
 * This class translates user input commands into actions performed on the
 * {@link ClientModel} and sends requests to the {@link VirtualServer}.
 */
public class CommandHandler {
    private final VirtualServer server;
    private UUID clientId;
    private final ClientModel clientModel;
    private final UiRenderer renderer;
    private boolean notified = false;

    public CommandHandler(VirtualServer server, ClientModel model, UiRenderer renderer) {
        this.server = server;
        this.clientModel = model;
        this.renderer = renderer;
    }

    /**
     * Sets the `notified` field to `true` and starts a new thread that resets it
     * to `false` after a delay of 100 milliseconds.<br>
     * This method is used to notify the handler if an exception has been reported;
     * if this is not the case, a message of confirmation of correct input
     * can be shown to the user.
     */
    public void setNotified(){
        notified = true;
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            notified = false;
        }).start();
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     * Handles the "start game" command.
     */
    public void handleStartGame(String[] tokens) throws InterruptedException {
        if(tokens.length != 4) {
            renderer.onError("Usage: start game <players> <level> ");
            return;
        }
        try {
            server.setMaxPlayers(clientId, Integer.parseInt(tokens[2]));
            server.setGameLevel(clientId, Integer.parseInt(tokens[3]));
        }catch (NumberFormatException e) {
            renderer.onError("Players and level must be numbers");
        } catch (Exception e) {
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "set nickname" command.
     * @param tokens An array of strings representing the command and its arguments.
     */
    public void handleSetNickname(String[] tokens) throws InterruptedException {
        if(tokens.length != 3 || !tokens[1].equals("nickname")) {
            renderer.onError("Usage: set nickname <nickname>");
            renderer.printPromptSymbol();
            return;
        }
        if(clientModel.getClientState() != ClientState.NICKNAME_SETTING) {
            renderer.onError("You can't set nickname now");
            renderer.printPromptSymbol();
            return;
        }
        try {
            clientModel.setNickname(tokens[2]);
            server.setNickname(tokens[2], clientId);
        } catch (Exception e){
            renderer.reportException(e);
        }
    }


    /**
     * Handles the "set" command (used to set context for card effects).
     * @param tokens An array of strings representing the command and its arguments.
     */
    protected void handleSet(String[] tokens) throws InterruptedException {
        if(clientModel.getClientState() != ClientState.EFFECT_RESOLUTION) {
            return;
        }
        Map<String, Object> contextMap = new HashMap<>();
        EffectType required = clientModel.getCurrentCard().getType();

        try {
            switch (required) {
                case SMUGGLERS:
                    if (tokens.length == 5) {
                        setDoubleCoordinates(tokens, contextMap);
                    } else if (tokens.length == 4) {
                        contextMap.put("Good", tokens[1]);
                        contextMap.put("x", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[3]) - 4);
                        server.setEffectContext(clientId, contextMap);
                    } else if (tokens.length == 1) {
                        server.setEffectContext(clientId, null);
                    }
                    return;
                case SLAVERS:
                    if (tokens.length == 5) {
                        setDoubleCoordinates(tokens, contextMap);
                    } else if (tokens.length == 3) {
                        contextMap.put("x", Integer.parseInt(tokens[1]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[2]) - 4);
                        server.setEffectContext(clientId, contextMap);
                    } else if (tokens.length == 1) {
                        server.setEffectContext(clientId, null);
                    }
                    return;
                case COMBATZONE:
                    if(tokens.length == 6 && tokens[1].equals("shield")) {
                        contextMap.put("shield", true);
                        contextMap.put("x1", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y1", Integer.parseInt(tokens[3]) - 4);
                        contextMap.put("x2", Integer.parseInt(tokens[4]) - 5);
                        contextMap.put("y2", Integer.parseInt(tokens[5]) - 4);
                        server.setEffectContext(clientId, contextMap);
                    }else if (tokens.length == 5) {
                        setDoubleCoordinates(tokens, contextMap);
                    } else if (tokens.length == 3) {
                        contextMap.put("x", Integer.parseInt(tokens[1]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[2]) - 4);
                        server.setEffectContext(clientId, contextMap);
                    }  else if (tokens.length == 4) {
                        contextMap.put("Good", tokens[1]);
                        contextMap.put("x", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[3]) - 4);
                        server.setEffectContext(clientId, contextMap);
                    }
                    return;


                case PLANETS:
                    if( tokens.length == 1) {
                        contextMap.put("decline", true);
                        server.setCardContext(clientId, contextMap);
                    }
                    else if (tokens.length == 2) {
                        contextMap.put("planetIndex", Integer.parseInt(tokens[1]) - 1);
                        server.setCardContext(clientId, contextMap);
                    } else if (tokens.length == 4) {
                        contextMap.put("Good", tokens[1]);
                        contextMap.put("x", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[3]) - 4);
                        server.setEffectContext(clientId, contextMap);
                        return;
                    }
                    break;

                case PIRATES:
                    if(tokens.length == 6 && tokens[1].equals("shield")) {
                        contextMap.put("shield", true);
                        contextMap.put("x1", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y1", Integer.parseInt(tokens[3]) - 4);
                        contextMap.put("x2", Integer.parseInt(tokens[4]) - 5);
                        contextMap.put("y2", Integer.parseInt(tokens[5]) - 4);
                        server.setEffectContext(clientId, contextMap);
                        return;
                    }
                case OPENSPACE, METEORSWARM:
                    if (tokens.length == 5) {
                        setDoubleCoordinates(tokens, contextMap);
                    }
                    break;

                case ABANDONED:
                    if (tokens.length == 1) {
                        contextMap.put("accept", tokens[0].equals("accept"));
                        server.setCardContext(clientId, contextMap);
                        if (tokens[0].equals("accept")) {
                            renderer.onInfo("You have accepted the card");
                        }
                    }
                    if (tokens.length == 3) {
                        contextMap.put("x", Integer.parseInt(tokens[1]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[2]) - 4);
                        server.setEffectContext(clientId, contextMap);
                        return;
                    } else if (tokens.length == 4) {
                        contextMap.put("x", Integer.parseInt(tokens[2]) - 5);
                        contextMap.put("y", Integer.parseInt(tokens[3]) - 4);
                        contextMap.put("Good", tokens[1]);
                        server.setEffectContext(clientId, contextMap);
                        return;
                    }
                    break;

                case SPECIAL:
                    if (tokens.length == 1) {
                        server.setEffectContext(clientId, null);
                        return;
                    }
                    break;
            }
            contextMap.clear();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            renderer.onError("Invalid coordinates");
        } catch (Exception e) {
            renderer.reportException(e);
        }
    }

    private void setDoubleCoordinates(String[] tokens, Map<String, Object> contextMap) throws Exception {
        contextMap.put("x1", Integer.parseInt(tokens[1]) - 5);
        contextMap.put("y1", Integer.parseInt(tokens[2]) - 4);
        contextMap.put("x2", Integer.parseInt(tokens[3]) - 5);
        contextMap.put("y2", Integer.parseInt(tokens[4]) - 4);
        try {
            server.setCardContext(clientId, contextMap);
        } catch (NullPointerException e) {
            renderer.onError("One of the selected spots contains no components");
        } catch (Exception e) {
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "apply" command.
     */
    protected void handleApply(){
        try {
            server.setEffectContext(clientId, null);
        } catch (Exception e) {
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "quit" command.
     */
    protected void handleQuit() throws Exception {
        if(clientModel.getClientState() == ClientState.GAME_SELECTION) {
            renderer.onInfo("You are leaving the application, goodbye!");
            ClientModel.closeClient();
            return;
        }
        clientModel.setClientState(ClientState.INITIAL);
        server.leaveGame(clientId);
    }

    /**
     * Handles the "start hourglass" command.
     */
    protected void handleStart() throws Exception {
        server.startHourGlass(clientId);
    }

    /**
     * Handles the "start final hourglass" command.
     */
    protected void handleStartFinal() throws Exception {
        server.startFinalHourGlass(clientId);
    }

    /**
     * Handles the "remove component" command.
     */
    protected void handleRemoveComponent(String[] tokens) throws Exception {
        if (tokens.length != 4) {
            renderer.onError("Usage: remove component <row> <column>");
            return;
        }
        int x = 0, y = 0;
        try {
            x = Integer.parseInt(tokens[2]) - 5;
            y = Integer.parseInt(tokens[3]) - 4;
        } catch (NumberFormatException e) {
            renderer.onError("Coordinates must be numbers");
            return;
        }
        try {
            if(clientModel.getCurrentComponent() == null) {
                server.removeComponent(clientId, x, y);
                clientModel.setCurrentComponent(clientModel.getMyShip().getComponent(x, y));
                clientModel.getMyShip().removeComponent(x, y);
            }
            else{
                renderer.onError("You can not remove component, you have one component drawn");
            }
        } catch (Exception e) {
            renderer.onError("You cannot remove component if it isn't the last component that you placed");
        }
    }

    /**
     * Handles the "draw component" command.
     */
    public void handleDrawComponent(String[] tokens) throws Exception {
        if (tokens.length == 2 && tokens[1].equals("component")) {
            try {
                Component component = clientModel.getCurrentComponent();
                if(component != null)
                    server.putDownComponent(component, clientId);
                server.drawFaceDownComponent(clientId);
            }catch (Exception e) {
                renderer.reportException(e);
            }
        } else if (tokens.length == 4 && tokens[1].equals("face") && tokens[2].equals("down") && tokens[3].equals("component")) {
            server.drawFaceDownComponent(clientId);
        } else if (tokens.length == 4 && tokens[1].equals("face") && tokens[2].equals("up") && tokens[3].equals("component")) {
            renderer.onError("Missing index for face up component");
        } else if (tokens.length == 4 && tokens[1].equals("reserved") && tokens[2].equals("component") && clientModel.getLevel() == 2) {
            try{
                int index = Integer.parseInt(tokens[3]) - 1;
                //clientModel.getMyShip().removeReservedComponent(index);
                server.drawFromReserved(index, clientId);
            }catch(Exception e){
                renderer.onError("Invalid index");
            }
        } else {
            renderer.onError("Usage: draw component | draw face up component <index>");
        }
    }

    /**
     * Handles the "draw face up component" command.
     */
    public void handleDrawFaceUpComponent(String[] tokens) throws Exception {
        if (tokens.length == 5) {
            try {
                int id = Integer.parseInt(tokens[4]);
                Component component = clientModel.getCurrentComponent();
                if(component != null)
                    server.putDownComponent(component, clientId);
                server.drawFaceUpComponent(id, clientId);
                for(Component c : clientModel.getFaceUpComponents()){
                    if(c.getId() == id){
                        clientModel.setCurrentComponent(c);
                        clientModel.getFaceUpComponents().remove(c);
                        break;
                    }
                }
            }catch (NumberFormatException | NullPointerException e){
                renderer.onError("Invalid id for face up component");
            } catch (RuntimeException e){
                renderer.onError("Selected component can't be drawn");
            }
        } else {
            renderer.onError("Usage: draw face up component <index>");
        }
    }

    /**
     * Handles the "attach component" command.
     */
    protected void handleAttach(String[] tokens) throws InterruptedException {
        if (tokens.length != 4 || !tokens[1].equals("component")) {
            renderer.onError("Usage: attach component <row> <column>");
            return;
        }
        if(clientModel.getCurrentComponent() == null){
            renderer.onError("You have to draw a component first");
            return;
        }

        try {
            int x = Integer.parseInt(tokens[2]);
            int y = Integer.parseInt(tokens[3]);

            server.attachComponent(clientModel.getCurrentComponent(), x  - 5, y - 4, clientId);
            clientModel.addComponentToMyShip(x  - 5, y - 4);
            clientModel.setCurrentComponent(null);
            synchronized (renderer) {
                renderer.wait(50);
                renderer.drawShip(clientModel.getMyShip());
                renderer.printPromptSymbol();
            }
        } catch (NumberFormatException e) {
            renderer.onError("Coordinates must be numbers");
        } catch (Exception e) {
            renderer.onError("Invalid position");
        }
    }

    /**
     * Handles the "apply" command.
     */
    protected void handleRoll(){
        try {
            server.rollDice(clientId);
        }catch(Exception e){
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "show (ship, subdeck, face up components)" command.
     */
    public void handleShow(String[] tokens) throws Exception {
        if (tokens.length == 2 && tokens[1].equals("ship")) {
            renderer.drawShip(clientModel.getMyShip());
        } else if (tokens.length == 3 && tokens[2].equals("ship")) {
            if(tokens[1].equals(clientModel.getNickname())) {
                renderer.drawShip(clientModel.getMyShip());
                renderer.printPromptSymbol();
                return;
            }
            try{
                server.showShip(clientId, tokens[1]);
            }catch(Exception e) {
                renderer.onError("Player doesn't exist");
            }
        } else if (tokens.length == 4 && tokens[1].equals("face") && tokens[2].equals("up") && tokens[3].equals("components")) {
            server.getFaceUpComponentList(clientId);
        } else if (tokens.length == 4 && tokens[1].equals("sub") && tokens[2].equals("deck")){
            try{
                server.getSubDeck(clientId, Integer.parseInt(tokens[3]) - 1);
            }catch(NumberFormatException e){
                renderer.onError("Index must be a number");
            } catch (Exception e){
                renderer.reportException(e);
            }
        } else {
            renderer.onError("Usage: show ship | show <nickname> ship | show face up components | show sub deck <index>");
        }
    }

    /**
     * Handles the "end assembling phase" command.
     */
    public void handleEndAssembling(String[] tokens) throws Exception {
        if (tokens.length != 3 || !tokens[1].equals("assembling") || !tokens[2].equals("phase")) {
            renderer.onError("Usage: end assembling phase");
            return;
        }

        renderer.endAssemblingPhase();
        if(clientModel.getLevel() == 2) {
            renderer.printPromptSymbol();
            clientModel.setClientState(ClientState.ENDED_ASSEMBLE);
        } else {
            clientModel.setClientState(ClientState.LOCKED);
        }
        if(clientModel.getCurrentComponent() != null)
            server.putDownComponent(clientModel.getCurrentComponent(), clientId);
        server.endAssemblePhase(clientId);
    }

    /**
     * Handles the "end crew placing phase" command.
     */
    public void handleEndCrewPlacing() throws Exception {
        clientModel.setClientState(ClientState.LOCKED);
        renderer.onInfo("You have ended the crew placing phase\nWait for other players to finish");
        server.endCrewPlacingPhase(clientId);
    }

    /**
     * Handles the "draw card" command.
     */
    public void handleDrawCard(String[] tokens) throws Exception {
        if(clientModel.getClientState() != ClientState.FLIGHT){
            renderer.onError("You can't use this command now");
            renderer.printPromptSymbol();
            return;
        }
        if (tokens.length == 2 && tokens[1].equals("card")) {
            server.drawCard(clientId);
        } else {
            renderer.onError("Usage: draw card");
            renderer.printPromptSymbol();
        }
    }

    /**
     * Handles the "set position" command.
     */
    public void handleSetPosition(String[] tokens) throws InterruptedException {
        if (tokens.length != 3 || !tokens[1].equals("position") ) {
            renderer.onError("Usage: set position <position>");
            renderer.printPromptSymbol();
            return;
        }
        try{
            int position = Integer.parseInt(tokens[2]);
            if(position < 1 || position > clientModel.getPlayers().size()) {
                renderer.onError("Invalid position");
                renderer.printPromptSymbol();
                return;
            }
            server.placeRocket(clientId, position);
        } catch (NumberFormatException e){
            renderer.onError("Position must be a number");
            return;
        } catch (Exception e) {
            renderer.reportException(e);
            return;
        }
        synchronized (renderer) {
            renderer.wait(50);
            if(notified){
                notified = false;
                return;
            }
            if(clientModel.getClientState() != ClientState.CREW_PLACING)
                renderer.onInfo("Position set correctly, wait for other players to set their position");
        }
    }

    /**
     * Handles the "humans"/"alien" command.
     */
    public void handleAddCrewMember(String[] tokens) throws InterruptedException {
        if ((tokens.length != 4 && tokens.length != 3) || (!tokens[0].equals("alien") && !tokens[0].equals("humans")) ) {
            renderer.onError("Usage: alien <color> <x> <y> | humans <x> <y>");
            return;
        }
        try {
            switch (tokens[0]) {
                case "alien":
                    if (!tokens[1].equals("brown") && !tokens[1].equals("purple")) {
                        renderer.onError("Invalid color");
                        renderer.printPromptSymbol();
                        return;
                    }
                    server.placeCrewMember(clientId, Integer.parseInt(tokens[2]) - 5, Integer.parseInt(tokens[3]) - 4, tokens[1].equals("brown") ? AlienColor.BROWN : AlienColor.PURPLE);
                    break;
                case "humans":
                    server.placeCrewMember(clientId, Integer.parseInt(tokens[1]) - 5, Integer.parseInt(tokens[2]) - 4, AlienColor.NONE);
                    break;
                default:
                    renderer.onError("Usage: alien <color> <x> <y> | humans <x> <y>");
            }
        }catch(NumberFormatException e){
            renderer.onError("Invalid coordinates");
            return;
        } catch (Exception e) {
            renderer.reportException(e);
            return;
        }
        synchronized (renderer) {
            renderer.wait(50);
            if(notified){
                notified = false;
                return;
            }
            if (tokens[0].equals("humans")) {
                System.out.println("Humans placed correctly");
            } else if (tokens[0].equals("alien")) {
                System.out.println("Alien placed correctly");
            }
            renderer.printPromptSymbol();
        }
    }

    /**
     * Handles the "rotate" command.
     */
    public void handleRotate(String[] tokens) throws InterruptedException {
        if(tokens.length != 1 || !tokens[0].equals("rotate")) {
           renderer.onError("Usage: rotate");
            return;
        }
        if(clientModel.getCurrentComponent() == null){
            renderer.onError("You have to draw a component first");
            return;
        }
        clientModel.getCurrentComponent().modifyOrientation(Side.RIGHT);
        renderer.showsDrawnComponent(clientModel.getCurrentComponent());
        renderer.printPromptSymbol();
    }

    /**
     * Handles the "abandon race" command.
     */
    public void handleAbandon() {
        try {
            renderer.notifyDefeat(4);
            server.abandonRace(clientId);
        }catch(Exception e){
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "create" command.
     */
    public void handleCreate() {
        try{
            server.createGame(clientId);
        } catch(Exception e){
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "join" command.
     */
    public void handleJoin(String[] tokens) throws Exception {
        try{
            server.joinGame(clientId, Integer.parseInt(tokens[1]));
        } catch(NumberFormatException e){
            renderer.onError("invalid game index");
        } catch(Exception e){
            renderer.reportException(e);
        }
    }

    /**
     * Handles the "view list" command.
     */
    public void handleViewGameList() throws Exception {
        try{
            server.viewGame(clientId);
        } catch(Exception e){
            renderer.reportException(e);
        }
    }

}
