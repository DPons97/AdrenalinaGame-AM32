package it.polimi.ingsw.server.model;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 */
public class Powerup implements Serializable {

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
		// [effect] can be null for testing purposes
		if (effect != null) parseEffect(effect);
	}

	public  String getName(){
		return name;
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
	public Action getEffect(){ return effect; }

	/**
	 * @return bonus resource
	 */
	public Resource getBonusResource() { return bonusResource;	}

	/**
	 * @return JSON representation of this
	 */
	public JSONObject toJSON(){
		JSONObject p = new JSONObject();
		p.put("name", name);
		p.put("description", description);
		p.put("bonusResource", bonusResource.toString());
		return p;
	}

	public static Powerup parseJSON(JSONObject toParse){
		String name = toParse.get("name").toString();
		String description = toParse.get("description").toString();
		Resource resource = Resource.valueOf(toParse.get("bonusResource").toString());
		return new Powerup(name, description, resource, null);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Powerup powerup = (Powerup) o;
		return Objects.equals(getName(), powerup.getName()) &&
				getBonusResource() == powerup.getBonusResource();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getBonusResource());
	}
}