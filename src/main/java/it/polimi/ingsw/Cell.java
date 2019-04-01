package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public abstract class Cell {
	/**
	 * 
	 */
	private Color color;

	/**
	 * 
	 */
	private List<Player> players;

	/**
	 * 
	 */
	private Side nord;

	/**
	 * 
	 */
	private Side sud;

	/**
	 * 
	 */
	private Side ovest;

	/**
	 * 
	 */
	private Side est;

	/**
	 * Default constructor
	 */
	public Cell() {
	}

	/**
	 * @return
	 */
	public Boolean isSpawn() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public int getCoordX() {
		// TODO implement here
		return 0;
	}

	/**
	 * @return
	 */
	public int getCoordY() {
		// TODO implement here
		return 0;
	}

	/**
	 * @return
	 */
	public List<Player> getPlayers() {
		// TODO implement here
		return null;
	}

}