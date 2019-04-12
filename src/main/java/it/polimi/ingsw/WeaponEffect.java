package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import it.polimi.ingsw.custom_exceptions.WeaponNotLoadedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class WeaponEffect extends Weapon {
	/**
	 * Weapon base effect
	 */
	private Action primaryEffect;

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

	/**
	 * @param actions json object to parse
	 */
	@Override
	protected void parseEffects(JSONObject actions) {
		int i = 0;
		try{
			primaryEffect = new Action("base effect",(JSONObject) actions.get("base-effect"));
			JSONArray optionalEffectJ = (JSONArray) actions.get("secondaryEffects");
			for(Object effectName:optionalEffectJ){
				switch(i){
					case 0:
						firstOptional = new Action(effectName.toString(),
								(JSONObject) actions.get(effectName.toString()));
						break;
					case 1:
						secondOptional = new Action(effectName.toString(),
								(JSONObject) actions.get(effectName.toString()));
						break;
					default:
						throw new InvalidJSONException();
				}
				i++;
			}
		} catch (InvalidJSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shoot with this weapon
	 * @param effectID effect identifier (0 primary, 1 firstOptional, 2 secondOptional)
	 * @param shooter player shooting
	 */
	@Override
	public void shoot(int effectID, Player shooter) throws WeaponNotLoadedException {
		if(!loaded) throw new WeaponNotLoadedException();
		switch (effectID){
			case 0:
				executePrimaryEffect(shooter);
				break;
			case 1:
				executeFirstOptional(shooter);
				break;
			case 2:
				executeSecondOptional(shooter);
				break;
			default:
				throw new IllegalArgumentException();
		}
		//TODO: change this when controller is implemented
		this.loaded = false;
	}

	/**
	 * @return list of possible shoot actions
	 */
	@Override
	public List<Action> getShootActions() {
		List<Action> toReturn = new ArrayList<>();
		toReturn.add(primaryEffect);
		if(firstOptional!=null) toReturn.add(firstOptional);
		if(secondOptional!=null) toReturn.add(secondOptional);
		return toReturn;
	}

	/**
	 * @param caller player that excecute the effect
	 */
	private void executePrimaryEffect(Player caller) {
		for(Action.BaseAction e: primaryEffect.getActions()){
			e.applyOn(caller);
		}
	}

	/**
	 * @param caller player that excecute the effect
	 */
	private void executeFirstOptional(Player caller) {
		for(Action.BaseAction e: firstOptional.getActions()){
			e.applyOn(caller);
		}
	}

	/**
	 * @param caller player that excecute the effect
	 */
	private void executeSecondOptional(Player caller) {
		for(Action.BaseAction e: secondOptional.getActions()){
			e.applyOn(caller);
		}
	}


}