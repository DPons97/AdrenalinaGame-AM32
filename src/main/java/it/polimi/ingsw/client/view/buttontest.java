package it.polimi.ingsw.client.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class buttontest extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(" Adrenalina Lobby");
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));



        Text scenetitle = new Text("Lobby");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label label = new Label("Players online:\t\t");
        grid.add(label, 0, 1);
        Label label1 = new Label("Matches:\t\t");
        grid.add(label1, 0, 2);
        Label label2 = new Label("Matches List:\t\t");
        grid.add(label2, 0, 3);

        ScrollPane matches = new ScrollPane();
        matches.setPrefSize(primaryStage.getWidth()*0.9, primaryStage.getHeight()*0.6);
        GridPane matchGrid = new GridPane();
        matchGrid.setHgap(0);
        matchGrid.setVgap(0);

        grid.add(matches,0,4);
        matches.setContent(matchGrid);

        Button btn = new Button();
        btn.setText("Match:\tMax Players:\tMax Deaths:\tMap Id:\tTurn Duration\tPlayers in game:");
        matchGrid.add(btn, 0, 0);
        Button btn1 = new Button();
        btn1.setText("Match:\tMax Players:\tMax Deaths:\tMap Id:\tTurn Duration\tPlayers in game:");
        matchGrid.add(btn1, 0, 1);
        Button btn2 = new Button();
        btn2.setText("Match:\tMax Players:\tMax Deaths:\tMap Id:\tTurn Duration\tPlayers in game:");
        matchGrid.add(btn2, 0, 2);
        Button btn3 = new Button();
        btn3.setText("Match:\tMax Players:\tMax Deaths:\tMap Id:\tTurn Duration\tPlayers in game:");
        matchGrid.add(btn3, 0, 3);






    }
}