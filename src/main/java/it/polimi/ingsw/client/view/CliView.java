package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.controller.ConnectionType;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CliView extends ClientView {
    private class IntersectionChar {
        char character;
        boolean north;
        boolean south;
        boolean east;
        boolean west;

        public IntersectionChar(char character, boolean north, boolean south, boolean east, boolean west) {
            this.character = character;
            this.north = north;
            this.south = south;
            this.east = east;
            this.west = west;
        }

        public boolean isRightChar(boolean north, boolean south, boolean east, boolean west) {
            return east == this.east && north == this.north && south == this.south && west == this.west;
        }

        public char getCharacter() {
            return character;
        }
    }

    List<IntersectionChar> intersectionChars;

    /**
     * ASCII encoding of walls (Customizable from json)
     */
    private static char WALL_O;
    private static char WALL_V;
    private static char WALL_CROSS_R;
    private static char WALL_CROSS_L;
    private static char WALL_CROSS_U;
    private static char WALL_CROSS_D;
    private static char FREE_CROSS;


    /**
     * ASCII encoding of free spaces (not customizable)
     */
    private static char FREE_O   = '─';
    private static char FREE_V = '│';

    /**
     * Number of characters used to build walls + 1 (one of two vertical walls)
     */
    private static final int cellCharWidth = 22;

    /**
     * Number of characters used to build walls + 1 (one of two horizontal walls)
     */
    private static final int cellCharHeight = 10;

    /**
     * ANSI color encoding for easy use
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public CliView(ClientPlayer player) {
        super(player);
        parseCliAssets();
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
        if(matches.size() == 0){
            System.out.println("Wow, such empty...");
            System.out.println("To create a new match press enter 1, to reload enter 0: ");
            Scanner in = new Scanner (System.in);
            int response = in.nextInt();
            if(response == 1)
                createNewGame();
            else if(response == 0){
                player.updateLobby();
                return;
            }
        }
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

    @Override
    public void createNewGame() {
        int maxPlayers;
        int maxDeaths;
        int turnDuration;
        int mapID;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of players: ");
        maxPlayers = in.nextInt();
        System.out.print("Enter number of deaths: ");
        maxDeaths = in.nextInt();
        System.out.print("Enter turn duration [seconds]: ");
        turnDuration = in.nextInt();
        System.out.print("Enter map id: ");
        mapID = in.nextInt();

        player.createGame(maxPlayers,maxDeaths,turnDuration, mapID);
    }

    /**
     * Print map
     */
    public static void main(String[] args) {
        AdrenalinaMatch match = new AdrenalinaMatch(5, 5, 120, 1);
        CliView view = new CliView(null);
        for (char[] col : view.getMapToDraw(match.getBoardMap())) {
            for (char c : col) System.out.print(c);
            System.out.print("\n");
        }
    }

    /**
     * Parse json with cli assets
     */
    private void parseCliAssets() {
        this.intersectionChars = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().
                    getResourceAsStream("/json/cliAssets.json"), StandardCharsets.UTF_8)));
            JSONArray jsonArray = (JSONArray) obj;

            for (Object character : jsonArray) {
                JSONObject currentChar = (JSONObject) character;

                intersectionChars.add(new IntersectionChar(
                        currentChar.get("char").toString().charAt(0),
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
        WALL_O = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                false, false, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        WALL_V = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                true, true, false, false)).collect(Collectors.toList()).get(0).getCharacter();

        WALL_CROSS_D = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                false, true, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        WALL_CROSS_U = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                true, false, true, true)).collect(Collectors.toList()).get(0).getCharacter();

        WALL_CROSS_R = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                true, true, true, false)).collect(Collectors.toList()).get(0).getCharacter();

        WALL_CROSS_L = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                true, true, false, true)).collect(Collectors.toList()).get(0).getCharacter();

        FREE_CROSS = intersectionChars.stream().filter(intersectionChar -> intersectionChar.isRightChar(
                false, false, false, false)).collect(Collectors.toList()).get(0).getCharacter();
    }

    /**
     * @return ready-to-print map
     */
    public char[][] getMapToDraw(Map map) {
        int printWidth = map.getYSize() * cellCharWidth + 1;
        int printHeight = map.getXSize() * cellCharHeight + 1;

        char[][] mapToReturn = new char[printHeight][printWidth];

        // Init map
        for (int i = 0; i < mapToReturn.length; i++) {
            for (int j = 0; j < mapToReturn[0].length; j++) mapToReturn[i][j] = ' ';
        }

        for (int i = 0; i < map.getXSize(); i++) {
            for (int j = 0; j < map.getYSize(); j++) drawCell(mapToReturn, map.getCell(i,j), i, j,
                    map.getXSize(), map.getYSize());
        }


        return mapToReturn;
    }

    /**
     * Set correct character in charMap based on cell toDraw
     * @param charMap character map that will be printed
     * @param toDraw cell to draw
     * @param x coordinate of toDraw
     * @param y coordinate of toDraw
     */
    public void drawCell(char[][] charMap, Cell toDraw, int x, int y, int xSize, int ySize) {
        if (toDraw == null) return;

        int printWidth = ySize * cellCharWidth + 1;
        int printHeight = xSize * cellCharHeight + 1;

        // Starting and
        int startingX = x * cellCharHeight;
        int endingX = startingX + cellCharHeight;
        int startingY = y * cellCharWidth;
        int endingY = startingY + cellCharWidth;

        // Sides
        for (Direction dir : Direction.values()) {
            switch (dir) {
                case NORTH:
                    drawHorizontalSide(charMap, toDraw.getSide(dir), startingX, startingY + 1);
                    break;
                case SOUTH:
                    drawHorizontalSide(charMap, toDraw.getSide(dir), endingX, startingY + 1);
                    break;
                case WEST:
                    drawVerticalSide(charMap, toDraw.getSide(dir), startingX + 1, startingY);
                    break;
                case EAST:
                    drawVerticalSide(charMap, toDraw.getSide(dir), startingX + 1, endingY);
                    break;
            }
        }

        // Corners
        // Draw every corner of cell
        drawCorner(charMap, startingX, startingY, xSize, ySize);   // North-West
        drawCorner(charMap, startingX,startingY + cellCharWidth, xSize, ySize);   // North-East
        drawCorner(charMap, startingX + cellCharHeight, startingY, xSize, ySize);   // South-West
        drawCorner(charMap, startingX + cellCharHeight, startingY + cellCharWidth, xSize, ySize); // South-East

        // Color

        // Players
    }

    /**
     * Draw a horizontal wall/door/free
     * @param charMap map of character to draw in
     * @param sideType type of side to draw
     * @param startingX coordinate of side
     * @param startingY coordinate of side
     */
    private void drawHorizontalSide(char[][] charMap, Side sideType, int startingX, int startingY) {
        int sideWidth = cellCharWidth - 1;
        switch (sideType) {
            case FREE:
                // Draw "─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─"
                boolean toDraw = true;
                for (int i = startingY; i < startingY + sideWidth; i++) {
                    if (toDraw) {
                        charMap[startingX][i] = FREE_O;
                        toDraw = false;
                    } else toDraw = true;
                }
                break;

            case BORDER:
            case WALL:
                // Draw "══════════════════════"
                for (int i = startingY; i < startingY + sideWidth; i++) {
                    charMap[startingX][i] = WALL_O;
                }
                break;

            case DOOR:
                int openingWidth = 10;
                // Draw "═════╣          ╠═════"
                for (int i = startingY; i < startingY + sideWidth; i++) {
                    if (i == (startingY + sideWidth/2 - openingWidth/2)) charMap[startingX][i] = WALL_CROSS_L;
                    else if (i == (startingY + sideWidth/2 + openingWidth/2)) charMap[startingX][i] = WALL_CROSS_R;
                    else if (i < (startingY + sideWidth/2 - openingWidth/2) ||
                            i > (startingY + sideWidth/2 + openingWidth/2))
                        charMap[startingX][i] = WALL_O;
                }
                break;
        }
    }

    /**
     * Draw a horizontal wall/door/free
     * @param charMap map of character to draw in
     * @param sideType type of side to draw
     * @param startingX coordinate of side
     * @param startingY coordinate of side
     */
    private void drawVerticalSide(char[][] charMap, Side sideType, int startingX, int startingY) {
        int sideHeight = cellCharHeight - 1;
        switch (sideType) {
            case FREE:
                // Draw vertical "─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─"
                boolean toDraw = true;
                for (int i = startingX; i < startingX + sideHeight; i++) {
                    if (toDraw) {
                        charMap[i][startingY] = FREE_V;
                        toDraw = false;
                    } else toDraw = true;
                }
                break;

            case BORDER:
            case WALL:
                // Draw vertical "══════════════════════"
                for (int i = startingX; i < startingX + sideHeight; i++) {
                    charMap[i][startingY] = WALL_V;
                }
                break;

            case DOOR:
                int openingHeight = 4;
                // Draw vertical "═════╣         ╠═════"
                for (int i = startingX; i < startingX + sideHeight; i++) {
                    if (i == (startingX + sideHeight/2 - openingHeight/2)) charMap[i][startingY] = WALL_CROSS_U;
                    else if (i == (startingX + sideHeight/2 + openingHeight/2)) charMap[i][startingY] = WALL_CROSS_D;
                    else if (i < (startingX + sideHeight/2 - openingHeight/2) ||
                            i > (startingX + sideHeight/2 + openingHeight/2))
                        charMap[i][startingY] = WALL_V;
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
    private void drawCorner(char[][] charMap, int x, int y, int xSize, int ySize) {
        int printWidth = ySize * cellCharWidth;
        int printHeight = xSize * cellCharHeight;

        if (x < 0 || y < 0 || x > printHeight || y > printWidth) return;

        // North/South/West/East are true if there is a wall to be connected in this direction
        boolean north = (x > 0) ? (charMap[x-1][y] == WALL_V) : false;
        boolean south = (x < printHeight) ? (charMap[x+1][y] == WALL_V) : false;
        boolean east = (y < printWidth) ? (charMap[x][y+1] == WALL_O) : false;
        boolean west = (y > 0) ? (charMap[x][y-1] == WALL_O) : false;

        for (IntersectionChar character : intersectionChars) {
            if (character.isRightChar(north, south, east, west)) {
                charMap[x][y] = character.getCharacter();
                break;
            }
        }
        return;
    }

    /**
     * Clear console (UNIX system only)
     */
    private static void clearConsole() {
        System.out.print("\033\143");
        System.out.flush();
    }
}
