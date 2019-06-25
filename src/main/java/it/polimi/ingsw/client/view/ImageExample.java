package it.polimi.ingsw.client.view;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
public class ImageExample extends Application {

    private final static String MAP1 = "/img/maps/4.png";
    //private final static String MAP2 = "/img/maps/2.png";
    //private final static String MAP3 = "/img/maps/3.png";
    //private final static String MAP4 = "/img/maps/4.png";

    private final static String TAB1 = "/img/tabs/purple.png";
    private final static String TAB2 = "/img/tabs/green.png";
    private final static String TAB3 = "/img/tabs/grey.png";
    private final static String TAB4 = "/img/tabs/yellow.png";
    private final static String TAB5 = "/img/tabs/blue.png";
    //private final static String TAB6 = "/img/tabs/purpleback.png";
    //private final static String TAB7 = "/img/tabs/greenback.png";
    //private final static String TAB8 = "/img/tabs/greyback.png";
    //private final static String TAB9 = "/img/tabs/yellowback.png";
    // private final static String TAB10 = "/img/tabs/blueback.png";
   // private final static String TAB11 = "/img/tabs/purpleback.png";
   // private final static String TAB12 = "/img/tabs/greenback.png";
   // private final static String TAB13 = "/img/tabs/greyback.png";
   // private final static String TAB14 = "/img/tabs/yellowback.png";
   // private final static String TAB15 = "/img/tabs/blueback.png";
    private final static String CARDBACK00 = "/img/cards/AD_powerups_IT_02.png";
    private final static String CARDBACK01 = "/img/cards/AD_weapons_IT_0212.png";
    private final static String DROPLET = "/img/droplets/dropblue.png";
    private final static String PAWN = "/img/pawns/blue.png";
    private final static String AMMO = "/img/ammo/AD_ammo_0434.png";
    private final static String BOARDINO = "/img/tabs/boardino.png";
    private final static String MYDASHBOARD = "/img/tabs/mydashboard.png";
    public static final String SKULL = "/img/others/skull.png";

