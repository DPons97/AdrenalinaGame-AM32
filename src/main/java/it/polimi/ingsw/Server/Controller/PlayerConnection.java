package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Client.Model.Player;
import it.polimi.ingsw.Server.Model.Cell;

import java.util.*;

/**
 * 
 */
public abstract class PlayerConnection extends Observable {

	/**
	 * Default constructor
	 */
	public PlayerConnection() {
	}

	/**
	 * 
	 */
	private String name;

	/**
	 * @param selectable
	 * @return
	 */
	public Player selectPlayer(List<Player> selectable) {
		// TODO implement here
		return null;
	}

	/**
	 * @param selectable 
	 * @return
	 */
	public Cell selectCell(List<Cell> selectable) {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public TurnAction selectAction() {
		// TODO implement here
		return null;
	}

	/**
	 * @return
	 */
	public boolean reload() {
		// TODO implement here
		return false;
	}

}