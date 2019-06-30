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
    private final static String TAB3 = "/img/tabs/white.png";
    private final static String TAB4 = "/img/tabs/yellow.png";
    private final static String TAB5 = "/img/tabs/blueback.png";
    //private final static String TAB6 = "/img/tabs/purpleback.png";
    //private final static String TAB7 = "/img/tabs/greenback.png";
    //private final static String TAB8 = "/img/tabs/whiteback.png";
    //private final static String TAB9 = "/img/tabs/yellowback.png";
    // private final static String TAB10 = "/img/tabs/blueback.png";
    // private final static String TAB11 = "/img/tabs/purpleback.png";
    // private final static String TAB12 = "/img/tabs/greenback.png";
    // private final static String TAB13 = "/img/tabs/whiteback.png";
    // private final static String TAB14 = "/img/tabs/yellowback.png";
    // private final static String TAB15 = "/img/tabs/blueback.png";
    private final static String CARDBACK00 = "/img/cards/powerup-back.png";
    private final static String CARDBACK01 = "/img/cards/grenade launcher.png";
    private final static String DROPLET = "/img/droplets/dropblue.png";
    private final static String PAWN = "/img/pawns/blue.png";
    private final static String AMMO = "/img/ammo/redblueblue.png";
    private final static String BOARDINO = "/img/tabs/boardino.png";
    private final static String MYDASHBOARD = "/img/tabs/mydashboard.png";
    public static final String SKULL = "/img/others/skull.png";
    public static final String SQUARE = "/img/squares/yellow.png";
    public static final String SELECTSPAWN = "/img/others/spawn.jpeg";
    public static final String BUTTONRESOURCES= "/img/others/buttonresources.png";

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
        URL urlSquare = getClass().getResource(SQUARE);
        Image square = new Image(new FileInputStream(urlSquare.getFile().replace("%20"," ")));
        URL urlSelectSpawn = getClass().getResource(SELECTSPAWN);
        Image selectspawn = new Image (new FileInputStream(urlSelectSpawn.getFile().replace("%20", " ")));
        URL urlButtonResource= getClass().getResource(BUTTONRESOURCES);
        Image buttonresources= new Image(new FileInputStream(urlButtonResource.getFile().replace("%20", " ")));




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
        double powerupsDeckSize= height9*0.07;
        double ammoDeckX= (width16*0.523);
        double ammoDeckY= (height9*0.215);
        double ammoDeckSize= height9*0.10;

        ImageView powerupsDeckView= new ImageView(cardback00);
        powerupsDeckView.setX(powerupsDeckX);
        powerupsDeckView.setY(powerupsDeckY);
        powerupsDeckView.setFitWidth(powerupsDeckSize);
        powerupsDeckView.setPreserveRatio(true);
        root.getChildren().add(powerupsDeckView);

        ImageView ammoDeckView= new ImageView(cardback01);
        ammoDeckView.setX(ammoDeckX);
        ammoDeckView.setY(ammoDeckY);
        ammoDeckView.setFitWidth(ammoDeckSize);
        ammoDeckView.setPreserveRatio(true);
        root.getChildren().add(ammoDeckView);



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


        //death track (droplets)
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

        //droplets status bar x4
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

        //Squares status bar x4
        double tabSquareY0 = height9*0.07;
        double tabSquareX0 = width16*0.93;
        double tabSquareOffY = height9*0.175;
        double tabSquareOffX = width16*0.022;
        double tabSquareOffY2 = height9*0.03;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <3;j++){
                for(int k =0; k<3; k++){
                    ImageView SquareView = new ImageView(square);
                    SquareView.setX(tabSquareX0+tabSquareOffX*j);
                    SquareView.setY(tabSquareY0+tabSquareOffY*i+tabSquareOffY2*k);
                    SquareView.setPreserveRatio(true);
                    SquareView.setFitWidth(tabLeftSize*0.04);
                    root.getChildren().add(SquareView);
                }
            }
        }
