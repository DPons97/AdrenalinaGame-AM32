package it.polimi.ingsw.Server.Model;

import java.util.ArrayList;
import java.util.List;

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
	private Side south;

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
	 * id for targeting effects
	 */
	private int id;

	/**
	 * @param north upper side of the cell
	 * @param south bottom side of the cell
	 * @param west left side of the cell
	 * @param east right side of the cell
	 * @param c cell color
	 * @param x cell x coordinate
	 * @param y cell y coordinate
	 */
	public Cell(Side north, Side south, Side west, Side east, Color c, int x, int y) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.color = c;
		players = new ArrayList<>();
		this.coordX = x;
		this.coordY = y;
		this.id = -1;
	}

	/**
	 * return the cell id
	 */
	public int getID() {
		return id;
	}

	/**
	 * set cell id
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
 	 * @param direction
	 * @return type of side in [direction]
	 */
	public Side getSide(Direction direction) {
		switch (direction) {
			case NORTH:
				return getNorth();
			case EAST:
				return getEast();
			case WEST:
				return getWest();
			case SOUTH:
				return getSouth();
			default:
				return null;
		}
	}

	/**
	 * return the north side
	 */
	public Side getNorth() {
		return north;
	}
	/**
	 * return the south side
	 */
	public Side getSouth() {
		return south;
	}
	/**
	 * return the west side
	 */
	public Side getWest() {
		return west;
	}
	/**
	 * return the east side
	 */
	public Side getEast() {
		return east;
	}

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

	/**
	 * Place [toPlace] inside this map cell, if it's not present
	 * @param toPlace player to be placed on the cell
	 */
	public void addPlayer(Player toPlace) {
		if (!players.contains(toPlace)) players.add(toPlace);
	}

	/**
	 * Remove [toRemove] from this map cell, if it's present
	 * @param toRemove player to be removed from cell
	 */
	public void removePlayer(Player toRemove) {
		players.remove(toRemove);
	}

	public abstract boolean isSpawn();
}