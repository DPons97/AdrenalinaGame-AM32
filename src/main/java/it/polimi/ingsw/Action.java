package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.DeadPlayerException;
import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import it.polimi.ingsw.custom_exceptions.InvalidStringException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 
 */
public class Action {

	/**
	 * Functional interface to compose base actions
	 */
	public interface BaseAction {
		/**
		 *
		 */
		public void applyOn(Player caller);
	}

	/**
	 * Action name
	 */
	private String name;

	/**
	 * Action description
	 */
	private String description;

	/**
	 * Action description
	 */
	private List<Resource> cost;

	/**
	 * List of base actions to run in order
	 */
	private List<BaseAction> actions;

	/**
	 * List of target players
	 */
	private List<Player> targetPlayers;

	/**
	 * List of target cells
	 */
	private List<Cell> targetCells;

	/**
	 * direction to apply action (may not be needed in some actions)
	 */
	private Direction targetDirection;

	/**
	 * max distace for action
	 */
	private int maxDistance;

	/**
	 * min distace for action
	 */
	private int minDistance;

	/**
	 * @param name action name
	 * @param effect action JSONObject effect to parse
	 */
	public Action(String name,JSONObject effect) {
		this.name = name;
		this.actions = new ArrayList<>();
		this.targetCells = new ArrayList<>();
		this.targetDirection = null;
		this.targetPlayers = new ArrayList<>();
		this.cost = new ArrayList<>();
		parseEffect(effect);
	}

	/**
	 * @return copy of actions list
	 */
	public List<BaseAction> getActions() {
		return new ArrayList<>(actions);
	}

	/**
	 * @return copy of actions list
	 */
	public List<Resource> getCost() {
		return cost;
	}

	/**
	 * @param effect json effect to parse
	 */
	public void parseEffect(JSONObject effect) {
		try {
			this.description = effect.get("description").toString();
			JSONArray costJSON = (JSONArray) effect.get("cost");
			for(Object res: costJSON){
				this.cost.add(AdrenalinaMatch.stringToResource(res.toString()));
			}

			JSONArray baseActionsJSON = (JSONArray) effect.get("actions");
			for(Object baseActionObj: baseActionsJSON){
				JSONObject baseActionJSON = (JSONObject) baseActionObj;
				switch(baseActionJSON.get("type").toString()){
					case "SELECT":
						// TODO implement here
						// might be controller work

						break;
					case "DAMAGE":
						for(int j = 0; j < targetPlayers.size(); j++){
							int finalJ = j;
							actions.add(caller -> {
								int dmg = Integer.parseInt(((JSONArray)baseActionJSON.get("value")).get(finalJ).toString());
								IntStream.range(0, dmg).forEach(nDmg -> {
									try {
										targetPlayers.get(finalJ).takeDamage(caller);
									} catch (DeadPlayerException e) {
										e.printStackTrace();
									}
								});
							});
						}
						break;
					case "MOVE":
						for(int j = 0; j < targetPlayers.size(); j++){
							int finalJ = j;
							actions.add(caller -> targetPlayers.get(finalJ).move(targetCells.get(0)));
						}
						break;
					case "MARK":
						for(int j = 0; j < targetPlayers.size(); j++){
							int finalJ = j;
							actions.add(caller -> {
								int dmg = Integer.parseInt(((JSONArray)baseActionJSON.get("value")).get(finalJ).toString());
								IntStream.range(0, dmg).forEach(nDmg -> targetPlayers.get(finalJ).takeMark(caller));
							});
						}
						break;
					default:
						throw new InvalidJSONException();
				}
			}
		} catch (InvalidJSONException | InvalidStringException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return copy of target players
	 */
	public List<Player> getTargetPlayers() {
		return new ArrayList<>(targetPlayers);
	}

	/**
	 * @return copy of target cells
	 */
	public List<Cell> getTargetCells() {
		return new ArrayList<>(targetCells);
	}

	/**
	 * @return target direction
	 */
	public Direction getTargetDirection() {
		return targetDirection;
	}

	/**
	 * @param newP new players to add as targets
	 */
	public void setTargetPlayers(List<Player> newP) {
		this.targetPlayers.addAll(newP);
	}

	/**
	 * @param newC new cell to add as targets
	 */
	public void setTargetCells(List<Cell> newC) {
		this.targetCells.addAll(newC);
	}

	/**
	 * @param dir new direction for action
	 */
	public void setTargetDirection(Direction dir) {
		this.targetDirection = dir;
	}

}