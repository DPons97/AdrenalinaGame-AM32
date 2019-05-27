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


public class newgame extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create new game");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Text scenetitle = new Text("Create new game");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        //numero di giocatori
        // numero di morti
        // durata del turno
        // id della mappa

        Label nPlayers = new Label("Number of players:");
        grid.add(nPlayers, 0, 1);

        TextField nPlayersBox = new TextField();
        grid.add(nPlayersBox, 1, 1);

        Label nDeaths = new Label("Number of deaths:");
        grid.add(nDeaths, 0, 2);

        TextField nDeathsBox = new TextField();
        grid.add(nDeathsBox, 1, 2);

        Label turnDuration = new Label("Turn duration:");
        grid.add(turnDuration, 0, 3);

        TextField turnDurationBox = new TextField();
        grid.add(turnDurationBox, 1, 3);

        Label mapId = new Label("Map Id:");
        grid.add(mapId, 0, 4);

        TextField mapIdBox = new TextField();
        grid.add(mapIdBox, 1, 4);





        Button btnNewGame = new Button("Create New Game");
        HBox hbBtnNewGame = new HBox(10);
        hbBtnNewGame.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtnNewGame.getChildren().add(btnNewGame);
        grid.add(hbBtnNewGame, 1, 8);



        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 9);

        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("New game created");
            }
        });

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}