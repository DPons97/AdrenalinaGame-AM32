package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.client.model.Cell;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.Ammo;
import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Resource;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class GuiView extends ClientView{

    // Constants definition
    public static final double ISTANT_RESIZE = 1.01;
    public static final double DELAY_IN = 0.4;
    public static final int DELEYED_RESIZE = 2;
    public static final double DELAY_OUT = 0.3;
    public static final String RESOURCE_STRING_FIX = "_BOX";

    // map constants
    private final static String MAP = "/img/maps/";
    private final static String MAP_EXTENSION = ".png";
    private final static double MAP_X = 0;
    private final static double MAP_Y = 0;
    public static final double RELATIVE_MAP_HEIGHT = 0.8;

    // player tabs
    private final static String TAB = "/img/tabs/";
    private final static String TAB_EXTENSION = ".png";
    private final static String TAB_BACK = "back";
    private final static double TAB_OFFSET = 0.175;
    private static final double RELATIVE_TAB_HEIGHT = 0.20;
    private static final double PLAYER_TAB_X = 0;
    private static final double PLAYER_TAB_Y = RELATIVE_MAP_HEIGHT;

    // signs
    private static final String SKULL_PATH = "/img/others/skull.png";
    private static final double ENEMY_SKULL_DROPLET_Y0 = 0.125;
    private static final double ENEMY_SKULL_DROPLET_X0 = 0.675;
    private static final double ENEMY_SKULL_DROPLET_OFFSET_Y = 0.175;
    private static final double ENEMY_SKULL_DROPLET_OFFSET_X = 0.022;
    private static final double ENEMY_SKULL_SIZE = 0.08;
    private static final String DROPLET_DIR = "/img/droplets/drop";
    private static final String DROPLET_EXTENSION = ".png";
    private static final double ENEMY_DAMAGE_DROPLET_Y0 = 0.07;
    private static final double ENEMY_DAMAGE_DROPLET_X0 = 0.64;
    private static final double ENEMY_DAMAGE_DROPLET_OFFSET_Y = 0.175;
    private static final double ENEMY_DAMAGE_DROPLET_OFFSET_X = 0.022;
    private static final double ENEMY_MARK_DROPLET_Y0 = 0;
    private static final double ENEMY_MARK_DROPLET_X0 = 0.82;
    private static final double ENEMY_MARK_DROPLET_OFFSET_Y = 0.175;
    private static final double ENEMY_MARK_DROPLET_OFFSET_X = 0.022;
    private static final double ENEMY_DROPLET_SIZE = 0.04;
    private static final double DEATHTRACK_DROPLET_Y0 = 0.032;
    private static final double DEATHTRACK_DROPLET_X0 = 0.042;
    private static final double DEATHTRACK_DROPLET_OFFSET_X = 0.045;
    private static final double DEATHTRACK_DROPLET_OFFSET_X2 = 0.008;
    private static final double DEATHTRACK_DROPLET_SIZE = 0.024;
    private static final double PLAYER_SKULL_DROPLET_Y0 = 0.94;
    private static final double PLAYER_SKULL_DROPLET_X0 = 0.1;
    private static final double PLAYER_SKULL_DROPLET_OFFSET_Y = 0.175;
    private static final double PLAYER_SKULL_DROPLET_OFFSET_X = 0.022;
    private static final double PLAYER_SKULL_SIZE = 0.08;
    private static final double PLAYER_DAMAGE_DROPLET_Y0 = 0.875;
    private static final double PLAYER_DAMAGE_DROPLET_X0 = 0.05;
    private static final double PLAYER_DAMAGE_DROPLET_OFFSET_X = 0.025;
    private static final double PLAYER_MARK_DROPLET_Y0 = 0.8;
    private static final double PLAYER_MARK_DROPLET_X0 = 0.26;
    private static final double PLAYER_MARK_DROPLET_OFFSET_Y = 0.175;
    private static final double PLAYER_MARK_DROPLET_OFFSET_X = 0.022;
    private static final double PLAYER_DAMAGE_DROPLET_SIZE = 0.045;
    private static final double PLAYER_MARK_DROPLET_SIZE = 0.04;

    // enemy cards
    private static final double ENEMY_WEAPON_CARD_Y0 = 0;
    private static final double ENEMY_WEAPON_CARD_X0 = 0.7;
    private static final double ENEMY_POWERUP_CARD_Y0 = 0.005;
    private static final double ENEMY_POWERUP_CARD_X0 = 0.63;
    private static final double ENEMY_CARD_OFFSET_Y = 0.175;
    private static final double ENEMY_CARD_OFFSET_X = 0.0155;
    private static final double ENEMY_CARD_SIZE = 0.015;

    // weapons
    private final static String WEAPON_DIR = "/img/cards/";
    private final static String WEAPON_EXTENSION = ".png";
    private final static double WEAPON_CARD_SIZE = 0.055;
    // spawn points weapon cards
    private static final double SPAWN_POINT_OFF = 0.115;
    private static final double SPAWN_POINT_1_BASE_X = 0.318;
    private static final double SPAWN_POINT_1_BASE_Y = 0;
    private static final double SPAWN_POINT_1_ROTATION = 0;
    private static final double SPAWN_POINT_2_BASE_X = 0.017;
    private static final double SPAWN_POINT_2_BASE_Y = 0.26;
    private static final double SPAWN_POINT_2_ROTATION = 90;
    private static final double SPAWN_POINT_3_BASE_X = 0.524;
    private static final double SPAWN_POINT_3_BASE_Y = 0.422;
    private static final double SPAWN_POINT_3_ROTATION = -90;

    // Player pawns
    private static final double PAWN_X0 = 0.1;
    private static final double PAWN_Y0 = 0.18;
    private static final double PAWN_OFFSET = 0.19;
    private static final double PAWN_INTERNAL_OFFSET = 0.042;
    private static final double PAWN_SIZE = 0.045;
    private static final String PAWN_DIR = "/img/pawns/";
    private static final String PAWN_EXTENSION = ".png";

    // User dashboard
    private static final double DASHBOARD_WEAPON_OFFSET_X = 0.145;
    private static final double DASHBOARD_WEAPON_X= 0.725;
    private static final double DASHBOARD_WEAPON_Y = 0.815;
    private static final double DASHBOARD_POWERUP_X = 0.48;
    private static final double DASHBOARD_POWERUP_Y = 0.815;
    private static final double DASHBOARD_POWERUP_OFFSET = 0.15;
    private static final double DASHBOARD_CARDS_SIZE  =0.055;
    private static final String DASHBOARD_PATH  ="/img/tabs/mydashboard.png";

    // Ammo cards
    private static final String AMMO_DIR = "/img/ammo/";
    private static final String AMMO_EXTENSION = ".png";
    private static final double AMMO_X0 = 0.12;
    private static final double AMMO_Y0 = 0.27;
    private static final double AMMO_OFFSET = 0.19;
    private static final double AMMO_SIZE = 0.05;

    // Decks
    private static final String DECK_POWERUP_PATH = "/img/cards/powerup-back.png";
    private static final String DECK_WEAPON_PATH = "/img/cards/weapon-back.png";
    private static final double DECK_POWERUP_SIZE = 0.07;
    private static final double DECK_WEAPON_SIZE = 0.1;
    private static final double DECK_POWERUP_X = 0.54;
    private static final double DECK_POWERUP_Y = 0.04;
    private static final double DECK_WEAPON_X = 0.523;
    private static final double DECK_WEAPON_Y = 0.215;

    // resources
    private static final String RESOURCE_DIR = "/img/squares/";
    private static final String RESOURCE_EXTENSION = ".png";
    private static final double RESOURCE_ENEMY_X0 = 0.93;
    private static final double RESOURCE_ENEMY_Y0 = 0.07;
    private static final double RESOURCE_PLAYER_X0 = 0.38;
    private static final double RESOURCE_PLAYER_Y0 = 0.87;
    private static final double RESOURCE_OFFSET_X = 0.022;
    private static final double RESOURCE_OFFSET_Y = 0.03;
    private static final double RESOURCE_OFFSET_Y2 = 0.175;
    private static final double RESOURCE_SIZE = 0.02;

    // cell buttons
    private static final double CELL_BUTTON_EDGE = 0.16;
    private static final double CELL_BUTTON_X0 = 0.1;
    private static final double CELL_BUTTON_Y0 = 0.18;
    private static final double CELL_BUTTON_PADDING = 0.019;

    // tab buttons
    private static final double ACTION_BTN_SIZE_X = 0.05;
    private static final double ACTION_BTN_NO_FRENZY_SIZE_Y = 0.03;
    private static final double ACTION_BTN_FRENZY_SIZE_Y = 0.02;
    private static final double ACTION_BTN_X0 = 0;
    private static final double ACTION_BTN_NO_FRENZY_Y0 = 0.845;
    private static final double ACTION_BTN_FRENZY_Y0 = 0.83;
    private static final double ACTION_BTN_FRENZY_DOWN_SIZE_Y0 = 0.94;
    private static final double ACTION_BTN_NO_FRENZY_PADDING = 0.03;
    private static final double ACTION_BTN_FRENZY_PADDING = 0.025;

    // spawn selection popup
    private static final String SPAWN_POPUP_PATH = "/img/others/spawn.jpeg";
    private static final double SPAWN_POPUP_SIZE = 0.6;
    private static final double SPAWN_POPUP_X0 = 0.5;
    private static final double SPAWN_POPUP_Y0 = 0.5;
    private static final double SPAWN_POWERUP_X0 = 0.21;
    private static final double SPAWN_POWERUP_Y0 = 0.45;
    private static final double SPAWN_POWERUP_OFFSET_X = 0.215;
    private static final double SPAWN_POWERUP_SIZE = 0.17;

    // css effects
    private static final String STANDARD_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0), 0, 0, 0, 0);" +
                                                  "-fx-cursor: arrow";
    private static final String CLICKABLE_EFFECT =
            "-fx-effect: dropshadow(three-pass-box, rgba(225,280,82,0.85), 2, 2, 2, 2);" +
            "-fx-cursor: hand;";
    private static final String BUTTONS_EFFECT = "-fx-background-color: rgba(0,255,0,0.3);" +
                                                 "-fx-cursor: hand;";

    private double width;
    private double height;

    private List<ImageView> players;
    private List<ImageView> weapons;
    private List<ImageView> spawnWeapons;
    private List<ImageView> powerups;
    private List<ImageView> resources;

    private boolean loading;

    private List<ImageView> nodes;

    private GuiSelection selection;

    private Object lock = new Object();

    public GuiView(ClientPlayer player) {
        super(player);
        players = new ArrayList<>();
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
        resources = new ArrayList<>();
        spawnWeapons = new ArrayList<>();
        selection = new GuiSelection();
        nodes = new ArrayList<>();
    }

    /**
     * Shows the lobby
     */
    @Override
    public void showLobby(String lobby) {

        JSONObject lobbiObj = (JSONObject) JSONValue.parse(lobby);
        int nPlayers = Integer.parseInt(lobbiObj.get("n_players").toString());
        JSONArray matchesArray = (JSONArray) lobbiObj.get("matches");

        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = (GridPane) FXWindow.getPane();
            setEscExit();
            initGrid(grid);

            Text scenetitle = new Text("Lobby");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            Label label = new Label("Players online: "+nPlayers);
            grid.add(label, 0, 1);
            Label label1 = new Label("Match list:\t\t");
            grid.add(label1, 0, 2);

            Button refresh = new Button();
            refresh.setText("Refresh");

            refresh.setOnAction(e -> {
                grid.getChildren().clear();
                player.updateLobby();
            });

            ScrollPane matches = new ScrollPane();
            matches.setPrefSize(primaryStage.getWidth()*0.9, primaryStage.getHeight()*0.6);
            GridPane matchGrid = new GridPane();
            matchGrid.setHgap(0);
            matchGrid.setVgap(0);

            grid.add(matches,0,4);
            matches.setContent(matchGrid);

            if(matchesArray.size() == 0){
                Label msg = new Label("Wow, such empty...");
            }else {
                for(int i = 0; i < matchesArray.size(); i++) {
                    JSONObject match = (JSONObject) matchesArray.get(i);
                    int maxPlayers = Integer.parseInt(match.get("n_players").toString());
                    int mapID = Integer.parseInt(match.get("mapID").toString());
                    int maxDeaths = Integer.parseInt(match.get("max_deaths").toString());
                    JSONArray players = (JSONArray) match.get("players");
                    String s = "Max players: " + maxPlayers+ "   Max deaths: "+ maxDeaths + "   Map: "+mapID+"\nPlayers in game: ";
                    for(Object o: players) s = s + (o.toString()+"\t");

                    int finalI = i;

                    Button btn = new Button();
                    btn.setText(s);
                    btn.setPrefWidth(primaryStage.getWidth()*0.9);
                    matchGrid.add(btn,0,finalI);
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            player.joinGame(finalI);
                        }
                    });
                }
            }

            Button newGame= new Button();
            HBox buttons = new HBox();
            buttons.setAlignment(Pos.BOTTOM_RIGHT);
            buttons.getChildren().add(newGame);
            buttons.getChildren().add(refresh);
            buttons.setSpacing(20);
            grid.add(buttons, 0,5);
            newGame.setText("Create new game");
            newGame.setOnAction(e -> {
                player.getView().createNewGame();
            });

        });

    }

    public void createNewGame() {

        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = (GridPane) FXWindow.getPane();
            initGrid(grid);


            Text scenetitle = new Text("Create new game");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            //numero di giocatori
            // numero di morti
            // durata del turno
            // id della mappa

            Label nPlayers = new Label("Number of players:");
            grid.add(nPlayers, 0, 1);

            TextField nPlayersBox = new TextField();
            grid.add(nPlayersBox, 1, 1);

            Label nDeaths = new Label("Number of deaths:");
            grid.add(nDeaths, 0, 2);

            TextField nDeathsBox = new TextField();
            grid.add(nDeathsBox, 1, 2);

            Label turnDuration = new Label("Turn duration:");
            grid.add(turnDuration, 0, 3);

            TextField turnDurationBox = new TextField();
            grid.add(turnDurationBox, 1, 3);

            Label mapId = new Label("Map Id:");
            grid.add(mapId, 0, 4);

            TextField mapIdBox = new TextField();
            grid.add(mapIdBox, 1, 4);

            //port allows only integers
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String text = change.getText();

                if (text.matches("[0-9]*")) {
                    return change;
                }

                return null;
            };
            mapIdBox.setTextFormatter(new TextFormatter<Object>(filter));
            turnDurationBox.setTextFormatter(new TextFormatter<Object>(filter));
            nDeathsBox.setTextFormatter(new TextFormatter<Object>(filter));
            nPlayersBox.setTextFormatter(new TextFormatter<Object>(filter));



            Button btnNewGame = new Button("Create New Game");
            HBox hbBtnNewGame = new HBox(10);
            hbBtnNewGame.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtnNewGame.getChildren().add(btnNewGame);
            grid.add(hbBtnNewGame, 1, 8);



            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 9);

            btnNewGame.setOnAction(e -> {
                if (!nPlayersBox.getText().equals("") && !nDeathsBox.getText().equals("") &&
                        !mapIdBox.getText().equals("") && !turnDurationBox.getText().equals("")) {
                    player.createGame(Integer.parseInt(nPlayersBox.getText()), Integer.parseInt(nDeathsBox.getText()),
                            Integer.parseInt(turnDurationBox.getText()), Integer.parseInt(mapIdBox.getText()));
                } else {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Please fill in all fields!");
                }
            });

        });

    }

    @Override
    public void showAlert(String message) {
        Platform.runLater(()->{
            final Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            Label label = new Label(message);
            label.setStyle("-fx-background-color: rgba(50,50,50,0.25);\n" +
                            "    -fx-padding: 10;\n" +
                            "    -fx-border-radius: 20; \n" +
                            "    -fx-background-radius: 20;\n" +
                            "    -fx-border-width: 5;\n" +
                            "    -fx-font-size: 16;");
            popup.getContent().add(label);

            Stage stage = FXWindow.getStage();
            popup.setOnShown(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    popup.setX(stage.getX() + stage.getWidth()/2 - popup.getWidth()/2);
                    popup.setY(stage.getY() + stage.getHeight()*4/5 - popup.getHeight()/2);
                }
            });
            popup.show(stage);
            PauseTransition wait = new PauseTransition(Duration.seconds(ALERT_DURATION));
            wait.setOnFinished((e) -> {
                popup.hide();
            });
            wait.play();
        });
    }

    @Override
    public void initMatch() {
        Platform.runLater(()->{
            Stage stage = FXWindow.getStage();
            BorderPane borders = new BorderPane();
            Pane root = new Pane();
            FXWindow.setPane(root);
            Scene scene = new Scene(borders, stage.getScene().getWidth(),stage.getScene().getHeight());
            borders.setCenter(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setResizable(false);

            double pV, pH;
            // set fixed proportions
            if(root.getHeight() > root.getWidth()*9/16){
                pH = 0;
                pV = (root.getHeight() - root.getWidth()*9/16)/2;
                System.out.println("stagew h: " + stage.getHeight());
                System.out.println("margin v: " + pV);
            } else {
                pV = 0;
                pH =  (root.getWidth()-root.getHeight()*16/9)/2;
            }

            width = root.getWidth()-2*pH;
            height = root.getHeight()-2*pV;

            BorderPane.setMargin(root, new Insets(pV, pH, pV, pH));

            //Creating a scene object
            borders.setPrefSize(scene.getWidth(),scene.getHeight());

            borders.setStyle("-fx-background-color: #222");
            root.setStyle("-fx-background-color: #222");

            root.setPrefSize(stage.getHeight()*16/9, stage.getHeight());
        });
    }

    /**
     * Shows the launcher options
     */
    @Override
    public void showMatch() {
        switch (player.getMatch().getState()){
            case NOT_STARTED:
                showWaitingRoom();
                break;
            case LOADING:
            case PLAYER_TURN:
                loading = true;
                showGameBoard();
                loading = false;
                break;
        }
    }

    private void showGameBoard() {
        AdrenalinaMatch match= player.getMatch();
        Platform.runLater(()-> {

            loadLayout();

            setEscExit();

        });
    }

    private void showWaitingRoom() {
        AdrenalinaMatch match= player.getMatch();
        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = (GridPane) FXWindow.getPane();

            initGrid(grid);


            Text scenetitle = new Text("Waiting room");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0);
            Label label = new Label("Your name: "+player.getNickname());
            grid.add(label, 0, 1);
            Label label1 = new Label("Players: "+match.getPlayers().size()+" / "+ match.getnPlayers());
            grid.add(label1, 0, 2);
            ScrollPane players = new ScrollPane();
            players.setPrefSize(primaryStage.getWidth()*0.8, primaryStage.getHeight()*0.5);
            GridPane playersGrid = new GridPane();
            playersGrid.setHgap(0);
            playersGrid.setVgap(0);
            grid.add(players,0,6);
            players.setContent(playersGrid);
            int i =0;
            for(Player p :match.getPlayers()){
                    Label msg = new Label("Player Name: " + p.getNickname() +
                            "\tColor Player: " + p.getColor()+
                            "\tReady: "+ (p.isReadyToStart()?"ready":"not ready"));
                    playersGrid.add(msg, 0, i + 1);
                    i++;
            }

            Button btnReady = new Button("READY");
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.BOTTOM_RIGHT);
            buttons.getChildren().add(btnReady);
            grid.add(buttons, 0, 8);

            if(match.getPlayers().size()<match.getnPlayers()){
                btnReady.setDisable(true);
            }

            Button btnBack = new Button("GO BACK");
            HBox hbBtnBack = new HBox(10);
            buttons.getChildren().add(btnBack);

            final Text actiontarget = new Text();
            grid.add(actiontarget, 0, 9);

            btnReady.setOnAction(e -> {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Waiting for other players");
                player.setReady(true);
            });

            btnBack.setOnAction(e -> {
                player.backToLobby();
                player.updateLobby();
            });



        });


    }

    private void initGrid(GridPane grid) {
        grid.getChildren().clear();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.getChildren().clear();
    }

    private void waitLoading(){
        System.out.println("WAITING LOADING");
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }while(loading == true);
        System.out.println("DONE LOADING");
    }

    /**
     * Lets client select a player from a list
     * @param selectables list of players
     * @return selected player
     */
    @Override
    public String selectPlayer(List<String> selectables) {
        // set buttons
        Platform.runLater(()-> {
                    selectables.forEach(selectable -> {
                        players.stream().filter(p -> p.getImage().getUrl().
                                contains(player.getMatch().getPlayerByName(selectable).getColor().toString().toLowerCase() + PAWN_EXTENSION)).
                                forEach(pawnImg -> {
                                    selection.setNodeClickable(pawnImg, selectable);
                                    setClickableEffects(pawnImg);
                                });
                    });
        });

        //wait for selection
        String selected = selection.getValue();

        //remove buttons
        Platform.runLater(()-> {
            selectables.forEach(selectable -> {
                players.stream().filter(p->p.getImage().getUrl().
                        contains(player.getMatch().getPlayerByName(selectable).getColor().toString().toLowerCase()+PAWN_EXTENSION)).
                        forEach(pawnImg -> {
                            selection.setNodeNotClickable(pawnImg);
                            setNotClickableEffects(pawnImg);
                        });
            });
        });

        return selected;
    }

    /**
     * Lets client select a cell from a list
     * @param selectables list of points
     * @return selected point
     */
    @Override
    public Point selectCell(List<Point> selectables) {
        List<Button> buttons = new ArrayList<>();
        Platform.runLater(()->{
            int i = 0;
            for(Point p: selectables){
                Button cellButton = new Button();
                cellButton.setLayoutX(CELL_BUTTON_X0*width + (CELL_BUTTON_PADDING*height + CELL_BUTTON_EDGE*height)*p.getY());
                cellButton.setLayoutY(CELL_BUTTON_Y0*height + (CELL_BUTTON_PADDING*height + CELL_BUTTON_EDGE*height)*p.getX());
                cellButton.setPrefWidth(CELL_BUTTON_EDGE*height);
                cellButton.setPrefHeight(CELL_BUTTON_EDGE*height);
                setButtonEffects(cellButton);
                selection.setNodeClickable(cellButton, String.valueOf(i));
                FXWindow.getPane().getChildren().add(cellButton);
                buttons.add(cellButton);
                i++;
            }
        });
        int selected = Integer.parseInt(selection.getValue());
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(buttons);
        });
        return selectables.get(selected);
    }

    /**
     * Lets client select a room from a list
     * @param selectables list of rooms
     * @return selected room
     */
    @Override
    public List<Point> selectRoom(List<List<Point>> selectables) {
        return null;
    }

    /**
     * Lets client select a weapon and effect from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectShoot(List<String> selectables) {
        return null;
    }
    /**
     * Lets client select a weapon to reload from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectReload(List<String> selectables) {
        return selectWeapon(selectables);
    }

    /**
     * Lets client select a weapon  from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectWeapon(List<String> selectables) {
        System.out.println("SELECT WEAPON");
        waitLoading();
        // set weapon buttons
        ArrayList<ImageView> weaponsOnScreen = new ArrayList<>();
        weaponsOnScreen.addAll(weapons);
        weaponsOnScreen.addAll(spawnWeapons);
        Platform.runLater(()->{
            selectables.forEach(selectable -> {
                //String fileName = getWeaponFileName(selectable);
                weaponsOnScreen.stream().
                        filter(w -> w.getImage().getUrl().contains(selectable.toLowerCase())).
                        forEach(w -> {
                            selection.setNodeClickable(w, selectable);
                            setClickableEffects(w);
                            System.out.println("SETTING CLICKABLE"+selectable);
                });
            });
        });
        // get selected weapon
        String weaponSelected = selection.getValue();
        WeaponCard weaponCardSelected = player.getMatch().getWeaponByName(weaponSelected);
        // remove weapon  button
        Platform.runLater(()->{
            selectables.forEach(selectable -> {
                //String fileName = getWeaponFileName(selectable);
                weapons.stream().filter(p -> p.getImage().getUrl().contains(selectable.toLowerCase())).forEach(p -> {
                    selection.setNodeNotClickable(p);
                    setNotClickableEffects(p);
                });
            });
        });

        // set powerup discuont buttons
        List<Resource> toPay = weaponCardSelected.getCost();
        if(!player.getThisPlayer().getWeapons().contains(weaponCardSelected)){
          toPay.remove(0);
        }

        List<Powerup> selectedPowerups = new ArrayList<>();
        while(!toPay.isEmpty()&& !player.getMatch().getPlayerByName(player.getNickname()).getPowerups().isEmpty()) {
            List<Powerup> usable = new ArrayList<>();
            player.getMatch().getPlayerByName(player.getNickname()).getPowerups().forEach(powerup -> {
                if (toPay.contains(powerup.getBonusResource())) {
                    usable.add(powerup);
                }
            });
            if(usable.isEmpty())break;
            Powerup selectedPowerup = selectPowerup(usable);
            if(selectedPowerup != null){
                selectedPowerups.add(selectedPowerup);
                player.getMatch().getPlayerByName(player.getNickname()).getPowerups().remove(selectedPowerup);
                toPay.remove(selectedPowerup.getBonusResource());
            }
            else break;
        }

        WeaponSelection selected= new WeaponSelection();
        selected.setWeapon(weaponSelected);
        selected.setDiscount(selectedPowerups);
        return selected;
    }

    @Override
    public WeaponSelection selectWeaponFree(List<String> selectables) {
        return null;
    }

    /**
     * Lets client select a powerup from a list
     * @param selectables list of powerups
     * @return selected powerup
     */
    @Override
    public Powerup selectPowerup(List<Powerup> selectables) {
        waitLoading();
        if(player.getMatch().getPlayerByName(player.getNickname()).getPowerups().containsAll(selectables)) { // is a standard selection
            // set buttons
            Platform.runLater(()->{
                selectables.forEach(selectable -> {
                    String fileName = getPowerupFileName(selectable);
                    powerups.stream().filter(p -> p.getImage().getUrl().contains(fileName)).forEach(p -> {
                        selection.setNodeClickable(p, String.valueOf(selectables.indexOf(selectable)));
                        setClickableEffects(p);
                    });
                });
            });


            // wait for selection
            int selected = Integer.parseInt(selection.getValue());

            // remove buttons
            Platform.runLater(()->{
                selectables.forEach(selectable -> {
                    String fileName = getPowerupFileName(selectable);
                    powerups.stream().filter(p -> p.getImage().getUrl().contains(fileName)).forEach(p -> {
                        selection.setNodeClickable(p, String.valueOf(selectables.indexOf(selectable)));
                        setClickableEffects(p);
                    });
                });
            });

            return selectables.get(selected);
        }else {
            return spawnSelection(selectables);
        }
    }

    private Powerup spawnSelection(List<Powerup> selectables) {
        // show spawn
        List<Node> toRemove = new ArrayList<>();
        Platform.runLater(()->{
            Pane root = FXWindow.getPane();
            ImageView spawnPopUp = loadImage(SPAWN_POPUP_PATH);
            spawnPopUp.setPreserveRatio(true);
            spawnPopUp.setFitWidth(SPAWN_POPUP_SIZE*width);
            spawnPopUp.setX((width-width*SPAWN_POPUP_SIZE)*SPAWN_POPUP_X0);
            spawnPopUp.setY((height-getHeight(spawnPopUp))*SPAWN_POPUP_Y0);
            root.getChildren().add(spawnPopUp);
            toRemove.add(spawnPopUp);
            // put cards and put buttons
            int i = 0;
            for(Powerup powerup: selectables){
                ImageView powerupImg = getPowerupImage(powerup);
                powerupImg.setY(SPAWN_POWERUP_Y0*height);
                powerupImg.setX(SPAWN_POWERUP_X0*width+ SPAWN_POWERUP_OFFSET_X*height*i);
                powerupImg.setPreserveRatio(true);
                powerupImg.setFitWidth(SPAWN_POWERUP_SIZE*height);
                setClickableEffects(powerupImg);
                root.getChildren().add(powerupImg);
                toRemove.add(powerupImg);
                selection.setNodeClickable(powerupImg,String.valueOf(i));
                i++;
            }
        });
        // get value
        int selected = Integer.parseInt(selection.getValue());
        // remove all HOPEFULLY IT'S DONE BY AN UPDATE
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });
        return selectables.get(selected);
    }

    private Resource resourceSelection(List<Resource> selectables) {
        // set buttons
        Platform.runLater(()->{
            selectables.forEach(selectable -> {
                String fileName = getResourceFileName(selectable);
                powerups.stream().filter(p -> p.getImage().getUrl().contains(fileName)).forEach(p -> {
                    selection.setNodeClickable(p, String.valueOf(selectables.indexOf(selectable)));
                    setClickableEffects(p);
                });
            });
        });



        // wait for selection
        int selected = Integer.parseInt(selection.getValue());

        // remove buttons
        Platform.runLater(()->{
            selectables.forEach(selectable -> {
                String fileName = getResourceFileName(selectable);
                powerups.stream().filter(p -> p.getImage().getUrl().contains(fileName)).forEach(p -> {
                    selection.setNodeClickable(p, String.valueOf(selectables.indexOf(selectable)));
                    setClickableEffects(p);
                });
            });
        });

        return selectables.get(selected);
    }

    /**
     * Select an action to make
     * @return action to make
     */
    @Override
    public TurnAction actionSelection() {
        System.out.println("ACTION SELECTION");
        List<Button> buttons = new ArrayList<>();
        Platform.runLater(()->{
            if(!player.getMatch().isFrenzyEnabled()){
                int i = 0;
                for(TurnAction t: TurnAction.values()){
                    Button actionButton = new Button();
                    actionButton.setLayoutX(ACTION_BTN_X0*width);
                    actionButton.setLayoutY(ACTION_BTN_NO_FRENZY_Y0*height+ACTION_BTN_NO_FRENZY_PADDING*height*i);
                    actionButton.setMinHeight(0);
                    actionButton.setPrefHeight(ACTION_BTN_NO_FRENZY_SIZE_Y*height);
                    actionButton.setPrefWidth(ACTION_BTN_SIZE_X*height);
                    setButtonEffects(actionButton);
                    selection.setNodeClickable(actionButton, t.toString());
                    FXWindow.getPane().getChildren().add(actionButton);
                    buttons.add(actionButton);
                    i++;
                }
            }
            //TODO FRENZY ENABLED BUTTONS
        });

        String selected = selection.getValue();
        System.out.println("DONE SELECT ACTION:..... REMOVING BUTTONS");
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(buttons);
        });

        return TurnAction.valueOf(selected);
    }

    private void loadLayout(){
        Pane root = FXWindow.getPane();
        clearView(root);
        ImageView map = loadMap();
        addNode(root,map);
        loadPlayersTabs(root, map);
        loadSpawnPointCards(root);
        loadBoardItems(root);

    }

    private void addNode(Pane root, ImageView node) {
        root.getChildren().add(node);
        nodes.add(node);
    }

    private void clearView(Pane root) {
        root.getChildren().removeAll(nodes);
    }

    private void loadBoardItems(Pane root) {
        Map map = player.getMatch().getBoardMap();
        loadDeathTrack(root);
        loadCellItems(root, map);
        loadDeckImages(root);
    }

    private void loadDeckImages(Pane root) {
        ImageView weaponDeck = loadImage(DECK_WEAPON_PATH);
        ImageView powerupDeck = loadImage(DECK_POWERUP_PATH);
        powerupDeck.setX(DECK_POWERUP_X*width);
        powerupDeck.setY(DECK_POWERUP_Y*height);
        powerupDeck.setPreserveRatio(true);
        powerupDeck.setFitWidth(DECK_POWERUP_SIZE*height);
        weaponDeck.setX(DECK_WEAPON_X*width);
        weaponDeck.setY(DECK_WEAPON_Y*height);
        weaponDeck.setPreserveRatio(true);
        weaponDeck.setFitWidth(DECK_WEAPON_SIZE*height);
        addNode(root,weaponDeck);
        addNode(root,powerupDeck);
    }

    private void loadCellItems(Pane root, Map map) {
        for(int i = 0; i<map.getYSize(); i++){
            for(int j = 0; j<map.getXSize(); j++){
                Cell cell =  map.getCell(j,i);
                if(cell != null) {
                    int k = 0;

                    // players
                    players.clear();
                    for (Player player : cell.getPlayers()) {
                        String path = PAWN_DIR + player.getColor().toString().toLowerCase() + PAWN_EXTENSION;
                        ImageView pawn = loadImage(path);
                        pawn.setX(PAWN_X0*width+PAWN_OFFSET*height*i+PAWN_INTERNAL_OFFSET*height*(k%3));
                        pawn.setY(PAWN_Y0*height+PAWN_OFFSET*height*j+PAWN_INTERNAL_OFFSET*height*(k/3));
                        pawn.setPreserveRatio(true);
                        pawn.setFitWidth(PAWN_SIZE*height);
                        addNode(root,pawn);
                        players.add(pawn);
                        k++;
                    }

                    // ammo

                    if(!cell.isSpawn() && ((AmmoCell)cell).getResource()!= null) {
                        ImageView ammo = getAmmoImage(((AmmoCell)cell).getResource());
                        ammo.setX(AMMO_X0*width+AMMO_OFFSET*height*i);
                        ammo.setY(AMMO_Y0*height+AMMO_OFFSET*height*j);
                        ammo.setPreserveRatio(true);
                        ammo.setFitWidth(AMMO_SIZE*height);
                        addNode(root,ammo);
                    }
                }

            }
        }
    }

    private void loadDeathTrack(Pane root) {
        List<Player> deathtrack = player.getMatch().getDeathTrack();
        List<Boolean> overkills = player.getMatch().getOverkills();
        for(int i = 0, j = 0; i<overkills.size(); i++, j++){
            ImageView droplet = getDropletImage(deathtrack.get(i));
            droplet.setY(DEATHTRACK_DROPLET_Y0*height);
            droplet.setX(DEATHTRACK_DROPLET_X0*width+DEATHTRACK_DROPLET_OFFSET_X*j);
            droplet.setPreserveRatio(true);
            droplet.setFitWidth(DEATHTRACK_DROPLET_SIZE);
            addNode(root,droplet);
            if(overkills.get(j)){
                i++;
                ImageView additionalDroplet = getDropletImage(deathtrack.get(i));
                additionalDroplet.setY(DEATHTRACK_DROPLET_Y0*height);
                additionalDroplet.setX(DEATHTRACK_DROPLET_X0*width+DEATHTRACK_DROPLET_OFFSET_X*height*j+DEATHTRACK_DROPLET_OFFSET_X2*height);
                additionalDroplet.setPreserveRatio(true);
                additionalDroplet.setFitWidth(DEATHTRACK_DROPLET_SIZE);
                addNode(root,additionalDroplet);
            }
        }
    }

    private void loadSpawnPointCards(Pane root) {
        spawnWeapons.clear();
        for(SpawnCell spawnCell: player.getMatch().getBoardMap().getSpawnPoints()){
            int i = 0;
            switch(spawnCell.getColor()){
                case BLUE:
                    for(WeaponCard weapon : spawnCell.getWeapons()){
                        ImageView weaponView = getWeaponImage(weapon.getName());
                        weaponView.setY(SPAWN_POINT_1_BASE_Y*height);
                        weaponView.setX(SPAWN_POINT_1_BASE_X*width+SPAWN_POINT_OFF*height*i);
                        weaponView.setPreserveRatio(true);
                        weaponView.setFitWidth(width*WEAPON_CARD_SIZE);
                        weaponView.setRotate(SPAWN_POINT_1_ROTATION);
                        addNode(root,weaponView);
                        //setHoverEffect(weaponView, width*WEAPON_CARD_SIZE);
                        spawnWeapons.add(weaponView);
                        i++;
                    }
                    break;
                case RED:
                    for(WeaponCard weapon : spawnCell.getWeapons()){
                        ImageView weaponView = getWeaponImage(weapon.getName());
                        weaponView.setY(SPAWN_POINT_2_BASE_Y*height+SPAWN_POINT_OFF*height*i);
                        weaponView.setX(SPAWN_POINT_2_BASE_X*width);
                        weaponView.setPreserveRatio(true);
                        weaponView.setFitWidth(width*WEAPON_CARD_SIZE);
                        weaponView.setRotate(SPAWN_POINT_2_ROTATION);
                        addNode(root,weaponView);
                        //setHoverEffect(weaponView, width*WEAPON_CARD_SIZE);
                        spawnWeapons.add(weaponView);
                        i++;
                    }
                    break;
                case YELLOW:
                    for(WeaponCard weapon : spawnCell.getWeapons()){
                        ImageView weaponView = getWeaponImage(weapon.getName());
                        weaponView.setY(SPAWN_POINT_3_BASE_Y*height+SPAWN_POINT_OFF*height*i);
                        weaponView.setX(SPAWN_POINT_3_BASE_X*width);
                        weaponView.setPreserveRatio(true);
                        weaponView.setFitWidth(width*WEAPON_CARD_SIZE);
                        weaponView.setRotate(SPAWN_POINT_3_ROTATION);
                        addNode(root,weaponView);
                        //setHoverEffect(weaponView, width*WEAPON_CARD_SIZE);
                        spawnWeapons.add(weaponView);
                        i++;
                    }
                    break;
            }
        }
    }

    private void loadPlayersTabs(Pane root, ImageView map) {
        double tabSize =(width - getWidth(map));
        int i = 0;
        for(Player p: player.getMatch().getPlayers()){
            if(p.getNickname().equals(player.getNickname())){
                ImageView tab = getTabImage(p);
                tab.setX(PLAYER_TAB_X*width);
                tab.setY(PLAYER_TAB_Y*height);
                tab.setPreserveRatio(true);
                tab.setFitHeight(height*RELATIVE_TAB_HEIGHT);
                Tooltip.install(tab, new Tooltip(p.getNickname()));
                addNode(root,tab);
                loadPlayerDashboard(root, tab);
                loadPlayerSigns(root, tab);
            } else {
                ImageView tab = getTabImage(p);
                tab.setPreserveRatio(true);
                tab.setFitWidth(tabSize);
                tab.setX(getWidth(map));
                tab.setY(height * TAB_OFFSET * i);
                Tooltip.install(tab, new Tooltip(p.getNickname()));
                addNode(root,tab);
                loadEnemySigns(root,tab, p, i);
                i++;
            }
        }
    }


    private void loadPlayerSigns(Pane root, ImageView tab) {
        int i = 0;
        //damage
        for(Player p: player.getMatch().getPlayerByName(player.getNickname()).getDmgPoints()){
            ImageView droplet = getDropletImage(p);
            droplet.setX(PLAYER_DAMAGE_DROPLET_X0*width+PLAYER_DAMAGE_DROPLET_OFFSET_X*width*i);
            droplet.setY(PLAYER_DAMAGE_DROPLET_Y0*height);
            droplet.setPreserveRatio(true);
            droplet.setFitWidth(PLAYER_DAMAGE_DROPLET_SIZE);
            addNode(root,droplet);
            i++;
        }
        //marks
        i = 0;
        for(Player p: player.getMatch().getPlayerByName(player.getNickname()).getMarks()){
            ImageView droplet = getDropletImage(p);
            droplet.setX(PLAYER_MARK_DROPLET_X0*width+PLAYER_MARK_DROPLET_OFFSET_X*width*i);
            droplet.setY(PLAYER_MARK_DROPLET_Y0*height);
            droplet.setPreserveRatio(true);
            droplet.setFitWidth(PLAYER_MARK_DROPLET_SIZE);
            addNode(root,droplet);
            i++;
        }
        //deaths
        for(int j = 0; j < player.getMatch().getPlayerByName(player.getNickname()).getDeaths(); j++){
            ImageView skull = loadImage(SKULL_PATH);
            skull.setX(PLAYER_SKULL_DROPLET_X0*width+PLAYER_SKULL_DROPLET_OFFSET_X*width*i);
            skull.setY(PLAYER_SKULL_DROPLET_Y0*height);
            skull.setPreserveRatio(true);
            skull.setFitWidth(PLAYER_SKULL_SIZE);
            addNode(root,skull);
            i++;
        }

        //resources
        i = 0;
        resources.clear();
        for(Resource r: player.getMatch().getPlayerByName(player.getNickname()).getAmmos()){
            ImageView resource = getResourceImage(r);
            resource.setX(RESOURCE_PLAYER_X0*width+RESOURCE_OFFSET_X*width*(i%3));
            resource.setY(RESOURCE_PLAYER_Y0*height+RESOURCE_OFFSET_Y*height*(i/3));
            resource.setPreserveRatio(true);
            resource.setFitWidth(RESOURCE_SIZE*width);
            addNode(root,resource);
            resources.add(resource);
            i++;
        }
    }

    private void loadEnemySigns(Pane root,ImageView tab, Player player, int i) {
        int k = 0;
        //damage
        for(Player p: player.getDmgPoints()){
            ImageView droplet = getDropletImage(p);
            droplet.setX(ENEMY_DAMAGE_DROPLET_X0 *width+ ENEMY_DAMAGE_DROPLET_OFFSET_X *width*k);
            droplet.setY(ENEMY_DAMAGE_DROPLET_Y0 *height+ ENEMY_DAMAGE_DROPLET_OFFSET_Y *height*i);
            droplet.setPreserveRatio(true);
            droplet.setFitWidth(getWidth(tab)* ENEMY_DROPLET_SIZE);
            addNode(root,droplet);
            k++;
        }

        //marks
        k = 0;
        for(Player p: player.getMarks()){
            ImageView droplet = getDropletImage(p);
            droplet.setX(ENEMY_MARK_DROPLET_X0 *width+ ENEMY_MARK_DROPLET_OFFSET_X *width*k);
            droplet.setY(ENEMY_MARK_DROPLET_Y0 *height+ ENEMY_MARK_DROPLET_OFFSET_Y *height*i);
            droplet.setPreserveRatio(true);
            droplet.setFitWidth(getWidth(tab)* ENEMY_DROPLET_SIZE);
            addNode(root,droplet);
            k++;
        }

        //deaths
        k = 0;
        for(int j = 0; j<player.getDeaths(); j++){
            ImageView skull = loadImage(SKULL_PATH);
            skull.setX(ENEMY_SKULL_DROPLET_X0*width+ENEMY_SKULL_DROPLET_OFFSET_X*width*k);
            skull.setY(ENEMY_SKULL_DROPLET_Y0*height+ENEMY_SKULL_DROPLET_OFFSET_Y*height*i);
            skull.setPreserveRatio(true);
            skull.setFitWidth(ENEMY_SKULL_SIZE);
            addNode(root,skull);
            k++;
        }

        //weapons
        k = 0;
        for(WeaponCard w: player.getWeapons()){
            ImageView weapon;
            if(player.getLoadedWeapons().contains(w)){
                weapon = loadImage(DECK_WEAPON_PATH);
            }else {
                weapon = getWeaponImage(w.getName());
            }
            weapon.setX(ENEMY_WEAPON_CARD_X0*width+ENEMY_CARD_OFFSET_X*width*k);
            weapon.setY(ENEMY_WEAPON_CARD_Y0*height+ENEMY_CARD_OFFSET_Y*height*i);
            weapon.setPreserveRatio(true);
            weapon.setFitWidth(ENEMY_CARD_SIZE*width);
            addNode(root,weapon);
            k++;
        }

        //powerup
        k = 0;
        for(Powerup powerup: player.getPowerups()){
            ImageView powerupImage = loadImage(DECK_POWERUP_PATH);
            powerupImage.setX(ENEMY_POWERUP_CARD_X0*width+ENEMY_CARD_OFFSET_X*width*k);
            powerupImage.setY(ENEMY_POWERUP_CARD_Y0*height+ENEMY_CARD_OFFSET_Y*height*i);
            powerupImage.setPreserveRatio(true);
            powerupImage.setFitWidth(ENEMY_CARD_SIZE*width);
            addNode(root,powerupImage);
            k++;
        }

        //resources
        k = 0;
        for(Resource r: player.getAmmos()){
            ImageView resource = getResourceImage(r);
            resource.setX(RESOURCE_ENEMY_X0*width+RESOURCE_OFFSET_X*width*(k%3));
            resource.setY(RESOURCE_ENEMY_Y0*height+RESOURCE_OFFSET_Y*height*(k/3)+RESOURCE_OFFSET_Y2*height*i);
            resource.setPreserveRatio(true);
            resource.setFitWidth(RESOURCE_SIZE*width);
            addNode(root,resource);
            k++;
        }
    }

    private void loadPlayerDashboard(Pane root, ImageView tab) {
        ImageView dashboard = loadImage(DASHBOARD_PATH);
        dashboard.setPreserveRatio(true);
        dashboard.setY(PLAYER_TAB_Y*height);
        dashboard.setX(getWidth(tab));
        dashboard.setFitWidth(width-getWidth(tab));
        addNode(root,dashboard);
        loadPlayerWeapons(root);
        loadPlayerPowerups(root);
    }


    private void loadPlayerPowerups(Pane root) {
        powerups.clear();
        int i = 0;
        for(Powerup powerup: player.getMatch().getPlayerByName(player.getNickname()).getPowerups()){
            ImageView powerupImg = getPowerupImage(powerup);
            powerupImg.setPreserveRatio(true);
            powerupImg.setY(DASHBOARD_POWERUP_Y*height);
            powerupImg.setFitWidth(DASHBOARD_CARDS_SIZE*width);
            powerupImg.setX(DASHBOARD_POWERUP_X*width+DASHBOARD_POWERUP_OFFSET*height*i);
            addNode(root,powerupImg);
            powerups.add(powerupImg);
            i++;
        }
    }

    private void loadPlayerWeapons(Pane root) {
        weapons.clear();
        int i = 0;
        for(WeaponCard weapon: player.getMatch().getPlayerByName(player.getNickname()).getWeapons()){
            ImageView weaponImg = getWeaponImage(weapon.getName());
            weaponImg.setPreserveRatio(true);
            weaponImg.setY(DASHBOARD_WEAPON_Y*height);
            weaponImg.setX(DASHBOARD_WEAPON_X*width+DASHBOARD_WEAPON_OFFSET_X*height*i);
            weaponImg.setFitWidth(DASHBOARD_CARDS_SIZE*width);
            addNode(root,weaponImg);
            weapons.add(weaponImg);
            i++;
        }
    }

    private ImageView loadMap() {
        double mapH = (height* RELATIVE_MAP_HEIGHT);

        ImageView map = loadImage(MAP+player.getMatch().getMapID()+MAP_EXTENSION);
        map.setX(MAP_X);
        map.setY(MAP_Y);
        map.setPreserveRatio(true);
        map.setFitHeight(mapH);
        return map;
    }

    private ImageView loadImage(String filePath){
        System.out.println(filePath);
        URL url = getClass().getResource(filePath);
        System.out.println(url.toString());
        //try {
            //Image img = new Image(new FileInputStream(url.getFile().replace("%20", " ")));
            Image img = new Image(url.toString().replace("%20", " "));
            return new ImageView(img);

        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}
        //return null;
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


    private void setEscExit() {
        FXWindow.getStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)){
                    ButtonType canc = new ButtonType("OK, LET'S GO BACK TO FIGHT", ButtonBar.ButtonData.OK_DONE);
                    ButtonType quit = new ButtonType("I'M LAME, I WANNA QUIT AND CRY", ButtonBar.ButtonData.CANCEL_CLOSE);
                    Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION, "", canc, quit);
                    exitAlert.setTitle("RAGE QUIT IS LAME");
                    exitAlert.setHeaderText("You sure you waanna quit this amazing game?");

                    Optional<ButtonType> option = exitAlert.showAndWait();
                    if (option.get().equals(quit)) {
                        System.out.println("pressed exit");
                        FXWindow.getStage().close();
                    }
                    event.consume();
                }
            }
        );
    }

    private ImageView getWeaponImage(String name){
        String file = getWeaponFileName(name);
        return loadImage(file);
    }

    private String getWeaponFileName(String name) {
        return WEAPON_DIR+name.toLowerCase()+WEAPON_EXTENSION;
    }

    private ImageView getPowerupImage(Powerup powerup){
        String file = getPowerupFileName(powerup);
        return loadImage(file);
    }

    private String getPowerupFileName(Powerup powerup) {
        return WEAPON_DIR+powerup.getName().toLowerCase()+"-" +
                powerup.getBonusResource().toString().replace(RESOURCE_STRING_FIX, "").toLowerCase() +
                WEAPON_EXTENSION;
    }

    private ImageView getAmmoImage(Ammo ammo){

        String file = AMMO_DIR;
        for (Resource resource: ammo.getResources()){
            file += resource.toString().replace(RESOURCE_STRING_FIX, "").toLowerCase();
        }
        file+=AMMO_EXTENSION;
        return loadImage(file);
    }

    private ImageView getResourceImage(Resource r){
        String file = getResourceFileName(r);
        return loadImage(file);
    }
    private ImageView getTabImage(Player player) {
        String file = TAB + player.getColor().toString().toLowerCase() +
                (player.isFrenzyPlayer() ? "" : TAB_BACK) + TAB_EXTENSION;
        return loadImage(file);
    }

    private String getResourceFileName(Resource r) {
        return RESOURCE_DIR+ r.toString().replace(RESOURCE_STRING_FIX, "").toLowerCase()+RESOURCE_EXTENSION;
    }

    private ImageView getDropletImage(Player p){
        String file = DROPLET_DIR+p.getColor().toString().toLowerCase()+DROPLET_EXTENSION;
        return loadImage(file);
    }

    public void setClickableEffects(Node n){
        n.setStyle(CLICKABLE_EFFECT);
    }

    public void setNotClickableEffects(Node n){
        n.setStyle(STANDARD_EFFECT);
    }

    public void setButtonEffects(Node n){
        n.setStyle(BUTTONS_EFFECT);
    }
}