//droplet up side
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
        //Cardback00 up side
        double tabCardback00Y0 = height9*0.005;
        double tabCardback00X0 = width16*0.63;
        double tabCardback00OffY = height9*0.175;
        double tabCardback00OffX = width16*0.022;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <3;j++){
                ImageView Cardback00View = new ImageView(cardback00);
                Cardback00View.setX(tabCardback00X0+tabCardback00OffX*j);
                Cardback00View.setY(tabCardback00Y0+tabCardback00OffY*i);
                Cardback00View.setPreserveRatio(true);
                Cardback00View.setFitWidth(tabLeftSize*0.04);
                root.getChildren().add(Cardback00View);
            }
        }





        //cardback01 up side
        double tabCardback01Y0 = height9*0.005;
        double tabCardback01X0 = width16*0.70;
        double tabCardback01OffY = height9*0.175;
        double tabCardback01OffX = width16*0.022;

        for(int i = 0; i<4; i++){
            for(int j = 0 ; j <3;j++){
                ImageView Cardback01View = new ImageView(cardback01);
                Cardback01View.setX(tabCardback01X0+tabCardback01OffX*j);
                Cardback01View.setY(tabCardback01Y0+tabCardback01OffY*i);
                Cardback01View.setPreserveRatio(true);
                Cardback01View.setFitWidth(tabLeftSize*0.04);
                root.getChildren().add(Cardback01View);
            }
        }





        //skull others tab
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

        //skull my tab
        double mytabSkullY0 = height9*0.94;
        double mytabSkullX0 = width16*0.10;
        double mytabSkullOffY = height9*0.175;
        double mytabSkullOffX = width16*0.022;
        double mytabSkullSize= tabLeftSize*0.08;

        for(int j = 0 ; j <6;j++){
            ImageView skullView = new ImageView(skull);
            skullView.setX(mytabSkullX0+mytabSkullOffX*j);
            skullView.setY(mytabSkullY0);
            skullView.setPreserveRatio(true);
            skullView.setFitWidth(tabSkullSize);
            root.getChildren().add(skullView);
        }

        //droplet mex 3 up my tab
        double mytabDropletUPY0 = height9*0.8;
        double mytabDropletUPX0 = width16*0.26;
        double mytabDropletOffUPY = height9*0.175;
        double mytabDropletOffUPX = width16*0.022;

        for(int j = 0 ; j <3;j++){
            ImageView dropletView = new ImageView(droplet);
            dropletView.setX(mytabDropletUPX0+mytabDropletOffUPX*j);
            dropletView.setY(mytabDropletUPY0);
            dropletView.setPreserveRatio(true);
            dropletView.setFitWidth(tabLeftSize*0.04);
            root.getChildren().add(dropletView);
        }

        //droplet my tab max 12
        double mytabDropletY0 = height9*0.875;
        double mytabDropletX0 = width16*0.05;
        double mytabDropletOffY = height9*0.21;
        double mytabDropletOffX = width16*0.025;

        for(int j = 0 ; j <12;j++){
            ImageView dropletView = new ImageView(droplet);
            dropletView.setX(mytabDropletX0+mytabDropletOffX*j);
            dropletView.setY(mytabDropletY0);
            dropletView.setPreserveRatio(true);
            dropletView.setFitWidth(tabLeftSize*0.045);
            root.getChildren().add(dropletView);
        }

        //Squares my tab
        double myTabSquareY0 = height9*0.9;
        double myTabSquareX0 = width16*0.38;
        double myTabSquareOffY = height9*0.175;
        double myTabSquareOffX = width16*0.022;
        double myTabSquareOffY2 = height9*0.03;

        for(int j = 0 ; j <3;j++){
            for(int k =0; k<3; k++){
                ImageView SquareView = new ImageView(square);
                SquareView.setX(myTabSquareX0+myTabSquareOffX*j);
                SquareView.setY(myTabSquareY0+myTabSquareOffY2*k);
                SquareView.setPreserveRatio(true);
                SquareView.setFitWidth(tabLeftSize*0.04);
                root.getChildren().add(SquareView);
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

    /*
        //my tab button trasparent PURPLE & PURBLEBACKFIRE
        double tabButtonSizeX = height9*0.05;
        double tabButtonSizeY= height9*0.02;
        double tabButtonX0 = width16*0;
        double tabButtonY0 = height9*0.83;
        double tabButtonPadding = height9*0.025;

        // cell tabButtons PURPLE & PURBLEBACKFIRE
            for(int j = 0; j< 3; j++) {
                Button celltabButton = new Button();
                celltabButton.setLayoutX(tabButtonX0);
                celltabButton.setLayoutY(tabButtonY0 + (tabButtonPadding)*j);
                celltabButton.setMinHeight(0);
                celltabButton.setPrefHeight(tabButtonSizeY);
                celltabButton.setPrefWidth(tabButtonSizeX);
                root.getChildren().add(celltabButton);
                celltabButton.setStyle("-fx-background-color: rgba(20,20,20,0)");
                celltabButton.setOnMouseEntered(mouseEvent ->
                        celltabButton.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                                "-fx-cursor: hand;"));
                celltabButton.setOnMouseExited(mouseEvent ->
                        celltabButton.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                                "-fx-cursor: arrow"));
                //int finalI = i;
                int finalJ = j;
                celltabButton.setOnMouseClicked(mouseEvent ->
                        System.out.println("myTabButton " + "," + finalJ +" pressed"));
            }

        //my tab Button2 trasparent
        double tabButton2SizeX = height9*0.05;
        double tabButton2SizeY= height9*0.02;
        double tabButton2X0 = width16*0;
        double tabButton2Y0 = height9*0.94;
        double tabButton2Padding = height9*0.025;

        // cell tabButton2s
        for(int j = 0; j< 2; j++) {
            Button celltabButton2 = new Button();
            celltabButton2.setLayoutX(tabButton2X0);
            celltabButton2.setLayoutY(tabButton2Y0 + (tabButton2Padding)*j);
            celltabButton2.setMinHeight(0);
            celltabButton2.setPrefHeight(tabButton2SizeY);
            celltabButton2.setPrefWidth(tabButton2SizeX);
            root.getChildren().add(celltabButton2);
            celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,0)");
            celltabButton2.setOnMouseEntered(mouseEvent ->
                    celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                            "-fx-cursor: hand;"));
            celltabButton2.setOnMouseExited(mouseEvent ->
                    celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                            "-fx-cursor: arrow"));
            //int finalI = i;
            int finalJ = j;
            celltabButton2.setOnMouseClicked(mouseEvent ->
                    System.out.println("myTabButton2 " + "," + finalJ +" pressed"));
        }
*/

        //my tab button trasparent PURPLEBACK
        double tabButtonSizeX = height9*0.05;
        double tabButtonSizeY= height9*0.03;
        double tabButtonX0 = width16*0;
        double tabButtonY0 = height9*0.845;
        double tabButtonPadding = height9*0.03;

        // cell tabButtons PURPLEBACK
        for(int j = 0; j< 3; j++) {
            Button celltabButton = new Button();
            celltabButton.setLayoutX(tabButtonX0);
            celltabButton.setLayoutY(tabButtonY0 + (tabButtonPadding)*j);
            celltabButton.setMinHeight(0);
            celltabButton.setPrefHeight(tabButtonSizeY);
            celltabButton.setPrefWidth(tabButtonSizeX);
            root.getChildren().add(celltabButton);
            celltabButton.setStyle("-fx-background-color: rgba(20,20,20,1)");
            celltabButton.setOnMouseEntered(mouseEvent ->
                    celltabButton.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                            "-fx-cursor: hand;"));
            celltabButton.setOnMouseExited(mouseEvent ->
                    celltabButton.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                            "-fx-cursor: arrow"));
            //int finalI = i;
            int finalJ = j;
            celltabButton.setOnMouseClicked(mouseEvent ->
                    System.out.println("myTabButton " + "," + finalJ +" pressed"));
        }

        //my tab Button2 trasparent PURPLEBACK
        double tabButton2SizeX = height9*0.03;
        double tabButton2SizeY= height9*0.03;
        double tabButton2X0 = width16*0.01;
        double tabButton2Y0 = height9*0.95;
        double tabButton2Padding = height9*0.025;

        // cell tabButton2s PURPLE BACK
        for(int j = 0; j< 1; j++) {
            Button celltabButton2 = new Button();
            celltabButton2.setLayoutX(tabButton2X0);
            celltabButton2.setLayoutY(tabButton2Y0 + (tabButton2Padding)*j);
            celltabButton2.setMinHeight(0);
            celltabButton2.setPrefHeight(tabButton2SizeY);
            celltabButton2.setPrefWidth(tabButton2SizeX);
            root.getChildren().add(celltabButton2);
            celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,1)");
            celltabButton2.setOnMouseEntered(mouseEvent ->
                    celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                            "-fx-cursor: hand;"));
            celltabButton2.setOnMouseExited(mouseEvent ->
                    celltabButton2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                            "-fx-cursor: arrow"));
            //int finalI = i;
            int finalJ = j;
            celltabButton2.setOnMouseClicked(mouseEvent ->
                    System.out.println("myTabButton2 " + "," + finalJ +" pressed"));
        }

