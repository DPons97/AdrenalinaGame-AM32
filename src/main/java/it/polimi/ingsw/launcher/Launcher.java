package it.polimi.ingsw.launcher;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.controller.ConnectionType;
import it.polimi.ingsw.client.view.FXWindow;
import it.polimi.ingsw.server.controller.LoginHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
        System.out.print("Launch as Server [0] or Client [1]?");
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
            System.out.print("Connection type [Socket 0, RMI 1]");
            ConnectionType c = in.next().equals("1") ? ConnectionType.RMI : ConnectionType.SOCKET;
            System.out.print("Nickname: ");
            nickname = in.next();
            startClient(serverddress, port, nickname, c, 0);
        }
    }

    public void startLauncherGui(){
        // TODO gui launcher
        new Thread(()->Application.launch(FXWindow.class)).start();

        synchronized (FXWindow.lock) {
            while (FXWindow.getStage() == null) {
                try {
                    FXWindow.lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            FXWindow.lock.notifyAll();
        }
        Platform.setImplicitExit(false);
        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = FXWindow.getGrid();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Text scenetitle = new Text("Welcome to Adrenalina");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            Label label = new Label("Launch as:\t\t");

            // Group
            ToggleGroup group = new ToggleGroup();

            // Radio 1: Client
            RadioButton button1 = new RadioButton("Client");
            button1.setToggleGroup(group);
            button1.setSelected(true);

            // Radio 3: Server.
            RadioButton button2 = new RadioButton("Server");
            button2.setToggleGroup(group);

            HBox root = new HBox();
            root.setSpacing(5);
            root.getChildren().addAll(button1, button2);
            grid.add(label, 0, 2);
            grid.add(root, 1, 2);

            Label label1 = new Label("Connection mode:\t");

            // Group
            ToggleGroup group1 = new ToggleGroup();

            // Radio 1: Male
            RadioButton button3 = new RadioButton("SOCKET");
            button3.setToggleGroup(group1);
            button3.setSelected(true);

            // Radio 3: Female.
            RadioButton button4 = new RadioButton("RMI");
            button4.setToggleGroup(group1);

            HBox root1 = new HBox();
            root1.setSpacing(5);
            root1.getChildren().addAll( button3, button4);
            grid.add(label1, 0, 3);
            grid.add(root1, 1, 3);


            Label ip = new Label("Ip:");
            grid.add(ip, 0, 5);

            TextField ipBox = new TextField();
            grid.add(ipBox, 1, 5);

            Label port = new Label("Port:");
            grid.add(port, 0, 6);

            TextField portBox = new TextField();
            grid.add(portBox, 1, 6);

            Button btnServer = new Button("START SERVER");
            HBox hbBtnServer = new HBox(10);
            hbBtnServer.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtnServer.getChildren().add(btnServer);
            grid.add(hbBtnServer, 1, 4);
            btnServer.setVisible(false);

            Button btnClient = new Button("START CLIENT");
            HBox hbBtnClient = new HBox(10);
            hbBtnClient.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtnClient.getChildren().add(btnClient);
            grid.add(hbBtnClient, 1, 8);


            Label userName = new Label("User Name:");
            grid.add(userName, 0, 4);

            TextField userTextField = new TextField();
            grid.add(userTextField, 1, 4);
            userTextField.setEditable(true);

            button2.setOnAction(actionEvent -> {
                userName.setVisible(!button2.isSelected());
                userTextField.setVisible(!button2.isSelected());
                ip.setVisible(!button2.isSelected());
                ipBox.setVisible(!button2.isSelected());
                port.setVisible(!button2.isSelected());
                portBox.setVisible(!button2.isSelected());
                btnClient.setVisible(!button2.isSelected()); //CLIENT
                btnServer.setVisible(button2.isSelected()); //SERVER
                root1.setVisible(!button2.isSelected());
                label1.setVisible(!button2.isSelected());
            });

            button1.setOnAction(actionEvent -> {
                userTextField.setVisible(button1.isSelected());
                userName.setVisible(button1.isSelected());
                ip.setVisible(button1.isSelected());
                port.setVisible(button1.isSelected());
                ipBox.setVisible(button1.isSelected());
                portBox.setVisible(button1.isSelected());
                btnServer.setVisible(!button1.isSelected()); //SERVER
                btnClient.setVisible(button1.isSelected()); //CLIENT
                root1.setVisible(button1.isSelected());
                label1.setVisible(button1.isSelected());
            });

            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 9);

            btnClient.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Sign in button pressed");
                }
            });

        });
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
        // read params and start right launcher
        Launcher l= new Launcher();
        List<String> argsList = new ArrayList<>(Arrays.asList(args));
        String cfgFile = l.parseCfgFile(argsList);
        if(cfgFile!= null) {
            l.parseFile(cfgFile);
            return;
        }
        if(l.needLauncher(argsList)) {
            int view = l.parseView(argsList);
            if(view == 1 ) l.startLauncherGui();
            else l.startLaunherCli();
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
