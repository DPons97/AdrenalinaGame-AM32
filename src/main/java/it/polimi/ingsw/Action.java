package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class Action {

	/**
	 *
	 */
	public interface BaseAction {
		/**
		 *
		 */
		public void applyOn();
	}

	/**
	 * 
	 */
	private List<BaseAction> actions;

	/**
	 * 
	 */
	private List<Player> targetPlayers;

	/**
	 * 
	 */
	private List<Cell> targetCells;

	/**
	 * 
	 */
	private Direction targetDirection;

	/**
	 * Default constructor
	 */
	public Action() {
	}

	/**
	 * 
	 */
	public void getActions() {
		// TODO implement here
	}

	/**
	 * @param
	 */
	public void parseEffect(/*JSONObject*/) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public List<Player> getTargetPlayers() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public List<Cell> getTargetCells() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public List<Direction> getTargetDirection() {
		// TODO implement here
		return null;
	}

	/**
	 * @param newP
	 */
	public void setTargetPlayers(List<Player> newP) {
		// TODO implement here
	}

	/**
	 * @param newP
	 */
	public void setTargetCells(List<Cell> newP) {
		// TODO implement here
	}

	/**
	 * @param dir
	 */
	public void setTargetDirection(Direction dir) {
		// TODO implement here
	}

}