    public static final double ISTANT_RESIZE = 1.01;
    public static final double DELAY_IN = 0.4;
    public static final int DELEYED_RESIZE = 2;
    public static final double DELAY_OUT = 0.3;

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
        URL urlMydashboard = getClass().getResource(MYDASHBOARD);
        Image mydashboard = new Image(new FileInputStream(urlMydashboard.getFile().replace("%20", " ")));
        URL urlSkull = getClass().getResource(SKULL);
        Image skull = new Image(new FileInputStream(urlSkull.getFile().replace("%20", " ")));
        ImageView mapView = new ImageView(map);
        ImageView mytabView = new ImageView(tab5);
       // ImageView cardback00View = new ImageView(cardback00);
        ImageView mydashboardView = new ImageView(mydashboard);
        BorderPane borders = new BorderPane();
        Pane root = new Pane(mapView, mytabView, mydashboardView);

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
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if(newValue != null && !newValue.booleanValue())
                    stage.setFullScreen(true);

            }
        });
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
      //  double boardinoSize = (width16-getWidth(mapView));
        double mydashboardSize =(width16-getWidth(mytabView));

        // position on X and Y

        double mapX=0;
        double mapY=0;
        double tab0X=0;//my tab X
        double tab0Y=(mapH); //my tab Y
        double powerupsDeckX= (width16*0.54);
        double powerupsDeckY= (height9*0.04);
        double ammoDeckX= (width16*0.523);
        double ammoDeckY= (height9*0.1905);


        //Setting the position of the map

      //  cardback00View.setX(powerupsDeckX);
      //  cardback00View.setY(powerupsDeckY);

        mapView.setX(mapX);
        mapView.setY(mapY);

        mytabView.setX(tab0X);
        mytabView.setY(tab0Y);

        //Setting the preserve ratio of the map view
        mapView.setPreserveRatio(true);
      //  cardback00View.setPreserveRatio(true);
        mydashboardView.setPreserveRatio(true);
        mytabView.setPreserveRatio(true);

        mapView.setFitHeight(mapH);
        mytabView.setFitHeight(height9-mapH);
       // cardback00View.setFitWidth(powerupsSize);
       // boardinoView.setFitWidth(tab4Size);
        mydashboardView.setFitWidth(width16-getWidth(mytabView));
        mydashboardView.setX(getWidth(mytabView));
        mydashboardView.setY(mapH);

        //others's tab for
        double tabLeftOffsetY = height9 * 0.175;
        double tabLeftBaseX = getWidth(mapView);
        double tabLeftBaseY = 0;
        double tabLeftSize= (width16-getWidth(mapView));
        for (int i=0; i<4; i++){
            {
                ImageView tab1View = new ImageView(tab1);
                tab1View.setX(tabLeftBaseX);
                tab1View.setY(tabLeftBaseY+tabLeftOffsetY*i);
                tab1View.setFitWidth(tabLeftSize);
                tab1View.setPreserveRatio(true);
                root.getChildren().add(tab1View);


            }
        }


        //droplet for
        double dropletOffsetX = height9 * 0.045;
        double dropletOffsetX2 = height9 * 0.008;
        double dropletBaseX = width16*0.042;
        double dropletBaseY = height9*0.032;
        double dropletSize1= width16*0.024;
        for (int i=0; i<8; i++){
            {
                ImageView dropletView  = new ImageView(droplet);
                ImageView droplet2View = new ImageView(droplet);
                droplet2View.setY(dropletBaseY);
                droplet2View.setX(dropletBaseX+dropletOffsetX*i+dropletOffsetX2);
                dropletView.setY(dropletBaseY);
                dropletView.setX(dropletBaseX+dropletOffsetX*i);
                dropletView.setFitWidth(dropletSize1);
                droplet2View.setFitWidth(dropletSize1);
                droplet2View.setPreserveRatio(true);
                dropletView.setPreserveRatio(true);
                root.getChildren().add(droplet2View);
                root.getChildren().add(dropletView);


            }
        }






         //double tab1droplet1Y = (getHeight(tab1View)*0.2); //riga fino a 4
        //double tab1droplet1X = (width16*((0.6220+(0.0215*6)))); //colonna fino a 10
        double tabDropletY0 = height9*0.07;
        double tabDropletX0 = width16*0.64;
        double tabDropletOffY = height9*0.175;
        double tabDropletOffX = width16*0.022;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <12;j++){
                ImageView dropletView = new ImageView(droplet);
                dropletView.setX(tabDropletX0+tabDropletOffX*j);
                dropletView.setY(tabDropletY0+tabDropletOffY*i);
                dropletView.setPreserveRatio(true);
                dropletView.setFitWidth(tabLeftSize*0.04);
                root.getChildren().add(dropletView);
            }
        }

        double tabDropletUPY0 = height9*0;
        double tabDropletUPX0 = width16*0.82;
        double tabDropletOffUPY = height9*0.175;
        double tabDropletOffUPX = width16*0.022;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <3;j++){
                ImageView dropletView = new ImageView(droplet);
                dropletView.setX(tabDropletUPX0+tabDropletOffUPX*j);
                dropletView.setY(tabDropletUPY0+tabDropletOffUPY*i);
                dropletView.setPreserveRatio(true);
                dropletView.setFitWidth(tabLeftSize*0.04);
                root.getChildren().add(dropletView);
            }
        }

        double tabSkullY0 = height9*0.125;
        double tabSkullX0 = width16*0.675;
        double tabSkullOffY = height9*0.175;
        double tabSkullOffX = width16*0.022;
        double tabSkullSize= tabLeftSize*0.08;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <6;j++){
                ImageView skullView = new ImageView(skull);
                skullView.setX(tabSkullX0+tabSkullOffX*j);
                skullView.setY(tabSkullY0+tabSkullOffY*i);
                skullView.setPreserveRatio(true);
                skullView.setFitWidth(tabSkullSize);
                root.getChildren().add(skullView);
            }
        }









