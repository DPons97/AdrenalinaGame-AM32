package it.polimi.ingsw.client.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class waitingroom extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
        public void start(Stage primaryStage) {

            primaryStage.setTitle("Waiting room");
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));



            Text scenetitle = new Text("Waiting room");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0);
            Label label = new Label("Your name: Michele ");
            grid.add(label, 0, 1);

            ScrollPane players = new ScrollPane();
            players.setPrefSize(primaryStage.getWidth()*0.8, primaryStage.getHeight()*0.5);
            GridPane playersGrid = new GridPane();
            playersGrid.setHgap(0);
            playersGrid.setVgap(0);
            grid.add(players,0,6);
            players.setContent(playersGrid);
            Label msg = new Label("Player Name: \t Color Player:\t");
            playersGrid.add(msg, 0,1);
            Label msg1 = new Label("Player Name: \t Color Player:\t");
            playersGrid.add(msg1, 0,2);
            Label msg2 = new Label("Player Name: \t Color Player:\t");
            playersGrid.add(msg2, 0,3);
            Label msg3 = new Label("Player Name: \t Color Player:\t");
            playersGrid.add(msg3,0,4);




            Button btnReady = new Button("READY");
            HBox hbBtnReady = new HBox(10);
            hbBtnReady.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtnReady.getChildren().add(btnReady);
            grid.add(hbBtnReady, 0, 8);



            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 9);

            btnReady.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Ready button pressed");
                }
            });

            Scene scene = new Scene(grid, 900, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
    }
}