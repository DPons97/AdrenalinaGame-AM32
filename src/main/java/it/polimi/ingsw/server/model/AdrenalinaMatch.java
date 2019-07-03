package it.polimi.ingsw.server.model;
import it.polimi.ingsw.custom_exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
public class AdrenalinaMatch {

	private static final Color[] PLAYER_COLORS = {Color.YELLOW, Color.PURPLE, Color.BLUE, Color.WHITE, Color.GREEN};

	/**
	 *  Point reward to give to players inside death track
	 */
	private static int[] killRewards = {8, 6, 4, 2, 1, 1};

	/**
	 * Maximum number of players in a game (as defined in rules)
	 */
	private static int maxPlayers = 5;

	/**
	 * Minimum number of players in a game (as defined in rules)
	 */
	private static int minPlayers = 2;

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
	 *  Ordered list parallel to deathtrack to keep track of overkills
	 */
	private List<Boolean> overkills;


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
	 * Number of players of this match (Max and Min)
	 */
	private int nPlayers;

	/**
	 * map ID
	 */
	private int mapID;

	/**
	 * List of players in the match
	 */
	private List<Player> players;

	/**
	 * First player to play
	 */
	private Player firstPlayer;

	/**
	 * True if firstPlayer played his frenzy turn
	 */
	private boolean firstPlayedFrenzy;

	/**
	 * 	duration of a turn in seconds.
	 */
	private int turnDuration;

	/**
	 * 	turn number
	 */
	private int turn;

	/**
	 * Current match's state
	 */
	private MatchState state;

	/**
	 * List of available colors
	 */
	private List<Color> availableColors= new ArrayList<>(Arrays.asList(PLAYER_COLORS));

	/**
	 * @param nPlayers number of players [3,5].
	 * @param maxDeaths maximum number of deaths before frenzy.
	 * 					the same as skulls in the manual
	 * @param turnDuration max duration of a turn in seconds.
	 * @param mapID id of map to play [1,4].
	 *
	 */
	public AdrenalinaMatch(int nPlayers, int maxDeaths, int turnDuration, int mapID) {
		// Round number of players in this match as defined in rules
		if (nPlayers < minPlayers ) this.nPlayers = minPlayers;
		else if (nPlayers > maxPlayers) this.nPlayers = maxPlayers;
		else this.nPlayers = nPlayers;
		if(mapID > 4 || mapID < 1) mapID = 1;
		this.maxDeaths = maxDeaths;
		this.turnDuration = turnDuration;
		this.turn = 0;
		this.mapID = mapID;
		this.players = new ArrayList<>();
		this.firstPlayedFrenzy = false;
		this.state = MatchState.NOT_STARTED;
		this.currentDeaths = 0;
		this.deathTrack = new ArrayList<>();
		this.overkills = new ArrayList<>();
		this.initAmmoDeck();
		this.initPowerupDeck();
		this.initWeaponDeck();

		// Map initialization
		this.buildMap(mapID);
	}

