package it.polimi.ingsw;
import it.polimi.ingsw.custom_exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
		this.started = false;
		this.currentDeaths = 0;
		this.deathTrack = new ArrayList<>();
		this.spawnPoints = new ArrayList<>();
		this.frenzyEnabled = false;
		this.initAmmoDeck();
		this.initPerkDeck();
		this.initWeaponDeck();
		this.buildMap(mapID);
		this.initAmmoCells();
		this.matchID = -1; // will be given a unique value from controller when match starts
	}
	/**
	 *	create map from a json file mapid.json (eg. map1.json)
	 * @param mapID identifies map to build from json file
	 */
	private void buildMap(int mapID) {
		JSONParser parser = new JSONParser();
		int xSize;
		int ySize;
		String fileName = "././././././resources/json/map"+mapID+".json";
		String[] cardinals = {"north", "sud", "west", "east"};
		try {
			int i = 0;
			Object obj = parser.parse(new FileReader(fileName));

			JSONObject jsonObject = (JSONObject) obj;

			//get map size
			JSONArray mapSize = (JSONArray) jsonObject.get("Size");
			xSize = Integer.parseInt(mapSize.get(0).toString());
			ySize = Integer.parseInt(mapSize.get(1).toString());
			map = new Cell[xSize][ySize];

			// get the array of cells
			JSONArray mapCells = (JSONArray) jsonObject.get("Cells");

			// for every cell object in the list
			for (Object mapCell : mapCells) {
				JSONObject currCell = (JSONObject) mapCell;
				Side[] cords = new Side[4];
				String color = currCell.get("color").toString();
				Color c = null;
				// if color is X then is a corner empyt cell
				if (color.equals("X")) {
					map[(i / ySize) % xSize][i % ySize] = null;
				} else { // otherwise is a valid cell
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
					// for every side add the value to the coords array [noth, sud, wesr, east]
					for (int k = 0; k < 4; k++) {
						switch (currCell.get(cardinals[k]).toString()) {
							case "B":
								cords[k] = Side.BORDER;
								break;
							case "W":
								cords[k] = Side.WALL;
								break;
							case "N":
								cords[k] = Side.FREE;
								break;
							case "D":
								cords[k] = Side.DOOR;
								break;

						}
					}
					// create the right kind of cell
					if (Boolean.parseBoolean(currCell.get("isSpawn").toString())) {
						// create spawn cell
						map[(i / 4) % 3][i % 4] = new SpawnCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
						spawnPoints.add((SpawnCell) map[(i / ySize) % xSize][i % ySize]);
					} else {
						// create AmmoCell
						map[(i / 4) % 3][i % 4] = new AmmoCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
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
		// utility list to add parsed resources
		List<Resource> resources = new ArrayList<>();
		ammoDeck = new Deck<>();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("././././././resources/json/ammos.json"));

			JSONObject jsonObject = (JSONObject) obj;

			JSONArray ammoCards = (JSONArray) jsonObject.get("ammoCards");
			// ammo card is the json object of a card in the list of cards in the file
			for (Object ammoCard : ammoCards) {
				JSONObject currAmmo = (JSONObject) ammoCard;
				// get list of ammos of the current card
				JSONArray ammos = (JSONArray) currAmmo.get("ammos");

				//clear utility list
				resources.clear();
				// for every ammo in the current ammo list add the resource to the utility list
				for (Object ammo : ammos) {
					switch (ammo.toString()){
						case "R":
							resources.add(Resource.RED_BOX);
							break;
						case "B":
							resources.add(Resource.BLUE_BOX);
							break;
						case "Y":
							resources.add(Resource.YELLOW_BOX);
							break;
					}
				}
				// based on the number of resources create a new card
				if(resources.size()==2)
					ammoDeck.discardCard(new Ammo(resources.get(0), resources.get(1)));
				else
					ammoDeck.discardCard(new Ammo(resources.get(0), resources.get(1),resources.get(2)));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
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
	 * private method to initialize ammo cells with an ammo card
	 */
	private void initAmmoCells() {
		for(int i = 0; i<map.length; i++){
			for(int j = 0; j< map[i].length; j++){
				if(map[i][j] != null && !map[i][j].isSpawn()){ //if not empty cell and not a spawn cell
					// safe cast to Ammo Cell and set ammo drawing a card from ammo deck
                    try {
                        ((AmmoCell) map[i][j]).setAmmo(ammoDeck.drawCard());
                    } catch (AmmoAlreadyOnCellException e) {
                        e.printStackTrace();
                    }
                }
			}
		}
	}

	/**
	 *	Add  new player to the game, only possible before the match starts
	 */
	public void addPlayer(Player toAdd) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
		if(!started){
			if(players.size() < nPlayers) {
				for(Player p: players){
					if(p.getNickname().equals(toAdd.getNickname())){
						throw new PlayerAlreadyExistsException();
					}
				}
				players.add(toAdd);
			} else {
				throw new TooManyPlayersException();
			}
		} else {
			throw new MatchAlreadyStartedException();
		}
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
	public void addDeath(Player killer, boolean isOverkill) throws PlayerNotExistsException {
		if(players.contains(killer)) {
			currentDeaths++;
			deathTrack.add(killer);
			if (isOverkill) deathTrack.add(killer);
			if (currentDeaths>= maxDeaths) frenzyEnabled = true;
		} else throw new PlayerNotExistsException("Error, player not in game");
	}


	/**
	 * Sets attributes to start the match
	 */
	public void startMatch(int matchID) throws NotEnoughPlayersException, MatchAlreadyStartedException {
		if(nPlayers == players.size()) {
			if(!started) {
				int luckyFirstPlayerNumber = new Random().nextInt(nPlayers);
				this.matchID = matchID;
				this.started = true;
				this.firstPlayer = players.get(luckyFirstPlayerNumber);
			}else {
				throw new MatchAlreadyStartedException();
			}

		} else {
			throw new NotEnoughPlayersException();
		}
	}

	/**
	 * started getter
	 */
	public boolean isStarted (){
		return started;
	}
}