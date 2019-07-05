package it.polimi.ingsw.server.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Ammunition class (Resources + Bonus powerups)
 */
public class Ammo {
	/**
	 * List of resources on the card
	 */
	private List<Resource> resources;

	/**
	 * Identifies presence of powerup on the card. if present resources.size()==2
	 */
	private boolean powerup;

	/**
	 * Constructor for ammo card with 3 resources
	 * @param res1 first resource
	 * @param res2 second resource
	 * @param res3 third resource
	 */
	public Ammo(Resource res1, Resource res2, Resource res3) {
		this.resources = new ArrayList<>();
		this.resources.add(res1);
		this.resources.add(res2);
		this.resources.add(res3);
		this.powerup  = false;
	}

    /**
     * Constructor for ammo card with 2 resources and a perk card
	 * @param res1 first resource
	 * @param res2 second resource
     */
	public Ammo(Resource res1, Resource res2) {
		this.resources = new ArrayList<>();
		this.resources.add(res1);
		this.resources.add(res2);
		this.powerup = true;
	}

	/**
	 * @return list of resources
	 */
	public List<Resource> getResources() {
		return new ArrayList<>(this.resources);
	}

	/**
	 * @return true if perk is present, otherwise false
	 */
	public boolean hasPowerup() {
		return powerup;
	}

	/**
	 * @return JSON representation of this
	 * */
	public JSONObject toJSON(){
		JSONObject toRet = new JSONObject();
		JSONArray res = new JSONArray();

		resources.forEach(r -> res.add(r.toString()));

		toRet.put("powerup", powerup);
		toRet.put("resources", res);

		return toRet;
	}

	/**
	 * Builds a json object from a json representation
	 * @param toParse json representation to parse
	 * @return corresponding ammo
	 */
	public static Ammo parseJSON(JSONObject toParse){
		JSONArray res =(JSONArray) toParse.get("resources");
		if(res.size()==2){
			return new Ammo(Resource.valueOf(res.get(0).toString()),
							Resource.valueOf(res.get(1).toString()));
		} else {
			return new Ammo(Resource.valueOf(res.get(0).toString()),
							Resource.valueOf(res.get(1).toString()),
							Resource.valueOf(res.get(2).toString()));
		}
	}

}