package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import it.polimi.ingsw.custom_exceptions.RequirementsNotMetException;
import it.polimi.ingsw.custom_exceptions.WeaponNotLoadedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
			if(optionalEffectJ != null) {
				for (Object effectName : optionalEffectJ) {
					switch (i) {
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
	public void shoot(int effectID, Player shooter) throws WeaponNotLoadedException, RequirementsNotMetException {
		if(!loaded) throw new WeaponNotLoadedException();
		String requirement;
		switch (effectID){
			case 0:
				executePrimaryEffect(shooter);
				primaryEffect.setExecuted(true);
				break;
			case 1:
				if(firstOptional == null) throw new IllegalArgumentException();
				requirement = firstOptional.getRequires();
				if ( (primaryEffect.getName().equals(requirement) && !primaryEffect.isExecuted()) ||
					 (secondOptional != null && secondOptional.getName().equals(requirement) && !secondOptional.isExecuted()) ) throw new RequirementsNotMetException();

				executeFirstOptional(shooter);
				firstOptional.setExecuted(true);
				break;
			case 2:
				if(secondOptional == null) throw new IllegalArgumentException();
				requirement = secondOptional.getRequires();
				if ( (primaryEffect.getName().equals(requirement) && !primaryEffect.isExecuted()) ||
						(firstOptional != null && firstOptional.getName().equals(requirement) && !firstOptional.isExecuted()) ) throw new RequirementsNotMetException();

				executeSecondOptional(shooter);
				secondOptional.setExecuted(true);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * @param id of effect to get name
	 * @return name of effect
	 */
	@Override
	public Action getAction(int id) {
		switch (id) {
			case 0:
				return primaryEffect;
			case 1:
				return firstOptional;
			case 2:
				return secondOptional;
			default:
				throw new IllegalArgumentException();
		}
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
	 * @return list of valid shoot actions
	 */
	@Override
	public List<Action> getValidActions() {
		return getShootActions().stream().
				filter(a -> a.getRequires() == null || canExecute(a.getRequires())).
				collect(Collectors.toList());
	}

	/**
	 * @param sequence list of effect IDs to evaluate execution
	 * @return True if given sequence can be executed
	 */
	@Override
	public boolean isValidActionSequence(List<Integer> sequence) {
		boolean canBeExecuted = true;

		// Convert integer sequence to action sequence
		List<Action> actionSequence = new ArrayList<>();
		for (Integer id : sequence) {
			actionSequence.add(getAction(id));
		}

		// Check sequence contains primary effect
		if (!actionSequence.contains(primaryEffect)) canBeExecuted = false;
		else {
			for (Action action : actionSequence) {
				// Get require
				for(Action a: getShootActions()){
					if(a.getName().equals(action.getRequires()) &&
							(!actionSequence.contains(a) ||
							actionSequence.indexOf(a) > actionSequence.indexOf(action))) {
						canBeExecuted = false;
						break;
					}
				}

				if (!canBeExecuted) break;
			}
		}
		return canBeExecuted;
	}

	/**
	 * @param actionName action to check
	 * @return true if action can be executed
	 */
	private boolean canExecute(String actionName) {
		for(Action a: getShootActions()){
			if(a.getName().equals(actionName))
				return a.isExecuted();
		}
		throw new IllegalArgumentException();
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

	@Override
	public void reload() {
		super.reload();
		getShootActions().forEach(p->p.setExecuted(false));
	}

}