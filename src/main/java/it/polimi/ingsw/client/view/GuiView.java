package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.controller.ConnectionType;
import it.polimi.ingsw.client.controller.ServerConnection;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
import java.util.function.UnaryOperator;

public class GuiView extends ClientView{

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

            primaryStage.show();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));



            Text scenetitle = new Text("Lobby");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            Label label = new Label("Players online: "+nPlayers);
            grid.add(label, 0, 1);
            Label label1 = new Label("Match list:\t\t");
            grid.add(label1, 0, 2);

            Button refresh = new Button();
            refresh.setText("Refresh");
            grid.add(refresh, 1, 2);
            refresh.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    grid.getChildren().clear();
                    player.updateLobby();
                }
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
                            grid.getChildren().clear();
                            player.joinGame(finalI);
                        }
                    });
                }
            }

            Button newGame= new Button();
            grid.add(newGame, 0,5);
            newGame.setText("Create new game");
            newGame.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    grid.getChildren().clear();
                    player.getView().createNewGame();
                }
            });

        });

    }

    public void createNewGame() {

        Platform.runLater(()->{
            Stage primaryStage = FXWindow.getStage();
            GridPane grid = FXWindow.getGrid();

            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));



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

            btnNewGame.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    if(!nPlayersBox.getText().equals("")&& !nDeathsBox.getText().equals("") &&
                            !mapIdBox.getText().equals("") && !turnDurationBox.getText().equals("")){
                        player.createGame(Integer.parseInt(nPlayersBox.getText()), Integer.parseInt(nDeathsBox.getText()),
                                Integer.parseInt(turnDurationBox.getText()), Integer.parseInt(mapIdBox.getText()));
                    }else {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("Please fill in all fields!");
                    }
                }
            });

        });

    }

    /**
     * Shows the launcher options
     */
    @Override
    public void showMatch() {

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
    public WeaponSelection selectCShoot(List<String> selectables) {
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
}
