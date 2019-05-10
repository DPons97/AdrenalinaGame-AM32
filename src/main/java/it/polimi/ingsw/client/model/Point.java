package it.polimi.ingsw.client.model;

/**
 * Represents a point in the plane.
 * Used to identify a cell in client/server communications
 */
public class Point {
    /**
     *  X coordinate
     */
    private int x;

    /**
     *  Y coordinate
     */
    private int y;

    /**
     * Constructor: builds a point with given x and y coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(int x,int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