/*
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
        */

        //rooms button

        //size of type button
        double n123buttonH= height9*0.16;
        double n3buttonW = height9*0.537;
        double n2buttonW = height9*0.358;
        double n1buttonW = height9*0.16;
        double n2x2buttonWH= height9*0.358;
        double n2rotatebuttonH=height9*0.358; //vertical button

        //position square map 1
        double blue1X= width16*0.1; //x=0
        double blue1Y= height9*0.18;// y=0
        double red1X= width16*0.1; // x=0
        double red1Y= height9*0.179+height9*0.18;// y=1
        double grey1X=(width16*0.1)+(height9*0.16); // x=1
        double grey1Y=height9*0.538; //y=2
        double yellow1X=(height9*0.537)+(width16*0.1); //x=3
        double yellow1Y=height9*0.359; //y=1
        //position square map 2
        double blue2X= width16*0.1; //x=0
        double blue2Y= height9*0.18;// y=0
        double red2X=width16*0.1; //x=1
        double red2Y=height9*0.179+height9*0.18;// y=1
        double green2X=(height9*0.537)+(width16*0.1);
        double green2Y=height9*0.18;
        double grey2X=(width16*0.1)+(height9*0.16);
        double grey2Y=height9*0.538;
        double yellow2X=(height9*0.358)+(width16*0.1);
        double yellow2Y=(height9*0.179)+(height9*0.18);
        //position square map 3
        double red3X=width16*0.1;
        double red3Y=height9*0.18;
        double blue3X=(width16*0.1)+(height9*0.16);
        double blue3Y=height9*0.18;
        double green3X=(height9*0.537)+(width16*0.1);
        double green3Y=height9*0.18;
        double yellow3X=(height9*0.358)+(width16*0.1);
        double yellow3Y=(height9*0.179)+(height9*0.18);
        double grey3X=width16*0.1;
        double grey3Y=height9*0.538;
        double purple3X=(width16*0.1)+(height9*0.16);
        double purple3Y=height9*0.179+height9*0.18;
        //position square map 4
        double red4X=width16*0.1;
        double red4Y=height9*0.18;
        double blue4X=(width16*0.1)+(height9*0.16);
        double blue4Y=height9*0.18;
        double purple4X=(width16*0.1)+(height9*0.16);
        double purple4Y=height9*0.179+height9*0.18;
        double yellow4X=(height9*0.537)+(width16*0.1);
        double yellow4Y=height9*0.359;
        double grey4X=width16*0.1;
        double grey4Y=height9*0.538;

