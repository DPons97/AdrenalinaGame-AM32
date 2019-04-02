package it.polimi.ingsw;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
	private int nPlayers;

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
	 * 	duration of a turn in seconds.
	 */
	private int turnDuration;

	/**
	 * 	false before first turn takes place.
	 */
	private boolean started;

	/**
	 * @param nPlayers number of players [3,5].
	 * @param maxDeaths maximum number of deaths befor frenzy.
	 * 					the same as skulls in the manual
	 * @param turnDuration max duration of a turn in seconds.
	 * @param mapID id of map to play.
	 *
	 */
	public AdrenalinaMatch(int nPlayers, int maxDeaths, int turnDuration, int mapID) {
		this.nPlayers = nPlayers;
		this.maxDeaths = maxDeaths;
		this.turnDuration = turnDuration;
		this.players = new ArrayList<Player>();
		currentDeaths = 0;
		deathTrack = new ArrayList<Player>();
		frenzyEnabled = false;
		this.initAmmoDeck();
		this.initPerkDeck();
		this.initWeaponDeck();
		this.buildMap(mapID);
	}

	/**
	 *	create map from a json file mapid.json (eg. map1.json)
	 * @param mapID identifies map to build from json file
	 */
	private void buildMap(int mapID) {
		// TODO implement here
		map = new Cell[3][4];
	}

	/**
	 * private method to initialize ammo deck.
	 * parses a ammos.json file
	 */
	private void initAmmoDeck() {
		// TODO implement here
		ammoDeck = new Deck<Ammo>();
	}

	/**
	 * private methos to initialize perk deck
	 * parses perks.json file
	 */
	private void initPerkDeck() {
		// TODO implement here
		perksDeck = new Deck<Perk>();

	}

	/**
	 * private method to initialize weapon deck
	 * parses weapons.json file.
	 */
	private void initWeaponDeck() {
		// TODO implement here
		weaponDeck = new Deck<Weapon>();
	}

	/**
	 *
	 */
	public void addPlayer(Player toAdd) {
		if(!started)
			players.add(toAdd);
	}

	/**
	 * @return the cells that are spawn points.
	 */
	public Cell[] getSpawnPoints() {
		// TODO implement here
		return null;
	}

	/**
	 * @return the first player.
	 */
	public Player getFirstPlayer() {
		// TODO implement here
		return players.get(0);
	}

	/**
	 * @return a List<Player> with the players in the game.
	 */
	public List<Player> getPlayers() {
		// TODO implement here
		return players;
	}

	/**
	 * @return a Deck<Ammo> with the ammo cards.
	 */
	public Deck<Ammo> getAmmoDeck() {
		return ammoDeck;
	}

	/**
	 * @return the match Id as an int.
	 */
	public int getMatchID() {
		return matchID;
	}

	/**
	 * @return the number of maximun deaths, usually 8 for a standard match.
	 */
	public int getMaxDeaths() {
		return maxDeaths;
	}

	/**
	 * @return the number of occurred deaths.
	 */
	public int getCurrentDeaths() {
		return currentDeaths;
	}
	/**
	 * @return the death track as a list of players.
	 */
	public List<Player> getDeathTrack() {
		return deathTrack;
	}

	/**
	 * @return the weapon deck.
	 */
	public Deck<Weapon> getWeaponDeck() {
		return weaponDeck;
	}

	/**
	 * @return the perk deck
	 */
	public Deck<Perk> getPerksDeck() {
		return perksDeck;
	}

	/**
	 * @return true if the frenzy is on, otherwise returns false
	 */
	public Boolean isFrenzyEnabled() {
		return frenzyEnabled;
	}

	/**
	 * @return the maximum time for a turn.
	 */
	public int getTurnDuration() {
		return turnDuration;
	}

	/**
	 * Insert new death in the game board, updates # of deaths and death track
	 */
	public void addDeath(Player killer, boolean isOverkill) throws PlayerNotExistsException{
		if(players.contains(killer)) {
			currentDeaths++;
			deathTrack.add(killer);
			if (isOverkill) deathTrack.add(killer);
			if (currentDeaths>= maxDeaths) frenzyEnabled = true;
		} else throw new PlayerNotExistsException("Error, player not in game");
	}
}