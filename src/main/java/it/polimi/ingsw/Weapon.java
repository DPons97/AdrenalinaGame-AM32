package it.polimi.ingsw;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public abstract class Weapon {
	/**
	 * weapon name
	 */
	private String name;

	/**
	 * true when weapon is ready to shoot
	 */
	protected boolean loaded;

	/**
	 * weapon cost
	 */
	private List<Resource> cost;

	/**
	 * @param name weapon name
	 * @param cost weapon cost
	 * @param actions json object with effects description
	 */
	public Weapon(String name, List<Resource> cost, JSONObject actions) {
		this.name = name;
		this.loaded = true;
		this.cost = cost;
		parseEffects(actions);
	}

	/**
	 * @return weapon name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return load
	 */
	public Boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return weapon cost
	 */
	public List<Resource> getCost() {
		return new ArrayList<>(cost);
	}

	/**
	 * testing purposes
	 * @param cost weapon cost
	 */
	public void setCost(List<Resource> cost) {
		this.cost = new ArrayList<>(cost);
	}

	/**
	 * Reload weapon
	 */
	public void reload() {
		loaded = true;
	}

	/**
	 * Shoot with this weapon
	 * @param effectID effect identifier
	 * @param shooter player shooting
	 */
	public abstract void shoot(int effectID, Player shooter);

	/**
	 * @return list of possible shoot actions
	 */
	public abstract List<Action> getShootActions();


	/**
	 * @param actions json object read from weapon file
	 * @return
	 */
	 protected abstract void parseEffects(JSONObject actions);

}