package it.polimi.ingsw.client.view;

import javafx.application.Application;
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


public class FXExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Adrenalina Login");
        GridPane grid = new GridPane();
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

        // Radio 1: Male
        RadioButton button1 = new RadioButton("Client");
        button1.setToggleGroup(group);
        button1.setSelected(true);

        // Radio 3: Female.
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





        Label userName = new Label("User Name:");
        grid.add(userName, 0, 4);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 4);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 5);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 5);

        Label ip = new Label("Ip:");
        grid.add(ip, 0, 6);

        TextField ipBox = new TextField();
        grid.add(ipBox, 1, 6);

        Label port = new Label("Port:");
        grid.add(port, 0, 7);

        TextField portBox = new TextField();
        grid.add(portBox, 1, 7);

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



        button2.setOnAction(actionEvent -> {
            userName.setVisible(!button2.isSelected());
            userTextField.setVisible(!button2.isSelected());
            pw.setVisible(!button2.isSelected());
            pwBox.setVisible(!button2.isSelected());
            ip.setVisible(!button2.isSelected());
            ipBox.setVisible(!button2.isSelected());
            port.setVisible(!button2.isSelected());
            portBox.setVisible(!button2.isSelected());
            btnClient.setVisible(!button2.isSelected()); //CLIENT
            btnServer.setVisible(button2.isSelected()); //SERVER
            button3.setVisible(!button2.isSelected());
            button4.setVisible(!button2.isSelected());
            label1.setVisible((!button2.isSelected()));

        });

        button1.setOnAction(actionEvent -> {
            userTextField.setVisible(button1.isSelected());
            userName.setVisible(button1.isSelected());
            pw.setVisible(button1.isSelected());
            ip.setVisible(button1.isSelected());
            port.setVisible(button1.isSelected());
            pwBox.setVisible(button1.isSelected());
            ipBox.setVisible(button1.isSelected());
            portBox.setVisible(button1.isSelected());
            btnServer.setVisible(!button1.isSelected()); //SERVER
            btnClient.setVisible(button1.isSelected()); //CLIENT

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

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}