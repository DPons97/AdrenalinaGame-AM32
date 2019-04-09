package it.polimi.ingsw;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * 
 */
public class WeaponMode extends Weapon {
	/**
	 * Weapon primary mode
	 */
	private Action primaryMode;

	/**
	 * Weapon secondary mode
	 */
	private Action secondaryMode;

	/**
	 * @param name        weapon name
	 * @param cost        weapon cost
	 * @param actions json object with effects description
	 */
	public WeaponMode(String name, List<Resource> cost, JSONObject actions) {
		super(name, cost, actions);
	}

	@Override
	protected void parseEffects(JSONObject actions) {
		primaryMode = new Action("base mode",(JSONObject) actions.get("base-mode"));
		secondaryMode = new Action(actions.get("secondary-mode").toString(),
				(JSONObject) actions.get(actions.get("secondary-mode").toString()));
	}

	/**
	 * 
	 */
	public void chooseMode() {
		// TODO implement here
	}

}