/*
 //MAP 1
        //red button map 1
        Button red1 = new Button();
        red1.setLayoutX(red1X);
        red1.setLayoutY(red1Y);
        red1.setPrefWidth(n3buttonW);
        red1.setPrefHeight(n123buttonH);
        root.getChildren().add(red1);
        red1.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        red1.setOnMouseEntered(mouseEvent ->
                red1.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        red1.setOnMouseExited(mouseEvent ->
                red1.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        red1.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //blue button map 1
        Button blue1 = new Button();
        blue1.setLayoutX(blue1X);
        blue1.setLayoutY(blue1Y);
        blue1.setPrefWidth(n3buttonW);
        blue1.setPrefHeight(n123buttonH);
        root.getChildren().add(blue1);
        blue1.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        blue1.setOnMouseEntered(mouseEvent ->
                blue1.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        blue1.setOnMouseExited(mouseEvent ->
                blue1.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        blue1.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //grey button map 1
        Button grey1 = new Button();
        grey1.setLayoutX(grey1X);
        grey1.setLayoutY(grey1Y);
        grey1.setPrefWidth(n2buttonW);
        grey1.setPrefHeight(n123buttonH);
        root.getChildren().add(grey1);
        grey1.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        grey1.setOnMouseEntered(mouseEvent ->
                grey1.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        grey1.setOnMouseExited(mouseEvent ->
                grey1.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        grey1.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //yellow button map 1
        Button yellow1 = new Button();
        yellow1.setLayoutX(yellow1X);
        yellow1.setLayoutY(yellow1Y);
        yellow1.setPrefWidth(n123buttonH);
        yellow1.setPrefHeight(n2rotatebuttonH);
        root.getChildren().add(yellow1);
        yellow1.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        yellow1.setOnMouseEntered(mouseEvent ->
                yellow1.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        yellow1.setOnMouseExited(mouseEvent ->
                yellow1.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        yellow1.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

*/

