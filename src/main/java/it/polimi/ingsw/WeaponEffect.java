package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * 
 */
public class WeaponEffect extends Weapon {
	/**
	 * Weapon base effect
	 */
	private Action primaryFire;

	/**
	 * Weapon first optional effect (null if not present)
	 */
	private Action firstOptional;

	/**
	 * Weapon second optional effect (null if not present)
	 */
	private Action secondOptional;


	/**
	 * @param name        weapon name
	 * @param cost        weapon cost
	 * @param actions json object with effects description
	 */
	public WeaponEffect(String name, List<Resource> cost, JSONObject actions) {
		super(name, cost, actions);
	}

	@Override
	protected void parseEffects(JSONObject actions) {
		int i = 0;
		try{
		primaryFire = new Action("base effect",(JSONObject) actions.get("base-effect"));
		JSONArray optionalEffectJ = (JSONArray) actions.get("secondaryEffects");
		for(Object effectName:optionalEffectJ){
			switch(i){
				case 0:
					firstOptional = new Action(effectName.toString(),
							(JSONObject) actions.get(effectName.toString()));
					i++;
					break;
				case 1:
					secondOptional = new Action(effectName.toString(),
							(JSONObject) actions.get(effectName.toString()));
					i++;
					break;
				default:
					throw new InvalidJSONException();
			}
		}
		} catch (InvalidJSONException e) {
			e.printStackTrace();
		}

	}


}