package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.client.model.AdrenalinaMatch;
import it.polimi.ingsw.client.model.AmmoCell;
import it.polimi.ingsw.client.model.Cell;
import it.polimi.ingsw.client.model.Map;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.model.SpawnCell;
import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CliView extends ClientView {

    private static final int TIMEOUT = 200;

    /**
     * Data structure to hold cli assets
     */
    private static class CharAsset {
        // This asset's character
        String character;

        // Informations about walls:
        //  [direction] == true --> there is a wall
        //  [direction] == false --> no wall in this direction
        boolean north;
        boolean south;
        boolean east;
        boolean west;

        CharAsset(String character, boolean north, boolean south, boolean east, boolean west) {
            this.character = character;
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
        }

        /**
         * Get if this character is the right one with this adjacent walls
         * @param north true if there is a wall to the north of this char
         * @param south true if there is a wall to the south of this char
         * @param east true if there is a wall to the east of this char
         * @param west true if there is a wall to the west of this char
         * @return True if this char has given walls
         */
        boolean isRightChar(boolean north, boolean south, boolean east, boolean west) {
            return east == this.east && north == this.north && south == this.south && west == this.west;
        }

        String getCharacter() {
            return character;
        }
    }

    private static List<CharAsset> charAssets;

    /**
     * Reader class
     */
    private static class CommandReader extends Thread {
        private final Scanner stringReader;

        private boolean stopReader;

        private String buffer;

        private boolean pendingReading;

        private CommandReader restartedReader;

        CommandReader() {
            stringReader = new Scanner(System.in);
            this.stopReader = false;
            this.pendingReading = false;
            this.buffer = "";
        }

        @Override
        public void run() {
            while (!stopReader)
                synchronized (stringReader) {
                    if (buffer.isEmpty()) {
                        String inputBuffer = stringReader.nextLine();

                        if (restartedReader != null) {
                            restartedReader.setBuffer(inputBuffer);
                            restartedReader.start();
                        } else buffer = inputBuffer;

                        stringReader.notifyAll();
                    }
                }
        }

        /**
         * Send reader command to stop after next line
         */
        public synchronized void shutdownReader() {
            stopReader = true;
        }

        /**
         * Force new buffer into this reader
         * @param buffer new buffer
         */
        public void setBuffer(String buffer) { this.buffer = buffer; }

        /**
         * Restart this reader creating a new one
         * @return new reader
         */
        public CommandReader restartReader() {
            restartedReader = new CommandReader();
            shutdownReader();
            return restartedReader;
        }

        /**
         * Retreive next line read and reset buffer
         * @return last read line
         */
        String nextLine() {
            String toReturn;

            // Do not read buffer if someone else is doing it already
            if (pendingReading || stopReader) return null;
            pendingReading = true;

            synchronized (stringReader) {
                // If reader is restarting or shutting down, don't use buffer
                if (stopReader) {
                    stringReader.notifyAll();
                    return null;
                }

                toReturn = buffer;
                buffer = "";

                pendingReading = false;
                stringReader.notifyAll();
            }

            return toReturn;
        }
    }

    /**
     * ASCII encoding of walls (Customizable from json)
     */
    private static String wallO = "═";
    private static String wallV = "║";
    private static String wallCrossR = "╠";
    private static String wallCrossL = "╣";
    private static String wallCrossU = "╩";
    private static String wallCrossD = "╦";

    /**
     * ASCII encoding of free spaces (not customizable)
     */
    private static final String FREE_O = "─";
    private static final String FREE_V = "│";

    /**
     * Number of characters used to build walls + 1 (one of two vertical walls)
     */
    private static final int CELL_CHAR_WIDTH = 22;

    /**
     * Number of characters used to build walls + 1 (one of two horizontal walls)
     */
    private static final int CELL_CHAR_HEIGHT = 10;

    /**
     * Width of players' box inside cell
     */
    private static final int PLAYER_BOX_WIDTH = 3;

    /**
     * ANSI color encoding for easy use
     */
    private static final String COLOR_BLOCK = "█";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001b[31;1m";
    private static final String ANSI_GREEN = "\u001b[32;1m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001b[34;1m";
    private static final String ANSI_PURPLE = "\u001b[35;1m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String SPAWN_ART =  "  ___ \n" +
                                             "/ __>\n" +
                                             "\\__ \\\n" +
                                             "<___/ ";

    private static final String AMMO_BLOCK = "■";

    /**
     * ASCII icon for dead players
     */
    private static final String DEAD_PLAYER = "!";

    /**
     * ASCII icon for marks
     */
    private static final String MARK = "■";

    /**
     * ASCII icon for hit point
     */
    private static final String HIT_POINT = "■";

    /**
     * Standard format for text inside lobby table
     */
    private static final String LOBBY_HEADER =       "+--------------------------+-----------------+-------------+%n" +
                                                     "|         Nickname         |      Color      |    Ready    |%n" +
                                                     "+--------------------------+-----------------+-------------+%n";
    private static final String LOBBY_TABLE_FORMAT = "|   %-20s   |      %-7s    |  %-9s  |%n";
    private static final String LOBBY_CLOSER =       "+--------------------------+-----------------+-------------+%n%n";

    /**
     * Standard format for text inside match table
     */
    private static final String MATCHES_HEADER =         "|         Matches:       %-4s                                         |%n";
    private static final String MATCH_INFO_HEADER =     "|  ID  |  Max players  |  Max Deaths  |   Map   |       Players       |%n";
    private static final String MATCH_INFO_FORMAT =     "| %-4s |  %-11s  |  %-10s  |   %-3s   |  %-17s  |%n";
    private static final String MATCH_PLAYER_FORMAT =   "|      |               |              |         |  %-17s  |%n";
    private static final String MATCH_INFO_CLOSER =     "+------+---------------+--------------+---------+---------------------+%n";

    private static final int INFO_OFFSET = 20;

    /**
     * Standard format for text inside weapon info table
     */
    private static final String WEAPON_INFO_HEADER =    "|  Spawn  |        Weapon        |  Reload cost  |";
    private static final String WEAPON_INFO_FORMAT =    "|    %-2s    | %-20s |  %-3s %-3s %-3s  |";
    private static final String WEAPON_INFO_CLOSER =    "+---------+----------------------+---------------+";

    /**
     * Standard format for player powerups
     */
    private static final String PLAYER_POWERUP_HEADER =    "|        Powerup        |  Resource  |";
    private static final String PLAYER_POWERUP_FORMAT =    "|  %-19s  |      %-3s     |";
    private static final String PLAYER_POWERUP_CLOSER =    "+-----------------------+------------+";

    /**
     * Standard format for text inside player info table
     */
    private static final String PLAYER_INFO_HEADER =    "|         Nickname         |       Life points       |  Marks  |        Ammos        |";
    private static final String PLAYER_INFO_FORMAT =    "|   %s%-20s%s   | %-24s|  %-6s |  %-18s |";
    private static final String PLAYER_REWARD_HEADER =  "|::::::::::::::::::::::::::| %-24s|:::::::::|:::::::::::::::::::::|";
    private static final String PLAYER_WEAPON_HEADER =  "|              Weapons:    | %-33s |  %-18s |";
    private static final String PLAYER_WEAPON_FORMAT =  "|                          | %-33s |  %-18s |";
    private static final String PLAYER_INFO_CLOSER =    "+--------------------------+-------------------------+---------+---------------------+";

    /**
     * Leaderboard format
     */
    private static final String LEADERBOARD_INTRO =
            "%n    ___    ____  ____  _______   _____    __    _____   _____ %n" +
            "   /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / /   |%n" +
            "  / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / /| |%n" +
            " / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / ___ |%n" +
            "/_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_/  |_|%n" +
            "                                                              %n";

    private static final String LEADERBOARD_HEADER =        "\t\t╔═══╦════════════════════════╦═════════╗%n" +
                                                            "\t\t║   ║         Player         ║  Score  ║%n" +
                                                            "\t\t╠═══╬════════════════════════╬═════════╣%n";
    private static final String LEADERBOARD_FORMAT =        "\t\t║ %-3s ║  %s%-20s%s  ║  %-5s  ║%n";
    private static final String LEADERBOARD_CLOSER =        "\t\t╚═══╩════════════════════════╩═════════╝%n";

    /**
     * Default action selection messages
     */
    private static final String INVALID_SELECTION = "Invalid selection";

    private static final String IDLE_MESSAGE = "Waiting server...";

    private static final String NOT_YOUR_TURN = "It's someone else's turn...";

    private static final String ACTION_SELECTION =
            "It's your turn! Here's what you can do: \n\n" +
            "[R]un: move up to 3 cells \n" +
            "[P]ick: move up to 1 cell and pick something (2+ damage >>> +1 movement) \n" +
            "[S]hoot: shoot someone with one of your weapons! (6+ damage >>> +1 movement before shooting) \n\n" +
            "[U]se powerup (Newton or Teleporter) \n\n" +
            "What's your choice?  ";

    private static final String FRENZY_ACTION_BEFORE_FIRST =
            "FREEEEEEEENZYYYYY! Now it's your last chance to get those points! Here's what you can do: \n\n" +
            "[S] Move up to 1 cell, reload (if you want), then SHOOT! \n" +
            "[R] Move up to 4 cells \n" +
            "[P] Move up to 2 cell, then pick something \n\n" +
            "[U] Use powerup (Newton or Teleporter) \n\n" +
            "What's your choice?  ";

    private static final String FRENZY_ACTION_AFTER_FIRST =
            "FREEEEEEEENZYYYYY! Now it's your last chance to get those points! Here's what you can do: \n\n" +
            "[S] Move up to 2 cell, reload (if you want), then SHOOT! \n" +
            "[P] Move up to 3 cell, then pick something \n\n" +
            "[U] Use powerup (Newton or Teleporter) \n\n" +
            "What's your choice?  ";

    /**
     * Player selection message
     */
    private static final String PLAYER_SELECTION = "Select one of players listed above: \n";

    /**
     * Cell selection
     */
    private static final String CELL_SELECTION = "Select one of cells listed above: \n";

    /**
     * Room selection
     */
    private static final String ROOM_SELECTION = "Select one of rooms listed above: \n";

    /**
     * Weapon selection
     */
    private static final String WEAPON_SELECTION = "Select one of weapons listed above: \n";

    /**
     * Effect selection
     */
    private static final String EFFECT_SELECTION = "Select one of listed effects to apply ([X] will confirm selection): \n";

    /**
     * Powerup selection
     */
    private static final String POWERUP_SELECTION = "Select one of powerups listed above: \n";

    /**
     *  Command Line reader
     */
    private CommandReader cmdReader = new CommandReader();

    private String selectionMessage;

    private String alertMessage;

    private final Object selectionLock = new Object();

    public CliView(ClientPlayer player) {
        super(player);
        parseCliAssets();
        selectionMessage = IDLE_MESSAGE;
        alertMessage = "";
        cmdReader.start();
    }

    /**
     * Retreive last response (waiting if none is provided)
     * @return Last player's response
     */
    private String getResponse() {
        String response;
        response = cmdReader.nextLine();

        if (response == null) return null;

        while (response.isEmpty()) {
            response = cmdReader.nextLine();
            if (response == null) return null;
            try {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return response;
    }

    /**
     * Shows the lobby
     */
    @Override
    public void showLobby(String lobby) {
        JSONObject lobbiObj = (JSONObject) JSONValue.parse(lobby);
        int nPlayers = Integer.parseInt(lobbiObj.get("n_players").toString());

        clearConsole();
        System.out.format(
                "...::: Adrenalina LOBBY :::... %n" +
                "Current players online: " + nPlayers + "%n%n");

        JSONArray matches = (JSONArray) lobbiObj.get("matches");
        System.out.printf(MATCH_INFO_CLOSER);
        System.out.format(MATCHES_HEADER, matches.size());
        System.out.printf(MATCH_INFO_CLOSER);

        // Print header table
        System.out.format(MATCH_INFO_HEADER + MATCH_INFO_CLOSER);

        for(int i = 0; i < matches.size(); i++){
            // Print table
            JSONObject match = (JSONObject) matches.get(i);
            int maxPlayers = Integer.parseInt(match.get("n_players").toString());
            int mapID = Integer.parseInt(match.get("mapID").toString());
            int maxDeaths = Integer.parseInt(match.get("max_deaths").toString());
            JSONArray players = (JSONArray) match.get("players");

            // Draw Match infos
            System.out.format(MATCH_INFO_FORMAT, i + 1, maxPlayers, maxDeaths, mapID, players.get(0).toString());

            // Draw match players
            for(Object o: players) {
                if (!o.toString().equals(players.get(0).toString()))
                    System.out.format(MATCH_PLAYER_FORMAT, o.toString());
            }
            System.out.format(MATCH_INFO_CLOSER);
        }

        if(matches.isEmpty()){
            System.out.printf("Wow, such empty...%n");
            System.out.printf("CREATE [N]ew match. [Any key to reload]%n");
        } else {
            System.out.printf("Do you want to CREATE [N]ew match, or JOIN an existing one?%n" +
                    "(Specify match's ID to join) [Any key to reload]%n");
        }

        String response;
        response = getResponse();
        if (response == null) return;

        int choice;
        try {
            choice = Integer.parseInt(response) - 1;
        } catch (NumberFormatException e) {
            if (response.equals("N") || response.equals("n")) createNewGame();
            else player.updateLobby();
            return;
        }

        if(choice >= 0 && choice <  matches.size()) {
            player.joinGame(choice);
            clearConsole();
        } else {
            player.updateLobby();
        }
    }

    // Main for testing cli
    public static void main(String[] args) {
        it.polimi.ingsw.server.model.AdrenalinaMatch match = new it.polimi.ingsw.server.model.AdrenalinaMatch(5, 8, 120, 1);

        it.polimi.ingsw.server.model.Player newPlayer1 = new it.polimi.ingsw.server.model.Player(match, "Davide");
        newPlayer1.setColor(Color.WHITE);

        ClientPlayer p1;
        try {
            p1 = new ClientPlayer("Davide");
            CliView view = new CliView(p1);

            it.polimi.ingsw.server.model.Player newPlayer2 = new it.polimi.ingsw.server.model.Player(match, "Luca");
            newPlayer2.setColor(Color.BLUE);
            it.polimi.ingsw.server.model.Player newPlayer3 = new it.polimi.ingsw.server.model.Player(match, "Mike");
            newPlayer3.setColor(Color.GREEN);
            it.polimi.ingsw.server.model.Player newPlayer4 = new it.polimi.ingsw.server.model.Player(match, "Conti");
            newPlayer4.setColor(Color.PURPLE);
            it.polimi.ingsw.server.model.Player newPlayer5 = new it.polimi.ingsw.server.model.Player(match, "Lorenzo");
            newPlayer5.setColor(Color.YELLOW);

            match.addPlayer(newPlayer1);
            match.addPlayer(newPlayer2);
            match.addPlayer(newPlayer3);
            match.addPlayer(newPlayer4);
            match.addPlayer(newPlayer5);

            match.setMatchState(MatchState.PLAYER_TURN);

            newPlayer1.respawn(match.getBoardMap().getSpawnPoints().get(1));
            newPlayer3.respawn(match.getBoardMap().getSpawnPoints().get(0));
            newPlayer4.respawn(match.getBoardMap().getSpawnPoints().get(1));
            newPlayer5.respawn(match.getBoardMap().getSpawnPoints().get(2));

            newPlayer1.takeDamage(newPlayer2);
            newPlayer1.takeDamage(newPlayer2);
            newPlayer1.takeDamage(newPlayer3);
            newPlayer1.takeDamage(newPlayer3);
            newPlayer1.takeDamage(newPlayer4);
            newPlayer1.takeDamage(newPlayer4);
            newPlayer1.takeDamage(newPlayer4);
            newPlayer1.takeDamage(newPlayer4);
            newPlayer1.takeDamage(newPlayer3);
            newPlayer1.takeDamage(newPlayer3);
            newPlayer1.takeDamage(newPlayer2);

            newPlayer1.takeMark(newPlayer3);
            newPlayer1.takeMark(newPlayer4);
            newPlayer1.takeMark(newPlayer3);

            newPlayer2.enableFrenzy();

            newPlayer2.takeDamage(newPlayer1);
            newPlayer2.takeDamage(newPlayer1);
            newPlayer2.takeDamage(newPlayer3);
            newPlayer2.takeDamage(newPlayer5);
            newPlayer2.takeDamage(newPlayer4);
            newPlayer2.takeDamage(newPlayer3);
            newPlayer2.takeDamage(newPlayer1);
            newPlayer2.takeDamage(newPlayer1);

            newPlayer1.addAmmo(Resource.BLUE_BOX);
            newPlayer1.addAmmo(Resource.BLUE_BOX);
            newPlayer1.addAmmo(Resource.BLUE_BOX);
            newPlayer1.addAmmo(Resource.RED_BOX);
            newPlayer1.addAmmo(Resource.RED_BOX);
            newPlayer1.addAmmo(Resource.RED_BOX);
            newPlayer1.addAmmo(Resource.YELLOW_BOX);
            newPlayer1.addAmmo(Resource.YELLOW_BOX);
            newPlayer1.addAmmo(Resource.YELLOW_BOX);

            newPlayer5.addAmmo(Resource.BLUE_BOX);
            newPlayer5.addAmmo(Resource.RED_BOX);
            newPlayer5.addAmmo(Resource.YELLOW_BOX);

            newPlayer5.pickWeapon(match.getBoardMap().getSpawnPoints().get(2).getWeapons().get(0), new ArrayList<>());

            newPlayer5.addAmmo(Resource.BLUE_BOX);
            newPlayer5.addAmmo(Resource.RED_BOX);
            newPlayer5.addAmmo(Resource.YELLOW_BOX);

            newPlayer5.pickWeapon(match.getBoardMap().getSpawnPoints().get(2).getWeapons().get(1), new ArrayList<>());

            AdrenalinaMatch clientMatch = new AdrenalinaMatch();
            clientMatch.update(match.toJSON());
            p1.setMatch(clientMatch);

            List<WeaponCard> newLoaded = new ArrayList<>();
            newLoaded.add(clientMatch.getPlayers().get(4).getLoadedWeapons().get(0));
            clientMatch.getPlayers().get(4).setLoadedWeapons(newLoaded);

            view.showMatch();
            List<String> leaderboard = new ArrayList<>();
            for (int i=0; i < clientMatch.getPlayers().size(); i++) {
                clientMatch.getPlayers().get(i).setScore( clientMatch.getPlayers().size() - i);
                leaderboard.add(clientMatch.getPlayers().get(i).getNickname());
            }

            view.showLeaderboard(leaderboard);

        } catch (TooManyPlayersException | MatchAlreadyStartedException | PlayerAlreadyExistsException | InventoryFullException | InsufficientResourcesException e) {
            e.printStackTrace();
        } catch (NoItemInInventoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initMatch() {
        // Shutdown and restart input reader (read pending from showMatch
        cmdReader = cmdReader.restartReader();
    }

    /**
     * Shows the launcher options
     */
    @Override
    public void showMatch() {
        AdrenalinaMatch match = player.getMatch();
        boolean isReady = match.getPlayers().stream().
                filter(p -> p.getNickname().equals(player.getNickname())).
                collect(Collectors.toList()).get(0).isReadyToStart();

        clearConsole();

        // Draw match pre-game
        if (match.getState() == MatchState.NOT_STARTED) {
            // Print header + table
            System.out.format(
                    "...::: Welcome to the match, " + player.getNickname() + " :::... %n%n" +
                            "Players: " + match.getPlayers().size() + " / " + match.getnPlayers() + "%n" + LOBBY_HEADER);

            for (Player p : match.getPlayers()) {
                System.out.format(LOBBY_TABLE_FORMAT, p.getNickname(), p.getColor().toString(),
                        (p.isReadyToStart()) ? "Ready" : "Not Ready");
            }

            System.out.printf(LOBBY_CLOSER);

            lobbyNextCommand(isReady);

        } else if (match.getState() == MatchState.PLAYER_TURN || match.getState() == MatchState.FRENZY_TURN) {
            synchronized (this) {
                // Print players
                System.out.printf("%n      ");
                for (Player p : match.getPlayers()) {
                    String playerString;
                    if (p.isDead())
                        playerString = ANSI_RED + DEAD_PLAYER + " " + p.getNickname() + " [DEAD] " + DEAD_PLAYER + ANSI_RESET;
                    else playerString = getANSIColor(p) + p.getNickname() + ANSI_RESET;

                    System.out.print(playerString + "      ");
                }
                System.out.printf("%n%n");

                // Print map
                drawMap(match);

                // Print alerts
                System.out.printf(alertMessage);
            }
        } else if (match.getState() == MatchState.FINAL_SCORING) {
            synchronized (this) {
                System.out.print("Match ended. Retrieving leaderboard...");
            }
        }
    }

    /**
     * Retreive and handles next command given by player
     * @param isReady true if player is ready
     */
    private void lobbyNextCommand(boolean isReady) {
        String response;
        if (!isReady) {
            System.out.printf("Are you [R]eady? ([E] to go back to lobby)%n");

            while (true) {
                // Retreive next command
                response = getResponse();
                if (response == null || player.getMatch().getState() != MatchState.NOT_STARTED) return;

                if (response.equals("R") || response.equals("r")) {
                    player.setReady(true);
                    break;
                } else if (response.equals("E") || response.equals("e")) {
                    player.backToLobby();
                    player.updateLobby();
                    break;
                } else System.out.printf("Invalid command. Press [R] if you are ready or [E] to go back to lobby%n");
            }

        } else {
            System.out.print("You are now ready to play. Take a snack while waiting to start... ([E]xit or [N]ot-Ready)");

            while (true) {
                response = getResponse();
                if (response == null || player.getMatch().getState() != MatchState.NOT_STARTED) return;

                if (response.equals("E") || response.equals("e")) {
                    player.backToLobby();
                    player.updateLobby();
                    break;
                } else if (response.equals("N") || response.equals("n")) {
                    player.setReady(false);
                    break;
                } else System.out.printf("Invalid command. Press [E] to go back to lobby or [N]ot-Ready%n");
            }
        }
    }

    @Override
    public void showLeaderboard(List<String> leaderboard) {
        clearConsole();

        System.out.printf(LEADERBOARD_INTRO);
        System.out.printf(LEADERBOARD_HEADER);

        for (String playerName : leaderboard) {
            Player p = player.getThisPlayer().getMatch().getPlayerByName(playerName);

            if (p == null) continue;

            System.out.format(LEADERBOARD_FORMAT, getANSIColor(p) + AMMO_BLOCK + ANSI_RESET,
                    getANSIColor(p), p.getNickname(), ANSI_RESET, String.valueOf(p.getScore()));
        }

        System.out.printf(LEADERBOARD_CLOSER);
    }

    /**
     * Lets client select a player from a list
     * @param selectables list of players
     * @return selected player
     */
    @Override
    public String selectPlayer(List<String> selectables) {
        StringBuilder messageToPrint = new StringBuilder();
        messageToPrint.append(PLAYER_SELECTION);

        for (int i = 0; i < selectables.size(); i++) {
            messageToPrint.append("[").append(i+1).append("] ").append(selectables.get(i)).append("\n");
        }
        return getIndexedResponse(selectables, messageToPrint);
    }

    /**
     * Lets client select a cell from a list
     * @param selectables list of points
     * @return selected point
     */
    @Override
    public Point selectCell(List<Point> selectables) {
        StringBuilder messageToPrint = new StringBuilder();
        messageToPrint.append(CELL_SELECTION);

        for (int i = 0; i < selectables.size(); i++) {
            messageToPrint.append("[").append(i+1).append("] ").append("<").append(selectables.get(i).getX()).append(", ").append(selectables.get(i).getY()).append(">").append("\n");
        }
        return getIndexedResponse(selectables, messageToPrint);
    }

    /**
     * Lets client select a room from a list
     * @param selectables list of rooms
     * @return selected room
     */
    @Override
    public List<Point> selectRoom(List<List<Point>> selectables) {
        StringBuilder messageToPrint = new StringBuilder();
        messageToPrint.append(ROOM_SELECTION);

        for (int i = 0; i < selectables.size(); i++) {
            int x = selectables.get(i).get(0).getX();
            int y = selectables.get(i).get(0).getY();

            messageToPrint.append("[").append(i+1).append("] ")
                    .append(player.getMatch().getBoardMap().getCell(x, y).getColor()).append("\n");
        }
        return getIndexedResponse(selectables, messageToPrint);
    }

    /**
     * Lets client select a weapon and effect from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectShoot(List<String> selectables) {
        WeaponSelection selection = selectWeaponFree(selectables);
        if (selection.getWeapon() == null) return selection;

        WeaponCard selectedWeapon = player.getThisPlayer().getWeapon(selection.getWeapon());

        // Let player select effects to apply
        selection.setEffectID(selectEffects(selectedWeapon));

        // Select powerups to use as discount
        List<Resource> totalCost = new ArrayList<>();
        for (Integer eff : selection.getEffectID()) {
            totalCost.addAll(selectedWeapon.getEffects().get(eff).getCost());
        }

        selection.setDiscount(selectDiscount(totalCost));

        return selection;
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
     * Lets client select effects to use given a already selected weapon
     * @param selectedWeapon selected weapon
     * @return List of integer representing ready-to-send effects IDs
     */
    private List<Integer> selectEffects(WeaponCard selectedWeapon) {
        StringBuilder messageToPrint = new StringBuilder();
        List<WeaponCard.Effect> effects = selectedWeapon.getEffects();
        List<WeaponCard.Effect> selectedEffects = new ArrayList<>();
        WeaponCard.Effect selectedEffect;
        do {
            messageToPrint.append(EFFECT_SELECTION);

            // Print selected weapon
            messageToPrint.append("Selected weapon: ").append(selectedWeapon.getName()).append("\n");

            for (int i = 0; i < effects.size(); i++) {
                // Print name and description
                messageToPrint.append("[").append(i+1).append("] ")
                        .append(effects.get(i).getName()).append("\n\t- Cost = ");

                // Print cost
                for (Resource res : effects.get(i).getCost())
                    messageToPrint.append(getANSIColor(res)).append(AMMO_BLOCK).append(" ").append(ANSI_RESET);

                messageToPrint.append("\n");
            }
            messageToPrint.append("\n");

            // Print already selected effects
            messageToPrint.append("Selected effects: \n");
            for (WeaponCard.Effect eff : selectedEffects)
                messageToPrint.append(eff.getName()).append("\n");

            selectedEffect = getIndexedResponse(effects, messageToPrint);
            messageToPrint = new StringBuilder();

            if (selectedEffect != null && !selectedEffects.contains(selectedEffect)) {
                selectedEffects.add(selectedEffect);
                effects.remove(selectedEffect);
            }
        } while (selectedEffect != null && selectedWeapon.isEffect() && !effects.isEmpty());

        // Transform selected effects in integers to be sent through network
        List<Integer> effectsInteger = new ArrayList<>();
        for (WeaponCard.Effect eff : selectedEffects) {
            effectsInteger.add(selectedWeapon.getEffects().indexOf(eff));
        }
        return effectsInteger;
    }

    /**
     * Lets client select powerups to use as discount in different payment operations
     * @param toBePayed amount of resources to be payed
     * @return list of powerups used as discount
     */
    private List<Powerup> selectDiscount(List<Resource> toBePayed) {
        StringBuilder messageToPrint = new StringBuilder();
        List<Powerup> selectedDiscount = new ArrayList<>();
        Powerup selectedPowerup;

        // Print only powerups that can be used as discount
        List<Powerup> powerups = player.getThisPlayer().getPowerups().stream()
                .filter(powerup -> toBePayed.contains(powerup.getBonusResource())).collect(Collectors.toList());

        if (powerups.isEmpty()) return selectedDiscount;

        do {
            messageToPrint.append(POWERUP_SELECTION);

            for (int i = 0; i < powerups.size(); i++) {
                messageToPrint.append("[").append(i+1).append("] ").append(powerups.get(i).getName()).append(" - ")
                        .append(getANSIColor(powerups.get(i).getBonusResource())).append(AMMO_BLOCK).append(ANSI_RESET)
                        .append("\n");
            }
            messageToPrint.append("\n");

            // Print already selected powerups
            messageToPrint.append("Selected powerups: \n");
            for (Powerup pow : selectedDiscount)
                messageToPrint.append(getANSIColor(pow.getBonusResource())).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");

            messageToPrint.append("\n");

            selectedPowerup = getIndexedResponse(powerups, messageToPrint);
            if (selectedPowerup != null && !selectedDiscount.contains(selectedPowerup)) {
                selectedDiscount.add(selectedPowerup);
                powerups.remove(selectedPowerup);
            }
            messageToPrint = new StringBuilder();
        } while (selectedPowerup != null);

        return selectedDiscount;
    }

    /**
     * Lets client select a weapon  from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    @Override
    public WeaponSelection selectWeaponFree(List<String> selectables) {
        StringBuilder messageToPrint = new StringBuilder();

        // Let player select a weapon
        messageToPrint.append(WEAPON_SELECTION);

        // Get all weapon cards with given names
        List<WeaponCard> selectableCards = new ArrayList<>();
        for (String selectable : selectables) {
            selectableCards.add(player.getMatch().getWeapons().stream()
                    .filter(weaponCard -> weaponCard.getName().equals(selectable))
                    .collect(Collectors.toList()).get(0));
        }

        for (int i = 0; i < selectableCards.size(); i++) {
            messageToPrint.append("[").append(i+1).append("] ")
                    .append(selectableCards.get(i).getName()).append(" ( ");

            // Print weapon cost
            for (Resource res : selectableCards.get(i).getCost()) {
                messageToPrint.append(getANSIColor(res)).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");
            }
            messageToPrint.append(")\n");

            // Print all weapon infos
            for (WeaponCard.Effect eff : selectableCards.get(i).getEffects()) {
                StringBuilder formattedCost = new StringBuilder();

                for (Resource res : eff.getCost()) {
                    formattedCost.append(getANSIColor(res)).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");
                }

                messageToPrint.append("\t").append(eff.getName()).append(" - ").append(" ( ")
                        .append(formattedCost).append(")\n");
            }

            messageToPrint.append("\n");
        }
        messageToPrint.delete(messageToPrint.length()-1, messageToPrint.length()-1);

        WeaponSelection toReturn = new WeaponSelection();
        toReturn.setWeapon(getIndexedResponse(selectables, messageToPrint));
        return toReturn;
    }

    /**
     * Lets client select a weapon from a list (for buy and reload)
     * @param selectables list of weapons
     * @return selected weapon and discount
     */
    @Override
    public WeaponSelection selectWeapon(List<String> selectables) {
        WeaponSelection toReturn = selectWeaponFree(selectables);
        if (toReturn.getWeapon() == null) return toReturn;

        WeaponCard selectedWeapon = player.getMatch().getWeaponByName(toReturn.getWeapon());

        // Select weapon to pick and discount
        if (player.getThisPlayer().getWeapons().contains(selectedWeapon)) {
            // Reload case (pay all cost)
            toReturn.setDiscount(selectDiscount(selectedWeapon.getCost()));
        } else {
            // Player is buying a new weapon (weapon is reloaded)
            List<Resource> weaponCost = selectedWeapon.getCost();
            weaponCost.remove(0);

            toReturn.setDiscount(selectDiscount(weaponCost));
        }

        return toReturn;
    }

    /**
     * Lets client select a powerup from a list
     * @param selectables list of powerups
     * @return selected powerup
     */
    @Override
    public Powerup selectPowerup(List<Powerup> selectables) {
        StringBuilder messageToPrint = new StringBuilder();

        messageToPrint.append(POWERUP_SELECTION);

        // Print powerups that can be choosen
        for (int i = 0; i < selectables.size(); i++) {
            messageToPrint.append("[").append(i+1).append("] ").append(selectables.get(i).getName()).append(" - ")
                    .append(getANSIColor(selectables.get(i).getBonusResource())).append(AMMO_BLOCK).append(ANSI_RESET)
                    .append("\n");
        }

        return getIndexedResponse(selectables, messageToPrint);
    }

    /**
     * Retreive a response from user given a selectable list
     * @param selectables list of selectable options
     * @param messageToPrint additional message to print
     * @param <T> type of selectable
     * @return T selected from user
     */
    private <T> T getIndexedResponse(List<T> selectables, StringBuilder messageToPrint) {
        messageToPrint.append("\n[X] to STOP selection");

        synchronized (selectionLock) {
            selectionMessage = messageToPrint.toString();
            showMatch();

            String response = getResponse();
            if (response == null) {
                selectionLock.notifyAll();
                return null;
            }

            while (!response.equals("X") && !response.equals("x")) {
                int choice;
                try {
                    choice = Integer.parseInt(response) - 1;
                } catch (NumberFormatException e) {
                    System.out.println(INVALID_SELECTION);
                    response = getResponse();
                    if (response == null) {
                        selectionLock.notifyAll();
                        return null;
                    }
                    continue;
                }

                if (choice >= 0 && choice < selectables.size()) {
                    selectionMessage = IDLE_MESSAGE;
                    showMatch();

                    selectionLock.notifyAll();
                    return selectables.get(choice);
                } else {
                    System.out.println(INVALID_SELECTION);
                    response = getResponse();
                    if (response == null) {
                        selectionLock.notifyAll();
                        return null;
                    }
                }
            }
            selectionMessage = IDLE_MESSAGE;
            showMatch();

            selectionLock.notifyAll();
        }
        return null;
    }

    /**
     * Select an action to make
     * @return action to make
     */
    @Override
    public TurnAction actionSelection() {
        synchronized (selectionLock) {
            if (!player.getMatch().isFrenzyEnabled()) {
                selectionMessage = ACTION_SELECTION;
                showMatch();

                String response = getResponse();
                while (true) {
                    if (response == null) return null;

                    switch (response) {
                        case "R":
                        case "r":
                            selectionMessage = IDLE_MESSAGE;
                            System.out.println(IDLE_MESSAGE);

                            selectionLock.notifyAll();
                            return TurnAction.MOVE;
                        case "P":
                        case "p":
                            selectionMessage = IDLE_MESSAGE;
                            System.out.println(IDLE_MESSAGE);

                            selectionLock.notifyAll();
                            return TurnAction.PICK;
                        case "S":
                        case "s":
                            selectionMessage = IDLE_MESSAGE;
                            System.out.println(IDLE_MESSAGE);

                            selectionLock.notifyAll();
                            return TurnAction.SHOOT;
                        case "u":
                        case "U":
                            selectionMessage = IDLE_MESSAGE;
                            System.out.println(IDLE_MESSAGE);

                            selectionLock.notifyAll();
                            return TurnAction.POWERUP;
                        default:
                            System.out.println(INVALID_SELECTION);
                            response = getResponse();
                    }
                }
            } else {
                if (!player.getMatch().isFirstPlayedFrenzy()) {
                    selectionMessage = FRENZY_ACTION_BEFORE_FIRST;
                    showMatch();

                    String response = getResponse();
                    while (true) {
                        if (response == null) return null;

                        switch (response) {
                            case "S":
                            case "s":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.SHOOT;
                            case "R":
                            case "r":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.MOVE;
                            case "P":
                            case "p":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.PICK;
                            case "u":
                            case "U":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.POWERUP;
                            default:
                                System.out.println(INVALID_SELECTION);
                                response = getResponse();
                        }
                    }
                } else {
                    selectionMessage = FRENZY_ACTION_AFTER_FIRST;
                    showMatch();

                    String response = getResponse();
                    while (true) {
                        if (response == null) return null;

                        switch (response) {
                            case "S":
                            case "s":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.SHOOT;
                            case "P":
                            case "p":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.PICK;
                            case "u":
                            case "U":
                                selectionMessage = IDLE_MESSAGE;
                                System.out.println(IDLE_MESSAGE);

                                selectionLock.notifyAll();
                                return TurnAction.POWERUP;
                            default:
                                System.out.println(INVALID_SELECTION);
                                response = getResponse();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void createNewGame() {
        int maxPlayers;
        int maxDeaths;
        int turnDuration;
        int mapID;
        System.out.print("Enter number of players: ");
        maxPlayers = Integer.parseInt(getResponse());
        System.out.print("Enter number of deaths: ");
        maxDeaths = Integer.parseInt(getResponse());
        System.out.print("Enter turn duration [seconds]: ");
        turnDuration = Integer.parseInt(getResponse());
        System.out.print("Enter map id: ");
        mapID = Integer.parseInt(getResponse());

        player.createGame(maxPlayers,maxDeaths,turnDuration, mapID);
    }

    @Override
    public void showAlert(String message) {
        alertMessage = "ALERT: " + message;
        if(message.equals("Time's over"))
            selectionMessage = IDLE_MESSAGE;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                alertMessage = "";
            }
        }, ALERT_DURATION * 1000);

    }

    /**
     * Parse json with cli assets
     */
    private static void parseCliAssets() {
        charAssets = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new BufferedReader(new InputStreamReader(CliView.class
                    .getResourceAsStream("/json/cliAssets.json"), StandardCharsets.UTF_8)));
            JSONArray jsonArray = (JSONArray) obj;

            for (Object character : jsonArray) {
                JSONObject currentChar = (JSONObject) character;

                charAssets.add(new CharAsset(
                        currentChar.get("char").toString(),
                        Boolean.parseBoolean(currentChar.get("north").toString()),
                        Boolean.parseBoolean(currentChar.get("south").toString()),
                        Boolean.parseBoolean(currentChar.get("east").toString()),
                        Boolean.parseBoolean(currentChar.get("west").toString()))
                );
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        // Init walls
        wallO = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                false, false, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        wallV = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, true, false, false)).collect(Collectors.toList()).get(0).getCharacter();

        wallCrossD = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                false, true, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        wallCrossU = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, false, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        wallCrossR = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, true, true, false)).collect(Collectors.toList()).get(0).getCharacter();

        wallCrossL = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, true, false, true)).collect(Collectors.toList()).get(0).getCharacter();
    }

    /**
     * Print a complete version of current map in current state
     */
    private void drawMap(AdrenalinaMatch match) {
        // Initializing print canvas size (map + weapon and additional tables)
        int printWidth = match.getBoardMap().getYSize() * CELL_CHAR_WIDTH + 1 + INFO_OFFSET;
        int printHeight = match.getBoardMap().getXSize() * CELL_CHAR_HEIGHT + 1 + INFO_OFFSET;

        String[][] mapToReturn = new String[printHeight][printWidth];

        // Init map
        for (int i = 0; i < mapToReturn.length; i++) {
            for (int j = 0; j < mapToReturn[0].length; j++) mapToReturn[i][j] = "\u0020";
        }

        // Draw single cells
        for (int i = 0; i < match.getBoardMap().getXSize(); i++) {
            for (int j = 0; j < match.getBoardMap().getYSize(); j++) drawCell(mapToReturn, match.getBoardMap().getCell(i,j), i, j,
                    match.getBoardMap().getXSize(), match.getBoardMap().getYSize());
        }

        // Draw weapon info table
        drawWeaponInfo(mapToReturn, match.getBoardMap(), 0, printWidth - INFO_OFFSET/2);

        // Draw player infos
        drawPlayerInfo(mapToReturn, match, 15, printWidth - INFO_OFFSET/2);

        // Draw player's powerups
        drawPlayerPowerups(mapToReturn, match, 0, printWidth - 3);

        // Write selection message
        int k = ((match.getBoardMap().getYSize() - 1) * CELL_CHAR_HEIGHT) + 2;
        int w = 0;
        for (char c : selectionMessage.toCharArray()) {
            if (c != '\n') {
                mapToReturn[k][w] = String.valueOf(c);
                w++;
            } else {
                w = 0;
                k++;
            }
        }

        // Actual drawing
        for (String[] col : mapToReturn) {
            for (int i = 0; i < printWidth; i++) {
                System.out.print(col[i]);
            }
            System.out.printf("%n");
        }
    }

    /**
     * Set correct character in charMap based on cell toDraw
     * @param charMap character map that will be printed
     * @param toDraw cell to draw
     * @param x coordinate of toDraw
     * @param y coordinate of toDraw
     */
    private void drawCell(String[][] charMap, Cell toDraw, int x, int y, int xSize, int ySize) {
        if (toDraw == null) return;

        // Starting and
        int startingX = x * CELL_CHAR_HEIGHT;
        int endingX = startingX + CELL_CHAR_HEIGHT;
        int startingY = y * CELL_CHAR_WIDTH;
        int endingY = startingY + CELL_CHAR_WIDTH;

        // Sides
        for (Direction dir : Direction.values()) {
            switch (dir) {
                case NORTH:
                    drawHorizontalSide(charMap, toDraw, dir, startingX, startingY + 1);
                    break;
                case SOUTH:
                    drawHorizontalSide(charMap, toDraw, dir, endingX, startingY + 1);
                    break;
                case WEST:
                    drawVerticalSide(charMap, toDraw, dir, startingX + 1, startingY);
                    break;
                case EAST:
                    drawVerticalSide(charMap, toDraw, dir, startingX + 1, endingY);
                    break;
            }
        }

        // Corners
        // Draw every corner of cell
        drawCorner(charMap, startingX, startingY, xSize, ySize);   // North-West
        drawCorner(charMap, startingX,startingY + CELL_CHAR_WIDTH, xSize, ySize);   // North-East
        drawCorner(charMap, startingX + CELL_CHAR_HEIGHT, startingY, xSize, ySize);   // South-West
        drawCorner(charMap, startingX + CELL_CHAR_HEIGHT, startingY + CELL_CHAR_WIDTH, xSize, ySize); // South-East

        // Draw cell coordinates
        charMap[startingX + CELL_CHAR_HEIGHT-2][startingY+CELL_CHAR_WIDTH-6] = "<";
        charMap[startingX + CELL_CHAR_HEIGHT-2][startingY+CELL_CHAR_WIDTH-5] = String.valueOf(toDraw.getCoordX());
        charMap[startingX + CELL_CHAR_HEIGHT-2][startingY+CELL_CHAR_WIDTH-4] = ",";
        charMap[startingX + CELL_CHAR_HEIGHT-2][startingY+CELL_CHAR_WIDTH-3] = String.valueOf(toDraw.getCoordY());
        charMap[startingX + CELL_CHAR_HEIGHT-2][startingY+CELL_CHAR_WIDTH-2] = ">";

        // Draw players
        drawPlayers(charMap, toDraw, startingX, startingY);

        // Draw weapons and ammos
        if (toDraw.isSpawn()) {
            drawCellWeapon(charMap, (SpawnCell) toDraw, startingX, startingY );
        } else drawCellAmmo(charMap, (AmmoCell) toDraw, startingX, startingY );
    }

    /**
     * Draw a horizontal wall/door/free
     * @param charMap map of character to draw in
     * @param cell where the wall belongs
     * @param dir direction of wall
     * @param startingX coordinate of side
     * @param startingY coordinate of side
     */
    private void drawHorizontalSide(String[][] charMap, Cell cell, Direction dir, int startingX, int startingY) {
        Side sideType = cell.getSide(dir);

        int toBeColored = startingX;
        if (dir == Direction.NORTH) toBeColored++;
        else if (dir == Direction.SOUTH) toBeColored--;

        int sideWidth = CELL_CHAR_WIDTH - 1;
        switch (sideType) {
            case FREE:
                // Draw "─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─"
                boolean toDraw = true;

                // Draw colored border
                charMap[startingX][startingY] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                for (int i = startingY+1; i < startingY + sideWidth; i++) {
                    if (toDraw) {
                        charMap[startingX][i] = FREE_O;
                        toDraw = false;
                    } else toDraw = true;
                }
                charMap[startingX][startingY+sideWidth-1] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                break;
            case BORDER:
            case WALL:
                // Draw "══════════════════════"
                for (int i = startingY; i < startingY + sideWidth; i++) {
                    charMap[startingX][i] = wallO;

                    // Draw colored border
                    charMap[toBeColored][i] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                }

                break;
            case DOOR:
                int openingWidth = 10;
                // Draw "═════╣          ╠═════"
                for (int i = startingY; i < startingY + sideWidth; i++) {
                    if (i == (startingY + sideWidth/2 - openingWidth/2)) charMap[startingX][i] = wallCrossL;
                    else if (i == (startingY + sideWidth/2 + openingWidth/2)) charMap[startingX][i] = wallCrossR;
                    else if (i < (startingY + sideWidth/2 - openingWidth/2) ||
                            i > (startingY + sideWidth/2 + openingWidth/2)) {
                        charMap[startingX][i] = wallO;

                        // Draw colored border
                        charMap[toBeColored][i] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;

                    }
                }
                break;
        }
    }

    /**
     * Draw a vertical wall/door/free
     * @param charMap map of character to draw in
     * @param cell where the wall belongs
     * @param dir direction of wall
     * @param startingX coordinate of side
     * @param startingY coordinate of side
     */
    private void drawVerticalSide(String[][] charMap, Cell cell, Direction dir, int startingX, int startingY) {
        Side sideType = cell.getSide(dir);

        int toBeColored = startingY;
        if (dir == Direction.EAST) toBeColored--;
        else if (dir == Direction.WEST) toBeColored++;

        int sideHeight = CELL_CHAR_HEIGHT - 1;
        switch (sideType) {
            case FREE:
                // Draw vertical "─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─"
                boolean toDraw = true;

                charMap[startingX][startingY] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                for (int i = startingX + 1; i < startingX + sideHeight; i++) {
                    if (toDraw) {
                        charMap[i][startingY] = FREE_V;
                        toDraw = false;
                    } else toDraw = true;
                }
                charMap[startingX + sideHeight - 1][startingY] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                break;

            case BORDER:
            case WALL:
                // Draw vertical "══════════════════════"
                for (int i = startingX; i < startingX + sideHeight; i++) {
                    charMap[i][startingY] = wallV;

                    // Draw colored border
                    charMap[i][toBeColored] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;
                }
                break;

            case DOOR:
                int openingHeight = 4;
                // Draw vertical "═════╣         ╠═════"
                for (int i = startingX; i < startingX + sideHeight; i++) {
                    if (i == (startingX + sideHeight/2 - openingHeight/2)) charMap[i][startingY] = wallCrossU;
                    else if (i == (startingX + sideHeight/2 + openingHeight/2)) charMap[i][startingY] = wallCrossD;
                    else if (i < (startingX + sideHeight/2 - openingHeight/2) ||
                            i > (startingX + sideHeight/2 + openingHeight/2)) {
                        charMap[i][startingY] = wallV;

                        // Draw colored border
                        charMap[i][toBeColored] = getANSIColor(cell) + COLOR_BLOCK + ANSI_RESET;

                    }

                }
                break;
        }
    }

    /**
     * Draw single cell's corner
     * @param charMap map of characters that will be printed
     * @param x coordinate of corner
     * @param y coordinate
     */
    private void drawCorner(String[][] charMap, int x, int y, int xSize, int ySize) {
        int printWidth = ySize * CELL_CHAR_WIDTH;
        int printHeight = xSize * CELL_CHAR_HEIGHT;

        if (x < 0 || y < 0 || x > printHeight || y > printWidth) return;

        // North/South/West/East are true if there is a wall to be connected in this direction
        boolean north = (x > 0) && (charMap[x - 1][y].equals(wallV));
        boolean south = (x < printHeight) && (charMap[x + 1][y].equals(wallV));
        boolean east = (y < printWidth) && (charMap[x][y + 1].equals(wallO));
        boolean west = (y > 0) && (charMap[x][y - 1].equals(wallO));

        for (CharAsset character : charAssets) {
            if (character.isRightChar(north, south, east, west)) {
                charMap[x][y] = character.getCharacter();
                break;
            }
        }
    }

    /**
     * Draw players in given cell
     * @param charMap map of strings to print
     * @param toDraw cell to get players from
     * @param startingX starting X coord
     * @param startingY starting Y coord
     */
    private void drawPlayers(String[][] charMap, Cell toDraw, int startingX, int startingY) {
        // Players
        List<Player> playersToDraw = toDraw.getPlayers();
        if (playersToDraw.isEmpty()) return;

        // Calculate x and y coords of player box
        int boxXOffset = 3;
        int boxYOffset = 15;

        int playerBoxX = startingX + boxXOffset;
        int playerBoxY = startingY + boxYOffset;

        for (Player p : playersToDraw) {
            charMap[playerBoxX][playerBoxY] = getANSIColor(p) + Character.toUpperCase(p.getNickname().charAt(0)) + ANSI_RESET;

            if (playerBoxY > startingY + boxYOffset + PLAYER_BOX_WIDTH - 2) {
                playerBoxX = playerBoxX + 2;
                playerBoxY = startingY + boxYOffset;
            } else playerBoxY = playerBoxY + 2;
        }
    }

    /**
     * Draw this cell's ammos
     * @param charMap map of strings to print
     * @param toDraw cell to get ammo from
     * @param startingX starting X coord
     * @param startingY starting Y coord
     */
    private void drawCellAmmo(String[][] charMap, AmmoCell toDraw, int startingX, int startingY) {
        Ammo ammoToDraw = toDraw.getResource();

        int boxXOffset = 3;
        int boxYOffset = 3;

        // Draw ammo box's walls
        charMap[startingX + boxXOffset][startingY+boxYOffset] = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                false, true, true, false)).collect(Collectors.toList()).get(0).getCharacter();

        for (int i = startingY+boxYOffset+1; i < startingY+boxYOffset+4; i++) charMap[startingX+boxXOffset][i] = wallO;
        for (int i = startingY+boxYOffset+1; i < startingY+boxYOffset+4; i++) charMap[startingX+boxXOffset+4][i] = wallO;

        charMap[startingX + boxXOffset][startingY+boxYOffset + 4] = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                false, true, false, true)).collect(Collectors.toList()).get(0).getCharacter();

        for (int i = startingX+boxXOffset+1; i < startingX+boxXOffset+4; i++) charMap[i][startingY+boxYOffset] = wallV;
        for (int i = startingX+boxXOffset+1; i < startingX+boxXOffset+4; i++) charMap[i][startingY+boxYOffset+4] = wallV;
        charMap[startingX + boxXOffset + 4][startingY+boxYOffset] = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, false, true, false)).collect(Collectors.toList()).get(0).getCharacter();
        charMap[startingX + boxXOffset + 4][startingY+boxYOffset + 4] = charAssets.stream().filter(charAsset -> charAsset.isRightChar(
                true, false, false, true)).collect(Collectors.toList()).get(0).getCharacter();

        // Write Ammos
        if (ammoToDraw == null) return;

        charMap[startingX+boxXOffset +1][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(0)) + AMMO_BLOCK +ANSI_RESET;
        charMap[startingX+boxXOffset+2][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(1)) +AMMO_BLOCK+ANSI_RESET;

        if (ammoToDraw.hasPowerup()) charMap[startingX+boxXOffset+3][startingY+boxYOffset+2] = "\u25B2";
        else charMap[startingX+boxXOffset+3][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(2)) +AMMO_BLOCK+ANSI_RESET;

    }

    /**
     * Draw spawn mark inside this cell
     * @param charMap map of strings to print
     * @param toDraw cell to get ammo from
     * @param startingX starting X coord
     * @param startingY starting Y coord
     */
    private void drawCellWeapon(String[][] charMap, SpawnCell toDraw, int startingX, int startingY) {
        int boxXOffset = 3;
        int boxYOffset = 3;

        int i = startingX + boxXOffset;
        int j = startingY + boxYOffset - 1;
        for (char c : SPAWN_ART.toCharArray()) {
            if (c == '\n') {
                i++;
                j = startingY + boxYOffset;
            } else {
                charMap[i][j] = getANSIColor(toDraw) + c + ANSI_RESET;
                j++;
            }
        }
    }

    /**
     * Draw weapon info
     * @param charMap map of strings to print
     * @param map to get weapon info from
     * @param startingX starting X coord of table
     * @param startingY starting Y coord of table
     */
    private void drawWeaponInfo(String[][] charMap, Map map, int startingX, int startingY) {
        charMap[startingX][startingY] = WEAPON_INFO_CLOSER;
        charMap[startingX+1][startingY] = WEAPON_INFO_HEADER;
        charMap[startingX+2][startingY] = WEAPON_INFO_CLOSER;

        int i = 3;
        for (SpawnCell spawn : map.getSpawnPoints()) {
            for (WeaponCard weapon : spawn.getWeapons()) {
                String cost1;
                String cost2 = "";
                String cost3 = "";

                cost1 = getANSIColor(weapon.getCost().get(0)) + " " + AMMO_BLOCK + ANSI_RESET + " ";
                if (weapon.getCost().size() > 1) cost2 = getANSIColor(weapon.getCost().get(1)) + " " + AMMO_BLOCK + ANSI_RESET + " ";
                if (weapon.getCost().size() > 2) cost3 = getANSIColor(weapon.getCost().get(2)) + " " + AMMO_BLOCK + ANSI_RESET + " ";

                charMap[startingX+i][startingY] = String.format(WEAPON_INFO_FORMAT,
                                                                getANSIColor(spawn) + COLOR_BLOCK + ANSI_RESET,
                                                                weapon.getName(), cost1, cost2, cost3);
                i++;
            }
            charMap[startingX+i][startingY] = WEAPON_INFO_CLOSER;
        }
    }

    /**
     * Draw player info
     * @param charMap map of strings to print
     * @param match to get player info from
     * @param startingX starting X coord of table
     * @param startingY starting Y coord of table
     */
    private void drawPlayerInfo(String[][] charMap, AdrenalinaMatch match, int startingX, int startingY) {
        charMap[startingX][startingY] = PLAYER_INFO_CLOSER;
        charMap[startingX+1][startingY] = PLAYER_INFO_HEADER;

        int i = 2;
        for (Player p : match.getPlayers() ) {
            StringBuilder dmgTrack = drawPlayerDamage(p);
            StringBuilder rewards = drawPlayerRewards(p);
            StringBuilder marks = drawPlayerMarks(p);
            StringBuilder ammos = drawPlayerAmmos(p);

            charMap[startingX+i][startingY] = PLAYER_INFO_CLOSER;
            i++;
            charMap[startingX+i][startingY] = String.format(PLAYER_INFO_FORMAT,
                    getANSIColor(p), p.getNickname() + ((p.getNickname().equals(player.getNickname())) ? " (YOU)" : "" ),
                    ANSI_RESET, dmgTrack, marks, ammos);
            i++;
            charMap[startingX+i][startingY] = String.format(PLAYER_REWARD_HEADER, rewards);
            i = drawPlayerWeapon(charMap, startingX, startingY, i, p);

            i++;
            charMap[startingX+i][startingY] = PLAYER_INFO_CLOSER;
        }
    }

    /**
     * Draw a player's weapons
     * @param charMap map to be drawn
     * @param startingX starting X coordinate
     * @param startingY starting Y coordinate
     * @param i current X offset
     * @param p player
     * @return new drawing offset
     */
    private int drawPlayerWeapon(String[][] charMap, int startingX, int startingY, int i, Player p) {
        // Draw weapons. See weapon name only if this player's weapon or not reloaded
        if (!p.getWeapons().isEmpty()) {
            for (int j = 0; j < p.getWeapons().size(); j++) {
                i++;

                StringBuilder weaponsString = new StringBuilder();
                StringBuilder weaponCost = new StringBuilder();
                WeaponCard currentWeapon = p.getWeapons().get(j);

                boolean weaponLoaded = p.getLoadedWeapons().contains(currentWeapon);
                if (p.getNickname().equals(player.getNickname())) {
                    weaponsString.append(currentWeapon.getName()).append(!weaponLoaded ? " (Unloaded)" : "");
                } else if (!weaponLoaded) {
                    weaponsString.append(currentWeapon.getName()).append(" (Unloaded)");
                } else {
                    weaponsString.append("[Hidden weapon]");
                    charMap[startingX+i][startingY] = String.format((j == 0) ? PLAYER_WEAPON_HEADER : PLAYER_WEAPON_FORMAT, weaponsString, weaponCost);
                    continue;
                }

                int colorLength = 0;
                for (Resource res : currentWeapon.getCost()) {
                    weaponCost.append(getANSIColor(res)).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");

                    colorLength += getANSIColor(res).length() + ANSI_RESET.length();
                }
                int stringLen = weaponCost.length() - colorLength;
                // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
                weaponCost.append(" ".repeat(Math.max(0, 18-stringLen)));


                charMap[startingX+i][startingY] = String.format((j == 0) ? PLAYER_WEAPON_HEADER : PLAYER_WEAPON_FORMAT, weaponsString, weaponCost);
            }
        }
        return i;
    }

    /**
     * Draw player powerups
     * @param charMap map of strings to print
     * @param match to get player info from
     * @param startingX starting X coord of table
     * @param startingY starting Y coord of table
     */
    private void drawPlayerPowerups(String[][] charMap, AdrenalinaMatch match, int startingX, int startingY) {
        if (player.getThisPlayer().getPowerups().isEmpty()) return;

        charMap[startingX][startingY] = PLAYER_POWERUP_CLOSER;
        charMap[startingX+1][startingY] = PLAYER_POWERUP_HEADER;
        charMap[startingX+2][startingY] = PLAYER_POWERUP_CLOSER;

        int i = 3;
        for (Powerup powerup : player.getThisPlayer().getPowerups()) {
            charMap[startingX+i][startingY] = String.format(PLAYER_POWERUP_FORMAT, powerup.getName(), getANSIColor(powerup.getBonusResource()) + AMMO_BLOCK + ANSI_RESET);
            i++;
        }
        charMap[startingX+i][startingY] = PLAYER_POWERUP_CLOSER;
    }

    /**
     * Draw a defined player's resources infos
     * @param p player to get info from
     * @return string builder initialized with infos
     */
    private StringBuilder drawPlayerAmmos(Player p) {
        int colorLength = 0;
        int stringLen;

        // Draw ammos
        StringBuilder ammos = new StringBuilder();
        for (Resource res : p.getAmmos()) {
            ammos.append(getANSIColor(res)).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");

            colorLength += getANSIColor(res).length() + ANSI_RESET.length();
        }
        stringLen = ammos.length() - colorLength;
        // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
        ammos.append(" ".repeat(Math.max(0, 18-stringLen)));
        return ammos;
    }

    /**
     * Draw a defined player's marks infos
     * @param p player to get info from
     * @return string builder initialized with infos
     */
    private StringBuilder drawPlayerMarks(Player p) {
        int colorLength = 0;
        int stringLen;

        // Draw marks
        StringBuilder marks = new StringBuilder();
        for (Player marker : p.getMarks()) {
            marks.append(getANSIColor(marker)).append(MARK).append(ANSI_RESET).append(" ");

            colorLength += getANSIColor(marker).length() + ANSI_RESET.length();
        }
        stringLen = marks.length() - colorLength;
        // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
        marks.append(" ".repeat(Math.max(0, 6-stringLen)));
        return marks;
    }

    /**
     * Draw a defined player's current rewards infos
     * @param p player to get info from
     * @return string builder initialized with infos
     */
    private StringBuilder drawPlayerRewards(Player p) {
        int colorLength = 0;
        int stringLen;

        // Draw rewards
        StringBuilder rewards = new StringBuilder();
        rewards.append((p.isFrenzyPlayer()) ? "    " : "1   ");
        int deaths = p.getDeaths();
        for (int reward : (p.isFrenzyPlayer()) ? Player.getFrenzyRewards() : Player.getKillRewards()) {
            if (deaths > 0) {
                rewards.append(ANSI_RED);

                // Keep track of lost chars due to colors
                colorLength += ANSI_RED.length();
                deaths--;
            }

            rewards.append(reward).append(ANSI_RESET).append(" ");
            colorLength += ANSI_RESET.length();
        }
        stringLen = rewards.length() - colorLength;
        rewards.append((p.isFrenzyPlayer()) ? "    >>  " : ">>  ").append(ANSI_RED).append("D M").append(ANSI_RESET);
        // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
        rewards.append(" ".repeat(Math.max(0, (((p.isFrenzyPlayer()) ? 13 : 17) - stringLen))));
        return rewards;
    }

    /**
     * Draw a defined player's damage points infos
     * @param p player to get info from
     * @return string builder initialized with infos
     */
    private StringBuilder drawPlayerDamage(Player p) {
        // Draw damage track
        StringBuilder dmgTrack = new StringBuilder();
        int colorLength = 0;
        for (Player damager : p.getDmgPoints()) {
            dmgTrack.append(getANSIColor(damager)).append(HIT_POINT).append(ANSI_RESET).append(" ");

            // Keep track of lost chars due to colors
            colorLength += getANSIColor(damager).length() + ANSI_RESET.length();
        }
        int stringLen = dmgTrack.length() - colorLength;
        // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
        dmgTrack.append(" ".repeat(Math.max(0, 24-stringLen)));
        return dmgTrack;
    }

    /**
     * Get cell's corresponding ANSI color code
     * @param cell to get color from. Set this to null to get RESET code
     * @return char color code
     */
    private String getANSIColor(Cell cell) {
        if (cell == null) return ANSI_RESET;

        switch (cell.getColor()) {
            case BLUE:
                return ANSI_BLUE;
            case RED:
                return ANSI_RED;
            case YELLOW:
                return ANSI_YELLOW;
            case GREEN:
                return ANSI_GREEN;
            case WHITE:
                return ANSI_WHITE;
            case PURPLE:
                return ANSI_PURPLE;
            default:
                return ANSI_RESET;
        }
    }

    /**
     * Get player's corresponding ANSI color code
     * @param player to get color from. Set this to null to get RESET code
     * @return char color code
     */
    private String getANSIColor(Player player) {
        if (player == null) return ANSI_RESET;

        switch (player.getColor()) {
            case BLUE:
                return ANSI_BLUE;
            case RED:
                return ANSI_RED;
            case YELLOW:
                return ANSI_YELLOW;
            case GREEN:
                return ANSI_GREEN;
            case WHITE:
                return ANSI_WHITE;
            case PURPLE:
                return ANSI_PURPLE;
            default:
                return ANSI_RESET;
        }
    }

    /**
     * Get player's corresponding ANSI color code
     * @param resource to get color from. Set this to null to get RESET code
     * @return char color code
     */
    private String getANSIColor(Resource resource) {
        if (resource == null) return ANSI_RESET;

        switch (resource) {
            case BLUE_BOX:
                return ANSI_BLUE;
            case RED_BOX:
                return ANSI_RED;
            case YELLOW_BOX:
                return ANSI_YELLOW;
            default:
                return ANSI_RESET;
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