	/**
	 *	create map from a json file mapid.json (eg. map1.json)
	 * @param mapID identifies map to build from json file
	 */
	private void buildMap(int mapID) {
		JSONParser parser = new JSONParser();
		int xSize;
		int ySize;
		String fileName = "/json/map" +mapID+".json";
		String[] cardinals = {"north", "south", "west", "east"};
		try {
			int i = 0;
			Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName), StandardCharsets.UTF_8)));
			JSONObject jsonObject = (JSONObject) obj;

			//get map size
			JSONArray mapSize = (JSONArray) jsonObject.get("Size");
			xSize = Integer.parseInt(mapSize.get(0).toString());
			ySize = Integer.parseInt(mapSize.get(1).toString());
			Cell[][] newMap = new Cell[xSize][ySize];
			ArrayList<SpawnCell> spawnPoints = new ArrayList<>();
			ArrayList<AmmoCell> ammoPoints = new ArrayList<>();

			// get the array of cells
			JSONArray mapCells = (JSONArray) jsonObject.get("Cells");

			// for every cell object in the list
			for (Object mapCell : mapCells) {
				JSONObject currCell = (JSONObject) mapCell;
				Side[] cords = new Side[4];
				String color = currCell.get("color").toString();
				Color c;
				// if color is X then is a corner empty cell
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
                        SpawnCell newCell = new SpawnCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
                        newMap[(i / ySize) % xSize][i % ySize] = newCell;
                        spawnPoints.add(newCell);
					} else {
						// create AmmoCell
                        AmmoCell newCell = new AmmoCell(cords[0], cords[1], cords[2], cords[3], c, (i / ySize) % xSize, i % ySize);
						newMap[(i / ySize) % xSize][i % ySize] = newCell;
						ammoPoints.add(newCell);
					}
				}
				i++;
			}

			// Generate Map object
			boardMap = new Map(newMap, spawnPoints, ammoPoints, xSize, ySize, ammoDeck, weaponDeck);

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
			Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/json/ammos.json"), StandardCharsets.UTF_8)));
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
			Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/json/powerups.json"), StandardCharsets.UTF_8)));

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
		try {
			Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/json/weapons.json"), StandardCharsets.UTF_8)));
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray weaponCards = (JSONArray) jsonObject.get("Weapons");

			for(Object weaponCard: weaponCards){
				List<Resource> cost = new ArrayList<>();
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
		if(state == MatchState.NOT_STARTED){
			if(players.size() < nPlayers) {
				for(Player p: players){
					if(p.getNickname().equals(toAdd.getNickname())){
						throw new PlayerAlreadyExistsException();
					}
				}
				Random r = new Random();
				int randomIndex = r.nextInt(availableColors.size());
				toAdd.setColor(availableColors.get(randomIndex));
				availableColors.remove(randomIndex);
				players.add(toAdd);
				if(firstPlayer == null) firstPlayer = toAdd;
			} else {
				throw new TooManyPlayersException();
			}
		} else {
			throw new MatchAlreadyStartedException();
		}
	}

	/**
	 * Kick a player from match and set his match to null
	 * @param toKick player that wants to leave
	 */
	public void kickPlayer(Player toKick) throws MatchAlreadyStartedException, PlayerNotExistsException {
		if(state == MatchState.NOT_STARTED){
			if(!players.contains(toKick))throw new PlayerNotExistsException();
			players.remove(toKick);
			availableColors.add(toKick.getColor());
			toKick.setMatch(null);
			for (Player p : players) {
				p.setReady(false);
			}
			for (Player p : players) {
				if(p.getConnection()!=null)
					p.getConnection().updateMatch(this);

			}
		} else throw new MatchAlreadyStartedException();
	}

	/**
	 * @return death track rewards
	 */
	public List<Integer> getRewards() {
		List<Integer> returnVal = new ArrayList<>();
		for (int reward : killRewards) returnVal.add(reward);
		return returnVal;
	}

	/**
	 * @return the first player.
	 */
	public Player getFirstPlayer() {
		return firstPlayer;
	}

	/**
	 * @return true if first player played frenzy turn
	 */
	public boolean isFirstPlayedFrenzy() { return firstPlayedFrenzy; }

	/**
	 * @param firstPlayedFrenzy set true if first player played frenzy
	 */
	public void setFirstPlayedFrenzy(boolean firstPlayedFrenzy) { this.firstPlayedFrenzy = firstPlayedFrenzy; }

	/**
	 * @return the first player.
	 */
	public int getMapID() {
		return mapID;
	}

	/**
	 * @return a List<Player> with the players in the game.
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Set max players of this match
	 * @param nPlayers that can join and play this match
	 */
	protected void setPlayerNumber(int nPlayers) { this.nPlayers = nPlayers; }

	/**
	 * @return max players in this match
	 */
	public int getPlayerNumber() { return nPlayers; }

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
	 * @return the death track as a list of players.
	 */
	public List<Boolean> getOverkills() {
		return overkills;
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
		return players.get((turn + players.indexOf(firstPlayer))%players.size());
	}

	/**
	 * @return match's current round.
	 */
	public int getTurn() { return turn; }

	/**
	 * Increment turn number by +1 and update match state
	 */
	public void nextTurn() {
	    turn++;
	    if (state != MatchState.FRENZY_TURN) setMatchState(MatchState.PLAYER_TURN);
	}

	/**
	 * @param nextState of match
	 */
	public void setMatchState(MatchState nextState) { state = nextState;}

	/**
	 * @return current match's state
	 */
	public MatchState getMatchState() { return state; }

	/**
	 * @return True if match's state is not equal to NOT_STARTED
	 */
	public boolean isStarted() { return state != MatchState.NOT_STARTED; }

	/**
	 * Insert new death in the game board, updates # of deaths and death track
	 */
	public void addDeath(Player killer, boolean isOverkill) throws PlayerNotExistsException {
		if(players.contains(killer)) {
			currentDeaths++;
			deathTrack.add(killer);
			if(isOverkill)deathTrack.add(killer);
			overkills.add(isOverkill);
			if (currentDeaths>= maxDeaths) {
				state = MatchState.FRENZY_TURN;
				System.out.println("FRENZY MODE STARTING");
				// All players without damage change their rewards to frenzy, and reset their deaths to 0
				for (Player p : players)
					if (p.getDmgPoints().isEmpty()) p.enableFrenzy();
			}
		} else throw new PlayerNotExistsException("Error, player not in game");
	}


	/**
	 * Sets attributes to start the match
	 */
	public void startMatch() throws NotEnoughPlayersException, MatchAlreadyStartedException {
		if (nPlayers == players.size()) {
			if(state == MatchState.NOT_STARTED) {
				int luckyFirstPlayerNumber = new Random().nextInt(nPlayers);
				this.state = MatchState.LOADING;
				this.firstPlayer = players.get(luckyFirstPlayerNumber);
			} else {
				throw new MatchAlreadyStartedException();
			}

		} else {
			throw new NotEnoughPlayersException();
		}
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
				if (players.isEmpty()) break;

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
			case "ANY":
				toReturn.addAll(caller.getCellAtDistance(minDistance,maxDistance).stream().map(Cell::getPlayers)
						.flatMap(List::stream).collect(Collectors.toList()));
				break;

			case "DAMAGED":
				toReturn.addAll(getPlayers().stream().filter(Player::isDamagedThisTurn).collect(Collectors.toList()));
				break;

			case "ATTACKER":
				if (!caller.getDmgPoints().isEmpty())
					toReturn.add(caller.getDmgPoints().get(caller.getDmgPoints().size()-1));
				break;
			default:
				// from is an integer id
				int id = Integer.parseInt(from);

				// we are selecting players-> first control if there are players with requested id
				toReturn.addAll(players.stream()
						.filter(p -> p.getID() == id).collect(Collectors.toList()));

				// if none were found, the id should be a cell id
				if(toReturn.isEmpty()){
					// look for cells with id
					List<Cell> foundCells = boardMap.getCellsByID(id);

					for(Cell c: foundCells){
						for(Cell pC: boardMap.getCellAtDistance(c,minDistance,maxDistance)){
							toReturn.addAll(pC.getPlayers());
						}
					}
				} /*else {
					// look for players at given distance
					toReturn = intersection(toReturn,
							toReturn.get(0).getCellAtDistance(minDistance,maxDistance).stream().
									map(Cell::getPlayers).
									flatMap(List::stream).
									collect(Collectors.toList()));
				}*/
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
										 int maxDistance, List<Player> players) {
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
						toReturn.addAll(p.getCellsToMove(maxDistance));
					}
				} /*else {
					List<Player> playersWithID = getPlayersByID(id);

					if (!playersWithID.isEmpty()) toReturn = intersection(toReturn, playersWithID.get(0).getCellAtDistance(minDistance,maxDistance));
				}*/
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
	 * Distribution of victory points based on a defined reward list
	 * @param track to an analyze
	 * @param rewards current reward
	 */
	public void rewardPlayers(List<Player> track, List<Integer> rewards) {
		/* This list contains a reference to every player in match.
			Every player's index is equal to the number of damage he dealt to the dead player
			Multiple players can share the same index */
		SortedMap<Integer, List<Player>> toReward = new TreeMap<>(Collections.reverseOrder());

		for (Player matchPlayer : getPlayers()) {
			toReward.computeIfAbsent(Collections.frequency(track, matchPlayer), k -> new ArrayList<>());

			toReward.get(Collections.frequency(track, matchPlayer)).add(matchPlayer);
		}

		int reward = 0;
		for (int i : toReward.keySet()) {
			// No damage = no score
			if (i == 0) continue;
			List<Player> damagers = toReward.get(i);

			if (damagers.size() == 1) {
				damagers.get(0).addScore(rewards.get(reward));
				reward++;
			} else {
					for (Player damager : track) {
						if (damagers.contains(damager)) {
							damager.addScore(rewards.get(reward));
							damagers.remove(damager);
							reward++;
						}
					}
			}
		}
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

	/**
	 * @return JSON representation of this
	 * */
	public JSONObject toJSON(){
		JSONObject toRet = new JSONObject();
		toRet.put("max_deaths", this.maxDeaths);
		toRet.put("mapID", this.mapID);
		toRet.put("state", this.state.toString());
		toRet.put("map", this.boardMap.toJSON());
		toRet.put("turn", this.turn);
		toRet.put("nPlayers", this.nPlayers);
		toRet.put("firstPlayedFrenzy", this.firstPlayedFrenzy);
		JSONArray playersArray = new JSONArray();
		players.forEach(p -> {
			playersArray.add(p.toJSON());
		});

		toRet.put("players", playersArray);
		JSONArray deathTrackAray = new JSONArray();
		deathTrack.forEach(p->deathTrackAray.add(p.getNickname()));
		JSONArray overkillAray = new JSONArray();
		overkills.forEach(p->overkillAray.add(p.booleanValue()));
		toRet.put("deathTrack", deathTrackAray);
		toRet.put("overkills", overkillAray);
		toRet.put("turnDuration", turnDuration);
		return toRet;
	}

}