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
import java.net.URL;

public class ImageExample extends Application {

    private final static String MAP1 = "/img/maps/1.png";
    //private final static String MAP2 = "/img/maps/2.png";
    //private final static String MAP3 = "/img/maps/3.png";
    //private final static String MAP4 = "/img/maps/4.png";

    private final static String TAB1 = "/img/tabs/01_purple.png";
    private final static String TAB2 = "/img/tabs/02_green.png";
    private final static String TAB3 = "/img/tabs/03_grey.png";
    private final static String TAB4 = "/img/tabs/04_yellow.png";
    private final static String TAB5 = "/img/tabs/05_blue.png";
    //private final static String TAB6 = "/img/tabs/01_purpleback.png";
    //private final static String TAB7 = "/img/tabs/02_greenback.png";
    //private final static String TAB8 = "/img/tabs/03_greyback.png";
    //private final static String TAB9 = "/img/tabs/04_yellowback.png";
    // private final static String TAB10 = "/img/tabs/05_blueback.png";
   // private final static String TAB11 = "/img/tabs/01_purpleback.png";
   // private final static String TAB12 = "/img/tabs/02_greenback.png";
   // private final static String TAB13 = "/img/tabs/03_greyback.png";
   // private final static String TAB14 = "/img/tabs/04_yellowback.png";
   // private final static String TAB15 = "/img/tabs/05_blueback.png";
    private final static String CARDBACK00 = "/img/cards/AD_powerups_IT_02.png";
    private final static String CARDBACK01 = "/img/cards/AD_weapons_IT_0212.png";
    private final static String DROPLET = "/img/droplets/dropblue-removebg.png";

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        //Creating an map
       // Image map = new Image (new FileInputStream = getClass().getResource("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/maps/1.png"));
        //FileInputStream map = getClass().getResource("C:/Users/miche/IdeaProjects/ing-sw-2019-Collini-Pons-Colazzo/src/main/resources/img/maps/1.png");
        //InputStream map = getClass().getResourceAsStream("/resources/maps/1.png");

        // ImageIcon, like many others, can receive an URL as argument,
// which is a better approach to load resources that are
// contained in the classpath
        URL urlMap1 = getClass().getResource(MAP1);
        Image map = new Image(new FileInputStream(urlMap1.getFile()));
        URL urlTab1 = getClass().getResource(TAB1);
        Image tab1 = new Image(new FileInputStream(urlTab1.getFile()));
        URL urlTab2 = getClass().getResource(TAB2);
        Image tab2 = new Image(new FileInputStream(urlTab2.getFile()));
        URL urlTab3 = getClass().getResource(TAB3);
        Image tab3 = new Image(new FileInputStream(urlTab3.getFile()));
        URL urlTab4 = getClass().getResource(TAB4);
        Image tab4 = new Image(new FileInputStream(urlTab4.getFile()));
        URL urlTab5 = getClass().getResource(TAB5);
        Image tab5 = new Image(new FileInputStream(urlTab5.getFile()));
        URL urlCardBack00 = getClass().getResource(CARDBACK00);
        Image cardback00 = new Image(new FileInputStream(urlCardBack00.getFile()));
        URL urlCardBack01 = getClass().getResource(CARDBACK01);
        Image cardback01 = new Image(new FileInputStream(urlCardBack01.getFile()));
        URL urlDroplets = getClass().getResource(DROPLET);
        Image droplet = new Image (new FileInputStream(urlDroplets.getFile()));


        ImageView mapView = new ImageView(map);
        ImageView tab1View = new ImageView(tab1);
        ImageView tab2View = new ImageView(tab2);
        ImageView tab3View = new ImageView(tab3);
        ImageView tab4View = new ImageView(tab4);
        ImageView tab5View = new ImageView(tab5);
        ImageView cardback00View = new ImageView(cardback00);
        ImageView cardback01View = new ImageView(cardback01);


