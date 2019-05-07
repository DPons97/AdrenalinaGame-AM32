package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Weapon;

import java.util.List;

/**
 *
 */
public abstract class PlayerConnection {
	/**
	 * Player's name
	 */
	protected String name;

	/**
	 * Current match's controller
	 */
	private MatchController currentMatch;

	/**
	 * Default constructor
	 */
	public PlayerConnection(String name) {
		this.name=name;
		currentMatch = null;
	}

	public String getName(){
	    return name;
    }

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
	 */
	public abstract Player selectPlayer(List<Player> selectable);

	/**
	 * select a cell in a given list
	 * @param selectable list of cells
	 * @return a cell from selectable
	 */
	public abstract Cell selectCell(List<Cell> selectable);

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	public abstract List<Cell> selectRoom(List<List<Cell>> selectable);

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	public abstract WeaponSelection reload(List<Weapon> canLoad);

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	public abstract WeaponSelection shoot(List<Weapon> loaded);

	/**
	 * select a turn action in a given list
	 * @return an action to make
	 */
	public abstract TurnAction selectAction();

	/**
	 * @return reference to current match's controller
	 */
	public MatchController getCurrentMatch() {
		return currentMatch;
	}

	/**
	 * @param currentMatch new player's match controller
	 */
	public void setCurrentMatch(MatchController currentMatch) {
		this.currentMatch = currentMatch;
	}

	/**
	 * Start this player's match, only if he's the host
	 */
	public void startMatch() {
		if (name.equals(currentMatch.getMatch().getPlayers().get(0).getNickname())) {
			currentMatch.beginTurn();
		}
	}

	/**
	 * Go back to lobby
	 */
	public void backToLobby() {
		// TODO: implement here
	}
}