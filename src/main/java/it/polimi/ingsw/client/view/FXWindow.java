package it.polimi.ingsw.client.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class FXWindow extends Application {
    private static Stage primaryStage;
    private static GridPane mainGrid;
    public static Object lock = new Object();
    public FXWindow(){
        super();
    }
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("starting gui");
        synchronized (lock) {
            primaryStage = stage;
            lock.notifyAll();
        }
        synchronized (lock){
            mainGrid = new GridPane();
            lock.notifyAll();
        }

        primaryStage.setTitle("Adrenalina: the best game ever made. \"ITS FUN COZ YOU SHOOT\"");
        Scene scene = new Scene(mainGrid,600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public  static Stage getStage(){

            return primaryStage;

    }

    public static GridPane getGrid(){

            return mainGrid;

    }
}
