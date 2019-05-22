package it.polimi.ingsw.launcher;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.controller.ConnectionType;
import it.polimi.ingsw.server.controller.LoginHandler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Will be the main method for the jar from which will be launched the right process
 */
public class Launcher{
    //   -m s/c -s serverAddress -p serverPort -cli/-gui -n nickname -c r/s -cfg configFIlePath
    private static final String MODE = "-m";
    private static final String SERVER = "-s";
    private static final String PORT = "-p";
    private static final String CLI = "-cli";
    private static final String GUI = "-gui";
    private static final String NICK = "-n";
    private static final String CFG = "-cfg";
    private static final String CONNECTION = "-c";

    public void startLaunherCli(){

        Scanner in = new Scanner(System.in);
        System.out.print("Launch as server[0] or client[1]?");
        if(in.nextInt()==0)
        {
            startServer();
        } else {
            String serverddress, nickname;
            int port;
            System.out.print("Server address: ");
            serverddress = in.next();
            System.out.print("Server port: ");
            port = in.nextInt();
            System.out.print("Connection type [socket 0, rmi 1]");
            ConnectionType c = in.next().equals("1") ? ConnectionType.RMI : ConnectionType.SOCKET;
            System.out.print("Nickname: ");
            nickname = in.next();
            startClient(serverddress, port, nickname, c, 0);
        }
    }

    public void startLauncherGui(){
        // TODO gui launcher
    }

    public void startServer(){
        LoginHandler.startServer();
    }

    public void startClient(String server, int port, String nick, ConnectionType c, int view){
        try {
            ClientPlayer clientPlayer = new ClientPlayer(nick, c, server, port, view==1);
        } catch (RemoteException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    private String parseMode(List<String> args){
        return args.contains(MODE) ? args.get(args.indexOf(MODE)+1) : null;
    }

    private String parseServerAddress(List<String> args){
        return args.contains(SERVER)? args.get(args.indexOf(SERVER)+1) : null;
    }

    private String parseServerPort(List<String> args){
        return args.contains(PORT)? args.get(args.indexOf(PORT)+1) : null;
    }

    private String parseNickname(List<String> args){
        return args.contains(NICK)? args.get(args.indexOf(NICK)+1) : null;
    }

    private String parseConnection(List<String> args){
        return args.contains(CONNECTION)? args.get(args.indexOf(CONNECTION)+1) : null;
    }

    private int parseView(List<String> args){
        if (args.contains(GUI))return 1;
        if (args.contains(CLI))return 0;
        return -1;
    }

    private String parseCfgFile(List<String> args){
        return args.contains(CFG)? args.get(args.indexOf(CFG)+1) : null;
    }

    private boolean needLauncher(List<String> args){
        return ! (args.contains(MODE) && (parseMode(args).equals("s") || ( args.contains(SERVER) &&
                 args.contains(NICK) &&  args.contains(PORT) && args.contains(CONNECTION) &&
                (parseConnection(args).equals("r") ||parseConnection(args).equals("s") )&&
                (args.contains(CLI) || args.contains(GUI)))));
    }

    private void parseFile(String file){
        //TODO PARSE CONFIG FROM FILE
    }

    public static void main(String[] args) {
        // read params and start right launcher o
        List<String> argsList = new ArrayList<>();
        Launcher l= new Launcher();
        argsList.addAll(Arrays.asList(args));
        String cfgFile = l.parseCfgFile(argsList);
        if(cfgFile!= null) {
            l.parseFile(cfgFile);
            return;
        }
        if(l.needLauncher(argsList)) {
            int view = l.parseView(argsList);
            if(view == 1 ) l.startLauncherGui();
            else l.startLaunherCli();
            return;
        } else {
            if(l.parseMode(argsList).equals("s")){
                l.startServer();
            } else {
                int port = Integer.parseInt(l.parseServerPort(argsList));
                String server = l.parseServerAddress(argsList);
                String nick = l.parseNickname(argsList);
                ConnectionType c = l.parseConnection(argsList).equals("r") ? ConnectionType.RMI : ConnectionType.SOCKET;
                int view = l.parseView(argsList);
                if(view == 0) l.startClient(server, port, nick, c, view);
            }
        }
    }

}
