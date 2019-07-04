package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.RequirementsNotMetException;
import it.polimi.ingsw.custom_exceptions.WeaponNotLoadedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public abstract class Weapon implements Serializable {
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
	 * @param name    weapon name
	 * @param cost    weapon cost
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
	 * @param loaded new loaded state
	 */
	public void setLoaded(boolean loaded) {	this.loaded = loaded; }

	/**
	 * @return weapon effective cost. If weapon's loaded, cost is reduced
	 */
	public List<Resource> getCost() {
		if (isLoaded()) {
			// Cost is reduced if weapon is loaded or partially loaded
			List<Resource> newCost = new ArrayList<>(cost);
			newCost.remove(0);
			return newCost;
		} else return new ArrayList<>(cost);
	}

	/**
	 * testing purposes
	 *
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
	 *
	 * @param effectID effect identifier
	 * @param shooter  player shooting
	 */
	public abstract void shoot(int effectID, Player shooter) throws WeaponNotLoadedException, RequirementsNotMetException;

	/**
	 * @param id of effect to get name
	 * @return name of effect
	 */
	public abstract Action getAction(int id);

	/**
	 * @return list of possible shoot actions.
	 * 			[0]  Primary action / mode
	 * 			[1]  First optional / secondary mode
	 * 			[2]  Second optional / null
	 */
	public abstract List<Action> getShootActions();

	/**
	 * @return list of possible shoot actions
	 */
	public abstract List<Action> getValidActions();

	/**
	 * @param sequence list of effect IDs to evaluate execution
	 * @return True if given sequence can be executed
	 */
	public abstract boolean isValidActionSequence(List<Integer> sequence);

	/**
	 * @param actions json object read from weapon file*
	 */
	protected abstract void parseEffects(JSONObject actions);

















}