/*
//MAP 2
        //grey button map 2
        Button grey2 = new Button();
        grey2.setLayoutX(grey2X);
        grey2.setLayoutY(grey2Y);
        grey2.setPrefWidth(n123buttonH);
        grey2.setPrefHeight(n123buttonH);
        root.getChildren().add(grey2);
        grey2.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        grey2.setOnMouseEntered(mouseEvent ->
                grey2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        grey2.setOnMouseExited(mouseEvent ->
                grey2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        grey2.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));



        //yellow button map 2
        Button yellow2 = new Button();
        yellow2.setLayoutX(yellow2X);
        yellow2.setLayoutY(yellow2Y);
        yellow2.setPrefWidth(n2x2buttonWH);
        yellow2.setPrefHeight(n2x2buttonWH);
        root.getChildren().add(yellow2);
        yellow2.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        yellow2.setOnMouseEntered(mouseEvent ->
                yellow2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        yellow2.setOnMouseExited(mouseEvent ->
                yellow2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        yellow2.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

//green button map 2
        Button green2 = new Button();
        green2.setLayoutX(green2X);
        green2.setLayoutY(green2Y);
        green2.setPrefWidth(n123buttonH);
        green2.setPrefHeight(n123buttonH);
        root.getChildren().add(green2);
        green2.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        green2.setOnMouseEntered(mouseEvent ->
                green2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        green2.setOnMouseExited(mouseEvent ->
                green2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        green2.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

//red button map 2
        Button red2 = new Button();
        red2.setLayoutX(red2X);
        red2.setLayoutY(red2Y);
        red2.setPrefWidth(n2buttonW);
        red2.setPrefHeight(n123buttonH);
        root.getChildren().add(red2);
        red2.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        red2.setOnMouseEntered(mouseEvent ->
                red2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        red2.setOnMouseExited(mouseEvent ->
                red2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        red2.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //blue button map 2
        Button blue2 = new Button();
        blue2.setLayoutX(blue2X);
        blue2.setLayoutY(blue2Y);
        blue2.setPrefWidth(n3buttonW);
        blue2.setPrefHeight(n123buttonH);
        root.getChildren().add(blue2);
        blue2.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        blue2.setOnMouseEntered(mouseEvent ->
                blue2.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        blue2.setOnMouseExited(mouseEvent ->
                blue2.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        blue2.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

*/
/*
//MAP 3
        //grey button map 3
        Button grey3 = new Button();
        grey3.setLayoutX(grey3X);
        grey3.setLayoutY(grey3Y);
        grey3.setPrefWidth(n2buttonW);
        grey3.setPrefHeight(n123buttonH);
        root.getChildren().add(grey3);
        grey3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        grey3.setOnMouseEntered(mouseEvent ->
                grey3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        grey3.setOnMouseExited(mouseEvent ->
                grey3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        grey3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));


        //purple button map 3
        Button purple3 = new Button();
        purple3.setLayoutX(purple3X);
        purple3.setLayoutY(purple3Y);
        purple3.setPrefWidth(n123buttonH);
        purple3.setPrefHeight(n123buttonH);
        root.getChildren().add(purple3);
        purple3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        purple3.setOnMouseEntered(mouseEvent ->
                purple3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        purple3.setOnMouseExited(mouseEvent ->
                purple3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        purple3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //green button map 3
        Button green3 = new Button();
        green3.setLayoutX(green3X);
        green3.setLayoutY(green3Y);
        green3.setPrefWidth(n123buttonH);
        green3.setPrefHeight(n123buttonH);
        root.getChildren().add(green3);
        green3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        green3.setOnMouseEntered(mouseEvent ->
                green3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        green3.setOnMouseExited(mouseEvent ->
                green3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        green3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

  //blue button map 3
        Button blue3 = new Button();
        blue3.setLayoutX(blue3X);
        blue3.setLayoutY(blue3Y);
        blue3.setPrefWidth(n2buttonW);
        blue3.setPrefHeight(n123buttonH);
        root.getChildren().add(blue3);
        blue3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        blue3.setOnMouseEntered(mouseEvent ->
                blue3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        blue3.setOnMouseExited(mouseEvent ->
                blue3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        blue3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));


        //red button map 3
        Button red3 = new Button();
        red3.setLayoutX(red3X);
        red3.setLayoutY(red3Y);
        red3.setPrefWidth(n123buttonH);
        red3.setPrefHeight(n2rotatebuttonH);
        root.getChildren().add(red3);
        red3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        red3.setOnMouseEntered(mouseEvent ->
                red3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        red3.setOnMouseExited(mouseEvent ->
                red3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        red3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //yellow button map 3
        Button yellow3 = new Button();
        yellow3.setLayoutX(yellow3X);
        yellow3.setLayoutY(yellow3Y);
        yellow3.setPrefWidth(n2x2buttonWH);
        yellow3.setPrefHeight(n2x2buttonWH);
        root.getChildren().add(yellow3);
        yellow3.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        yellow3.setOnMouseEntered(mouseEvent ->
                yellow3.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        yellow3.setOnMouseExited(mouseEvent ->
                yellow3.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        yellow3.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

*/

