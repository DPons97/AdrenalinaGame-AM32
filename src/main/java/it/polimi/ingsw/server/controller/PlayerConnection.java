package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Cell;
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
	 * @param selectable
	 * @return
	 */
	public abstract Player selectPlayer(List<Player> selectable);
	/**
	 * @param selectable
	 * @return
	 */
	public abstract Cell selectCell(List<Cell> selectable);

	/**
	 * @param selectable
	 * @return
	 */
	public abstract List<Cell> selectRoom(List<List<Cell>> selectable);

	/**
	 * @return
	 */
	public abstract WeaponSelection reload(List<Weapon> canLoad);

	/**
	 * @return
	 */
	public abstract WeaponSelection shoot(List<Weapon> loaded);

	/**
	 * @return
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