package it.polimi.ingsw.client.view;


import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Clickable {
    private ImageView img;
    private String value;

    public Clickable(ImageView img, String value){
        this.img = img;
        this.value = value;
    }

    public void setClickable(){
        img.setOnMouseClicked(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {

            }

        });
    }

}
