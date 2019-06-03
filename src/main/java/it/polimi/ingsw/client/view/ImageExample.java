package it.polimi.ingsw.client.view;


import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageExample extends Application {
    @Override
    public void start(Stage stage) throws FileNotFoundException {
        //Creating an image
        Image image = new Image(new FileInputStream("C://Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/1.png"));
        Image tab1 = new Image(new FileInputStream("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/01_purple.png"));
        Image tab2 = new Image(new FileInputStream("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/02_green.png"));
        Image tab3 = new Image(new FileInputStream("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/03_gray.png"));
        Image tab4 = new Image(new FileInputStream("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/04_yellow.png"));

        //Setting the image view
        ImageView imageView = new ImageView(image);
        ImageView tab1View = new ImageView(tab1);
        ImageView tab2View = new ImageView(tab2);
        ImageView tab3View = new ImageView(tab3);
        ImageView tab4View = new ImageView(tab4);

        Group root = new Group(imageView, tab1View, tab2View, tab3View, tab4View);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        //Creating a scene object
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight()-50);

        //Setting title to the Stage
        stage.setTitle("Loading an image");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        //Setting the position of the image
        imageView.setX(0);
        imageView.setY(0);
        tab1View.setX(670);
        tab1View.setY(0);
        tab2View.setX(670);
        tab2View.setY(100);
        tab3View.setX(670);
        tab3View.setY(200);
        tab4View.setX(670);
        tab4View.setY(300);

        stage.getWidth();
        stage.getHeight();

        //setting the fit height and width of the image view
        imageView.setFitHeight(stage.getHeight()*0.7);
        imageView.setFitWidth(stage.getWidth()*0.7);
        tab1View.setFitHeight(100);
        tab1View.setFitWidth(300);
        tab2View.setFitHeight(100);
        tab2View.setFitWidth(300);
        tab3View.setFitHeight(100);
        tab3View.setFitWidth(300);
        tab4View.setFitHeight(100);
        tab4View.setFitWidth(300);


        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);
        tab1View.setPreserveRatio(true);
        tab2View.setPreserveRatio(true);
        tab3View.setPreserveRatio(true);
        tab4View.setPreserveRatio(true);



        //Creating a Group object



    }
    public static void main(String args[]) {
        launch(args);
    }
}