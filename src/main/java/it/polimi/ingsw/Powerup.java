package it.polimi.ingsw;

import org.json.simple.JSONObject;

/**
 * 
 */
public class Powerup {
	/**
	 * Powerup name
	 */
	private String name;

	/**
	 * Powerup description
	 */
	private String description;

	/**
	 * Bonus resource given by the perk
	 */
	private Resource bonusResource;

	/**
	 * Powerup effect
	 */
	private Action effect;

	/**
	 * @param name powerup name
	 * @param description powerup description
	 * @param bonusResource powerup bonus resource
	 * @param effect JSONObject with effect to parse powerup type (targeting-scope, newton, tagback-granade, teleporter)
	 */
	public Powerup(String name, String description, Resource bonusResource, JSONObject effect) {
		this.name = name;
		this.description = description;
		this.bonusResource = bonusResource;
		parseEffect(effect);
	}

	/**
	 * @param effect powerup effect to parse
	 */
	private void parseEffect(JSONObject effect){
		this.effect = new Action(this.name, effect);
	}

	/**
	 * @param user player using the powerup
	 */
	public void useAsEffect(Player user) {
		for(Action.BaseAction a: effect.getActions()){
			a.applyOn(user);
		}
	}

	/**
	 * @return powerup effect
	 */
	public Action getEffect(){
		return effect;
	}

	/**
	 * @return bonus resource
	 */
	public Resource useAsResource() {
		return bonusResource;
	}


}