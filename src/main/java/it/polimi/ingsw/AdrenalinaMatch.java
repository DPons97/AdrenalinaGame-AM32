package it.polimi.ingsw;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class AdrenalinaMatch {
	/**
	 * Unique match identifier
	 */
	private int matchID;

	/**
	 * Representation of the map, bidemensional array of cells.
	 */
	private Cell[][] map;

	/**
	 * List to trace spawn cells, not necessary but convenient
	 */
	private List<SpawnCell> spawnPoints;

	/**
	 * Number of deaths in the match before the frenzy.
	 */
	private int maxDeaths;

	/**
	 * Number of deaths
	 */
	private int currentDeaths;

	/**
	 * Ordered list of player that simulate the death track.
	 */
	private List<Player> deathTrack;

	/**
	 * Deck of weapon cards
	 */
	private Deck<Weapon> weaponDeck;

	/**
	 * Deck of perk cards
	 */
	private Deck<Perk> perksDeck;

	/**
	 * Deck of ammo cards
	 */
	private Deck<Ammo> ammoDeck;

	/**
	 * Number of players
	 */
	private int nPlayers;

	/**
	 * List of players in the match
	 */
	private List<Player> players;

	/**
	 * First player to play
	 */
	private Player firstPlayer;

	/**
	 * True when frenzy is on
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
	 * @param mapID id of map to play [1,4].
	 *
	 */
	public AdrenalinaMatch(int nPlayers, int maxDeaths, int turnDuration, int mapID) {
		this.nPlayers = nPlayers;
		this.maxDeaths = maxDeaths;
		this.turnDuration = turnDuration;
		this.players = new ArrayList<>();
		currentDeaths = 0;
		deathTrack = new ArrayList<>();
		spawnPoints = new ArrayList<>();
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
		JSONParser parser = new JSONParser();

		String fileName = "././././././resources/json/map"+mapID+".json";
		map = new Cell[3][4];
		String cardinals[] = {"north", "sud", "west", "east"};
		try {
			int i = 0;
			Object obj = parser.parse(new FileReader(fileName));

			JSONObject jsonObject = (JSONObject) obj;

			JSONArray mapCells = (JSONArray) jsonObject.get("Cells");

			for (Object mapCell : mapCells) {
				JSONObject currCell = (JSONObject) mapCell;
				Side[] cords = new Side[4];
				String color = currCell.get("color").toString();
				Color c = null;
				if (color.equals("X")) {
					map[(i / 4) % 3][i % 4] = null;
				} else {
					switch (color) {
						case "B":
							c = Color.BLUE;
							break;
						case "R":
							c = Color.RED;
							break;
						case "W":
							c = Color.WHITE;
							break;
						case "P":
							c = Color.PURPLE;
							break;
						case "Y":
							c = Color.YELLOW;
							break;
					}
					for (int k = 0; k < 4; k++) {
						switch (currCell.get(cardinals[k]).toString()) {
							case "B":
								cords[k] = Side.Border;
								break;
							case "W":
								cords[k] = Side.Wall;
								break;
							case "N":
								cords[k] = Side.Free;
								break;
							case "D":
								cords[k] = Side.Door;
								break;

						}
					}

					if (Boolean.parseBoolean(currCell.get("isSpawn").toString())) {
						// create spawn cell
						map[(i / 4) % 3][i % 4] = new SpawnCell(cords[0], cords[1], cords[2], cords[3], c, (i / 4) % 3, i % 4);
						spawnPoints.add((SpawnCell) map[(i / 4) % 3][i % 4]);
					} else {
						// create AmmoCell
						map[(i / 4) % 3][i % 4] = new AmmoCell(cords[0], cords[1], cords[2], cords[3], c, (i / 4) % 3, i % 4);
					}
				}

			}

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * private method to initialize ammo deck.
	 * parses a ammos.json file
	 */
	private void initAmmoDeck() {
		// TODO implement here
		ammoDeck = new Deck<>();
	}

	/**
	 * private methos to initialize perk deck
	 * parses perks.json file
	 */
	private void initPerkDeck() {
		// TODO implement here
		perksDeck = new Deck<>();

	}

	/**
	 * private method to initialize weapon deck
	 * parses weapons.json file.
	 */
	private void initWeaponDeck() {
		// TODO implement here
		weaponDeck = new Deck<>();
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
	public List<SpawnCell> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * @return the first player.
	 */
	public Player getFirstPlayer() {
		return players.get(0);
	}

	/**
	 * @return a List<Player> with the players in the game.
	 */
	public List<Player> getPlayers() {
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