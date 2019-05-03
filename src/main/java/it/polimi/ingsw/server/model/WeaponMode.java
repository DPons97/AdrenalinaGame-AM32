package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.WeaponNotLoadedException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
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

	/**
	 * Shoot with this weapon
	 * @param effectID effect identifier (0 primary, 1 secondary)
	 * @param shooter player shooting
	 */
	@Override
	public void shoot(int effectID, Player shooter) throws WeaponNotLoadedException {
		if(!loaded) throw new WeaponNotLoadedException();
		switch (effectID){
			case 0:
				executePrimaryMode(shooter);
				primaryMode.setExecuted(true);
				break;
			case 1:
				executeSecondaryMode(shooter);
				secondaryMode.setExecuted(true);
				break;
			default:
				throw new IllegalArgumentException();
		}
		// TODO change this and delegate to controller
		this.loaded = false;
	}

	/**
	 * @return list of possible shoot actions
	 */
	@Override
	public List<Action> getShootActions() {
		List<Action> toReturn = new ArrayList<>();
		toReturn.add(primaryMode);
		toReturn.add(secondaryMode);
		return toReturn;
	}

	/**
	 * @return list of valid shoot actions
	 */
	@Override
	public List<Action> getValidActions() {
		return getShootActions();
	}

	/**
	 * @param actions json object to parse
	 */
	@Override
	protected void parseEffects(JSONObject actions) {
		primaryMode = new Action("base mode",(JSONObject) actions.get("base-mode"));
		secondaryMode = new Action(actions.get("secondaryMode").toString(),
				(JSONObject) actions.get(actions.get("secondaryMode").toString()));
	}

	/**
	 * @param caller player that excecute the effect
	 */
	private void executePrimaryMode(Player caller) {
		for(Action.BaseAction e: primaryMode.getActions()){
			e.applyOn(caller);
		}
	}

	/**
	 * @param caller player that excecute the effect
	 */
	private void executeSecondaryMode(Player caller) {
		for(Action.BaseAction e: secondaryMode.getActions()){
			e.applyOn(caller);
		}
	}

	@Override
	public void reload() {
		super.reload();
		primaryMode.setExecuted(false);
		secondaryMode.setExecuted(false);
	}


}