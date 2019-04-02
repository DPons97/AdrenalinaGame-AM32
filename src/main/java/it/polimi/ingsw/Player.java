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
	private Boolean dead;

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
	 * @return current Player's taken damage.
	 */
	public int getDmgPoints() {
		return dmgPoints.size();
	}

	/**
	 * @return true if player is dead
	 */
	public Boolean isDead() {
		return (getDmgPoints() >= maxDamage);
	}

	/**
	 * @return get current Player's position (Cell)
	 */
	public Cell getPosition() {
		return position;
	}

	/**
	 * @return death reward
	 */
	public int getReward() {
		return killRewards[
				(deaths <= killRewards.length) ?
				deaths :
				killRewards.length
				];
	}

	/**
	 * @return current Player's score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return List of Player's Weapons (Max: 3)
	 */
	public List<Weapon> getWeapons() {
		return new ArrayList<>(weapons);
	}

	/**
	 * @return List of Player's Perks (Max: 3)
	 */
	public List<Perk> getPerks() {
		return new ArrayList<>(perks);
	}

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
	 * @param destination Destination Cell to move into
	 */
	public void move(Cell destination) {
		// TODO implement here
	}

	/**
	 * @param source Damage dealt to this player
	 */
	public void takeDamage(Player source) {
		// TODO implement here
	}

	/**
	 * @param source Player who gave the mark
	 */
	public void takeMark(Player source) {
		// TODO implement here
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

}