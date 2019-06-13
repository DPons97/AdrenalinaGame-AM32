package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.AdrenalinaMatch;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.List;
import java.util.function.UnaryOperator;

public class GuiView extends ClientView{

    // Constants definition
    public static final double ISTANT_RESIZE = 1.01;
    public static final double DELAY_IN = 0.4;
    public static final int DELEYED_RESIZE = 2;
    public static final double DELAY_OUT = 0.3;

    private final static String MAP = "/img/maps/";
    private final static String MAP_EXTENSION = ".png";
    private final static double MAP_X = 0;
    private final static double MAP_Y = 0;
    public static final double RELATIVE_MAP_HEIGHT = 0.8;

    private final static String TAB = "/img/tabs/";
    private final static String TAB_EXTENSION = ".png";
    private final static String TAB_BACK = "back";
    private final static double TAB_OFFSET = 0.175;
    public static final double RELATIVE_TAB_HEIGHT = 0.20;


    private double width;
    private double height;



    public GuiView(ClientPlayer player) {
        super(player);
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
            GridPane grid = FXWindow.getGrid();

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
            GridPane grid = FXWindow.getGrid();
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
            PauseTransition wait = new PauseTransition(Duration.seconds(5));
            wait.setOnFinished((e) -> {
                popup.hide();
            });
            wait.play();
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
                showGameBoard();
                break;
        }
    }

    private void showGameBoard() {
        AdrenalinaMatch match= player.getMatch();
        Platform.runLater(()-> {
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = FXWindow.getGrid();

            initGrid(grid);

            loadLayout();

        });
    }

    private void showWaitingRoom() {
        AdrenalinaMatch match= player.getMatch();
        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = FXWindow.getGrid();

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
                player.setReady();
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

    /**
     * Lets client select a player from a list
     * @param selectables list of players
     * @return selected player
     */
    @Override
    public String selectPlayer(List<String> selectables) {
        return null;
    }

    /**
     * Lets client select a cell from a list
     * @param selectables list of points
     * @return selected point
     */
    @Override
    public Point selectCell(List<Point> selectables) {
        return null;
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
        return null;
    }

    /**
     * Lets client select a weapon  from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public String selectWeapon(List<String> selectables) {
        return null;
    }

    /**
     * Lets client select a powerup from a list
     * @param selectables list of powerups
     * @return selected powerup
     */
    @Override
    public String selectPowerup(List<String> selectables) {
        return null;
    }

    /**
     * Select an action to make
     * @return action to make
     */
    @Override
    public TurnAction actionSelection() {
        return null;
    }



    private void loadLayout(){
        Stage stage = FXWindow.getStage();
        BorderPane borders = new BorderPane();
        Pane root = new Pane();

        Scene scene = new Scene(borders, stage.getScene().getWidth(),stage.getScene().getHeight());

        borders.setCenter(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);

        double pV, pH;
        if(root.getHeight() > root.getWidth()*9/16){
            pH = 0;
            pV = (root.getHeight()- root.getWidth()*9/16)/2;
            System.out.println("stagew h: " + stage.getHeight());
            System.out.println("margin v: "+pV);
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
        root.setStyle("-fx-background-color: red");

        root.setPrefSize(stage.getHeight()*16/9, stage.getHeight());

        ImageView map = loadMap();
        root.getChildren().add(map);

        loadPlayersTabs(root, map);

    }

    private void loadPlayersTabs(Pane root, ImageView map) {
        double tabSize =(width - getWidth(map));
        int i = 0;
        for(Player p: player.getMatch().getPlayers()){
            if(p.getNickname().equals(player.getNickname())){ continue;}
            ImageView tab = loadImage(TAB+p.getColor().toString().toLowerCase()+
                    (p.isFrenzyPlayer()?"":TAB_BACK)+TAB_EXTENSION);
            tab.setPreserveRatio(true);
            tab.setFitWidth(tabSize);
            tab.setX(getWidth(map));
            tab.setY(height*TAB_OFFSET*i);
            root.getChildren().add(tab);
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
        URL url = getClass().getResource(filePath);
        try {
            Image img = new Image(new FileInputStream(url.getFile().replace("%20", " ")));
            return new ImageView(img);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
