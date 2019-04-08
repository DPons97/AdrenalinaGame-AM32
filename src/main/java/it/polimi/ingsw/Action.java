package it.polimi.ingsw;

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
	public Action() {
		this.actions = new ArrayList<>();
		this.targetCells = new ArrayList<>();
		this.targetDirection = null;
		this.targetPlayers = new ArrayList<>();
	}

	/**
	 * @return copy of actions list
	 */
	public List<BaseAction> getActions() {
		return new ArrayList<>(actions);
	}

	/**
	 * @param
	 */
	public void parseEffect(/*JSONObject*/) {
		// TODO implement here
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