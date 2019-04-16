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

    /**
     * @param minDist Minimum distance
     * @param maxDist Maximum distance. -1 is equal to INFINITE
     * @param relativeCell cell from which calculate distance
     * @return List of all cells between [minDist] and [maxDist] distance
     * @throws IllegalArgumentException if minDist < 0 or maxDist < -1
     */
    public List<Cell> getCellAtDistance(Cell relativeCell, int minDist, int maxDist) {
        if (minDist < 0 || maxDist < -1) throw new IllegalArgumentException();

        List<Cell> cellAtDistance = new ArrayList<>();

        if (maxDist == -1) {
            // Set min/max distances to get all cells from minDist to end of map
            maxDist = Math.max(this.getXSize(), this.getYSize());
        }

        for (Cell[] cols : this.getMap()) {
            // it's duplicated but in different classes ... what do we do?
            for (Cell cell : cols) {
                if (cell != null) {
                    // Calculate X and Y distances
                    int xDist = Math.abs(cell.getCoordX() - relativeCell.getCoordX());
                    int yDist = Math.abs(cell.getCoordY() - relativeCell.getCoordY());

                    // Add cell in case distance between minDist and maxDist
                    if (xDist + yDist >= minDist && xDist + yDist <= maxDist) cellAtDistance.add(cell);
                }
            }
        }

        return cellAtDistance;
    }
}
