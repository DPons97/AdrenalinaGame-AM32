package it.polimi.ingsw;
import java.util.*;

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
	 *  Perks this player has (Max 3)
	 */
	private List<Perk> perks;

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

		dead = false;
		deaths = 0;
		givenMarks = 0;
		position = null;

		weapons = new ArrayList<>();
		perks = new ArrayList<>();
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

		dead = false;
		deaths = 0;
		givenMarks = 0;
		position = spawnPosition;

		weapons = new ArrayList<>();
		perks = new ArrayList<>();
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
	 * @return List of Player's Weapons (Max: 3)
	 */
	public List<Weapon> getWeapons() { return new ArrayList<>(weapons); }

	/**
	 * @return List of Player's Perks (Max: 3)
	 */
	public List<Perk> getPerks() { return new ArrayList<>(perks); }

	/**
	 * @param toLoad Weapon to reload
	 */
	public void reload(Weapon toLoad) throws NoItemInInventoryException, InsufficientResourcesException {
		if (!weapons.contains(toLoad)) throw new NoItemInInventoryException();

		// Don't reload if it's already ready to shoot
		if (toLoad.isLoaded()) return;

		List<Resource> reloadCost = toLoad.getCost();
		for (Iterator<Resource> itr = reloadCost.iterator(); itr.hasNext(); ) {
			if (!ammos.contains(itr.next())) throw new InsufficientResourcesException();
			else {
				ammos.remove(itr.next());
				itr.remove();
			}
		}
		toLoad.reload();
	}

	/**
	 * Move player to destination
	 * @param destination Destination Cell to move into
	 */
	public void move(Cell destination) {
		// TODO implement here
	}

	/**
	 * Apply damage to player. Additional damage due to source's marks on this player are also applied.
	 * @param source Damage dealt to this player
	 * @return true if player's dead after damage
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
	 * @param maxDist Maximum distance
	 * @return List of all cells between [minDist] and [maxDist] distance
	 */
	public List<Cell> getCellAtDistance(int minDist, int maxDist) {
		// TODO implement here
		return null;
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
	 * @param location Cell from which this player take monitions
	 */
	public void pickAmmo(Cell location) {
		// TODO implement here
	}

	/**
	 * @param toUse Ammo to use
	 */
	public void useAmmo(Ammo toUse) {
		// TODO implement here
	}

	/**
	 * Pick perk from deck
	 */
	public void pickPerk() {
		// TODO implement here
	}

	/**
	 * @param toUse Perk to use
	 */
	public void usePerk(Perk toUse) {
		// TODO implement here
	}

	/**
	 * Add [toPick] weapon, if there are less then 3 weapons in player's inventory and
	 * if [toPick] is not in inventory.
	 * @param toPick Weapon to pick
	 */
	public void pickWeapon(Weapon toPick) throws TooManyWeaponsException {
		// Can't add weapons if inventory's full
		if (weapons.size() >= 3) throw new TooManyWeaponsException();
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