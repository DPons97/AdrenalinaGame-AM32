package it.polimi.ingsw.client.view;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public class ImageExample extends Application {

    private final static String MAP1 = "/img/maps/4.png";
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
    private final static String PAWN = "/img/pawns/blue.png";
    private final static String AMMO = "/img/ammo/AD_ammo_0434.png";

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
        Image map = new Image(new FileInputStream(urlMap1.getFile().replace("%20", " ")));
        URL urlTab1 = getClass().getResource(TAB1);
        Image tab1 = new Image(new FileInputStream(urlTab1.getFile().replace("%20", " ")));
        URL urlTab2 = getClass().getResource(TAB2);
        Image tab2 = new Image(new FileInputStream(urlTab2.getFile().replace("%20", " ")));
        URL urlTab3 = getClass().getResource(TAB3);
        Image tab3 = new Image(new FileInputStream(urlTab3.getFile().replace("%20", " ")));
        URL urlTab4 = getClass().getResource(TAB4);
        Image tab4 = new Image(new FileInputStream(urlTab4.getFile().replace("%20", " ")));
        URL urlTab5 = getClass().getResource(TAB5);
        Image tab5 = new Image(new FileInputStream(urlTab5.getFile().replace("%20", " ")));
        URL urlCardBack00 = getClass().getResource(CARDBACK00);
        Image cardback00 = new Image(new FileInputStream(urlCardBack00.getFile().replace("%20", " ")));
        URL urlCardBack01 = getClass().getResource(CARDBACK01);
        Image cardback01 = new Image(new FileInputStream(urlCardBack01.getFile().replace("%20", " ")));
        URL urlDroplets = getClass().getResource(DROPLET);
        Image droplet = new Image (new FileInputStream(urlDroplets.getFile().replace("%20", " ")));
        URL urlPawn = getClass().getResource(PAWN);
        Image pawn = new Image(new FileInputStream(urlPawn.getFile().replace("%20", " ")));
        URL urlAmmo = getClass().getResource(AMMO);
        Image ammo = new Image(new FileInputStream(urlAmmo.getFile().replace("%20", " ")));

        ImageView mapView = new ImageView(map);
        ImageView tab1View = new ImageView(tab1);
        ImageView tab2View = new ImageView(tab2);
        ImageView tab3View = new ImageView(tab3);
        ImageView tab4View = new ImageView(tab4);
        ImageView tab5View = new ImageView(tab5);
        ImageView cardback00View = new ImageView(cardback00);
        ImageView cardback01View = new ImageView(cardback01);

        BorderPane borders = new BorderPane();
        Pane root = new Pane(mapView, cardback00View, cardback01View, tab1View, tab2View, tab3View, tab4View, tab5View);

        borders.setCenter(root);

        //Creating a scene object
        Scene scene = new Scene(borders, 1223, 759);
        borders.setPrefSize(scene.getWidth(),scene.getHeight());

        borders.setStyle("-fx-background-color: #222");
        root.setStyle("-fx-background-color: red");

        root.setPrefSize(stage.getHeight()*16/9, stage.getHeight());

        //Setting title to the Stage
        stage.setTitle("Loading a map");


        //Adding scene to the stage
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);
        //Displaying the contents of the stage
        stage.show();
        double pV, pH, width16, height9;
        if(root.getHeight() > root.getWidth()*9/16){
            pH = 0;
            pV = (root.getHeight()- root.getWidth()*9/16)/2;
            System.out.println("stagew h: " + stage.getHeight());
            System.out.println("margin v: "+pV);
        } else {
            pV = 0;
            pH =  (root.getWidth()-root.getHeight()*16/9)/2;
        }

        BorderPane.setMargin(root, new Insets(pV, pH, pV, pH));
        width16 = root.getWidth()-2*pH;
        height9 = root.getHeight()-2*pV;
        System.out.println("ratio: "+ width16/height9 );
        System.out.println("pane h: "+root.getHeight());

        //size
        double mapH = (height9*0.8);
        double mapW = (map.getWidth());
        double mytabSize =(height9*0.20);
        System.out.println("myTab: "+ mytabSize  );

        double powerupsSize = (width16*0.038);
        double ammoSize = (width16*0.055);
        double dropletSize = (width16*0.0149);


        // position on X and Y
        double mapX=0;
        double mapY=0;

        double tab1Y=(height9*0.175*0);
        double tab2Y=(height9*0.175*1);
        double tab3Y=(height9*0.175*2);
        double tab4Y=(height9*0.175*3);

        double tab0X=0;//my tab X
        double tab0Y=(mapH); //my tab Y
        double powerupsDeckX= (width16*0.54);
        double powerupsDeckY= (height9*0.04);
        double ammoDeckX= (width16*0.523);
        double ammoDeckY= (height9*0.1905);
        double ammo1Y = 0;
        double ammo1_1X = (width16*0.317);
        double ammo1_2X = (width16*0.383);
        double ammo1_3X = (width16*0.449);
        double ammo1rotation = 0;
        double ammo2X = (width16*0.017);
        double ammo2_1Y = (height9*0.227);
        double ammo2_2Y = (height9*0.327);
        double ammo2_3Y = (height9*0.427);
        double ammo2rotation= (90);
        double ammo3X = (width16*0.528);
        double ammo3_1Y = (height9*0.37);
        double ammo3_2Y = (height9*0.47);
        double ammo3_3Y = (height9*0.57);
        double ammo3rotation= (-90);





        //Setting the position of the map

        cardback00View.setX(powerupsDeckX);
        cardback00View.setY(powerupsDeckY);
        cardback01View.setX(ammo2X);
        cardback01View.setY(ammo2_3Y);
        mapView.setX(mapX);
        mapView.setY(mapY);

        tab5View.setX(tab0X);
        tab5View.setY(tab0Y);




        //Setting the preserve ratio of the map view
        mapView.setPreserveRatio(true);
        tab1View.setPreserveRatio(true);
        tab2View.setPreserveRatio(true);
        tab3View.setPreserveRatio(true);
        tab4View.setPreserveRatio(true);
        tab5View.setPreserveRatio(true);
        cardback00View.setPreserveRatio(true);
        cardback01View.setPreserveRatio(true);
        //setting the fit height and width of the map view


        mapView.setFitHeight(mapH);

        double tab1size= (width16-getWidth(mapView));
        double tab2Size =(width16-getWidth(mapView));
        double tab3Size = (width16-getWidth(mapView));
        double tab4Size =(width16-getWidth(mapView));

        double tabOthers=(getWidth(mapView));
        tab1View.setX(tabOthers);
        tab1View.setY(tab1Y);
        tab2View.setX(tabOthers);
        tab2View.setY(tab2Y);
        tab3View.setX(tabOthers);
        tab3View.setY(tab3Y);
        tab4View.setX(tabOthers);
        tab4View.setY(tab4Y);

        tab1View.setFitWidth(tab1size);
        tab2View.setFitWidth(tab2Size);
        tab3View.setFitWidth(tab3Size);
        tab4View.setFitWidth(tab4Size);
        tab5View.setFitHeight(height9-mapH);
        cardback00View.setFitWidth(powerupsSize);
        cardback01View.setFitWidth(ammoSize);
        cardback01View.setRotate(ammo2rotation);

        //double tab1droplet1Y = (getHeight(tab1View)*0.2); //riga fino a 4
        //double tab1droplet1X = (width16*((0.6220+(0.0215*6)))); //colonna fino a 10
        double tabDropletY0 = height9*0.07;
        double tabDropletX0 = width16*0.64;
        double tabDropletOffY = height9*0.175;
        double tabDropletOffX = width16*0.022;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <10;j++){
                ImageView dropletView = new ImageView(droplet);
                dropletView.setX(tabDropletX0+tabDropletOffX*j);
                dropletView.setY(tabDropletY0+tabDropletOffY*i);
                dropletView.setPreserveRatio(true);
                dropletView.setFitWidth(tab1size*0.04);
                root.getChildren().add(dropletView);
            }
        }

        double buttonEdge = height9*0.16;
        double buttonX0 = width16*0.1;
        double buttonY0 = height9*0.18;
        double buttonPadding = height9*0.019;

        // cell buttons
        for(int i = 0; i < 4; i++){
            for(int j = 0; j< 3; j++) {
                Button cellButton = new Button();
                cellButton.setLayoutX(buttonX0 + (buttonPadding + buttonEdge)*i);
                cellButton.setLayoutY(buttonY0 + (buttonPadding + buttonEdge)*j);
                cellButton.setPrefWidth(buttonEdge);
                cellButton.setPrefHeight(buttonEdge);
                root.getChildren().add(cellButton);
                cellButton.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
                cellButton.setOnMouseEntered(mouseEvent ->
                        cellButton.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                                            "-fx-cursor: hand;"));
                cellButton.setOnMouseExited(mouseEvent ->
                        cellButton.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                                            "-fx-cursor: arrow"));
                int finalI = i;
                int finalJ = j;
                cellButton.setOnMouseClicked(mouseEvent ->
                        System.out.println("Button " + finalI + "," + finalJ +" pressed"));
            }
        }


        //lplayer pawns
        double pawnX0 = width16*0.1;
        double pawnY0 = height9*0.18;
        double pawnOffset = height9*0.19;
        double pawnInternalOffset = height9*0.042;
        double pawnSize = height9*0.045;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j< 3; j++) {
                for(int k = 0; k < 5; k++) {
                    ImageView pawnView = new ImageView(pawn);
                    pawnView.setX(pawnX0+ pawnOffset*i+ pawnInternalOffset*(k%3));
                    pawnView.setY(pawnY0+ pawnOffset*j+ pawnInternalOffset*(k/3));
                    pawnView.setPreserveRatio(true);
                    pawnView.setFitWidth(pawnSize);
                    root.getChildren().add(pawnView);
                }
            }
        }


        // ammo cards
        double ammoX0 = width16*0.12;
        double ammoY0 =  height9*0.27;
        double ammoOffset  = height9*0.19;
        double ammoCardSize = height9*0.05;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j< 3; j++) {
                ImageView ammoView = new ImageView(ammo);
                ammoView.setX(ammoX0+ammoOffset*i);
                ammoView.setY(ammoY0+ammoOffset*j);
                ammoView.setPreserveRatio(true);
                ammoView.setFitWidth(ammoCardSize);
                root.getChildren().add(ammoView);
            }
        }



    }
    public static void main(String args[]) {
        launch(args);
    }


    private Double getWidth(ImageView i){
        double ar = i.getImage().getWidth()/i.getImage().getHeight();
        System.out.println("fit width: " + i.getFitWidth());
        System.out.println("calc width: " +  i.getFitHeight()*ar);
        return i.getFitWidth() > i.getFitHeight()*ar || i.getFitWidth() == 0 ? i.getFitHeight()*ar :i.getFitWidth() ;
    }

    private Double getHeight(ImageView i){
        double ar = i.getImage().getWidth()/i.getImage().getHeight();
        return i.getFitHeight() > i.getFitWidth()/ar || i.getFitHeight() == 0 ? i.getFitWidth()/ar : i.getFitHeight();
    }

}