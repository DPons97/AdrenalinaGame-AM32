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
	private Boolean loaded;

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
	 * @return loard
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
	 */
	public void shoot() {
		// TODO implement here
		loaded = false;
	}

	/**
	 * @param actions json object read from weapon file
	 * @return
	 */
	 protected abstract void parseEffects(JSONObject actions);

}