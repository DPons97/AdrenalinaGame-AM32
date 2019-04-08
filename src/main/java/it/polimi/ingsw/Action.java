package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import it.polimi.ingsw.custom_exceptions.InvalidStringException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
		public void applyOn();
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
	 * Default constructor
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
	 * @param
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
						// TODO implement here
						break;
					case "MOVE":
						// TODO implement here
						break;
					case "MARK":
						// TODO implement here
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