//MAP 4
        //blue button map 4
        Button blue4 = new Button();
        blue4.setLayoutX(blue4X);
        blue4.setLayoutY(blue4Y);
        blue4.setPrefWidth(n2buttonW);
        blue4.setPrefHeight(n123buttonH);
        root.getChildren().add(blue4);
        blue4.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        blue4.setOnMouseEntered(mouseEvent ->
                blue4.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        blue4.setOnMouseExited(mouseEvent ->
                blue4.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        blue4.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //purple button map 4
        Button purple4 = new Button();
        purple4.setLayoutX(purple4X);
        purple4.setLayoutY(purple4Y);
        purple4.setPrefWidth(n2buttonW);
        purple4.setPrefHeight(n123buttonH);
        root.getChildren().add(purple4);
        purple4.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        purple4.setOnMouseEntered(mouseEvent ->
                purple4.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        purple4.setOnMouseExited(mouseEvent ->
                purple4.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        purple4.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //red button map 4
        Button red4 = new Button();
        red4.setLayoutX(red4X);
        red4.setLayoutY(red4Y);
        red4.setPrefWidth(n123buttonH);
        red4.setPrefHeight(n2rotatebuttonH);
        root.getChildren().add(red4);
        red4.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        red4.setOnMouseEntered(mouseEvent ->
                red4.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        red4.setOnMouseExited(mouseEvent ->
                red4.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        red4.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //yellow button map 4
        Button yellow4 = new Button();
        yellow4.setLayoutX(yellow4X);
        yellow4.setLayoutY(yellow4Y);
        yellow4.setPrefWidth(n123buttonH);
        yellow4.setPrefHeight(n2rotatebuttonH);
        root.getChildren().add(yellow4);
        yellow4.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        yellow4.setOnMouseEntered(mouseEvent ->
                yellow4.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        yellow4.setOnMouseExited(mouseEvent ->
                yellow4.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        yellow4.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));

        //grey button map 4
        Button grey4 = new Button();
        grey4.setLayoutX(grey4X);
        grey4.setLayoutY(grey4Y);
        grey4.setPrefWidth(n3buttonW);
        grey4.setPrefHeight(n123buttonH);
        root.getChildren().add(grey4);
        grey4.setStyle("-fx-background-color: rgba(20,20,20,0.0)");
        grey4.setOnMouseEntered(mouseEvent ->
                grey4.setStyle("-fx-background-color: rgba(20,20,20,0.3);" +
                        "-fx-cursor: hand;"));
        grey4.setOnMouseExited(mouseEvent ->
                grey4.setStyle("-fx-background-color: rgba(20,20,20,0.0);" +
                        "-fx-cursor: arrow"));
        grey4.setOnMouseClicked(mouseEvent ->
                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH)));



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

        //button resources
        ImageView buttonresourceView = new ImageView(buttonresources);
        double buttonresourcesSize= width16*0.1;
        double buttonresourcesX=width16*0.03;
        double buttonresourcesY=height9*0.73;
        buttonresourceView.setFitWidth(buttonresourcesSize);
        buttonresourceView.setX(buttonresourcesX);
        buttonresourceView.setY(buttonresourcesY);
        buttonresourceView.setPreserveRatio(true);
        root.getChildren().add(buttonresourceView);


        /*

// select spawn popup
        ImageView selectspawnView = new ImageView(selectspawn);
        double selectSpawnSize = width16*0.6;
        double selectSpawnX0= (width16-selectSpawnSize)/2;
        selectspawnView.setFitWidth(selectSpawnSize);
        selectspawnView.setX(selectSpawnX0);


        double selectSpawnY0= (height9-getHeight(selectspawnView))/2;
        selectspawnView.setY(selectSpawnY0);

        selectspawnView.setPreserveRatio(true);
        root.getChildren().add(selectspawnView);

        //powerups select spawn

        double powerupOffsetSelectSpawnX = height9* 0.215;
        double powerupSelectSpawnX=width16*0.21;
        double powerupBaseSelectSpawnY=height9*0.45;
        double powerupSelectSpawnSize=height9*0.17;


        for(int i = 0; i<5; i++){
            {
                ImageView cardback00SelectSpawnView = new ImageView(cardback00);
                cardback00SelectSpawnView.setY(powerupBaseSelectSpawnY);
                cardback00SelectSpawnView.setX(powerupSelectSpawnX+powerupOffsetSelectSpawnX*i);
                cardback00SelectSpawnView.setPreserveRatio(true);
                cardback00SelectSpawnView.setFitWidth(powerupSelectSpawnSize);
                root.getChildren().add(cardback00SelectSpawnView);
            }
        }
           */

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