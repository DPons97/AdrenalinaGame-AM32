package it.polimi.ingsw.client.model;

import java.util.*;
import java.util.stream.Collectors;

import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
	 * nickname
	 */
	private String nickname;

	/**
	 * color
	 */
	private Color color;

	/**
	 * score
	 */
	private int score;

	/**
	 * damage points
	 */
	private List<Player> dmgPoints;

	/**
	 * marks
	 */
	private List<Player> marks;

	/**
	 * true if is death
	 */
	private boolean dead;

	/**
	 * number of deaths
	 */
	private int deaths;

	/**
	 * True if this player is using frenzy rewards
	 */
	private boolean isFrenzyPlayer;

	/**
	 * number of given marks
	 */
	private int givenMarks;

	/**
	 * position
	 */
	private Cell position;

	/**
	 * weapons
	 */
	private List<WeaponCard> weapons;

	/**
	 * weapons that are loaded
	 */
	private List<WeaponCard> loadedWeapons;

	/**
	 * powerups
	 */
	private List<Powerup> powerups;

	/**
	 *  Ammos this player has (Max 3 of each type)
	 */
	private List<Resource> ammos;

	/**
	 * match
	 */
	private AdrenalinaMatch match;

	/**
	 * True if this player is ready to start the match
	 */
	private boolean readyToStart;

	/**
	 * Default constructor
	 */
	public Player() {
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

	public boolean isFrenzyPlayer() { return isFrenzyPlayer; }

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
	 * @return player's color
	 */
	public Color getColor() { return color; }

	/**
	 * @param color new color this player's will be assigned
	 */
	public void setColor(Color color) { this.color = color; }

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
	public List<WeaponCard> getWeapons() {
		return weapons;
	}

	/**
	 * Get a particular player's weapon given name
	 * @param weaponName weapon's name to retreive
	 * @return weapon with this weapon name
	 */
	public WeaponCard getWeapon(String weaponName){
		return weapons.stream().filter(w->w.getName().equals(weaponName)).
				collect(Collectors.toList()).get(0);
	}

	/**
	 * @param weapons list of the weapons to set
	 */
	public void setWeapons(List<WeaponCard> weapons) {
		this.weapons = weapons;
	}

	/**
	 * @return all loaded weapons
	 */
	public List<WeaponCard> getLoadedWeapons() {
		return loadedWeapons;
	}

	/**
	 * @param loadedWeapons new array of loaded weapons
	 */
	public void setLoadedWeapons(List<WeaponCard> loadedWeapons) {
		this.loadedWeapons = loadedWeapons;
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

	public boolean isReadyToStart() {
		return readyToStart;
	}

	public void setReadyToStart(boolean readyToStart) {
		this.readyToStart = readyToStart;
	}

	public boolean isDead() {
		return dead;
	}

	public List<Resource> getAmmos() {
		return ammos;
	}

	public void setAmmos(List<Resource> ammos) {
		this.ammos = ammos;
	}

	public void update(JSONObject toParse) {
		this.nickname = toParse.get("name").toString();
		this.readyToStart = Boolean.parseBoolean(toParse.get("ready").toString());
		this.deaths = Integer.parseInt(toParse.get("deaths").toString());
		this.dead = Boolean.parseBoolean(toParse.get("dead").toString());
		this.color = Color.valueOf(toParse.get("color").toString());
		this.isFrenzyPlayer = Boolean.parseBoolean(toParse.get("isFrenzyPlayer").toString());

		JSONArray weaponsArray = (JSONArray) toParse.get("weapons");
		JSONArray powerupsArray = (JSONArray) toParse.get("powerups");
		JSONArray marksArray = (JSONArray) toParse.get("marks");
		JSONArray dmgpointsArray = (JSONArray) toParse.get("dmgpoints");
		JSONArray ammosArray = (JSONArray) toParse.get("ammos");
		JSONArray loadedWeaponsArray = (JSONArray) toParse.get("loadedweapons");

		weapons = new ArrayList<>();
		powerups = new ArrayList<>();
		marks = new ArrayList<>();
		dmgPoints = new ArrayList<>();
		ammos = new ArrayList<>();
		loadedWeapons = new ArrayList<>();

		for(Object o: weaponsArray){
			weapons.add(match.getWeapons().stream().filter(w->w.getName().equals(o.toString())).collect(Collectors.toList()).get(0));
		}
		for(Object o: loadedWeaponsArray){
			loadedWeapons.add(match.getWeapons().stream().filter(w->w.getName().equals(o.toString())).collect(Collectors.toList()).get(0));
		}
		for(Object o: powerupsArray){
			powerups.add(Powerup.parseJSON((JSONObject) o));
		}
		for(Object o: marksArray){
			marks.add(match.getPlayers().stream().filter(p->p.getNickname().equals(o.toString())).collect(Collectors.toList()).get(0));
		}
		for(Object o: dmgpointsArray){
			dmgPoints.add(match.getPlayers().stream().filter(p->p.getNickname().equals(o.toString())).collect(Collectors.toList()).get(0));
		}
		for(Object o: ammosArray){
			ammos.add(Resource.valueOf(o.toString()));
		}
	}
}