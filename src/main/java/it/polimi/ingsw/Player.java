package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class Player {
	/**
	 * 
	 */
	private static int[] killRewards;

	/**
	 * 
	 */
	private static int[] frenzyRewards;

	/**
	 * 
	 */
	private String nickname;

	/**
	 * 
	 */
	private int score;

	/**
	 * 
	 */
	private List<Player> hirPoints;

	/**
	 * 
	 */
	private Boolean dead;

	/**
	 * 
	 */
	private int deaths;

	/**
	 * 
	 */
	private Cell position;

	/**
	 * 
	 */
	private List<Player> marks;

	/**
	 * 
	 */
	private List<Weapon> weapons;

	/**
	 * 
	 */
	private List<Perk> perks;

	/**
	 * 
	 */
	private int givenMarks;

	/**
	 * 
	 */
	private List<Ammo> ammos;

	/**
	 * 
	 */
	private AdrenalinaMatch match;

	/**
	 * Default constructor
	 */
	public Player() {
	}

	/**
	 * 
	 */
	public void beginPlay() {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public int getHealth() {
		// TODO implement here
		return 0;
	}

	/**
	 * @return
	 */
	public Boolean isDead() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public Cell getPosition() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public int getReward() {
		// TODO implement here
		return 0;
	}

	/**
	 * @return
	 */
	public int getScore() {
		// TODO implement here
		return 0;
	}

	/**
	 * @return
	 */
	public List<Weapon> getWeapons() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public List<Perk> getPerks() {
		// TODO implement here
		return null;
	}

	/**
	 * @param toLoad
	 */
	public void reload(Weapon toLoad) {
		// TODO implement here
	}

	/**
	 * @param destination
	 */
	public void move(Cell destination) {
		// TODO implement here
	}

	/**
	 * @param dmg
	 */
	public void takeDamage(int dmg) {
		// TODO implement here
	}

	/**
	 * @param source
	 */
	public void takeMark(Player source) {
		// TODO implement here
	}

	/**
	 * @param minDist 
	 * @param maxDist 
	 * @return
	 */
	public List<Cell> getCellAtDistance(int minDist, int maxDist) {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public List<Cell> getVisibleCells() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public List<Cell> getOutOfSightCells() {
		// TODO implement here
		return null;
	}

	/**
	 * @param location
	 */
	public void pickAmmo(Cell location) {
		// TODO implement here
	}

	/**
	 * @param toUse
	 */
	public void useAmmo(Ammo toUse) {
		// TODO implement here
	}

	/**
	 * 
	 */
	public void pickPerk() {
		// TODO implement here
	}

	/**
	 * @param toUse
	 */
	public void usePerk(Perk toUse) {
		// TODO implement here
	}

	/**
	 * @param toPick
	 */
	public void pickWeapon(Weapon toPick) {
		// TODO implement here
	}

	/**
	 * 
	 */
	public void shoot() {
		// TODO implement here
	}

}