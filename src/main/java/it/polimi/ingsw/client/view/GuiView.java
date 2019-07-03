package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.Cell;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.Ammo;
import it.polimi.ingsw.server.model.MatchState;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
    private final static String TAB_BACK_FIRE = "backfire";
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
    private static final double ENEMY_SKULL_SIZE = 0.030;
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
    private static final double ENEMY_DROPLET_SIZE = 0.015;
    private static final double DEATHTRACK_DROPLET_Y0 = 0.032;
    private static final double DEATHTRACK_DROPLET_X0 = 0.042;
    private static final double DEATHTRACK_DROPLET_OFFSET_X = 0.045;
    private static final double DEATHTRACK_DROPLET_OFFSET_X2 = 0.008;
    private static final double DEATHTRACK_DROPLET_SIZE = 0.024;
    private static final double PLAYER_SKULL_DROPLET_Y0 = 0.94;
    private static final double PLAYER_SKULL_DROPLET_X0 = 0.1;
    private static final double PLAYER_SKULL_DROPLET_OFFSET_Y = 0.175;
    private static final double PLAYER_SKULL_DROPLET_OFFSET_X = 0.022;
    private static final double PLAYER_SKULL_SIZE = 0.03;
    private static final double PLAYER_DAMAGE_DROPLET_Y0 = 0.875;
    private static final double PLAYER_DAMAGE_DROPLET_X0 = 0.05;
    private static final double PLAYER_DAMAGE_DROPLET_OFFSET_X = 0.025;
    private static final double PLAYER_MARK_DROPLET_Y0 = 0.8;
    private static final double PLAYER_MARK_DROPLET_X0 = 0.26;
    private static final double PLAYER_MARK_DROPLET_OFFSET_Y = 0.175;
    private static final double PLAYER_MARK_DROPLET_OFFSET_X = 0.022;
    private static final double PLAYER_DAMAGE_DROPLET_SIZE = 0.0157;
    private static final double PLAYER_MARK_DROPLET_SIZE = 0.015;

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
    public static final String TO_RELOAD_PATH = "/img/others/toreload.png";

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
    public static final String USE_POWERUP_PATH = "/img/others/buttonusepowerup.png";

    // spawn selection popup
    private static final String SPAWN_POPUP_PATH = "/img/others/spawn.jpeg";
    private static final double SPAWN_POPUP_SIZE = 0.6;
    private static final double SPAWN_POPUP_X0 = 0.5;
    private static final double SPAWN_POPUP_Y0 = 0.5;
    private static final double SPAWN_POWERUP_X0 = 0.21;
    private static final double SPAWN_POWERUP_Y0 = 0.45;
    private static final double SPAWN_POWERUP_OFFSET_X = 0.215;
    private static final double SPAWN_POWERUP_SIZE = 0.17;

    // shoot selection popup
    private static final String WEAPON_EFFECT_POPUP_PATH = "/img/others/selectweaponeffects.png";
    private static final double WEAPON_EFFECT_POPUP_SIZE = 0.6;
    private static final double WEAPON_EFFECT_POPUP_X = 0.2;
    private static final double WEAPON_EFFECT_POPUP_Y = 0.5;
    private static final double WEAPON_EFFECT_CARD_X = 0.27;
    private static final double WEAPON_EFFECT_CARD_Y = 0.34;
    private static final double WEAPON_EFFECT_CARD_SIZE = 0.27;
    private static final double WEAPON_EFFECT_BUTTON_SELECT_SIZE_W = 0.1;
    private static final double WEAPON_EFFECT_BUTTON_SELECT_SIZE_H = 0.05;
    private static final double WEAPON_EFFECT_BUTTON_SELECT_X0 = 0.55;
    private static final double WEAPON_EFFECT_BUTTON_SELECT_Y0 = 0.35;
    private static final double WEAPON_EFFECT_BUTTON_SELECT_OFF = 0.12;
    private static final double WEAPON_EFFECT_BUTTON_SHOOT_Y = 0.73;
    private static final double WEAPON_EFFECT_BUTTON_UNDO_Y = 0.79;

    // use resources button
    private static final String USE_RESOURCES_PATH = "/img/others/buttonresources.png";
    private static final double USE_RESOURCES_SIZE = 0.1;
    private static final double USE_RESOURCES_Y= 0.73;
    private static final double USE_RESOURCES_X = 0.03;

    // button no reload
    private static final String NO_RELOAD_PATH = "/img/others/buttonnoreload.png";
    private static final double NO_RELOAD_SIZE = 0.1;
    private static final double NO_RELOAD_Y= 0.73;
    private static final double NO_RELOAD_X = 0.03;

    // rooms buttons
    // DIMENSIONS
    private static final double ROOM_123_BUTTON_H = 0.16;
    private static final double ROOM_3_BUTTON_W = 0.537;
    private static final double ROOM_2_BUTTON_W = 0.358;
    private static final double ROOM_1_BUTTON_W = 0.16;
    private static final double ROOM_2X2_BUTTON_WH = 0.358;
    private static final double ROOM_2_ROTATE_BUTTON_H = 0.358;
    // POSITIONS
    // MAP 1
    private static final double MAP_1_BLUE_ROOM_X = 0.1;
    private static final double MAP_1_BLUE_ROOM_Y = 0.18;
    private static final double MAP_1_RED_ROOM_X = 0.1;
    private static final double MAP_1_RED_ROOM_Y = 0.359;
    private static final double MAP_1_WHITE_ROOM_X = 0.19;
    private static final double MAP_1_WHITE_ROOM_Y = 0.538;
    private static final double MAP_1_YELLOW_ROOM_X = 0.402;
    private static final double MAP_1_YELLOW_ROOM_Y = 0.359;
    // MAP 2
    private static final double MAP_2_BLUE_ROOM_X = 0.1;
    private static final double MAP_2_BLUE_ROOM_Y = 0.18;
    private static final double MAP_2_RED_ROOM_X = 0.1;
    private static final double MAP_2_RED_ROOM_Y = 0.359;
    private static final double MAP_2_GREEN_ROOM_X = 0.402;
    private static final double MAP_2_GREEN_ROOM_Y = 0.18;
    private static final double MAP_2_WHITE_ROOM_X = 0.19;
    private static final double MAP_2_WHITE_ROOM_Y = 0.538;
    private static final double MAP_2_YELLOW_ROOM_X = 0.301375;
    private static final double MAP_2_YELLOW_ROOM_Y = 0.359;
    // MAP 3
    private static final double MAP_3_RED_ROOM_X = 0.1;
    private static final double MAP_3_RED_ROOM_Y = 0.18;
    private static final double MAP_3_BLUE_ROOM_X = 0.19;
    private static final double MAP_3_BLUE_ROOM_Y = 0.18;
    private static final double MAP_3_GREEN_ROOM_X = 0.402;
    private static final double MAP_3_GREEN_ROOM_Y = 0.18;
    private static final double MAP_3_YELLOW_ROOM_X = 0.301375;
    private static final double MAP_3_YELLOW_ROOM_Y = 0.359;
    private static final double MAP_3_WHITE_ROOM_X = 0.1;
    private static final double MAP_3_WHITE_ROOM_Y = 0.538;
    private static final double MAP_3_PURPLE_ROOM_X = 0.19;
    private static final double MAP_3_PURPLE_ROOM_Y = 0.359;
    // MAP 4
    private static final double MAP_4_RED_ROOM_X = 0.1;
    private static final double MAP_4_RED_ROOM_Y = 0.18;
    private static final double MAP_4_BLUE_ROOM_X = 0.19;
    private static final double MAP_4_BLUE_ROOM_Y = 0.18;
    private static final double MAP_4_PURPLE_ROOM_X = 0.19;
    private static final double MAP_4_PURPLE_ROOM_Y = 0.359;
    private static final double MAP_4_YELLOW_ROOM_X = 0.402;
    private static final double MAP_4_YELLOW_ROOM_Y = 0.359;
    private static final double MAP_4_WHITE_ROOM_X = 0.1;
    private static final double MAP_4_WHITE_ROOM_Y = 0.538;

    // leaderboard popup
    private static final String LEADERBOARD_PATH = "/img/others/gameover.png";
    private static final double LEADERBOARD_SIZE = 0.6;
    private static final double LEADERBOARD_X = 0.2;
    private static final double LEADERBOARD_Y = 0.5;
    private static final double LEADERBOARD_LABEL_X = 0.45;
    private static final double LEADERBOARD_LABEL_Y = 0.41;
    private static final double LEADERBOARD_LABEL_OFF = 0.08;
    private static final String LEADERBOARD_LABEL_STYLE = "-fx-font: 36 arial; -fx-text-fill: #fff;";

    // css effects
    private static final String STANDARD_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0), 0, 0, 0, 0);" +
                                                  "-fx-cursor: arrow";
    private static final String CLICKABLE_EFFECT =
                                        "-fx-effect: dropshadow(three-pass-box, rgba(225,280,82,0.85), 2, 2, 2, 2);" +
                                        "-fx-cursor: hand;";
    private static final String BUTTONS_EFFECT = "-fx-background-color: rgba(0,255,0,0.3);" +
                                                 "-fx-cursor: hand;";

    private static final String SELECTED_EFFECT = "-fx-background-color: #5af278;";
    private static final String NOT_SELECTED_EFFECT = "-fx-background-color: #6ea5ff;";
    private static final String ALERT_STYLE = "-fx-background-color: rgba(237, 230, 97, 1);\n" +
                                            "    -fx-padding: 10;\n" +
                                            "    -fx-border-radius: 20; \n" +
                                            "    -fx-background-radius: 20;\n" +
                                            "    -fx-border-width: 5;\n" +
                                            "    -fx-font-size: 16;";

    private double width;
    private double height;

    private List<ImageView> players;
    private List<ImageView> weapons;
    private List<ImageView> spawnWeapons;
    private List<ImageView> powerups;
    private List<ImageView> resources;
    private List<Node> nodesLeftBehid;

    private boolean loading;
    private boolean initted;

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
        nodesLeftBehid = new ArrayList<>();
        initted = false;
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
            popup.setAutoHide(false);
            popup.setHideOnEscape(true);
            Label label = new Label(message);
            label.setStyle(ALERT_STYLE);
            popup.getContent().add(label);
            label.setViewOrder(-101);
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
        if(!initted) {
            loading = true;
            Platform.runLater(() -> {
                Stage stage = FXWindow.getStage();
                BorderPane borders = new BorderPane();
                Pane root = new Pane();
                FXWindow.setPane(root);
                Scene scene = new Scene(borders, stage.getScene().getWidth(), stage.getScene().getHeight());
                borders.setCenter(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setResizable(false);

                double pV, pH;
                // set fixed proportions
                if (root.getHeight() > root.getWidth() * 9 / 16) {
                    pH = 0;
                    pV = (root.getHeight() - root.getWidth() * 9 / 16) / 2;
                    System.out.println("stagew h: " + stage.getHeight());
                    System.out.println("margin v: " + pV);
                } else {
                    pV = 0;
                    pH = (root.getWidth() - root.getHeight() * 16 / 9) / 2;
                }

                width = root.getWidth() - 2 * pH;
                height = root.getHeight() - 2 * pV;

                BorderPane.setMargin(root, new Insets(pV, pH, pV, pH));

                //Creating a scene object
                borders.setPrefSize(scene.getWidth(), scene.getHeight());

                borders.setStyle("-fx-background-color: #222");
                root.setStyle("-fx-background-color: #222");

                root.setPrefSize(stage.getHeight() * 16 / 9, stage.getHeight());
            });
            initted = true;
            loading = false;
        }
    }

    /**
     * Shows the launcher options
     */
    @Override
    public void showMatch() {
        if(!initted && player.getMatch().getState() != MatchState.NOT_STARTED){
            initMatch();
            waitLoading();
        }
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

    @Override
    public void showLeaderboard(List<String> leaderboard) {
        waitLoading();
        //show popup
        Platform.runLater(()->{
            Pane root = FXWindow.getPane();
            ImageView leaderboardPopup = loadImage(LEADERBOARD_PATH);
            leaderboardPopup.setPreserveRatio(true);
            leaderboardPopup.setFitWidth(LEADERBOARD_SIZE*width);
            leaderboardPopup.setX(LEADERBOARD_X*width);
            leaderboardPopup.setY(LEADERBOARD_Y*(height-getHeight(leaderboardPopup)));
            root.getChildren().add(leaderboardPopup);

            int i = 0;
            for(String s: leaderboard){
                Label user = new Label();
                String text = i + ". " + s + ": " +player.getMatch().getPlayerByName(s).getScore();
                user.setStyle(LEADERBOARD_LABEL_STYLE);
                if(i == 0)user.setTextFill(Color.web("#f6ff00"));
                user.setText(text);
                user.setLayoutX(LEADERBOARD_LABEL_X*width);
                user.setLayoutY(LEADERBOARD_LABEL_Y*height+ LEADERBOARD_LABEL_OFF*i*height);
                root.getChildren().add(user);
                i++;
            }

        });

    }

    private void showGameBoard() {
        AdrenalinaMatch match= player.getMatch();
        Platform.runLater(()-> {
            FXWindow.getPane().getChildren().removeAll(nodesLeftBehid);
            nodesLeftBehid.clear();
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

            Button btnReady = new Button();

            if(player.getThisPlayer().isReadyToStart()){
                btnReady.setText("NOT READY");
            } else {
                btnReady.setText("READY");
            }
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
                player.setReady(!player.getThisPlayer().isReadyToStart());
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
                TimeUnit.MILLISECONDS.sleep(100);
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
        waitLoading();
        System.out.println("SELECT PLAYER");
        players.forEach(p-> System.out.println(p.getImage().getUrl()));
        Platform.runLater(()-> {
                    selectables.forEach(selectable -> {
                        System.out.println(selectable);
                        players.stream().filter(p -> p.getImage().getUrl().
                                contains(getPawnFileName(player.getMatch().getPlayerByName(selectable)))).
                                forEach(pawnImg -> {
                                    System.out.println("SET CLICKABLE "+ selectable);
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
        waitLoading();
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
                nodesLeftBehid.add(cellButton);
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
        waitLoading();
        switch(player.getMatch().getMapID()){
            case 1:
                return selectRoomMap1(selectables);
            case 2:
                return selectRoomMap2(selectables);
            case 3:
                return selectRoomMap3(selectables);
            case 4:
                return selectRoomMap4(selectables);
        }
        return null;
    }

    private List<Point> selectRoomMap1(List<List<Point>> selectables) {
        //Show buttons
        List<Button> toRemove = new ArrayList<>();
        Platform.runLater(()->{
            Pane root = FXWindow.getPane();
            //check if room is present
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.RED)) {
                // add room button
                Button red = new Button();
                formatButton(red, MAP_1_RED_ROOM_X * width, MAP_1_RED_ROOM_Y * height,
                        ROOM_3_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(red);
                selection.setNodeClickable(red, it.polimi.ingsw.server.model.Color.RED.toString());
                toRemove.add(red);
                nodesLeftBehid.add(red);
                root.getChildren().add(red);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.BLUE)){
                Button blue = new Button();
                formatButton(blue, MAP_1_BLUE_ROOM_X * width, MAP_1_BLUE_ROOM_Y * height,
                        ROOM_3_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(blue);
                selection.setNodeClickable(blue,  it.polimi.ingsw.server.model.Color.BLUE.toString());
                toRemove.add(blue);
                nodesLeftBehid.add(blue);
                root.getChildren().add(blue);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.WHITE)){
                Button white = new Button();
                formatButton(white, MAP_1_WHITE_ROOM_X * width, MAP_1_WHITE_ROOM_Y * height,
                        ROOM_2_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(white);
                selection.setNodeClickable(white,  it.polimi.ingsw.server.model.Color.WHITE.toString());
                toRemove.add(white);
                root.getChildren().add(white);
                nodesLeftBehid.add(white);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.YELLOW)){
                Button yellow = new Button();
                formatButton(yellow, MAP_1_YELLOW_ROOM_X * width, MAP_1_YELLOW_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_2_ROTATE_BUTTON_H * height);
                setButtonEffects(yellow);
                selection.setNodeClickable(yellow,  it.polimi.ingsw.server.model.Color.YELLOW.toString());
                toRemove.add(yellow);
                root.getChildren().add(yellow);
                nodesLeftBehid.add(yellow);
            }
        });
        // get selected value
        String selected = selection.getValue();

        // remove buttons
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        //get selected room and return it
        return selectables.get(getRoomIndex(selectables, it.polimi.ingsw.server.model.Color.valueOf(selected)));
    }

    private int getRoomIndex(List<List<Point>> selectables, it.polimi.ingsw.server.model.Color c){
        int i = 0;
        boolean found = false;
        for(List<Point> room: selectables){
            for(Point p : room){
                if(player.getMatch().getBoardMap().getCell(p.getX(), p.getY()).getColor() == c){
                    found = true;
                    break;
                }
            }
            if(found) break;
            i++;
        }
        return i;
    }

    private boolean isRoomPresent(List<List<Point>> selectables, it.polimi.ingsw.server.model.Color toCheck){
        return !selectables.stream().flatMap(List::stream).
                filter(p->player.getMatch().getBoardMap().
                       getCell(p.getX(),p.getY()).getColor() == it.polimi.ingsw.server.model.Color.RED).
                collect(Collectors.toList()).isEmpty();
    }

    /**
     * Formats a button
     * @param button to format
     * @param X coordinate
     * @param Y cordinate
     * @param WIDTH size
     * @param HEIGHT size
     */
    private void formatButton(Button button, double X, double Y, double WIDTH, double HEIGHT) {
        button.setMinHeight(0);
        button.setLayoutX(X);
        button.setLayoutY(Y);
        button.setPrefWidth(WIDTH);
        button.setPrefHeight(HEIGHT);
        FXWindow.getPane().getChildren().add(button);
        if(!nodesLeftBehid.contains(button))nodesLeftBehid.add(button);
    }

    private List<Point> selectRoomMap2(List<List<Point>> selectables) {
        //Show buttons
        Pane root = FXWindow.getPane();
        List<Button> toRemove = new ArrayList<>();
        Platform.runLater(()->{
            //check if room is present
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.WHITE)){
                Button white = new Button();
                formatButton(white, MAP_2_WHITE_ROOM_X * width, MAP_2_WHITE_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(white);
                selection.setNodeClickable(white,  it.polimi.ingsw.server.model.Color.WHITE.toString());
                toRemove.add(white);
                root.getChildren().add(white);
                nodesLeftBehid.add(white);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.YELLOW)){
                Button yellow = new Button();
                formatButton(yellow, MAP_2_YELLOW_ROOM_X * width, MAP_2_YELLOW_ROOM_Y * height,
                        ROOM_2X2_BUTTON_WH * height, ROOM_2X2_BUTTON_WH * height);
                setButtonEffects(yellow);
                selection.setNodeClickable(yellow,  it.polimi.ingsw.server.model.Color.YELLOW.toString());
                toRemove.add(yellow);
                root.getChildren().add(yellow);
                nodesLeftBehid.add(yellow);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.GREEN)){
                Button green = new Button();
                formatButton(green, MAP_2_GREEN_ROOM_X * width, MAP_2_GREEN_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(green);
                selection.setNodeClickable(green,  it.polimi.ingsw.server.model.Color.GREEN.toString());
                toRemove.add(green);
                root.getChildren().add(green);
                nodesLeftBehid.add(green);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.RED)) {
                // add room button
                Button red = new Button();
                formatButton(red, MAP_2_RED_ROOM_X * width, MAP_2_RED_ROOM_Y * height,
                        ROOM_2_BUTTON_W * width, ROOM_123_BUTTON_H * height);
                setButtonEffects(red);
                selection.setNodeClickable(red, it.polimi.ingsw.server.model.Color.RED.toString());
                toRemove.add(red);
                root.getChildren().add(red);
                nodesLeftBehid.add(red);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.BLUE)){
                Button blue = new Button();
                formatButton(blue, MAP_2_BLUE_ROOM_X * width, MAP_2_BLUE_ROOM_Y * height,
                        ROOM_3_BUTTON_W * width, ROOM_123_BUTTON_H * height);
                setButtonEffects(blue);
                selection.setNodeClickable(blue,  it.polimi.ingsw.server.model.Color.BLUE.toString());
                toRemove.add(blue);
                root.getChildren().add(blue);
                nodesLeftBehid.add(blue);
            }

        });
        // get selected value
        String selected = selection.getValue();

        // remove buttons
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        //get selected room and return it
        return selectables.get(getRoomIndex(selectables, it.polimi.ingsw.server.model.Color.valueOf(selected)));
    }

    private List<Point> selectRoomMap3(List<List<Point>> selectables) {
        //Show buttons
        List<Button> toRemove = new ArrayList<>();
        Platform.runLater(()->{
            Pane root = FXWindow.getPane();
            //check if room is present
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.WHITE)){
                Button white = new Button();
                formatButton(white, MAP_3_WHITE_ROOM_X * width, MAP_3_WHITE_ROOM_Y * height,
                        ROOM_2_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(white);
                selection.setNodeClickable(white,  it.polimi.ingsw.server.model.Color.WHITE.toString());
                toRemove.add(white);
                root.getChildren().add(white);
                nodesLeftBehid.add(white);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.PURPLE)){
                Button purple = new Button();
                formatButton(purple, MAP_3_PURPLE_ROOM_X * width, MAP_3_PURPLE_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(purple);
                selection.setNodeClickable(purple,  it.polimi.ingsw.server.model.Color.PURPLE.toString());
                toRemove.add(purple);
                root.getChildren().add(purple);
                nodesLeftBehid.add(purple);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.GREEN)){
                Button green = new Button();
                formatButton(green, MAP_3_GREEN_ROOM_X * width, MAP_3_GREEN_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(green);
                selection.setNodeClickable(green,  it.polimi.ingsw.server.model.Color.GREEN.toString());
                toRemove.add(green);
                root.getChildren().add(green);
                nodesLeftBehid.add(green);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.BLUE)){
                Button blue = new Button();
                formatButton(blue, MAP_3_BLUE_ROOM_X * width, MAP_3_BLUE_ROOM_Y * height,
                        ROOM_2_BUTTON_W * width, ROOM_123_BUTTON_H * height);
                setButtonEffects(blue);
                selection.setNodeClickable(blue,  it.polimi.ingsw.server.model.Color.BLUE.toString());
                root.getChildren().add(blue);
                toRemove.add(blue);
                nodesLeftBehid.add(blue);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.RED)) {
                // add room button
                Button red = new Button();
                formatButton(red, MAP_3_RED_ROOM_X * width, MAP_3_RED_ROOM_Y * height,
                        ROOM_123_BUTTON_H * width, ROOM_2_ROTATE_BUTTON_H * height);
                setButtonEffects(red);
                selection.setNodeClickable(red, it.polimi.ingsw.server.model.Color.RED.toString());
                root.getChildren().add(red);
                toRemove.add(red);
                nodesLeftBehid.add(red);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.YELLOW)){
                Button yellow = new Button();
                formatButton(yellow, MAP_3_YELLOW_ROOM_X * width, MAP_3_YELLOW_ROOM_Y * height,
                        ROOM_2X2_BUTTON_WH * height, ROOM_2X2_BUTTON_WH * height);
                setButtonEffects(yellow);
                selection.setNodeClickable(yellow,  it.polimi.ingsw.server.model.Color.YELLOW.toString());
                root.getChildren().add(yellow);
                toRemove.add(yellow);
                nodesLeftBehid.add(yellow);
            }

        });
        // get selected value
        String selected = selection.getValue();

        // remove buttons
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        //get selected room and return it
        return selectables.get(getRoomIndex(selectables, it.polimi.ingsw.server.model.Color.valueOf(selected)));
    }

    private List<Point> selectRoomMap4(List<List<Point>> selectables) {
        //Show buttons
        List<Button> toRemove = new ArrayList<>();
        Platform.runLater(()->{
            //check if room is present
            Pane root = FXWindow.getPane();
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.BLUE)){
                Button blue = new Button();
                formatButton(blue, MAP_4_BLUE_ROOM_X * width, MAP_4_BLUE_ROOM_Y * height,
                        ROOM_2_BUTTON_W * width, ROOM_123_BUTTON_H * height);
                setButtonEffects(blue);
                selection.setNodeClickable(blue,  it.polimi.ingsw.server.model.Color.BLUE.toString());
                toRemove.add(blue);
                root.getChildren().add(blue);
                nodesLeftBehid.add(blue);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.PURPLE)){
                Button purple = new Button();
                formatButton(purple, MAP_4_PURPLE_ROOM_X * width, MAP_4_PURPLE_ROOM_Y * height,
                        ROOM_2_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(purple);
                selection.setNodeClickable(purple,  it.polimi.ingsw.server.model.Color.PURPLE.toString());
                toRemove.add(purple);
                root.getChildren().add(purple);
                nodesLeftBehid.add(purple);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.RED)) {
                // add room button
                Button red = new Button();
                formatButton(red, MAP_4_RED_ROOM_X * width, MAP_4_RED_ROOM_Y * height,
                        ROOM_123_BUTTON_H * width, ROOM_2_ROTATE_BUTTON_H * height);
                setButtonEffects(red);
                selection.setNodeClickable(red, it.polimi.ingsw.server.model.Color.RED.toString());
                toRemove.add(red);
                root.getChildren().add(red);
                nodesLeftBehid.add(red);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.YELLOW)){
                Button yellow = new Button();
                formatButton(yellow, MAP_4_YELLOW_ROOM_X * width, MAP_4_YELLOW_ROOM_Y * height,
                        ROOM_123_BUTTON_H * height, ROOM_2_ROTATE_BUTTON_H * height);
                setButtonEffects(yellow);
                selection.setNodeClickable(yellow,  it.polimi.ingsw.server.model.Color.YELLOW.toString());
                root.getChildren().add(yellow);
                toRemove.add(yellow);
                nodesLeftBehid.add(yellow);
            }
            if(isRoomPresent(selectables, it.polimi.ingsw.server.model.Color.WHITE)){
                Button white = new Button();
                formatButton(white, MAP_4_WHITE_ROOM_X * width, MAP_4_WHITE_ROOM_Y * height,
                        ROOM_3_BUTTON_W * height, ROOM_123_BUTTON_H * height);
                setButtonEffects(white);
                selection.setNodeClickable(white,  it.polimi.ingsw.server.model.Color.WHITE.toString());
                toRemove.add(white);
                root.getChildren().add(white);
                nodesLeftBehid.add(white);
            }

        });
        // get selected value
        String selected = selection.getValue();

        // remove buttons
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        //get selected room and return it
        return selectables.get(getRoomIndex(selectables, it.polimi.ingsw.server.model.Color.valueOf(selected)));
    }

    /**
     * Lets client select a weapon and effect from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectShoot(List<String> selectables) {
        waitLoading();
        String weaponName = selectWeaponFree(selectables).getWeapon();
        if(weaponName == null) return new WeaponSelection(null, new ArrayList<>(),new ArrayList<>());
        WeaponCard toShootWith = player.getMatch().getWeaponByName(weaponName);
        WeaponSelection toReturn = new WeaponSelection();
        toReturn.setWeapon(toShootWith.getName());

        List<Node> toRemove = new ArrayList<>();
        List<WeaponCard.Effect> selectedEffects = new ArrayList<>();
        // select effects
        // show popup
        Platform.runLater(()->{
            Pane root = FXWindow.getPane();
            //load popup
            ImageView selectEffectPopup = loadImage(WEAPON_EFFECT_POPUP_PATH);
            selectEffectPopup.setPreserveRatio(true);
            selectEffectPopup.setFitWidth(WEAPON_EFFECT_POPUP_SIZE*width);
            selectEffectPopup.setX(WEAPON_EFFECT_POPUP_X*width);
            selectEffectPopup.setY((height-getHeight(selectEffectPopup))*WEAPON_EFFECT_POPUP_Y);
            root.getChildren().add(selectEffectPopup);
            toRemove.add(selectEffectPopup);
            nodesLeftBehid.add(selectEffectPopup);
            //load card
            ImageView selectedCard = getWeaponImage(toShootWith.getName());
            selectedCard.setY(WEAPON_EFFECT_CARD_Y*height);
            selectedCard.setX(WEAPON_EFFECT_CARD_X*width);
            selectedCard.setPreserveRatio(true);
            selectedCard.setFitWidth(WEAPON_EFFECT_CARD_SIZE*height);
            root.getChildren().add(selectedCard);
            toRemove.add(selectedCard);
            nodesLeftBehid.add(selectedCard);

            // load effect buttons
            if(toShootWith.isEffect()){
                // load effects and remember sequence
                setEffectButtons(toShootWith, toRemove, root, selectedEffects);

            }else {
                // show modes and pick one
                setModeButtons(toShootWith, toRemove, root, selectedEffects);
            }
        });

        String selected = selection.getValue();

        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        if(selected.equals("KO")){
            //undo was pressed
            return new WeaponSelection("", new ArrayList<>(), new ArrayList<>());
        }

        // show button to stop using powerups
        Platform.runLater(()-> {
            ImageView buttonResources = loadImage(USE_RESOURCES_PATH);
            buttonResources.setPreserveRatio(true);
            buttonResources.setFitWidth(USE_RESOURCES_SIZE*width);
            buttonResources.setX(USE_RESOURCES_X*width);
            buttonResources.setY(USE_RESOURCES_Y*height);
            FXWindow.getPane().getChildren().add(buttonResources);
            toRemove.add(buttonResources);
            nodesLeftBehid.add(buttonResources);
            selection.setNodeClickable(buttonResources, "-1");
            setButtonEffects(buttonResources);
        });

        // shoot was pressed -> get cost of effects selected and make player select powerups
        List<Resource> toPay = new ArrayList<>();
        selectedEffects.forEach(effect -> toPay.addAll(effect.getCost()));
        List<Powerup> selectedPowerups = new ArrayList<>();
        payWithPowerups(toPay, selectedPowerups);
        List<Integer> effectsID = new ArrayList<>();
        selectedEffects.forEach(effect -> effectsID.add(toShootWith.getEffects().indexOf(effect)));
        toReturn.setEffectID(effectsID);
        toReturn.setDiscount(selectedPowerups);

        return toReturn;
    }

    private void setModeButtons(WeaponCard toShootWith, List<Node> toRemove, Pane root, List<WeaponCard.Effect> selectedEffects) {
        int i = 0;
        for(WeaponCard.Effect effect: toShootWith.getEffects()) {
            Button selectButton = new Button();
            formatStandardButton(selectButton,
                    WEAPON_EFFECT_BUTTON_SELECT_X0 * width,
                    WEAPON_EFFECT_BUTTON_SELECT_Y0 * height+WEAPON_EFFECT_BUTTON_SELECT_OFF*i*height,
                    WEAPON_EFFECT_BUTTON_SELECT_SIZE_W * width,
                    WEAPON_EFFECT_BUTTON_SELECT_SIZE_H * height,
                    effect.getName());
            selectButton.setTooltip(new Tooltip(effect.getDescription()));
            root.getChildren().add(selectButton);
            toRemove.add(selectButton);
            nodesLeftBehid.add(selectButton);
            selectButton.setOnAction(e->selectedEffects.add(effect));
            selection.setNodeClickable(selectButton, "OK");
            i++;
        }
        // set undo button
        Button undoButton = new Button();
        formatStandardButton(undoButton,
                WEAPON_EFFECT_BUTTON_SELECT_X0*width,
                WEAPON_EFFECT_BUTTON_UNDO_Y*height,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_W*width,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_H*height,
                "Undo");
        root.getChildren().add(undoButton);
        toRemove.add(undoButton);
        nodesLeftBehid.add(undoButton);
        selection.setNodeClickable(undoButton,"KO");
    }

    private void setEffectButtons(WeaponCard toShootWith, List<Node> toRemove, Pane root, List<WeaponCard.Effect> selectedEffects) {
        int i = 0;
        List<Button> addedButtons = new ArrayList<>();
        for(WeaponCard.Effect effect: toShootWith.getEffects()) {
            Button selectButton = new Button();
            formatStandardButton(selectButton,
                    WEAPON_EFFECT_BUTTON_SELECT_X0 * width,
                    WEAPON_EFFECT_BUTTON_SELECT_Y0 * height+WEAPON_EFFECT_BUTTON_SELECT_OFF*i*height,
                    WEAPON_EFFECT_BUTTON_SELECT_SIZE_W * width,
                    WEAPON_EFFECT_BUTTON_SELECT_SIZE_H * height,
                    effect.getName());
            selectButton.setTooltip(new Tooltip(effect.getDescription()));
            root.getChildren().add(selectButton);
            toRemove.add(selectButton);
            addedButtons.add(selectButton);
            nodesLeftBehid.add(selectButton);
            setNotSelectedEffect(selectButton);
            selectButton.setOnAction(e -> {
                if (!selectedEffects.contains(effect)) {
                    selectedEffects.add(effect);
                    setSelectedEffect(selectButton);
                    selectButton.setText(effect.getName() + " (" + (selectedEffects.indexOf(effect) + 1) + ")");
                } else {
                    selectedEffects.remove(effect);
                    setNotSelectedEffect(selectButton);
                    selectButton.setText(effect.getName());
                    int t = 0;
                    for(Button b: addedButtons){
                        //System.out.println(toShootWith.getEffects().get(t).getName());
                        if(selectedEffects.contains(toShootWith.getEffects().get(t))){
                            b.setText(effect.getName() + " (" + (selectedEffects.indexOf(toShootWith.getEffects().get(t)) + 1) + ")");
                        }
                        t++;
                    }
                }
            });
            i++;
        }
        // set shoot button
        Button shootButton = new Button();
        formatStandardButton(shootButton,
                WEAPON_EFFECT_BUTTON_SELECT_X0*width,
                WEAPON_EFFECT_BUTTON_SHOOT_Y*height,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_W*width,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_H*height,
                "Shoot");
        root.getChildren().add(shootButton);
        toRemove.add(shootButton);
        nodesLeftBehid.add(shootButton);
        selection.setNodeClickable(shootButton,"OK");

        // set undo button
        Button undoButton = new Button();
        formatStandardButton(undoButton,
                WEAPON_EFFECT_BUTTON_SELECT_X0*width,
                WEAPON_EFFECT_BUTTON_UNDO_Y*height,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_W*width,
                WEAPON_EFFECT_BUTTON_SELECT_SIZE_H*height,
                "Undo");
        root.getChildren().add(undoButton);
        toRemove.add(undoButton);
        nodesLeftBehid.add(undoButton);
        selection.setNodeClickable(undoButton,"KO");
    }

    private void formatStandardButton(Button toFormat, double X, double Y, double WIDTH, double HEIGHT, String text){
        toFormat.setLayoutX(X);
        toFormat.setLayoutY(Y);
        toFormat.setText(text);
        toFormat.setPrefWidth(WIDTH);
        toFormat.setPrefHeight(HEIGHT);
        toFormat.setMinHeight(0);
        toFormat.setMinWidth(0);
    }

    /**
     * Lets client select a weapon to reload from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectReload(List<String> selectables) {
        // show button no reload
        waitLoading();
        List<ImageView> toRemove = new ArrayList<>();
        Platform.runLater(()-> {
            ImageView buttonNoReload = loadImage(NO_RELOAD_PATH);
            buttonNoReload.setPreserveRatio(true);
            buttonNoReload.setFitWidth(NO_RELOAD_SIZE*width);
            buttonNoReload.setX(NO_RELOAD_X*width);
            buttonNoReload.setY(NO_RELOAD_Y*height);
            FXWindow.getPane().getChildren().add(buttonNoReload);
            toRemove.add(buttonNoReload);
            selection.setNodeClickable(buttonNoReload, "");
            setButtonEffects(buttonNoReload);
        });

        WeaponSelection selected = selectWeapon(selectables);

        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        return selected;
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

        // select a weapon
        String weaponName = selectWeaponFree(selectables).getWeapon();
        if(weaponName == null)return new WeaponSelection("",new ArrayList<>(), new ArrayList<>());
        WeaponCard weaponCardSelected = player.getMatch().getWeaponByName(weaponName);

        // set powerup discuont buttons
        List<Resource> toPay = weaponCardSelected.getCost();
        if(!player.getThisPlayer().getWeapons().contains(weaponCardSelected)){
          toPay.remove(0);
        }

        List<ImageView> toRemove = new ArrayList<>();
        // show button to stop using powerups
        Platform.runLater(()-> {
            ImageView buttonResources = loadImage(USE_RESOURCES_PATH);
            buttonResources.setPreserveRatio(true);
            buttonResources.setFitWidth(USE_RESOURCES_SIZE*width);
            buttonResources.setX(USE_RESOURCES_X*width);
            buttonResources.setY(USE_RESOURCES_Y*height);
            FXWindow.getPane().getChildren().add(buttonResources);
            toRemove.add(buttonResources);
            nodesLeftBehid.add(buttonResources);
            selection.setNodeClickable(buttonResources, "-1");
            setButtonEffects(buttonResources);
        });


        List<Powerup> selectedPowerups = new ArrayList<>();
        payWithPowerups(toPay, selectedPowerups);

        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(toRemove);
        });

        WeaponSelection selected= new WeaponSelection();
        selected.setWeapon(weaponCardSelected.getName());
        selected.setDiscount(selectedPowerups);
        return selected;
    }

    private void payWithPowerups(List<Resource> toPay, List<Powerup> selectedPowerups) {
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
    }
    /**
     * Lets client select a weapon  from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectWeaponFree(List<String> selectables) {
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
                            System.out.println("SETTING CLICKABLE "+selectable);
                            w.setViewOrder(-100);
                        });
            });
        });
        // get selected weapon
        String weaponSelected = selection.getValue();
        if(weaponSelected.equals(""))return new WeaponSelection(null, null, null);

        WeaponCard weaponCardSelected = player.getMatch().getWeaponByName(weaponSelected);
        // remove weapon  button
        Platform.runLater(()->{
            selectables.forEach(selectable -> {
                //String fileName = getWeaponFileName(selectable);
                weaponsOnScreen.stream().filter(w -> w.getImage().getUrl().contains(selectable.toLowerCase())).forEach(w -> {
                    selection.setNodeNotClickable(w);
                    setNotClickableEffects(w);
                    System.out.println("REMOVING CLICKABLE "+selectable);
                });
            });
        });

        return new WeaponSelection(weaponCardSelected.getName(), null, null);
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
            if(selected == -1) return null;
            // remove buttons
            Platform.runLater(()->{
                selectables.forEach(selectable -> {
                    String fileName = getPowerupFileName(selectable);
                    powerups.stream().filter(p -> p.getImage().getUrl().contains(fileName)).forEach(p -> {
                        selection.setNodeNotClickable(p);
                        setNotClickableEffects(p);
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
            nodesLeftBehid.add(spawnPopUp);
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
                nodesLeftBehid.add(powerupImg);
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
        waitLoading();
        System.out.println("ACTION SELECTION");
        List<Node> buttons = new ArrayList<>();
        Platform.runLater(()->{
            if(!player.getMatch().isFrenzyEnabled()){
                int i = 0;
                for(TurnAction t: TurnAction.values()){
                    if(t.equals(TurnAction.POWERUP)){
                        setUsePowerupButton(buttons);
                        continue;
                    }
                    Button actionButton = new Button();
                    formatButton(actionButton,ACTION_BTN_X0*width,ACTION_BTN_NO_FRENZY_Y0*height+ACTION_BTN_NO_FRENZY_PADDING*height*i,
                            ACTION_BTN_SIZE_X*height,ACTION_BTN_NO_FRENZY_SIZE_Y*height);

                    setButtonEffects(actionButton);
                    selection.setNodeClickable(actionButton, t.toString());
                    buttons.add(actionButton);
                    i++;
                }
            }else if (!player.getMatch().isFirstPlayedFrenzy()){
                int i = 0;
                for(TurnAction t: TurnAction.values()){
                    if(t.equals(TurnAction.POWERUP)){
                        setUsePowerupButton(buttons);
                        continue;
                    }
                    Button actionButton = new Button();
                    formatButton(actionButton,ACTION_BTN_X0*width,ACTION_BTN_FRENZY_Y0*height+ACTION_BTN_FRENZY_PADDING*height*i,
                            ACTION_BTN_SIZE_X*height,ACTION_BTN_FRENZY_SIZE_Y*height);

                    setButtonEffects(actionButton);
                    selection.setNodeClickable(actionButton, t.toString());
                    buttons.add(actionButton);
                    i++;
                }
            } else {
                // bottom 2 actions
                int i = 0;
                for(TurnAction t: TurnAction.values()){
                    if(t.equals(TurnAction.POWERUP)){
                        setUsePowerupButton(buttons);
                        continue;
                    }
                    if(t.equals(TurnAction.MOVE)) continue;
                    Button actionButton = new Button();
                    formatButton(actionButton,ACTION_BTN_X0*width,ACTION_BTN_FRENZY_DOWN_SIZE_Y0*height+ACTION_BTN_FRENZY_PADDING*height*i,
                            ACTION_BTN_SIZE_X*height,ACTION_BTN_FRENZY_SIZE_Y*height);

                    setButtonEffects(actionButton);
                    selection.setNodeClickable(actionButton, t.toString());
                    buttons.add(actionButton);
                    i++;
                }
            }
        });

        String selected = selection.getValue();
        System.out.println("DONE SELECT ACTION:..... REMOVING BUTTONS");
        Platform.runLater(()->{
            FXWindow.getPane().getChildren().removeAll(buttons);
        });

        return TurnAction.valueOf(selected);
    }

    private void setUsePowerupButton(List<Node> buttons) {
        ImageView usePowerup = loadImage(USE_POWERUP_PATH);
        usePowerup.setX(USE_RESOURCES_X*width);
        usePowerup.setY(USE_RESOURCES_Y*height);
        usePowerup.setPreserveRatio(true);
        usePowerup.setFitWidth(USE_RESOURCES_SIZE*width);
        buttons.add(usePowerup);
        nodesLeftBehid.add(usePowerup);
        setButtonEffects(usePowerup);
        FXWindow.getPane().getChildren().add(usePowerup);
        selection.setNodeClickable(usePowerup, TurnAction.POWERUP.toString());
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
        players.clear();
        for(int i = 0; i<map.getYSize(); i++){
            for(int j = 0; j<map.getXSize(); j++){
                Cell cell =  map.getCell(j,i);
                if(cell != null) {
                    int k = 0;

                    // players
                    for (Player player : cell.getPlayers()) {
                        ImageView pawn = getPawnImage(player);
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
            droplet.setFitWidth(PLAYER_DAMAGE_DROPLET_SIZE*width);
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
            droplet.setFitWidth(PLAYER_MARK_DROPLET_SIZE*width);
            addNode(root,droplet);
            i++;
        }
        //deaths
        for(int j = 0; j < player.getMatch().getPlayerByName(player.getNickname()).getDeaths(); j++){
            ImageView skull = loadImage(SKULL_PATH);
            skull.setX(PLAYER_SKULL_DROPLET_X0*width+PLAYER_SKULL_DROPLET_OFFSET_X*width*i);
            skull.setY(PLAYER_SKULL_DROPLET_Y0*height);
            skull.setPreserveRatio(true);
            skull.setFitWidth(PLAYER_SKULL_SIZE*width);
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
            droplet.setFitWidth(ENEMY_DROPLET_SIZE*width);
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
            droplet.setFitWidth(ENEMY_DROPLET_SIZE*width);
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
            skull.setFitWidth(ENEMY_SKULL_SIZE*width);
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
            if(!player.getThisPlayer().getLoadedWeapons().contains(weapon)){
                ImageView toreloadView = loadImage(TO_RELOAD_PATH);
                toreloadView.setX(DASHBOARD_WEAPON_X*width+DASHBOARD_WEAPON_OFFSET_X*height*i);
                toreloadView.setY(DASHBOARD_WEAPON_Y*height);
                toreloadView.setPreserveRatio(true);
                toreloadView.setFitWidth(WEAPON_CARD_SIZE*width);
                root.getChildren().add(toreloadView);
            }
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
        String ext = TAB_BACK;
        if(player.getMatch().isFrenzyEnabled()){
            if(player.isFrenzyPlayer()){
                ext = "";
            } else {
                ext = TAB_BACK_FIRE;
            }
        }

        String file = TAB + player.getColor().toString().toLowerCase() + ext + TAB_EXTENSION;
        return loadImage(file);
    }

    private String getResourceFileName(Resource r) {
        return RESOURCE_DIR+ r.toString().replace(RESOURCE_STRING_FIX, "").toLowerCase()+RESOURCE_EXTENSION;
    }

    private ImageView getDropletImage(Player p){
        String file = DROPLET_DIR+p.getColor().toString().toLowerCase()+DROPLET_EXTENSION;
        return loadImage(file);
    }

    private ImageView getPawnImage(Player player){
        String path = getPawnFileName(player);
        return loadImage(path);
    }

    private String getPawnFileName(Player player) {
        return PAWN_DIR + player.getColor().toString().toLowerCase() + PAWN_EXTENSION;
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

    public void setSelectedEffect(Node n){
        n.setStyle(SELECTED_EFFECT);
    }

    public void setNotSelectedEffect(Node n){
        n.setStyle(NOT_SELECTED_EFFECT);
    }
}
