package it.polimi.ingsw.client.model;

import java.util.*;

import it.polimi.ingsw.server.model.Ammo;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Weapon;

/**
 * 
 */
public class Player {

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
	private List<Player> dmgPoints;

	/**
	 * 
	 */
	private List<Player> marks;

	/**
	 * 
	 */
	private boolean dead;

	/**
	 * 
	 */
	private int deaths;

	/**
	 * 
	 */
	private int givenMarks;

	/**
	 * 
	 */
	private Cell position;

	/**
	 * 
	 */
	private List<Weapon> weapons;

	/**
	 * 
	 */
	private List<Powerup> powerups;

	/**
	 * 
	 */
	private List<Ammo> ammons;

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
	 * @return nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname nickname to set
	 */

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return score
	 */

	public int getScore() {
		return score;
	}

	/**
	 * @param score score to set
	 */

	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return damage points
	 */

	public List<Player> getDmgPoints() {
		return dmgPoints;
	}

	/**
	 * @param dmgPoints damage points to set
	 */

	public void setDmgPoints(List<Player> dmgPoints) {
		this.dmgPoints = dmgPoints;
	}

	/**
	 * @return marks
	 */

	public List<Player> getMarks() {
		return marks;
	}

	/**
	 * @param marks marks to set
	 */

	public void setMarks(List<Player> marks) {
		this.marks = marks;
	}

	/**
	 * @return true if is death
	 */

	public boolean getDead() {
		return dead;
	}

	/**
	 * @param dead booleanean set
	 */

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * @return the deaths
	 */

	public int getDeaths() {
		return deaths;
	}

	/**
	 * @param deaths deaths to set
	 */

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	/**
	 * @return givenMarks
	 */

	public int getGivenMarks() {
		return givenMarks;
	}

	/**
	 * @param givenMarks givenMarks to set
	 */

	public void setGivenMarks(int givenMarks) {
		this.givenMarks = givenMarks;
	}

	/**
	 * @return the position of the cell
	 */

	public Cell getPosition() {
		return position;
	}

	/**
	 * @param position position of the cell to set
	 */

	public void setPosition(Cell position) {
		this.position = position;
	}

	/**
	 * @return list of the Weapons
	 */

	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * @param weapons list of the weapons to set
	 */

	public void setWeapons(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	/**
	 * @return powerups
	 */

	public List<Powerup> getPowerups() {
		return powerups;
	}

	/**
	 * @param powerups powerups to set
	 */

	public void setPowerups(List<Powerup> powerups) {
		this.powerups = powerups;
	}

	/**
	 * @return list of the ammo
	 */

	public List<Ammo> getAmmons() {
		return ammons;
	}

	/**
	 * @param ammons ammons to set
	 */

	public void setAmmons(List<Ammo> ammons) {
		this.ammons = ammons;
	}

	/**
	 * @return match
	 */

	public AdrenalinaMatch getMatch() {
		return match;
	}

	/**
	 * @param match match to set
	 */

	public void setMatch(AdrenalinaMatch match) {
		this.match = match;
	}
}