package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class AdrenalinaMatch {
	/**
	 * 
	 */
	private int matchID;

	/**
	 * 
	 */
	private Cell map[][];

	/**
	 * 
	 */
	private List<Cell> spawnPoints;

	/**
	 * 
	 */
	private int maxDeaths;

	/**
	 * 
	 */
	private int currentDeaths;

	/**
	 * 
	 */
	private List<Player> deathTrack;

	/**
	 * 
	 */
	private Deck<Weapon> weaponDeck;

	/**
	 * 
	 */
	private Deck<Perk> perksDeck;

	/**
	 * 
	 */
	private Deck<Ammo> ammoDeck;

	/**
	 * 
	 */
	private List<Player> players;

	/**
	 * 
	 */
	private Player firstPlayer;

	/**
	 * 
	 */
	private Boolean frenzyEnabled;

	/**
	 * 
	 */
	private int turnDuration;

	/**
	 * Default constructor
	 */
	public AdrenalinaMatch() {
	}

	/**
	 * @return
	 */
	private Cell[][] buildMap() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public Cell[] getSpawnPoints() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public Player getFirstPlayer() {
		// TODO implement here
		return null;
	}

}