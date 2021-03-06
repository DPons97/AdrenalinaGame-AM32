package it.polimi.ingsw.launcher;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.controller.ConnectionType;
import it.polimi.ingsw.client.view.FXWindow;
import it.polimi.ingsw.custom_exceptions.UsernameTakenException;
import it.polimi.ingsw.server.controller.LoginHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.UnaryOperator;

/**
 * Will be the main method for the jar from which will be launched the right process
 */
public class Launcher{
    //   -m s/c -s [serverAddress] -p [serverPort] -cli/-gui -n [nickname] -c [r/s] -cfg [configFIlePath]
    private static final String MODE = "-m";
    private static final String SERVER = "-s";
    private static final String PORT = "-p";
    private static final String CLI = "-cli";
    private static final String GUI = "-gui";
    private static final String NICK = "-n";
    private static final String CONNECTION = "-c";

    public void startLaunherCli(){

        Scanner in = new Scanner(System.in);
        int selected ;
        do {
            System.out.print("Launch as Server [0] or Client [1]?");
            try {
                selected = in.nextInt();
            }catch(Exception e){
                in.nextLine();
                selected = -1;
            }
        }while(!(selected == 0 || selected == 1));
        if(selected==0)
        {
            LoginHandler started;
            do {
                int port;
                do {
                    System.out.println("Listen on port: ");
                    try {
                        port = in.nextInt();
                    } catch (Exception e) {
                        in.nextLine();
                        port = 0;
                    }
                } while (port == 0);
                started = startServer(port);
            }while(started == null);
        } else {
            int res = 1;
            do{
                String serverddress, nickname;
                int port;
                System.out.print("Server address: ");
                serverddress = in.next();
                System.out.print("Server port: ");
                try {
                    port = in.nextInt();
                }catch (Exception e){
                    System.out.print("Invalid port");
                    continue;
                }
                System.out.print("Connection type [Socket 0, RMI 1]");
                ConnectionType c = (in.next().equals("1")) ? ConnectionType.RMI : ConnectionType.SOCKET;

                System.out.print("Nickname: ");
                nickname = in.next();

                res = startClient(serverddress, port, nickname, c, 0);
                if (res == 1) {
                    System.out.println("Error connecting: ip/port not valid");
                } else if (res == 2) {
                    System.out.println("Error connecting: username already in use");
                }
            } while(res != 0);
        }
    }

    public void startLauncherGui(){
        initView();
        Platform.setImplicitExit(false);
        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = (GridPane) FXWindow.getPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Text scenetitle = new Text("Welcome to Adrenalina");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            Label labelClientServer = new Label("Launch as:\t\t");

            // Group
            ToggleGroup groupClientServer = new ToggleGroup();

            // Radio 1: Client
            RadioButton rbClient = new RadioButton("Client");
            rbClient.setToggleGroup(groupClientServer);
            rbClient.setSelected(true);

            // Radio 3: Server.
            RadioButton rbServer = new RadioButton("Server");
            rbServer.setToggleGroup(groupClientServer);

            HBox csBox = new HBox();
            csBox.setSpacing(5);
            csBox.getChildren().addAll(rbClient, rbServer);
            grid.add(labelClientServer, 0, 2);
            grid.add(csBox, 1, 2);

            Label labelSktRmi = new Label("Connection mode:\t");

            // Group
            ToggleGroup groupSktRmi = new ToggleGroup();

            // Radio 1: Male
            RadioButton rbSocket = new RadioButton("SOCKET");
            rbSocket.setToggleGroup(groupSktRmi);
            rbSocket.setSelected(true);

            // Radio 3: Female.
            RadioButton rbRmi = new RadioButton("RMI");
            rbRmi.setToggleGroup(groupSktRmi);

            HBox srBox = new HBox();
            srBox.setSpacing(5);
            srBox.getChildren().addAll( rbSocket, rbRmi);
            grid.add(labelSktRmi, 0, 3);
            grid.add(srBox, 1, 3);

            Label ip = new Label("Ip:");
            grid.add(ip, 0, 5);

            TextField ipBox = new TextField();
            grid.add(ipBox, 1, 5);
            ipBox.setViewOrder(-1);

            Label port = new Label("Port:");
            grid.add(port, 0, 6);

            TextField portBox = new TextField();
            grid.add(portBox, 1, 6);

            //port allows only integers
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String text = change.getText();

                if (text.matches("[0-9]*")) {
                    return change;
                }

                return null;
            };
            TextFormatter<String> textFormatterPort = new TextFormatter<>(filter);
            portBox.setTextFormatter(textFormatterPort);

            Label portServer = new Label("Port:");
            grid.add(portServer, 0, 3);
            portServer.setVisible(false);

            TextField portBoxServer = new TextField();
            grid.add(portBoxServer, 1, 3);
            portBoxServer.setVisible(false);
            TextFormatter<String> textFormatterPortServer = new TextFormatter<>(filter);
            portBoxServer.setTextFormatter(textFormatterPortServer);

            Button btnServer = new Button("START SERVER");
            HBox hbBtnServer = new HBox(10);
            hbBtnServer.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtnServer.getChildren().add(btnServer);
            grid.add(hbBtnServer, 1, 5);
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

