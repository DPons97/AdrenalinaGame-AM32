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
import java.util.Objects;
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
	private Map boardMap;

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
	 * 	duration of a turn in seconds.
	 */
	private int turn;

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

		// Map initialization
		this.buildMap(mapID);

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
			boardMap = new Map(newMap, spawnPoints, xSize, ySize, ammoDeck);

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
		powerupDeck = new Deck<>();
		JSONParser parser = new JSONParser();
		String name;
		String description;
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
	 * @param id of players to search
	 * @return list of players with defined id
	 */
	public List<Player> getPlayersByID(int id) {
		return players.stream().filter(Objects::nonNull)
				.filter(p -> p.getID() == id).collect(Collectors.toList());
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
	public Map getBoardMap() { return boardMap; }

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
	 * @return this turn's player.
	 */
	public Player getTurnPlayer() {
		return players.get((turn+players.indexOf(firstPlayer))%players.size());
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
	 * @param notID id to exclude
	 * @param minDistance minimum distance between caller and target
	 * @param maxDistance maximum distance between caller and target (-1 if no max is required)
	 * @param players list of players for PREV_TARGET or TARGET_VISIBLE
	 * @return List with players that satisfy query
	 */
	public List<Player> getSelectablePlayers(Player caller, String from, int notID, int minDistance,
											  int maxDistance, List<Player> players) {
		List<Player> toReturn = new ArrayList<>();
		int pX;
		int pY;
		switch (from){
			case "VISIBLE":
				for(Cell c: caller.getVisibleCellsAtDistance(minDistance, maxDistance))
					toReturn.addAll(c.getPlayers());
				break;
			case "NOT_VISIBLE":
				List<Cell> validCells = caller.getOutOfSightCells(minDistance,maxDistance);
				for(Cell c: validCells)
					toReturn.addAll(c.getPlayers());
				break;
			case "TARGET_VISIBLE":
				for(Cell c: players.get(0).getVisibleCellsAtDistance(minDistance, maxDistance))
					toReturn.addAll(c.getPlayers());
				break;
			case "DIRECTION":
				pX = caller.getPosition().getCoordX();
				pY = caller.getPosition().getCoordY();
				for (Cell c: boardMap.getRow(pX))
					if (c != null && c.getCoordY() != pY) toReturn.addAll(c.getPlayers());
				for (Cell c: boardMap.getColumn(pY))
					if(c != null && c.getCoordX() != pX) toReturn.addAll(c.getPlayers());
				toReturn = intersection(toReturn,
										caller.getCellAtDistance(minDistance,maxDistance).stream().
										map(Cell::getPlayers).
										flatMap(List::stream).
										collect(Collectors.toList()));
				break;
			case "DIRECTION_VISIBLE":
				pX = caller.getPosition().getCoordX();
				pY = caller.getPosition().getCoordY();

				// start from caller cell go in the four cardinal direction one at a time,
				// stop when current cell has border or wall on the direction you are exploring

				int i;
				// exploring east -> right
				for(i = pX; boardMap.getCell(pX, i).getEast()!= Side.BORDER && boardMap.getCell(pX, i).getEast()!= Side.WALL; i++)
					toReturn.addAll(boardMap.getCell(pX, i).getPlayers());
				toReturn.addAll(boardMap.getCell(pX, i).getPlayers());

				// exploring west -> left
				for(i = pX; boardMap.getCell(pX, i).getWest()!= Side.BORDER && boardMap.getCell(pX, i).getWest()!= Side.WALL; i--)
					toReturn.addAll(boardMap.getCell(pX, i).getPlayers());
				toReturn.addAll(boardMap.getCell(pX, i).getPlayers());

				// exploring north -> up
				for(i = pY; boardMap.getCell(i,pY).getNorth()!= Side.BORDER && boardMap.getCell(i,pY).getNorth()!= Side.WALL; i--)
					toReturn.addAll(boardMap.getCell(i,pY).getPlayers());
				toReturn.addAll(boardMap.getCell(i,pY).getPlayers());

				// exploring south -> down
				for(i = pY; boardMap.getCell(i,pY).getNorth()!= Side.BORDER && boardMap.getCell(i,pY).getNorth()!= Side.WALL; i++)
					toReturn.addAll(boardMap.getCell(i,pY).getPlayers());
				toReturn.addAll(boardMap.getCell(i,pY).getPlayers());

				// keep only the ones at right distance
				toReturn = intersection(toReturn,
						caller.getCellAtDistance(minDistance,maxDistance).stream().
								map(Cell::getPlayers).
								flatMap(List::stream).
								collect(Collectors.toList()));

			break;
			case "ATTACKER":
				// TODO: implement here
				break;
			case "DAMAGED":
				// TODO: implement here
				break;
			case "ANY":
				toReturn.addAll(caller.getCellAtDistance(minDistance,maxDistance).stream().map(Cell::getPlayers)
						.flatMap(List::stream).collect(Collectors.toList()));
				break;
			default:
				// from is an integer id
				int id = Integer.parseInt(from);

				// we are selecting players-> first control if there are players with requested id
				toReturn.addAll(players.stream()
						.filter(p -> p.getID() == id).collect(Collectors.toList()));

				// if none were found, the id should be a cell id
				if(toReturn.isEmpty()){
					//look for cells with id
					List<Cell> foundCells = boardMap.getCellsByID(id);

					for(Cell c: foundCells){
						for(Cell pC: boardMap.getCellAtDistance(c,minDistance,maxDistance)){
							toReturn.addAll(pC.getPlayers());
						}
					}
				} else {
					toReturn = intersection(toReturn,
							caller.getCellAtDistance(minDistance,maxDistance).stream().
									map(Cell::getPlayers).
									flatMap(List::stream).
									collect(Collectors.toList()));
				}
		}
		// filter notID
		toReturn.removeIf(p -> p.getID() == notID);
		//there is an apposite call to get yourself
		toReturn.remove(caller);
		return toReturn;
	}

	/**
	 * @param caller player requesting the selection
	 * @param from string code identifying type of selecatble players: (VISIBLE, NOT_VISIBLE, TARGET_VISIBLE, DIRECTION,
	 *                DIRECTION_VISIBLE, #[playerid], ATTACKER, DAMAGED
	 * @param notID id to exclude
	 * @param minDistance minimum distance between caller and target
	 * @param maxDistance maximum distance between caller and target (-1 if no max is required)
	 * @param players list of players for PREV_TARGET or TARGET_VISIBLE
	 * @return List with cells that satisfy query
	 */
	public List<Cell> getSelectableCells(Player caller, String from, int notID, int minDistance,
										 int maxDistance, List<Player> players) throws InvalidStringException {
		List<Cell> toReturn = new ArrayList<>();
		int pX;
		int pY;
		switch (from){
			case "VISIBLE":
				toReturn.addAll(caller.getVisibleCellsAtDistance(minDistance,maxDistance));
				break;
			case "NOT_VISIBLE":
				toReturn.addAll(caller.getOutOfSightCells(minDistance,maxDistance));
				break;
			case "TARGET_VISIBLE":
				toReturn.addAll(players.get(0).getVisibleCellsAtDistance(minDistance,maxDistance));
				break;
			case "DIRECTION":
				pX = caller.getPosition().getCoordX();
				pY = caller.getPosition().getCoordY();
				for (Cell c: boardMap.getRow(pX))
					if(c!=null && c.getCoordY() != pY)toReturn.add(c);
				for (Cell c: boardMap.getColumn(pY))
					if(c!=null && c.getCoordX() != pX)toReturn.add(c);
				toReturn = intersection(toReturn, caller.getCellAtDistance(minDistance,maxDistance));
				break;
			case "DIRECTION_VISIBLE":
				pX = caller.getPosition().getCoordX();
				pY = caller.getPosition().getCoordY();
				// start from caller cell go in the four cardinal direction one at a time,
				// stop when current cell has border or wall on the direction you are exploring

				int i;
				// exploring east -> right
				for(i = pY; boardMap.getCell(pX, i).getEast()!= Side.BORDER && boardMap.getCell(pX, i).getEast()!= Side.WALL; i++)
					toReturn.add(boardMap.getCell(pX, i));
				toReturn.add(boardMap.getCell(pX, i));
				// exploring west -> left
				for(i = pY; boardMap.getCell(pX, i).getWest()!= Side.BORDER && boardMap.getCell(pX, i).getWest()!= Side.WALL; i--)
					toReturn.add(boardMap.getCell(pX, i));
				toReturn.add(boardMap.getCell(pX, i));
				// exploring north -> up
				for(i = pX; boardMap.getCell(i,pY).getNorth()!= Side.BORDER && boardMap.getCell(i,pY).getNorth()!= Side.WALL; i--)
					toReturn.add(boardMap.getCell(i,pY));
				toReturn.add(boardMap.getCell(i, pY));
				// exploring south -> down
				for(i = pX; boardMap.getCell(i,pY).getNorth()!= Side.BORDER && boardMap.getCell(i,pY).getNorth()!= Side.WALL; i++)
					toReturn.add(boardMap.getCell(i,pY));
				toReturn.add(boardMap.getCell(i, pY));
				// keep only the ones at right distance
				toReturn = intersection(toReturn, caller.getCellAtDistance(minDistance,maxDistance));

				break;
			case "ANY":
				toReturn.addAll(caller.getCellAtDistance(minDistance,maxDistance));
				break;
			default:
				// from is an integer id
				int id = Integer.parseInt(from);
				// we are selecting cells -> first control if there are cells with requested id
				toReturn.addAll(boardMap.getCellsByID(id));

				// if none were found, the id should be a player id
				if(toReturn.isEmpty()){
					//look for players with id
					List<Player> foundPlayers = getPlayersByID(id);

					for(Player p: foundPlayers){
						toReturn.addAll(p.getCellAtDistance(minDistance,maxDistance));
					}
				} else {
					toReturn = intersection(toReturn, caller.getCellAtDistance(minDistance,maxDistance));
				}



		}
		// filter notID
		toReturn.removeIf(p -> p.getID() == notID);
		return toReturn;
	}


	/**
	 * @param caller player requesting the selection
	 * @return List with cells that satisfy query
	 */
	public List<List<Cell>> getSelectableRooms(Player caller) {
		List<List<Cell>> toReturn = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			// Player has a door nearby
			if (caller.getPosition().getSide(dir) == Side.DOOR) {
				toReturn.add(
					boardMap.getRoomCells(
						boardMap.getCell( boardMap.getAdjacentCell(caller.getPosition(), dir).getCoordX(),
								     boardMap.getAdjacentCell(caller.getPosition(), dir).getCoordY()
						)
					)
				);
			}
		}
		return toReturn;
	}


	/**
	 * This was taken from stackoverflow
	 * @param list1 first list
	 * @param list2 second list
	 * @return List with intersection of first list and second list
	 **/
	private <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<>();

		for (T t : list1) {
			if(list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

}