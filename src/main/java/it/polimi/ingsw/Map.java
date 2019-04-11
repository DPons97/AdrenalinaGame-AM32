package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Map {
    /**
     * Representation of the map, bidemensional array of cells.
     */
    private Cell[][] map;

    /**
     * List to trace spawn cells, not necessary but convenient
     */
    private List<SpawnCell> spawnPoints;

    /**
     * Size of Map
     */
    private int xSize;
    private int ySize;

    public Map(Cell[][] map, List<SpawnCell> spawnPoints, int xSize, int ySize) {
        this.map = map;
        this.spawnPoints = spawnPoints;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    /**
     *
     * @return
     */
    public Cell[][] getMap() { return map; }

    /**
     * @return X size of map
     */
    public int getXSize() { return xSize; }

    /**
     * @return Y size of map
     */
    public int getYSize() { return ySize; }

    /**
     * @return list of spawn points in map
     */
    public List<SpawnCell> getSpawnPoints() { return new ArrayList<>(spawnPoints); }

    /**
     * @param x coordinate of desired cell
     * @param y coordinate of desired cell
     * @return cell at (x,y) coordinates
     */
    public Cell getCell(int x, int y) {
        return getMap()[x][y];
    }

    /**
     * @param x coordinate of cell
     * @param y coordinate of cell
     * @param direction where to look
     * @return cell in [direction] side
     */
    public Cell getAdjacentCell(int x, int y, Direction direction) {
        if (getCell(x,y).getSide(direction) != Side.BORDER) {
            switch (direction) {
                case NORTH:
                    return getMap()[x-1][y];
                case EAST:
                    return getMap()[x][y+1];
                case WEST:
                    return getMap()[x+1][y];
                case SOUTH:
                    return getMap()[x][y-1];
                default:
                    return null;
            }
        } else return null;
    }

    /**
     * @param position cell reference
     * @param direction where to look
     * @return cell in [direction] side
     */
    public Cell getAdjacentCell(Cell position, Direction direction) {
        if (getCell(position.getCoordX(),position.getCoordY()).getSide(direction) != Side.BORDER) {
            switch (direction) {
                case NORTH:
                    return getMap()[position.getCoordX() - 1][position.getCoordY()];
                case EAST:
                    return getMap()[position.getCoordX()][position.getCoordY()+1];
                case WEST:
                    return getMap()[position.getCoordX()][position.getCoordY() -  1];
                case SOUTH:
                    return getMap()[position.getCoordX() + 1][position.getCoordY()-1];
                default:
                    return null;
            }
        } else return null;
    }

    /**
     * @param cell cell to get room
     * @return all cells that are in the same room as [cell]
     */
    public List<Cell> getRoomCells(Cell cell) {
        return getCellsInRoom(cell, new ArrayList<>());
    }

    /**
     * Support function to getRoomCells. Recursively find all cells inside same room as this
     * @param currCell cell to check
     * @param visited cells already visited
     * @return list of visible cells
     */
    private List<Cell> getCellsInRoom(Cell currCell, List<Cell> visited) {
        if (currCell == null || visited.contains(currCell)) return new ArrayList<>();

        List<Cell> roomCells = new ArrayList<>();

        visited.add(currCell);
        for (Direction dir : Direction.values()) {
            // Border is free from walls or doors
            if (currCell.getSide(dir) == Side.FREE) {
                roomCells.addAll(getCellsInRoom(getAdjacentCell(currCell, dir), visited));
            }
        }
        return roomCells;
    }
}
