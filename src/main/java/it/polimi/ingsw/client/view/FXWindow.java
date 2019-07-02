package it.polimi.ingsw.client.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FXWindow extends Application {
    private static Stage primaryStage;
    private static Pane mainPane;
    public static Object lock = new Object();
    private static boolean running = false;
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
            mainPane = new GridPane();
            lock.notifyAll();
            running = true;
        }

        primaryStage.setTitle("Adrenalina: the best game ever made. \"ITS FUN COZ YOU SHOOT\"");

        stage.setMaximized(true);
        stage.setResizable(false);

        Scene scene = new Scene(mainPane,stage.getWidth(), stage.getHeight());

        stage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Stage is closing");
            Platform.exit();
        });

        primaryStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if(newValue != null && !newValue.booleanValue())
                    primaryStage.setFullScreen(true);
            }
        });

        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        primaryStage.show();
    }

    public  static Stage getStage(){

            return primaryStage;

    }

    public static Pane getPane(){

            return mainPane;

    }

    public static void setPane(Pane toSet){
        mainPane = toSet;
    }

    public static boolean isRunning(){

        return running;

    }

    @Override
    public void stop(){
       System.exit(0);
    }

}
