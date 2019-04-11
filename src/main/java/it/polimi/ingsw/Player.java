package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.DeadPlayerException;
import it.polimi.ingsw.custom_exceptions.InsufficientResourcesException;
import it.polimi.ingsw.custom_exceptions.InventoryFullException;
import it.polimi.ingsw.custom_exceptions.NoItemInInventoryException;

import java.util.ArrayList;
import java.util.Iterator;
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
	 *  player's unique id
	 */
	private int id;

	/**
	 *  This player's nickname
	 */
	private String nickname;

	/**
	 *  Current player's score
	 */
	private int score;

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
	 * Default constructor
	 * @param 	match Reference to match this player is playing
	 * @param	nickname This player's nickname
	 */
	public Player(AdrenalinaMatch match, String nickname) {
		this.match = match;
		this.nickname = nickname;
		score = 0;

		dmgPoints = new ArrayList<>();
		marks = new ArrayList<>();

		id = -1;
		dead = true;
		deaths = 0;
		givenMarks = 0;
		position = null;

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		ammos = new ArrayList<>();
	}

	/**
	 * Default Constructor
	 * @param 	match Reference to match this player is playing
	 * @param	nickname This player's nickname
	 * @param	spawnPosition Cell in which this player is spawning for the first time
	 */
	public Player(AdrenalinaMatch match, String nickname, Cell spawnPosition) {
		this.match = match;
		this.nickname = nickname;
		score = 0;

		dmgPoints = new ArrayList<>();
		marks = new ArrayList<>();

		id = -1;
		dead = true;
		deaths = 0;
		givenMarks = 0;
		position = spawnPosition;

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		ammos = new ArrayList<>();
	}

	/**
	 * Start this player's turn
	 */
	public void beginPlay() {
		// TODO implement here
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
	 * @return player's nickname
	 */
	public String getNickname(){
		return nickname;
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
	 * @return current Player's taken damage ponints
	 */
	public List<Player> getDmgPoints() { return new ArrayList<>(dmgPoints); }

	/**
	 * @return current Player's taken marks
	 */
	public List<Player> getMarks() { return new ArrayList<>(marks); }

	/**
	 * @return current ammos in player's inventory
	 */
	public List<Resource> getAmmos() { return new ArrayList<>(ammos); }

	/**
	 * @return true if player is dead
	 */
	public boolean isDead() { return dead; }

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

		// Player died at least once
		List<Integer> returnList = new ArrayList<>();
		if (deaths <= killRewards.length) {
			for (int i = deaths-1; i < killRewards.length; i++) {
				returnList.add(killRewards[i]);
			}
		} else {
			returnList.add(killRewards[killRewards.length - 1]);
		}
		return returnList;
	}

	/**
	 * @return current Player's score
	 */
	public int getScore() { return score; }

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
	 * @param toLoad Weapon to reload
	 * @throws NoItemInInventoryException if toLoad is not in player's inventory
	 * @throws InsufficientResourcesException if there are insufficient resources in player's inventory
	 */
	public void reload(Weapon toLoad) throws NoItemInInventoryException, InsufficientResourcesException {
		if (!weapons.contains(toLoad)) throw new NoItemInInventoryException();

		// Don't reload if it's already ready to shoot
		if (toLoad.isLoaded()) return;

		List<Resource> reloadCost = toLoad.getCost();
		// Check if there are enough resources in player's inventory
		for (Resource resource : reloadCost) {
			if (!ammos.contains(resource)) throw new InsufficientResourcesException();
		}

		// Remove used resources
		for (Iterator<Resource> itr = reloadCost.iterator(); itr.hasNext(); ) {
			if (ammos.contains(itr.next())) {
				ammos.remove(itr.next());
				itr.remove();
			}
		}
		toLoad.reload();
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
			return true;
		} else if (dmgPoints.size() == maxDamage) {
			// Player is dead, but not overkilled
			deaths++;
			dead = true;
			return true;
		}  else return false;
	}

	/**
	 * Add mark to this player, if maximum from source is not reached
	 * @param source Player who gave the mark
	 */
	public void takeMark(Player source) {
		// Count how many marks from source this player has
		int sourceMarks = 0;
		for (Player p : marks) sourceMarks = (p.equals(source)) ? sourceMarks + 1 : sourceMarks;

		if (sourceMarks < maxMarks) marks.add(source);
	}

	/**
	 * @param minDist Minimum distance
	 * @param maxDist Maximum distance. -1 is equal to INFINITE
	 * @return List of all cells between [minDist] and [maxDist] distance
	 * @throws IllegalArgumentException if minDist < 0 or maxDist < -1
	 */
	public List<Cell> getCellAtDistance(int minDist, int maxDist) {
		if (minDist < 0 || maxDist < -1) throw new IllegalArgumentException();

		Cell[][] matchMap = match.getMap();
		List<Cell> cellAtDistance = new ArrayList<>();

		if (maxDist == -1) {
			// Set min/max distances to get all cells from minDist to end of map
			maxDist = matchMap.length;
		}

		for (Cell[] cols : matchMap) {
			for (Cell cell : cols) {
				if (cell != null) {
					// Calculate X and Y distances
					int xDist = Math.abs(cell.getCoordX() - position.getCoordX());
					int yDist = Math.abs(cell.getCoordY() - position.getCoordY());

					// Add cell in case distance between minDist and maxDist
					if (xDist + yDist >= minDist && xDist + yDist <= maxDist) cellAtDistance.add(cell);
				}
			}
		}

		return cellAtDistance;
	}

	/**
	 * @return List of all visible cells from this player
	 */
	public List<Cell> getVisibleCells() {
		// TODO implement here
		return null;
	}

	/**
	 * @return List of non-visible cells
	 */
	public List<Cell> getOutOfSightCells() {
		// TODO implement here
		return null;
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
	public void pickWeapon(Weapon toPick) throws InventoryFullException {
		// Can't add weapons if inventory's full
		if (weapons.size() >= 3) throw new InventoryFullException();
		else {
			// Add weapon only if it's not already in inventory
			if(!weapons.contains(toPick)) weapons.add(toPick);
		}
	}

	/**
	 *  Shoot at another player
	 * @param toShoot Player to shoot
	 */
	public void shoot(Player toShoot) {
		// TODO implement here
	}

	/**
	 *	If dead, respawn player and reset his health
	 * @param respawnPosition position to respawn
	 */
	public void respawn(SpawnCell respawnPosition) {
		if (dead) {
			dmgPoints.clear();
			dead = false;
			move(respawnPosition);
		}
	}
}