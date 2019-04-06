package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public abstract class Cell {
	/**
	 * color of the cell
	 */
	private Color color;

	/**
	 * list of the players in the cell
	 */
	private List<Player> players;

	/**
	 * top side of the cell
	 */
	private Side north;

	/**
	 * down side of the cell
	 */
	private Side sud;

	/**
	 * left side of the cell
	 */
	private Side west;

	/**
	 * right side of the cell
	 */
	private Side east;

	/**
	 * return the coordinate X
	 */
	private int coordX ;

	/**
	 * return the coordinate Y
	 */
	private int coordY;
	/**
	 * return the north side
	 */

	/**
	 * basic initialization of the list of player, color and sides
	 */
	public Cell(Side north, Side sud, Side west, Side east, Color c, int x, int y) {
		this.north = north;
		this.sud = sud;
		this.east = east;
		this.west = west;
		this.color = c;
		players = new ArrayList<Player>();
		this.coordX = x;
		this.coordY = y;
	}

	public Side getNorth() {
		return north;
	}
	/**
	 * return the sud side
	 */
	public Side getSud() {
		return sud;
	}
	/**
	 * return the weast side
	 */
	public Side getweast() {
		return west;
	}
	/**
	 * return the east side
	 */
	public Side geteast() {
		return east;
	}


	/**
	 * if the spawn is in the cell is true
	 */

	/**
	 * @return the coordinate X
	 */
	public int getCoordX() {
		return coordX;
	}

	/**
	 * @return the coordinate Y
	 */
	public int getCoordY() {
		return coordY;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the players in the cell
	 */
	public List<Player> getPlayers() {
		return players;
	}

	public abstract boolean isSpawn();

}