        Group root = new Group(mapView, cardback00View, cardback01View, tab1View, tab2View, tab3View, tab4View, tab5View);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        //Creating a scene object
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight()-50);




        //Setting title to the Stage
        stage.setTitle("Loading a map");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        // position on X and Y
        double mapX=0;
        double mapY=0;

        double tab1Y=0;
        double tab2Y=(stage.getHeight()*0.15);
        double tab3Y=(stage.getHeight()*0.30);
        double tab4Y=(stage.getHeight()*0.45);
        double tabOthers=(stage.getWidth()*0.6);
        double tab0X=0;//my tab X
        double tab0Y=(stage.getHeight()*0.7); //my tab Y
        double powerupsDeckX= (stage.getWidth()*0.54);
        double powerupsDeckY= (stage.getHeight()*0.04);
        double ammoDeckX= (stage.getWidth()*0.523);
        double ammoDeckY= (stage.getHeight()*0.1905);
        double ammo1Y = 0;
        double ammo1_1X = (stage.getWidth()*0.317);
        double ammo1_2X = (stage.getWidth()*0.383);
        double ammo1_3X = (stage.getWidth()*0.449);
        double ammo1rotation = 0;
        double ammo2X = (stage.getWidth()*0.017);
        double ammo2_1Y = (stage.getHeight()*0.227);
        double ammo2_2Y = (stage.getHeight()*0.327);
        double ammo2_3Y = (stage.getHeight()*0.427);
        double ammo2rotation= (90);
        double ammo3X = (stage.getWidth()*0.528);
        double ammo3_1Y = (stage.getHeight()*0.37);
        double ammo3_2Y = (stage.getHeight()*0.47);
        double ammo3_3Y = (stage.getHeight()*0.57);
        double ammo3rotation= (-90);
        double tab1droplet1Y = (stage.getHeight()*((-0.09+(0.15*4)))); //riga fino a 4
        double tab1droplet1X = (stage.getWidth()*((0.6220+(0.0215*6)))); //colonna fino a 10
        double tabDropletY0 = (stage.getHeight()*(-0.09));
        double tabDropletX0 = stage.getWidth()*(0.6220);
        double tabDropletOffY = stage.getHeight()*0.15;
        double tabDropletOffX = stage.getWidth()*0.0215;



        //size
        double mapW = (stage.getWidth()*0.60);
        double mapH = (stage.getHeight()*0.15);
        double tab2Size =(stage.getHeight()*0.15);
        double tab3Size = (stage.getHeight()*0.15);
        double tab4Size =(stage.getHeight()*0.15);
        double mytabSize =(stage.getWidth()*0.60);
        double powerupsSize = (stage.getWidth()*0.038);
        double ammoSize = (stage.getWidth()*0.055);
        double dropletSize = (stage.getWidth()*0.0149);

        //Setting the position of the map

        cardback00View.setX(powerupsDeckX);
        cardback00View.setY(powerupsDeckY);
        cardback01View.setX(ammo2X);
        cardback01View.setY(ammo2_3Y);
        mapView.setX(mapX);
        mapView.setY(mapY);
        tab1View.setX(tabOthers);
        tab1View.setY(tab1Y);
        tab2View.setX(tabOthers);
        tab2View.setY(tab2Y);
        tab3View.setX(tabOthers);
        tab3View.setY(tab3Y);
        tab4View.setX(tabOthers);
        tab4View.setY(tab4Y);
        tab5View.setX(tab0X);
        tab5View.setY(tab0Y);

        for(int i = 1; i<=4; i++){
            for(int j = 1 ; j <=10;j++){
                ImageView dropletView = new ImageView(droplet);
                dropletView.setX(tabDropletX0+tabDropletOffX*j);
                dropletView.setY(tabDropletY0+tabDropletOffY*i);
                dropletView.setPreserveRatio(true);
                dropletView.setFitWidth(stage.getWidth()*0.0149);
                root.getChildren().add(dropletView);
            }
        }



        stage.getWidth();
        stage.getHeight();

        //setting the fit height and width of the map view

        mapView.setFitWidth(mapW);
        tab1View.setFitHeight(mapH);
        tab2View.setFitHeight(tab2Size);
        tab3View.setFitHeight(tab3Size);
        tab4View.setFitHeight(tab4Size);
        tab5View.setFitWidth(mytabSize);
        cardback00View.setFitWidth(powerupsSize);
        cardback01View.setFitWidth(ammoSize);
        cardback01View.setRotate(ammo2rotation);




        //Setting the preserve ratio of the map view
        mapView.setPreserveRatio(true);
        tab1View.setPreserveRatio(true);
        tab2View.setPreserveRatio(true);
        tab3View.setPreserveRatio(true);
        tab4View.setPreserveRatio(true);
        tab5View.setPreserveRatio(true);
        cardback00View.setPreserveRatio(true);
        cardback01View.setPreserveRatio(true);

        //Creating a Group object



    }
    public static void main(String args[]) {
        launch(args);
    }
}