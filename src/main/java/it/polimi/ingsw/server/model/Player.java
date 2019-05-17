package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.controller.PlayerConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Player {
	/**
	 *  Point reward to give to who kills this player during common turns
	 */
	private static int[] killRewards = {8, 6, 4, 2, 1, 1};

	/**
	 *  Point reward to give to who kills this player during frenzy
	 */
	private static int[] frenzyRewards = {2, 1, 1, 1};

	/**
	 *	Maximum damage that a player can take before he's considered dead
	 */
	private static int maxDamage = 10;

	/**
	 *	Maximum marks that a player can have
	 */
	private static int maxMarks = 3;

	/**
	 *	Match reference this player is playing
	 */
	private AdrenalinaMatch match;

	/**
	 * Connection equivalent of player
	 */
	private PlayerConnection connection;

	/**
	 *  player's unique id
	 */
	private int id;

	/**
	 *  This player's nickname
	 */
	private String nickname;

	/**
	 * True if this player is ready to start the match
	 */
	private boolean readyToStart;

	/**
	 *  Current player's score
	 */
	private int score;

	/**
	 * True if this player is using frenzy rewards
	 */
	private boolean isFrenzyPlayer;

	/**
	 *	Damage dealt to this player during the game (Every Player is a damage point)
	 *	Players in this list are those who damaged this
	 */
	private List<Player> dmgPoints;

	/**
	 * 	Marks given to this player
	 * 	Players in this list are those who marked this
	 */
	private List<Player> marks;

	/**
	 *  True if player is dead
	 */
	private boolean dead;

	/**
	 * True if player is dead and overkilled
	 */
	private boolean overkilled;

	/**
	 *  Number of times this player has been killed
	 */
	private int deaths;

	/**
	 *  Number of marks this player gave to others
	 */
	private int givenMarks;

	/**
	 *  Player's position
	 */
	private Cell position;

	/**
	 * 	Weapons this player has (Max 3)
	 */
	private List<Weapon> weapons;

	/**
	 *  Powerups this player has (Max 3)
	 */
	private List<Powerup> powerups;

	/**
	 *  Ammos this player has (Max 3 of each type)
	 */
	private List<Resource> ammos;

	/**
	 * TESTING PURPOSE: Default constructor
	 * @param 	match Reference to match this player is playing
	 * @param	nickname This player's nickname
	 */
	public Player(AdrenalinaMatch match, String nickname) {
		this.match = match;
		this.nickname = nickname;
		this.readyToStart = false;
		this.connection = null;
		score = 0;

		dmgPoints = new ArrayList<>();
		marks = new ArrayList<>();

		id = -1;
		dead = true;
		overkilled = false;
		deaths = 0;
		isFrenzyPlayer = false;
		givenMarks = 0;
		position = null;

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		ammos = new ArrayList<>();
	}

	/**
	 * Default constructor
	 * @param	nickname This player's nickname
	 */
	public Player(String nickname, PlayerConnection playerConnection) {
		this.nickname = nickname;
		this.readyToStart = false;
		this.connection = playerConnection;
		score = 0;

		dmgPoints = new ArrayList<>();
		marks = new ArrayList<>();

		id = -1;
		dead = true;
		overkilled = false;
		deaths = 0;
		isFrenzyPlayer = false;
		givenMarks = 0;
		position = null;

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		ammos = new ArrayList<>();
	}

	/**
	 * Default Constructor
	 * @param nickname This player's nickname
	 */
	public Player(String nickname) {
		this.match = null;
		this.nickname = nickname;
		this.readyToStart = false;
		this.connection = null;
		score = 0;

		dmgPoints = new ArrayList<>();
		marks = new ArrayList<>();

		id = -1;
		dead = true;
		overkilled = false;
		deaths = 0;
		isFrenzyPlayer = false;
		givenMarks = 0;

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		ammos = new ArrayList<>();
	}

	/**
	 * @return List of kill rewards
	 */
	public static List<Integer> getKillRewards() {
		List<Integer> returnReward = new ArrayList<>();
		for (int i : killRewards) {
			returnReward.add(i);
		}
		return returnReward;
	}

	/**
	 * @return List of kill rewards during frenzy
	 */
	public static List<Integer> getFrenzyRewards() {
		List<Integer> returnReward = new ArrayList<>();
		for (int i : frenzyRewards) {
			returnReward.add(i);
		}
		return returnReward;
	}

	/**
	 * @return player's nickname
	 */
	public String getNickname(){
		return nickname;
	}

	/**
	 * @return player's connection handler
	 */
	public PlayerConnection getConnection() { return connection; }

	/**
	 * @return player's match reference
	 */
	public AdrenalinaMatch getMatch() {
		return match;
	}

	/**
	 * Make this player to join match
	 * @param newMatch to join
	 */
	protected void setMatch(AdrenalinaMatch newMatch) { match = newMatch; }

	/**
	 *
	 * @return max damage a player can take before he's considered dead
	 */
	public static int getMaxDamage() { return maxDamage; }

	/**
	 *
	 * @return max marks a player can have
	 */
	public static int getMaxMarks() { return maxMarks; }

	/**
	 * @return current player's deaths
	 */
	public int getDeaths() { return deaths; }

	/**
	 * Init this player's rewards and deaths for frenzy
	 */
	public void enableFrenzy() {
		deaths = 0;
		isFrenzyPlayer = true;
	}

	/**
	 * @return true if this player is using frenzy board
	 */
	public boolean isFrenzyPlayer() { return isFrenzyPlayer; }

	/**
	 * @return current Player's taken damage ponints
	 */
	public List<Player> getDmgPoints() { return new ArrayList<>(dmgPoints); }

	/**
	 * @return current Player's taken marks
	 */
	public List<Player> getMarks() { return new ArrayList<>(marks); }

	/**
	 * @return number of marks this player has given to others
	 */
	public int getGivenMarks() { return givenMarks; }

	/**
	 * Add +1 marks given to other players
	 */
	public void increaseGivenMarks() { givenMarks++; }

	/**
	 * @return current ammos in player's inventory
	 */
	public List<Resource> getAmmos() { return new ArrayList<>(ammos); }

	/**
	 * Add ammo to player, if inventory not full
	 * @param toAdd
	 */
	public void addAmmo(Resource toAdd) { if (ammos.size() < 3) ammos.add(toAdd); }

	/**
	 * @return true if player is dead
	 */
	public boolean isDead() { return dead; }

	/**
	 * @return if player is dead and overkilled
	 */
	public boolean isOverkilled() { return (overkilled && dead); }

	/**
	 * @return get current Player's position (Cell)
	 */
	public Cell getPosition() { return position; }

	/**
	 * @return last kill reward list. Empty list if player never died
	 */
	public List<Integer> getReward() {
		// Player never died
		if (deaths <= 0) return new ArrayList<>();

		int[] currentRewards = (isFrenzyPlayer) ? frenzyRewards : killRewards;

		// Player died at least once
		List<Integer> returnList = new ArrayList<>();
		if (deaths <= currentRewards.length) {
			for (int i = deaths-1; i < currentRewards.length; i++) {
				returnList.add(currentRewards[i]);
			}
		} else {
			returnList.add(currentRewards[currentRewards.length - 1]);
		}
		return returnList;
	}

	/**
	 * @return current Player's score
	 */
	public int getScore() { return score; }

	/**
	 * Add score to player
	 * @param increment to apply
	 */
	public void addScore(int increment) { score += increment; }

	/**
	 * @return True if player's ready to start the match
	 */
	public boolean isReadyToStart() { return readyToStart; }

	/**
	 * @return player id
	 */
	public int getID() { return id; }

	/**
	 * @param id player id to set
	 */
	public void setID(int id) { this.id = id; }

	/**
	 * @return List of Player's Weapons (Max: 3)
	 */
	public List<Weapon> getWeapons() { return new ArrayList<>(weapons); }

	/**
	 * @return List of Player's Powerups (Max: 3)
	 */
	public List<Powerup> getPowerups() { return new ArrayList<>(powerups); }

	/**
	 * ONLY FOR TESTING
	 * @param toAdd powerup to add to player's inventory
	 */
	public void addPowerup(Powerup toAdd) { powerups.add(toAdd); }

	/**
	 * @param toLoad Weapon to reload
	 * @param discountPowerups Powerups to use as discount on reload price
	 * @throws NoItemInInventoryException if toLoad is not in player's inventory
	 * @throws InsufficientResourcesException if there are insufficient resources in player's inventory
	 */
	public void reload(Weapon toLoad, List<Powerup> discountPowerups) throws NoItemInInventoryException, InsufficientResourcesException {
		if (!weapons.contains(toLoad)) throw new NoItemInInventoryException();

		// Don't reload if it's already ready to shoot
		if (toLoad.isLoaded()) return;

		List<Resource> reloadCost = toLoad.getCost();

		// Apply discount from powerups used
		for (Powerup pow : discountPowerups) {
			reloadCost.remove(pow.getBonusResource());
			usePowerupResource(pow);
		}

		// Check if there are enough resources in player's inventory
		pay(reloadCost);
		toLoad.reload();
	}

	/**
	 * @param toLoad Weapon to reload
	 * @throws NoItemInInventoryException if toLoad is not in player's inventory
	 * @throws InsufficientResourcesException if there are insufficient resources in player's inventory
	 */
	public void reload(Weapon toLoad) throws NoItemInInventoryException, InsufficientResourcesException {
		reload(toLoad, new ArrayList<>());
	}

	/**
	 * Move player to destination and update previous and next cell
	 * @param destination Destination Cell to move into
	 * @throws NullPointerException if destination is a pointer
	 */
	public void move(Cell destination) {
		if (destination == null) throw new NullPointerException();

		// Update old player's cell only if there's one
		if (position != null) position.removePlayer(this);
		position = destination;
		position.addPlayer(this);
	}

	/**
	 * Apply damage to player. Additional damage due to source's marks on this player are also applied.
	 * @param source Damage dealt to this player
	 * @return true if player's dead after damage
	 * @throws DeadPlayerException if player is already dead
	 */
	public boolean takeDamage(Player source) throws DeadPlayerException {
		// Player take damage from source only if not overkilled
		if (dmgPoints.size() <= maxDamage) {
			dmgPoints.add(source);

			if (marks.contains(source)) {
				// Source has marked this player at least once
				marks.remove(source);
				takeDamage(source);
			}
		} else throw new DeadPlayerException();

		if (dmgPoints.size() > maxDamage) {
			// Player is overkilled
			overkilled = true;
			return true;
		} else if (dmgPoints.size() == maxDamage) {
			// Player is dead, but not overkilled
			deaths++;
			dead = true;
			overkilled = false;
			return true;
		}  else return false;
	}

	/**
	 * Add mark to this player, if maximum from source is not reached
	 * @param source Player who gave the mark
	 */
	public void takeMark(Player source) {
		// Count how many marks from source this player has
		int marksFromSource = 0;
		for (Player p : marks) marksFromSource = (p.equals(source)) ? marksFromSource + 1 : marksFromSource;

		// Count how many marks the source gave to other players
		int sourceMarks = source.givenMarks;

		if (sourceMarks < maxMarks && marksFromSource < maxMarks) {
			marks.add(source);
			source.increaseGivenMarks();
		}
	}

	/**
	 * @param minDist Minimum distance
	 * @param maxDist Maximum distance. -1 is equal to INFINITE
	 * @return List of all cells between [minDist] and [maxDist] distance from player
	 * @throws IllegalArgumentException if minDist < 0 or maxDist < -1
	 */
	public List<Cell> getCellAtDistance(int minDist, int maxDist) {
		return match.getBoardMap().getCellAtDistance(position, minDist, maxDist);
	}

	/**
	 *
	 * @param minDist Minimum distance
	 * @param maxDist Maximum distance. -1 is equal to INFINITE
	 * @return List of all visible cells from this player between distance minDist and maxDist
	 */
	public List<Cell> getVisibleCellsAtDistance(int minDist, int maxDist) {
		Map matchMap = match.getBoardMap();
		List<Cell> cellsAtDistance = getCellAtDistance(minDist, maxDist);

		// This player's room is visible
		List<Cell> visibleCells = new ArrayList<>(matchMap.getRoomCells(position));

		// Iterate for every possible direction
		for (Direction dir : Direction.values()) {
			// Player has a door nearby
			if (position.getSide(dir) == Side.DOOR) {
				visibleCells.addAll(matchMap.getRoomCells(matchMap.getAdjacentCell(position, dir)));
			}
		}

		// Remove all cells that are not visible between minDist, maxDist
		cellsAtDistance.removeIf(cell -> !visibleCells.contains(cell));
		return cellsAtDistance;
	}

	/**
	 * @param minDist Minimum distance
	 * @param maxDist Maximum distance. -1 is equal to INFINITE
	 * @return List of all non-visible cells from this player between distance minDist and maxDist
	 */
	public List<Cell> getOutOfSightCells(int minDist, int maxDist) {
		List<Cell> visible = getVisibleCellsAtDistance(minDist, maxDist);
		List<Cell> map = match.getBoardMap().getMap();

		map.removeIf(visible::contains);
		return map;
	}

	/**
	 * @return List of cells in which player can move (1, 2 or 3 distance)
	 */
	public List<Cell> getCellsToMove(int maxMovement) {
		List<Cell> canMoveTo = getCellsWithoutWalls(position, new ArrayList<>(), maxMovement);
		canMoveTo.remove(position);
		return canMoveTo;
	}

	/**
	 * Support function that retreives all cells in which the player can move
	 */
	private List<Cell> getCellsWithoutWalls(Cell currCell, List<Cell> visited, int distance) {
		if (currCell == null || visited.contains(currCell)) return new ArrayList<>();
		if (distance == 0) {
			List<Cell> lastCell = new ArrayList<>();
			lastCell.add(currCell);
			visited.add(currCell);
			return lastCell;
		}

		Map matchMap = match.getBoardMap();
		List<Cell> canMove = new ArrayList<>();
		canMove.add(currCell);

		visited.add(currCell);
		for (Direction dir : Direction.values()) {
			// Player has no wall/door in this direction
			if (currCell.getSide(dir) != Side.WALL && currCell.getSide(dir) != Side.BORDER) {
				canMove.addAll(getCellsWithoutWalls(matchMap.getAdjacentCell(currCell, dir), visited, distance - 1));
			}
		}
		return canMove;
	}

	/**
	 * Pick ammo and add resources + powerups to player's inventory
	 * @param location Cell from which this player take monitions
	 */
	public void pickAmmo(AmmoCell location) {
		// Pick resource
		Ammo resourcePicked = location.pickResource();
		List<Resource> resourcesInAmmo = resourcePicked.getResources();
		boolean addPowerup = resourcePicked.hasPowerup();
		List<Resource> resourcesToAdd = new ArrayList<>();

		// Check if there is space for at least one resource to add. If there are already too many resources, remove from resources to add
		for (Resource toAdd: resourcesInAmmo ) {
			// Get player's ammo quantity of toAdd's resource type
			int thisResourceQty = 0;
			for (Resource res: ammos) if (res.equals(toAdd)) thisResourceQty++;
			if (thisResourceQty < 3) resourcesToAdd.add(toAdd);
		}

		// Check if there is space for at least one powerup to add. If there are already too many powerups, do not add
		if (powerups.size() >= 3) addPowerup = false;

		// Add resources
		if (!resourcesToAdd.isEmpty()) ammos.addAll(resourcesToAdd);
		// Add powerup
		if (addPowerup) powerups.add(match.getPowerupDeck().drawCard());

		// Discard picked ammo to deck
		match.getAmmoDeck().discardCard(resourcePicked);
	}

	/**
	 * Use toUse as an effect
	 * @param toUse Powerup to use
	 * @throws NoItemInInventoryException if player hasn't toUse in his inventory
	 */
	public void usePowerupEffect(Powerup toUse) throws NoItemInInventoryException {
		if (!powerups.contains(toUse)) throw new NoItemInInventoryException();

		toUse.useAsEffect(this);
		powerups.remove(toUse);
		match.getPowerupDeck().discardCard(toUse);
	}

	/**
	 * Use toUse as a bonus resource
	 * @param toUse Powerup to use
	 */
	public void usePowerupResource(Powerup toUse) throws NoItemInInventoryException {
		if (!powerups.contains(toUse)) throw new NoItemInInventoryException();
		powerups.remove(toUse);
		match.getPowerupDeck().discardCard(toUse);
	}

	/**
	 * @param resource needed
	 * @return list of powerups that can be used as given resource
	 */
	public List<Powerup> getAllPowerupByResource(Resource resource) {
		List<Powerup> containResource = new ArrayList<>();

		for (Powerup powerup: powerups) {
			if (powerup.getBonusResource().equals(resource)) containResource.add(powerup);
		}

		return containResource;
	}

	/**
	 * Add [toPick] weapon, if there are less then 3 weapons in player's inventory and
	 * if [toPick] is not in inventory.
	 * @param toPick Weapon to pick
	 * @throws InventoryFullException if player has already 3 weapons
	 */
	public void pickWeapon(Weapon toPick) throws InventoryFullException, InsufficientResourcesException {
		// Can't add weapons if inventory's full
		if (weapons.size() >= 3) throw new InventoryFullException();
		else {
			// Add weapon only if it's not already in inventory
			if(!weapons.contains(toPick)) {
				weapons.add(toPick);
				pay(toPick.getCost());
			}
		}
	}

	/**
	 * Remove [toDrop] weapon from inventory
	 * @param toDrop weapon to drop
	 * @throws NoItemInInventoryException if weapon is not in player's inventory
	 */
	public void dropWeapon(Weapon toDrop) throws NoItemInInventoryException {
		if (!weapons.contains(toDrop)) throw new NoItemInInventoryException();
		else {
			// Remove weapon
			weapons.remove(toDrop);
		}
	}

	/**
	 *  Shoot at another player
	 * @param weapon Weapon to use
	 * @param effectID effect's ID to shoot
	 * @param discountPowerups Powerups to use as discount on reload price
	 */
	public void shoot(Weapon weapon, int effectID, List<Powerup> discountPowerups) throws NoItemInInventoryException, WeaponNotLoadedException, InsufficientResourcesException, RequirementsNotMetException {
		if (!weapons.contains(weapon)) throw new NoItemInInventoryException();
		List<Resource> shootCost = weapon.getShootActions().get(effectID).getCost();
		List<Powerup> usedResources = new ArrayList<>();

		// Apply discount from powerups used
		for (Powerup pow : discountPowerups) {
			if (shootCost.contains(pow.getBonusResource())) {
				usePowerupResource(pow);
				shootCost.remove(pow.getBonusResource());
				usedResources.add(pow);
			}
		}

		// Remove used powerups from discountPowerups
		discountPowerups.removeAll(usedResources);

		pay(shootCost);
		weapon.shoot(effectID, this);
	}

	/**
	 *  Shoot at another player
	 * @param weapon Weapon to use
	 * @param effectID effect's ID to shoot
	 */
	public void shoot(Weapon weapon, int effectID) throws NoItemInInventoryException, WeaponNotLoadedException, InsufficientResourcesException, RequirementsNotMetException {
		shoot(weapon, effectID, new ArrayList<>());
	}

	/**
	 * Pay a certain amount of resources
	 * @param cost to pay
	 * @throws InsufficientResourcesException if there are not enough resources in player's inventory
	 */
	private void pay(List<Resource> cost) throws InsufficientResourcesException {
		// Check if there are enough resources in player's inventory
		for (Resource resource : cost) {
			if (!ammos.contains(resource)) throw new InsufficientResourcesException();
		}

		// Remove used resources
		for (Resource res : cost) {
			ammos.remove(res);
		}
	}

	/**
	 * Check if player can pay the whole toPay cost with given powerups as discount
	 * @param toPay List of resources to pay.
	 * @param powerupsAsResource list of powerups used as discount
	 * @return True if, using given powerups as resources, player can handle toPay cost
	 */
	public boolean canPay(List<Resource> toPay, List<Powerup> powerupsAsResource) {
		List<Resource> resourcesToPay = new ArrayList<>(toPay);
		List<Powerup> usedPowerups = new ArrayList<>();

		// Remove resources that player would pay through powerups
		for (Powerup pow : powerupsAsResource) {
			if (resourcesToPay.contains(pow.getBonusResource()) && !usedPowerups.contains(pow)) {
				// Resource is still to be payed, and current powerup has not been used yet
				resourcesToPay.remove(pow.getBonusResource());
				usedPowerups.add(pow);
			}
		}

		// Remove resources that can be payed with player's ammos
		for (Resource res : ammos) {
			resourcesToPay.remove(res);
		}

		// Return that player can pay the whole amount, and set which powerups can be used as resources
		return resourcesToPay.isEmpty();
	}

	/**
	 * Check if player can pay the whole toPay cost with given powerups as discount
	 * @param toPay List of resources to pay.
	 * @return True if, using given powerups as resources, player can handle toPay cost
	 */
	public boolean canPay(List<Resource> toPay) {
		return canPay(toPay, new ArrayList<>());
	}

	/**
	 *	If dead, respawn player and reset his health
	 * @param respawnPosition position to respawn
	 */
	public void respawn(SpawnCell respawnPosition) {
		if (dead) {
			dmgPoints.clear();
			dead = false;
			overkilled = false;
			move(respawnPosition);
		}
	}

	/**
	 * @return JSON representation of this
	 */
	public JSONObject toJSON(){
		JSONObject player = new JSONObject();
		player.put("name", nickname);
		player.put("ready", readyToStart);
		player.put("deaths", deaths);

		JSONArray weaponsArray = new JSONArray();
		JSONArray powerupsArray = new JSONArray();
		JSONArray marksArray = new JSONArray();
		JSONArray dmgpointsArray = new JSONArray();
		JSONArray resourcesArray = new JSONArray();

		weapons.forEach(w -> weaponsArray.add(w.getName()));
		powerups.forEach(p -> powerupsArray.add(p.toJSON()));
		marks.forEach(m -> marksArray.add(m.getNickname()));
		dmgPoints.forEach(d -> dmgpointsArray.add(d.getNickname()));
		ammos.forEach(a -> resourcesArray.add(a.toString()));


		player.put("weapons", weaponsArray);
		player.put("powerups", powerupsArray);
		player.put("marks", marksArray);
		player.put("dmgpoints", dmgpointsArray);
		player.put("resources", resourcesArray);

		return player;
	}
}