// weapon box

        double weaponOffsetY = height9*0.115;
        double weaponBase2Y = height9*0.26;
        double ammo2X = (width16*0.017);
        double ammo2rotation= (90);
        double weaponBase3Y = height9*0.422;
        double ammo3X = (width16*0.524);
        double ammo3rotation= (-90);
        double ammo1Y = 0;
        double ammo1rotation = 0;
        double weaponBase1X = width16*0.318;

        for(int i = 0; i<3; i++){
            {
                ImageView cardback01View = new ImageView(cardback01);
                cardback01View.setX(ammo2X);
                cardback01View.setY(weaponBase2Y+weaponOffsetY*i);
                cardback01View.setPreserveRatio(true);
                cardback01View.setFitWidth(ammoSize);
                cardback01View.setRotate(ammo2rotation);
                root.getChildren().add(cardback01View);
            }
        }

        for(int i = 0; i<3; i++){
            {
                ImageView cardback01View = new ImageView(cardback01);
                cardback01View.setX(ammo3X);
                cardback01View.setY(weaponBase3Y+weaponOffsetY*i);
                cardback01View.setPreserveRatio(true);
                cardback01View.setFitWidth(ammoSize);
                cardback01View.setRotate(ammo3rotation);
                root.getChildren().add(cardback01View);
            }
        }

        for(int i = 0; i<3; i++){
            {
                ImageView cardback01View = new ImageView(cardback01);
                cardback01View.setY(ammo1Y);
                cardback01View.setX(weaponBase1X+weaponOffsetY*i);
                cardback01View.setPreserveRatio(true);
                cardback01View.setFitWidth(ammoSize);
                cardback01View.setRotate(ammo1rotation);
                root.getChildren().add(cardback01View);
            }
        }

        //weapon my dashboard

        double weaponOffsetMyDashboardX = height9 * 0.145;
        double weaponBaseMyDashboardX=width16*0.725;
        double weaponBaseMyDashboardY=height9*0.815;


        for(int i = 0; i<3; i++){
            {
                ImageView cardback01View = new ImageView(cardback01);
                cardback01View.setY(weaponBaseMyDashboardY);
                cardback01View.setX(weaponBaseMyDashboardX+weaponOffsetMyDashboardX*i);
                cardback01View.setPreserveRatio(true);
                cardback01View.setFitWidth(ammoSize);
                root.getChildren().add(cardback01View);
            }
        }

        //powerups my dashboard

        double powerupOffsetMyDashboardX = height9 * 0.15;
        double powerupMyDashboardX=width16*0.48;
        double powerupBaseMyDashboardY=height9*0.815;


        for(int i = 0; i<3; i++){
            {
                ImageView cardback00View = new ImageView(cardback00);
                cardback00View.setY(powerupBaseMyDashboardY);
                cardback00View.setX(powerupMyDashboardX+powerupOffsetMyDashboardX*i);
                cardback00View.setPreserveRatio(true);
                cardback00View.setFitWidth(ammoSize);
                root.getChildren().add(cardback00View);
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

                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH));

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

    public void setHoverEffect(ImageView i, double size){
        i.setOnMouseEntered(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {
                i.setFitWidth(size* ISTANT_RESIZE);
                PauseTransition wait = new PauseTransition(Duration.seconds(DELAY_IN));
                wait.setOnFinished((e) -> {
                    if(i.isHover()) {
                        i.setFitWidth(size * DELEYED_RESIZE);
                    }
                    else
                        i.setFitWidth(size);

                });
                wait.play();

            }

        });

        i.setOnMouseExited(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {
                PauseTransition wait = new PauseTransition(Duration.seconds(DELAY_OUT));
                wait.setOnFinished((e) -> {
                    if(!i.isHover())
                        i.setFitWidth(size);
                });
                wait.play();

            }

        });
    }

}