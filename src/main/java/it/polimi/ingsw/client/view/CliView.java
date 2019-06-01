package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.custom_exceptions.MatchAlreadyStartedException;
import it.polimi.ingsw.custom_exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.custom_exceptions.TooManyPlayersException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CliView extends ClientView {
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
         * @return
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
    private static String freeO = "─";
    private static String freeV = "│";

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
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String PLAYER = "X";

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
        AdrenalinaMatch match = new AdrenalinaMatch(5, 5, 120, 3);
        CliView view = new CliView(null);

        try {
            Player newPlayer1 = new Player(match, "Davide");
            newPlayer1.setColor(Color.RED);
            Player newPlayer2 = new Player(match, "Luca");
            newPlayer2.setColor(Color.BLUE);
            Player newPlayer3 = new Player(match, "Mike");
            newPlayer3.setColor(Color.GREEN);
            Player newPlayer4 = new Player(match, "Conti");
            newPlayer4.setColor(Color.PURPLE);
            Player newPlayer5 = new Player(match, "Lorenzo");
            newPlayer5.setColor(Color.YELLOW);

            match.addPlayer(newPlayer1);
            match.addPlayer(newPlayer2);
            match.addPlayer(newPlayer3);
            match.addPlayer(newPlayer4);
            match.addPlayer(newPlayer5);

            newPlayer1.respawn(match.getBoardMap().getSpawnPoints().get(0));
            newPlayer2.respawn(match.getBoardMap().getSpawnPoints().get(0));
            newPlayer3.respawn(match.getBoardMap().getSpawnPoints().get(0));
            newPlayer4.respawn(match.getBoardMap().getSpawnPoints().get(0));
            newPlayer5.respawn(match.getBoardMap().getSpawnPoints().get(0));
        } catch (TooManyPlayersException e) {
            e.printStackTrace();
        } catch (MatchAlreadyStartedException e) {
            e.printStackTrace();
        } catch (PlayerAlreadyExistsException e) {
            e.printStackTrace();
        }

        clearConsole();
        view.drawMap(match.getBoardMap());
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
    private void drawMap(Map map) {
        int printWidth = map.getYSize() * CELL_CHAR_WIDTH + 1;
        int printHeight = map.getXSize() * CELL_CHAR_HEIGHT + 1;

        String[][] mapToReturn = new String[printHeight][printWidth];

        // Init map
        for (int i = 0; i < mapToReturn.length; i++) {
            for (int j = 0; j < mapToReturn[0].length; j++) mapToReturn[i][j] = "\u0020";
        }

        for (int i = 0; i < map.getXSize(); i++) {
            for (int j = 0; j < map.getYSize(); j++) drawCell(mapToReturn, map.getCell(i,j), i, j,
                    map.getXSize(), map.getYSize());
        }

        // Actual drawing
        for (String[] col : mapToReturn) {
            for (int i = 0; i < printWidth; i++) {
                System.out.print(col[i]);
            }
            System.out.print("\n");
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
                        charMap[startingX][i] = freeO;
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
                        charMap[i][startingY] = freeV;
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
        charMap[startingX+boxXOffset +1][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(0)) +"■"+ANSI_RESET;
        charMap[startingX+boxXOffset+2][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(1)) +"■"+ANSI_RESET;

        if (ammoToDraw.hasPowerup()) charMap[startingX+boxXOffset+3][startingY+boxYOffset+2] = "\u25B2";
        else charMap[startingX+boxXOffset+3][startingY+boxYOffset+2] = getANSIColor(ammoToDraw.getResources().get(2)) +"■"+ANSI_RESET;

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
        int boxYOffset = 2;


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
