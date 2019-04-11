package it.polimi.ingsw;
import it.polimi.ingsw.custom_exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 *
 */
public class AdrenalinaMatch {
	/**
	 * Unique match identifier
	 */
	private int matchID;

	/**
	 * Reference to map
	 */
	private Map map;

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
	 * Deck of powerup cards
	 */
	private Deck<Powerup> powerupDeck;

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
		this.frenzyEnabled = false;
		this.initAmmoDeck();
		this.initPowerupDeck();
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
		String[] cardinals = {"north", "south", "west", "east"};
		try {
			int i = 0;
			Object obj = parser.parse(new FileReader(fileName));

			JSONObject jsonObject = (JSONObject) obj;

			//get map size
			JSONArray mapSize = (JSONArray) jsonObject.get("Size");
			xSize = Integer.parseInt(mapSize.get(0).toString());
			ySize = Integer.parseInt(mapSize.get(1).toString());
			Cell[][] newMap = new Cell[xSize][ySize];
			ArrayList<SpawnCell> spawnPoints = new ArrayList<>();

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
					newMap[(i / ySize) % xSize][i % ySize] = null;
				} else { // otherwise is a valid cell
					c = stringToColor(color);
					// for every side add the value to the coords array [north, south, west, east]
					for (int k = 0; k < 4; k++) {
						cords[k] = stringToSide(currCell.get(cardinals[k]).toString());
					}
					// create the right kind of cell
					if (Boolean.parseBoolean(currCell.get("isSpawn").toString())) {
						// create spawn cell
						newMap[(i / 4) % 3][i % 4] = new SpawnCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
						spawnPoints.add((SpawnCell) newMap[(i / ySize) % xSize][i % ySize]);
					} else {
						// create AmmoCell
						newMap[(i / 4) % 3][i % 4] = new AmmoCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
					}
				}
				i++;
			}

			// Generate Map object
			map = new Map(newMap, spawnPoints, xSize, ySize);

		} catch (ParseException | IOException | InvalidStringException e) {
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
					resources.add(stringToResource(ammo.toString()));
				}
				// based on the number of resources create a new card
				if(resources.size()==2)
					ammoDeck.discardCard(new Ammo(resources.get(0), resources.get(1)));
				else
					ammoDeck.discardCard(new Ammo(resources.get(0), resources.get(1),resources.get(2)));
			}
		} catch (ParseException | IOException | InvalidStringException e) {
			e.printStackTrace();
		}
	}

	/**
	 * private methos to initialize powerup deck
	 * parses powerup.json file
	 */
	private void initPowerupDeck() {
		// TODO implement here
		powerupDeck = new Deck<>();
		JSONParser parser = new JSONParser();
		String name, description;
		Resource bRes;
		try {
			Object obj = parser.parse(new FileReader("././././././resources/json/powerups.json"));

			JSONObject jsonObject = (JSONObject) obj;

			JSONArray pupCards = (JSONArray) jsonObject.get("Powerups");

			for(Object pupCard: pupCards){
				JSONObject currPup = (JSONObject) pupCard;
				name = currPup.get("name").toString();
				description = currPup.get("description").toString();
				bRes = stringToResource(currPup.get("bonusResource").toString());
				powerupDeck.discardCard(new Powerup(name, description, bRes, currPup));
			}
		} catch (ParseException | IOException | InvalidStringException e) {
			e.printStackTrace();
		}
	}

	/**
	 * private method to initialize weapon deck
	 * parses weapons.json file.
	 */
	private void initWeaponDeck() {
		weaponDeck = new Deck<>();
		JSONParser parser = new JSONParser();
		String name;
		List<Resource> cost = new ArrayList<>();
		try {
			Object obj = parser.parse(new FileReader("././././././resources/json/weapons.json"));

			JSONObject jsonObject = (JSONObject) obj;

			JSONArray weaponCards = (JSONArray) jsonObject.get("Weapons");

			for(Object weaponCard: weaponCards){
				JSONObject currWeapon = (JSONObject) weaponCard;
				name = currWeapon.get("name").toString();
				JSONArray cardCost = (JSONArray) currWeapon.get("cost");
				for(Object res:cardCost){
					cost.add(stringToResource(res.toString()));
				}

				if(currWeapon.get("type").toString().equals("EFFECT")){
					weaponDeck.discardCard(new WeaponEffect(name,cost, currWeapon));
				} else {
					weaponDeck.discardCard(new WeaponMode(name,cost, currWeapon));
				}

			}
		} catch (ParseException | IOException | InvalidStringException e) {
			e.printStackTrace();
		}
	}

	/**
	 * private method to initialize ammo cells with an ammo card
	 */
	private void initAmmoCells() {
		for (Cell[] cells : map.getMap()) {
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
	 * @return the first player.
	 */
	public Player getFirstPlayer() {
		return firstPlayer;
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
	 * @return the powerup deck
	 */
	public Deck<Powerup> getPowerupDeck() {
		return powerupDeck;
	}

	/**
	 * @return the match Id as an int.
	 */
	public int getMatchID() {
		return matchID;
	}

	/**
	 * @return current map
	 */
	public Map getMap() { return map; }

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

	/**
	 * gat Resource associated with string
	 */
	public static Resource stringToResource(String s) throws InvalidStringException {
		switch (s){
			case "R":
				return Resource.RED_BOX;
			case "B":
				return Resource.BLUE_BOX;
			case "Y":
				return Resource.YELLOW_BOX;
			case "U":
				return Resource.UNDEFINED_BOX;
			default:
				throw new InvalidStringException();
		}
	}

	/**
	 * @return color Colo associated with string
	 */
	public static Color stringToColor(String s) throws InvalidStringException {
		switch (s) {
			case "B":
				return Color.BLUE;
			case "R":
				return Color.RED;
			case "W":
				return Color.WHITE;
			case "P":
				return Color.PURPLE;
			case "Y":
				return Color.YELLOW;
			case "G":
				return Color.GREEN;
			default:
				throw new InvalidStringException();
		}
	}

	/**
	 * @return Side associated with string
	 */
	public static Side stringToSide(String s) throws InvalidStringException {
		switch (s) {
			case "B":
				return Side.BORDER;
			case "W":
				return Side.WALL;
			case "N":
				return Side.FREE;
			case "D":
				return Side.DOOR;
			default:
				throw new InvalidStringException();

		}
	}

	/**
	 * @param caller player requesting the selection
	 * @param from string code identifying type of selecatble players: (VISIBLE, NOT_VISIBLE, TARGET_VISIBLE, DIRECTION,
	 *                PREV_TARGET, #[playerid], ATTACKER, DAMAGED
	 * @param minDistance minimum distance between caller and target
	 * @param maxDistance maximum distance between caller and target (-1 if no max is required)
	 * @param players list of players for PREV_TARGET or TARGET_VISIBLE
	 * @return List with players that satisfy query
	 */
	public List<Player> getSelectablePlayers(Player caller, String from, int minDistance,
											  int maxDistance, List<Player> players) throws InvalidStringException {
		List<Player> toReturn = new ArrayList<>();
		switch (from){
			case "VISIBLE":
				for(Cell c: caller.getVisibleCellsAtDistance(0, -1))
					toReturn.addAll(c.getPlayers());
				break;
			case "NOT-VISIBLE":
				List<Cell> validCells = caller.getCellAtDistance(0,-1);
				validCells.removeAll(caller.getVisibleCellsAtDistance(0, -1));
				for(Cell c: validCells)
					toReturn.addAll(c.getPlayers());
				break;
			case "TARGET-VISIBLE":
				for(Cell c: players.get(0).getVisibleCellsAtDistance(0, -1))
					toReturn.addAll(c.getPlayers());
				break;
			case "DIRECTION":
				int pX = caller.getPosition().getCoordX();
				int pY = caller.getPosition().getCoordY();
				for (Cell c: map.getMap()[pX])
					toReturn.addAll(c.getPlayers());
				for (Cell[] c: map.getMap())
					if(c[pY].getCoordX() != pX)toReturn.addAll(c[pY].getPlayers());
				break;
			case "PREV_TARGET":
				toReturn.addAll(players);
				break;
			case "ATTACKER":
				// TODO: implement here
				break;
			case "DAMAGED":
				// TODO: implement here
				break;
			default:
				int id = Integer.parseInt(from);
				toReturn.addAll(players.stream()
						.filter(p -> p.getID() == id).collect(Collectors.toList()));

		}
		return toReturn;
	}

	/**
	 * @param caller player requesting the selection
	 * @param from string code identifying type of selecatble players: (VISIBLE, NOT_VISIBLE, TARGET_VISIBLE, DIRECTION,
	 *                PREV_TARGET, #[playerid], ATTACKER, DAMAGED
	 * @param minDistance minimum distance between caller and target
	 * @param maxDistance maximum distance between caller and target (-1 if no max is required)
	 * @param players list of players for PREV_TARGET or TARGET_VISIBLE
	 * @return List with cells that satisfy query
	 */
	public List<Cell> getSelectableCells(Player caller, String from, int minDistance,
										 int maxDistance, List<Player> players) throws InvalidStringException {
		List<Cell> toReturn = new ArrayList<>();
		switch (from){
			case "VISIBLE":
				toReturn.addAll(caller.getVisibleCellsAtDistance(0, -1));
				break;
			case "NOT-VISIBLE":
				List<Cell> validCells = caller.getCellAtDistance(0,-1);
				validCells.removeAll(caller.getVisibleCellsAtDistance(0, -1));
				toReturn.addAll(validCells);
				break;
			case "TARGET-VISIBLE":
				toReturn.addAll(players.get(0).getVisibleCellsAtDistance(0, -1));
				break;
			case "DIRECTION":
				int pX = caller.getPosition().getCoordX();
				int pY = caller.getPosition().getCoordY();
				toReturn.addAll(Arrays.asList(map.getMap()[pX]));
				for (Cell[] c: map.getMap())
					if(c[pY].getCoordX() != pX)toReturn.add(c[pY]);
				break;
			case "PREV_TARGET":
				// TODO implement here
				// serve?
				break;
			case "ANY":
				toReturn.addAll(caller.getCellAtDistance(0,-1));
				break;
			default:
				int id = Integer.parseInt(from);
				toReturn.addAll(Arrays.stream(map.getMap()).flatMap(Arrays::stream)
						.filter(c -> c.getID() == id).collect(Collectors.toList()));

		}
		return toReturn;
	}



}