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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    private class CommandReader extends Thread {
        private final Scanner stringReader;

        private boolean stopReader;

        private String buffer;

        CommandReader() {
            stringReader = new Scanner(System.in);
            this.stopReader = false;
            this.buffer = "";
        }

        @Override
        public void run() {
            while (!stopReader)
                synchronized (stringReader) {
                    if (buffer.isEmpty()) buffer = stringReader.nextLine();
                }
        }

        /**
         * Send reader command to stop after next line
         */
        public synchronized void shutdownReader() {
            stopReader = true;
        }

        /**
         * Retreive next line read and reset buffer
         * @return last read line
         */
        String nextLine() {
            String toReturn;
            synchronized (stringReader) {
                toReturn = buffer;
                buffer = "";
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
    private static final String MATCH_INFO_HEADER =     "|  ID  |  Max players  |  Max Deaths  |   Map   |     Players     |%n";
    private static final String MATCH_INFO_FORMAT =     "| %-4s |  %-11s  |  %-10s  |   %-3s   |  %-13s  |%n";
    private static final String MATCH_PLAYER_FORMAT =    "|      |               |              |         |  %-13s  |%n%n";
    private static final String MATCH_INFO_CLOSER =     "+------+---------------+--------------+---------+-----------------+%n";

    private static final int INFO_OFFSET = 20;

    /**
     * Standard format for text inside weapon info table
     */
    private static final String WEAPON_INFO_HEADER =    "|  Spawn  |        Weapon        |  Reload cost  |";
    private static final String WEAPON_INFO_FORMAT =    "|    %-2s    | %-20s |  %-3s %-3s %-3s  |";
    private static final String WEAPON_INFO_CLOSER =    "+---------+----------------------+---------------+";

    /**
     * Standard format for text inside player info table
     */
    private static final String PLAYER_INFO_HEADER =    "|         Nickname         |       Life points       |  Marks  |  Ammos  |";
    private static final String PLAYER_INFO_FORMAT =    "|   %s%-20s%s   | %-24s|  %-6s |  %-6s |";
    private static final String PLAYER_REWARD_HEADER =  "|                          | %-24s|         |";
    private static final String PLAYER_WEAPON_HEADER =  "|              Weapons:    | %-33s |  %-6s |";
    private static final String PLAYER_WEAPON_FORMAT =  "|                          | %-33s |  %-6s |";
    private static final String PLAYER_INFO_CLOSER =    "+--------------------------+-------------------------+---------+---------+";

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
            "What's your choice?  ";

    private static final String FRENZY_ACTION_BEFORE_FIRST =
            "FREEEEEEEENZYYYYY! Now it's your last chance to get those points! Here's what you can do: \n\n" +
            "[1] Move up to 1 cell, reload (if you want), then SHOOT! \n" +
            "[2] Move up to 4 cells \n" +
            "[3] Move up to 2 cell, then pick something \n\n" +
            "What's your choice?  ";

    private static final String FRENZY_ACTION_AFTER_FIRST =
            "FREEEEEEEENZYYYYY! Now it's your last chance to get those points! Here's what you can do: \n\n" +
            "[1] Move up to 2 cell, reload (if you want), then SHOOT! \n" +
            "[2] Move up to 3 cell, then pick something \n\n" +
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
     * Powerup selection
     */
    private static final String POWERUP_SELECTION = "Select one of powerups listed above: \n";

    /**
     *  Command Line reader
     */
    private static CommandReader cmdReader;

    private String selectionMessage;

    public CliView(ClientPlayer player) {
        super(player);
        parseCliAssets();
        selectionMessage = IDLE_MESSAGE;
        cmdReader = new CommandReader();
        cmdReader.start();
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
        System.out.print(MATCH_INFO_CLOSER);
        System.out.format("|         Matches:       %-4s                                     |%n", matches.size());
        System.out.print(MATCH_INFO_CLOSER);

        String response;

        if(matches.isEmpty()){
            System.out.println("Wow, such empty...");
            System.out.println("CREATE [N]ew match. [Any key to reload]");

            // Retreive next command
            response = getResponse();

            if(response.equals("N") || response.equals("n"))
                createNewGame();
            else {
                player.updateLobby();
                return;
            }
        }
        for(int i = 0; i < matches.size(); i++){
            // Print header table
            System.out.format(MATCH_INFO_CLOSER + "%n" + MATCH_INFO_HEADER);

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
            };
            System.out.format(MATCH_INFO_CLOSER);
        }

        System.out.printf("Do you want to CREATE [N]ew match, or JOIN an existing one?%n" +
                            "(Specify match's ID to join) [Any key to reload]");

        response = getResponse();
        int choice = 0;

        try {
            choice = Integer.parseInt(response) - 1;
        } catch (NumberFormatException e) {}

        if (response.equals("N") || response.equals("n")) createNewGame();
        else if(choice >= 0 && choice <  matches.size()) {
            player.joinGame(choice);
            clearConsole();
        }
        else {
            showLobby(lobby);
        }
    }

    /**
     * Retreive last response (waiting if none is provided)
     * @return Last player's response
     */
    private String getResponse() {
        String response;
        response = cmdReader.nextLine();
        while (response.isEmpty()) {
            response = cmdReader.nextLine();
            try {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

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
            newPlayer1.addAmmo(Resource.RED_BOX);
            newPlayer1.addAmmo(Resource.YELLOW_BOX);

            newPlayer5.addAmmo(Resource.BLUE_BOX);
            newPlayer5.addAmmo(Resource.RED_BOX);
            newPlayer5.addAmmo(Resource.YELLOW_BOX);

            newPlayer1.pickWeapon(match.getBoardMap().getSpawnPoints().get(1).getWeapons().get(0));
            newPlayer5.pickWeapon(match.getBoardMap().getSpawnPoints().get(2).getWeapons().get(0));

            newPlayer5.addAmmo(Resource.BLUE_BOX);
            newPlayer5.addAmmo(Resource.RED_BOX);
            newPlayer5.addAmmo(Resource.YELLOW_BOX);

            newPlayer5.pickWeapon(match.getBoardMap().getSpawnPoints().get(2).getWeapons().get(1));

            AdrenalinaMatch clientMatch = new AdrenalinaMatch();
            clientMatch.update(match.toJSON());
            p1.setMatch(clientMatch);

            List<WeaponCard> newLoaded = new ArrayList<>();
            newLoaded.add(clientMatch.getPlayers().get(4).getLoadedWeapons().get(0));
            clientMatch.getPlayers().get(4).setLoadedWeapons(newLoaded);

            view.showMatch();
        } catch (TooManyPlayersException | MatchAlreadyStartedException | PlayerAlreadyExistsException | InventoryFullException | InsufficientResourcesException e) {
            e.printStackTrace();
        }
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

            String response;
            if (isReady) {
                System.out.printf("Are you [R]eady? ([E] to go back to lobby)%n");

                // Retreive next command
                response = getResponse();

                if (response.equals("R") || response.equals("r")) {
                    player.setReady();
                } else if (response.equals("E") || response.equals("e")) {
                    player.backToLobby();
                    player.updateLobby();
                } else System.out.printf("Invalid command. Press [R] if you are ready or [E] to go back to lobby%n");

            } else {
                System.out.print("You are now ready to play. Take a snack while waiting to start... ([E]xit or [N]ot-Ready)");

                response = getResponse();

                if (response.equals("E") || response.equals("e")) {
                    player.backToLobby();
                    player.updateLobby();
                } else System.out.printf("Invalid command. Press [E] to go back to lobby or [N]ot-Ready%n");

            }
        } else if (match.getState() == MatchState.PLAYER_TURN) {
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

        }
    }

    /**-
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
        return null;
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

    private <T> T getIndexedResponse(List<T> selectables, StringBuilder messageToPrint) {
        messageToPrint.append("\n [X] to STOP selection (turn action could be lost!)");

        selectionMessage = messageToPrint.toString();
        String response = getResponse();

        while (!response.equals("X") && !response.equals("x")) {
            int choice;
            try {
                choice = Integer.parseInt(response) - 1;
            } catch (NumberFormatException e) {
                System.out.println(INVALID_SELECTION);
                response = getResponse();
                continue;
            }

            if (choice >= 0 && choice < selectables.size()) {
                selectionMessage = IDLE_MESSAGE;
                return selectables.get(choice);
            } else {
                System.out.println(INVALID_SELECTION);
                response = getResponse();
            }
        }
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
        if (!player.getMatch().isFrenzyEnabled()) {
            selectionMessage = ACTION_SELECTION;
            String response = getResponse();

            while (true) {
                switch (response) {
                    case "R":
                        selectionMessage = IDLE_MESSAGE;
                        return TurnAction.MOVE;
                    case "P":
                        selectionMessage = IDLE_MESSAGE;
                        return TurnAction.PICK;
                    case "S":
                        selectionMessage = IDLE_MESSAGE;
                        return TurnAction.SHOOT;
                    default:
                        System.out.println(INVALID_SELECTION);
                        response = getResponse();
                }
            }
        } else {
            if (!player.getMatch().isFirstPlayedFrenzy()) {
                selectionMessage = FRENZY_ACTION_BEFORE_FIRST;
                String response = getResponse();

                while (true) {
                    switch (response) {
                        case "1":
                            selectionMessage = IDLE_MESSAGE;
                            return TurnAction.SHOOT;
                        case "2":
                            selectionMessage = IDLE_MESSAGE;
                            return TurnAction.MOVE;
                        case "3":
                            selectionMessage = IDLE_MESSAGE;
                            return TurnAction.PICK;
                        default:
                            System.out.println(INVALID_SELECTION);
                            response = getResponse();
                    }
                }
            } else {
                selectionMessage = FRENZY_ACTION_AFTER_FIRST;
                String response = getResponse();

                while (true) {
                    switch (response) {
                        case "1":
                            selectionMessage = IDLE_MESSAGE;
                            return TurnAction.SHOOT;
                        case "2":
                            selectionMessage = IDLE_MESSAGE;
                            return TurnAction.PICK;
                        default:
                            System.out.println(INVALID_SELECTION);
                            response = getResponse();
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
        drawWeaponInfo(mapToReturn, match.getBoardMap(), 0, printWidth - 1);

        // Draw player infos
        drawPlayerInfo(mapToReturn, match, 15, printWidth - 1);

        // Write selection message
        int k = match.getBoardMap().getYSize() * (CELL_CHAR_HEIGHT-1);
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
            charMap[playerBoxX][playerBoxY] = getANSIColor(p) + p.getNickname().charAt(0) + ANSI_RESET;

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
                String cost1 = "";
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
                    getANSIColor(p), p.getNickname(), ANSI_RESET, dmgTrack, marks, ammos);
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
                if (p.getNickname().equals(player.getNickname())) {
                    weaponsString.append(currentWeapon.getName());
                } else if (!p.getLoadedWeapons().contains(currentWeapon)) {
                    weaponsString.append(currentWeapon.getName()).append(" (Unloaded)");

                    int colorLength = 0;
                    for (Resource res : currentWeapon.getCost()) {
                        weaponCost.append(getANSIColor(res)).append(AMMO_BLOCK).append(ANSI_RESET).append(" ");

                        colorLength += getANSIColor(res).length() + ANSI_RESET.length();
                    }
                    int stringLen = weaponCost.length() - colorLength;
                    // Formatted string allocates 24 characters for dmg track. Changing lost chars with spaces
                    weaponCost.append(" ".repeat(6-stringLen));

                } else weaponsString.append("[Hidden weapon]");

                charMap[startingX+i][startingY] = String.format((j == 0) ? PLAYER_WEAPON_HEADER : PLAYER_WEAPON_FORMAT, weaponsString, weaponCost);
            }
        }
        return i;
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
        ammos.append(" ".repeat(6-stringLen));
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
        marks.append(" ".repeat(6-stringLen));
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
        rewards.append(" ".repeat(((p.isFrenzyPlayer()) ? 13 : 17) - stringLen));
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
        dmgTrack.append(" ".repeat(24-stringLen));
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