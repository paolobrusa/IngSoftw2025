package it.polimi.ingsw.is25am31;

import it.polimi.ingsw.is25am31.network.SharedRmiSocket;
import it.polimi.ingsw.is25am31.network.rmi.client.RmiClient;
import it.polimi.ingsw.is25am31.network.socket.client.SocketClient;
import it.polimi.ingsw.is25am31.view.gui.GuiRenderer;
import it.polimi.ingsw.is25am31.view.tui.TuiRenderer;

import java.rmi.ConnectException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("""
                Welcome to the Galaxy Trucker game launcher.
                To start the server, write 'run server'.
                To start the client, write 'run <connection type> client <ui type>' (connection types: rmi, socket), (ui types: tui, gui).
                To quit the launcher, write 'quit'""");
        Scanner in = new Scanner(System.in);
        String input;
        do{
            System.out.print("> ");
            input = in.nextLine();
        }while (!parseInput(input));
    }

    private static boolean parseInput(String input) throws Exception {
        boolean connectionSuccessful = false;
        switch (input) {
            case "quit":
                System.exit(0);
                break;
            case "run server":
                SharedRmiSocket.start();
                break;
            case "run rmi client tui", "run rmi client gui", "run socket client tui", "run socket client gui":
                System.out.print("Insert server IP address\n> ");
                while (!connectionSuccessful) {
                    String serverIp = requestIpAddress();
                    try {
                        switch (input) {
                            case "run rmi client tui" -> RmiClient.connect(serverIp, TuiRenderer.getInstance());
                            case "run rmi client gui" -> RmiClient.connect(serverIp, GuiRenderer.getInstance());
                            case "run socket client tui" -> SocketClient.start(serverIp, "1235", TuiRenderer.getInstance());
                            case "run socket client gui" -> SocketClient.start(serverIp, "1235", GuiRenderer.getInstance());
                        }
                        connectionSuccessful = true;
                    } catch (java.net.ConnectException  | java.rmi.ConnectException _){
                        System.err.println("Connection attempt failed");
                    }
                    if (!connectionSuccessful) {
                        Thread.sleep(50);
                        System.out.println("Try inserting IP address again");
                        System.out.print("> ");
                    }
                }
                break;
            default:
                System.err.println("Invalid command");
                Thread.sleep(50);
                return false;
        }
        return true;
    }

    private static String requestIpAddress() throws InterruptedException {
        Scanner in = new Scanner(System.in);
        String ipAddress = in.nextLine();
        while(!isValidIpAddress(ipAddress)) {
            System.err.println("Invalid address format, try again");
            Thread.sleep(50);
            System.out.print("> ");
            ipAddress = in.nextLine();
        }
        return ipAddress;
    }

    private static boolean isValidIpAddress(String ipAddress) {
        return Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$").matcher(ipAddress).matches();
    }
}