            rbServer.setOnAction(actionEvent -> {
                userName.setVisible(!rbServer.isSelected());
                userTextField.setVisible(!rbServer.isSelected());
                ip.setVisible(!rbServer.isSelected());
                port.setVisible(!rbServer.isSelected());
                portBox.setVisible(!rbServer.isSelected());
                ipBox.setVisible(!rbServer.isSelected());
                portServer.setVisible(rbServer.isSelected());
                portBoxServer.setVisible(rbServer.isSelected());
                btnClient.setVisible(!rbServer.isSelected()); //CLIENT
                btnServer.setVisible(rbServer.isSelected()); //SERVER
                srBox.setVisible(!rbServer.isSelected());
                labelSktRmi.setVisible(!rbServer.isSelected());
            });

            rbClient.setOnAction(actionEvent -> {
                userTextField.setVisible(rbClient.isSelected());
                userName.setVisible(rbClient.isSelected());
                ip.setVisible(rbClient.isSelected());
                ipBox.setVisible(rbClient.isSelected());
                port.setVisible(rbClient.isSelected());
                portBox.setVisible(rbClient.isSelected());
                port.setVisible(rbServer.isSelected());
                portBox.setVisible(rbServer.isSelected());
                btnServer.setVisible(!rbClient.isSelected()); //SERVER
                btnClient.setVisible(rbClient.isSelected()); //CLIENT
                srBox.setVisible(rbClient.isSelected());
                labelSktRmi.setVisible(rbClient.isSelected());
            });

            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 9);

            btnClient.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    if(userTextField.getText().equals("") || ipBox.getText().equals("") || portBox.getText().equals("")) {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("Please, fill in the boxes.");
                    } else {
                        System.out.println("connecting to server");
                        actiontarget.setFill(Color.ALICEBLUE);
                        actiontarget.setText("Connecting to server...");
                        ConnectionType c = rbRmi.isSelected() ? ConnectionType.RMI : ConnectionType.SOCKET;
                        int p = startClient(ipBox.getText(), Integer.parseInt(portBox.getText()), userTextField.getText(), c, 1);
                        if(p==1){
                            actiontarget.setFill(Color.FIREBRICK);
                            actiontarget.setText("Error connecting to the server: invalid ip/port");
                        } else  if (p == 2) {
                            actiontarget.setFill(Color.FIREBRICK);
                            actiontarget.setText("Error connecting to the server: username already in use");
                        }else {
                            grid.getChildren().clear();
                        }
                    }
                }
            });

            btnServer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    LoginHandler server;
                    try{
                        server = startServer(Integer.parseInt(portBoxServer.getText()));
                    }catch (Exception e2) {
                        e2.printStackTrace();
                        server =startServer();
                    }
                    grid.getChildren().clear();
                    if(server != null) {
                        Text scenetitle = new Text("Adrenalina server is running...");
                        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                        grid.add(scenetitle, 0, 0, 2, 1);
                        Label ip = new Label("IP addresses: "+server.getAddress());
                        Label rmi = new Label("RMI port: "+server.getRmiPort());
                        Label socket = new Label("Socket port: "+server.getSocketPort());
                        grid.add(ip, 0,2);
                        grid.add(rmi, 0,3);
                        grid.add(socket, 0,4);
                    } else{
                        Text scenetitle = new Text("Error starting server :(");
                        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                        grid.add(scenetitle, 0, 0, 2, 1);
                        Label msg = new Label("Please restart the program.");
                        grid.add(msg, 0,2);
                    }
                }
            });

        });
    }

    public LoginHandler startServer(){
        return LoginHandler.startServer();
    }

    public LoginHandler startServer(String port){
        try {
            int toSet = Integer.parseInt(port);
            return LoginHandler.startServer(toSet);
        } catch (Exception e){
            e.printStackTrace();
            return LoginHandler.startServer();
        }
    }

    public LoginHandler startServer(int port){
        try {
            return LoginHandler.startServer(port);
        } catch (Exception e){
            return null;
        }
    }

    public int startClient(String server, int port, String nick, ConnectionType c, int view){
        try {
            if(view == 1)initView();
            ClientPlayer p =  new ClientPlayer(nick, c, server, port, view==1);

            return 0;
        } catch (NotBoundException | IOException e) {
           return 1;
        } catch (UsernameTakenException e) {
            return 2;
        }

    }

    private void initView() {
        if(FXWindow.isRunning())return;
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
    }

    private String parseMode(List<String> args){
        return args.contains(MODE) ? args.get(args.indexOf(MODE)+1) : null;
    }

    private String parseServerAddress(List<String> args) {
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

    private boolean needLauncher(List<String> args){
        return ! (args.contains(MODE) && (parseMode(args).equals("s") || ( args.contains(SERVER) &&
                 args.contains(NICK) &&  args.contains(PORT) && args.contains(CONNECTION) &&
                (parseConnection(args).equals("r") ||parseConnection(args).equals("s") )&&
                (args.contains(CLI) || args.contains(GUI)))));
    }

    public static void main(String[] args) {
        // read params and start right launcher
        Launcher l= new Launcher();
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        if(l.needLauncher(argsList)) {
            int view = l.parseView(argsList);
            if(view == 0 ) l.startLaunherCli();
            else l.startLauncherGui();
        } else {
            if(l.parseMode(argsList).equals("s")){
                if(l.parseServerPort(argsList) != null){
                    l.startServer(l.parseServerPort(argsList));
                } else {
                    l.startServer();
                }
            } else {
                int port = Integer.parseInt(l.parseServerPort(argsList));
                String server = l.parseServerAddress(argsList);
                String nick = l.parseNickname(argsList);
                ConnectionType c = l.parseConnection(argsList).equals("r") ? ConnectionType.RMI : ConnectionType.SOCKET;
                int view = l.parseView(argsList);
                l.startClient(server, port, nick, c, view);
            }
        }
    }

}
