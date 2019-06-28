package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.model.Map;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class GuiSelection {
    private boolean valid;
    private String value;
    private final static Object lock = new Object();

    public GuiSelection(){
        valid=false;
    }

    public String getValue(){
        String toReturn;
        synchronized (lock) {
            while (valid == false) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            toReturn = value;
            valid = false;
            lock.notifyAll();
        }
        return toReturn;
    }

    private void setValue(String value){
        synchronized (lock){
            if(valid == false){
                valid = true;
                this.value = value;
                lock.notifyAll();
            }
        }
    }

    public void setNodeClickable(Node node, String value){
        Platform.runLater(()->{
            node.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    setValue(value);
                }

            });
        });


    }

    public void setNodeNotClickable(Node node){
        Platform.runLater(()->{
            node.setOnMouseClicked(null);
        });

    }

}
