package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.AmmoAlreadyOnCellException;

import java.util.*;
import java.util.stream.Collectors;

public class Map {
    /**
     * Representation of the map, bidemensional array of cells.
     */
    private Cell[][] mapMatrix;

    /**
     * List to trace spawn cells, not necessary but convenient
     */
    private List<SpawnCell> spawnPoints;

    /**
     * Size of Map
     */
    private int xSize;
    private int ySize;

    public Map(Cell[][] mapMatrix, List<SpawnCell> spawnPoints, int xSize, int ySize, Deck<Ammo> ammoDeck) {
        this.mapMatrix = mapMatrix;
        this.spawnPoints = spawnPoints;
        this.xSize = xSize;
        this.ySize = ySize;

        initAmmoCells(ammoDeck);
    }

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
        return mapMatrix[x][y];
    }

    /**
     * @param x Coordinate of column (X)
     * @return List of cells in this column
     */
    public List<Cell> getRow(int x) {
        List<Cell> column = new ArrayList<>();
        Collections.addAll(column, mapMatrix[x]);
        return column;
    }

    /**
     * @param y Coordinate of row (Y)
     * @return List of cells in this row.
     */
    public List<Cell> getColumn(int y) {
        List<Cell> row = new ArrayList<>();
        for (Cell[] c : mapMatrix) { row.add(c[y]); }
        return row;
    }

    /**
     * @return list of all cells in map. Nulls are excluded
     */
    public List<Cell> getMap() {
        List<Cell> toReturn = new ArrayList<>();

        for (int i=0; i < getXSize(); i++) {
            toReturn.addAll(getRow(i));
        }
        toReturn.removeIf(Objects::isNull);
        return toReturn;
    }

    /**
     * @param x coordinate of cell
     * @param y coordinate of cell
     * @param direction where to look
     * @return cell in [direction] side
     */
    protected Cell getAdjacentCell(int x, int y, Direction direction) {
        if (getCell(x,y).getSide(direction) != Side.BORDER) {
            switch (direction) {
                case NORTH:
                    return mapMatrix[x-1][y];
                case EAST:
                    return mapMatrix[x][y+1];
                case WEST:
                    return mapMatrix[x+1][y];
                case SOUTH:
                    return mapMatrix[x][y-1];
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
    protected Cell getAdjacentCell(Cell position, Direction direction) {
        if (getCell(position.getCoordX(),position.getCoordY()).getSide(direction) != Side.BORDER) {
            switch (direction) {
                case NORTH:
                    return mapMatrix[position.getCoordX() - 1][position.getCoordY()];
                case EAST:
                    return mapMatrix[position.getCoordX()][position.getCoordY()+1];
                case WEST:
                    return mapMatrix[position.getCoordX()][position.getCoordY() -  1];
                case SOUTH:
                    return mapMatrix[position.getCoordX() + 1][position.getCoordY()-1];
                default:
                    return null;
            }
        } else return null;
    }

    /**
     * @param cell cell to get room
     * @return all cells that are in the same room as [cell]
     */
    protected List<Cell> getRoomCells(Cell cell) {
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
     * @param relativeCell cell from which calculate distance
     * @param minDist Minimum distance
     * @param maxDist Maximum distance. -1 is equal to INFINITE
     * @return List of all cells between [minDist] and [maxDist] distance
     * @throws IllegalArgumentException if minDist < 0 or maxDist < -1
     */
    protected List<Cell> getCellAtDistance(Cell relativeCell, int minDist, int maxDist) {
        if (minDist < 0 || maxDist < -1) throw new IllegalArgumentException();

        List<Cell> cellAtDistance = new ArrayList<>();

        if (maxDist == -1) {
            // Set min/max distances to get all cells from minDist to end of map
            maxDist = Math.max(this.getXSize(), this.getYSize());
        }

        for (Cell[] cols : mapMatrix) {
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

    /**
     * @param id to search
     * @return list of cells with specified id
     */
    protected List<Cell> getCellsByID(int id) {
        return Arrays.stream(mapMatrix).flatMap(Arrays::stream)
                .filter(c ->c != null && c.getID() == id).collect(Collectors.toList());
    }

    /**
     * private method to initialize ammo cells with an ammo card
     */
    private void initAmmoCells(Deck<Ammo> ammoDeck) {
        for (Cell[] cells : mapMatrix) {
            for (Cell cell : cells) {
                if (cell != null && !cell.isSpawn()) { //if not empty cell and not a spawn cell
                    // safe cast to Ammo Cell and set ammo drawing a card from ammo deck
                    try {
                        ((AmmoCell) cell).setAmmo(ammoDeck.drawCard());
                    } catch (AmmoAlreadyOnCellException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}