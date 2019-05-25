package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.AdrenalinaMatch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CliView extends ClientView {
    /**
     * ANSI color encoding for easy use
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Default constructor
     */
    public CliView(){
        super();
    }

    /**
     * Shows the lobby
     */
    @Override
    public void showLobby(String lobby) {
        JSONObject lobbiObj = (JSONObject) JSONValue.parse(lobby);
        int nPlayers = Integer.parseInt(lobbiObj.get("n_players").toString());
        System.out.println("Current players online: "+nPlayers);
        JSONArray matches = (JSONArray) lobbiObj.get("matches");
        System.out.println("Matches:");
        if(matches.size() == 0) System.out.println("Wow, such empty...");
        for(int i = 0; i < matches.size(); i++){
            JSONObject match = (JSONObject) matches.get(i);
            int maxPlayers = Integer.parseInt(match.get("n_players").toString());
            int mapID = Integer.parseInt(match.get("mapID").toString());
            int maxDeaths = Integer.parseInt(match.get("max_deaths").toString());
            JSONArray players = (JSONArray) match.get("players");

            System.out.println("\n"+(i+1)+". Max players: " + maxPlayers+"     Max deaths: "+ maxDeaths + "     Map: "+mapID);
            System.out.print("   Players in game: ");
            for(Object o: players) System.out.print(o.toString()+"     ");

        }
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

    /**
     * Print map
     */
    public static void main(String[] args) {
        AdrenalinaMatch match = new AdrenalinaMatch(5, 5, 120, 3);
        for (char[] col : match.getBoardMap().getMapToDraw()) {
            for (char c : col) System.out.print(c);
            System.out.print("\n");
        }
    }

    /**
     * Clear console (UNIX system only)
     */
    private static void clearConsole() {
        System.out.print("\033\143");
        System.out.flush();
    }
}
