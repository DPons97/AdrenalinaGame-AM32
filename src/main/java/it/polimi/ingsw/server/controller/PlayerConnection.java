package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.MatchAlreadyStartedException;
import it.polimi.ingsw.custom_exceptions.NotEnoughPlayersException;
import it.polimi.ingsw.custom_exceptions.PlayerNotExistsException;
import it.polimi.ingsw.custom_exceptions.PlayerNotReadyException;
import it.polimi.ingsw.server.model.*;

import java.util.List;

/**
 *	Allows server to send request to client without knowing connection method
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
	 * Current server's lobby
	 */
	private LobbyController serverLobby;

	/**
	 * Default constructor
	 */
	public PlayerConnection(String name) {
		this.name=name;
		this.serverLobby = null;
		this.currentMatch = null;
	}

	public String getName(){
	    return name;
    }

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
    **/
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
     * select a powerup card from a given list
     * @param selectable list of powerups
     * @return a powerup from selectable
     */
	public abstract Powerup choosePowerup(List<Powerup> selectable);

	/**
	 * select a weapon card from a given list
	 * @param selectable list of weapons
	 * @return a weapon from selectable
	 */
	public abstract WeaponSelection chooseWeapon(List<Weapon> selectable);

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
	 * Updates the client match view
	 * @param toGetUpdateFrom  match to get update from
	 */
	public abstract void updateMatch(AdrenalinaMatch toGetUpdateFrom);

	/**
	 * Updates the client lobby view
	 * @param toGetUpdateFrom  lobby to get update from
	 */
	public abstract void updateLobby(Lobby toGetUpdateFrom);

	/**
	 * Pings client to check connection
	 * @return Thread removing player
	 */
	public abstract Thread ping();

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
	 * @return current server's lobby
	 */
	protected LobbyController getServerLobby() {
		return serverLobby;
	}

	/**
	 * @param serverLobby new server lobby
	 */
	protected void setServerLobby(LobbyController serverLobby) {
		this.serverLobby = serverLobby;
	}

	/**
	 * Start this player's match, only if he's the host
	 */
	public void startMatch() throws PlayerNotReadyException, MatchAlreadyStartedException, NotEnoughPlayersException, PlayerNotExistsException {
		if (name.equals(currentMatch.getHostName()))
			currentMatch.startMatch();
	}

	/**
	 * Go back to lobby
	 */
	public void backToLobby() throws MatchAlreadyStartedException, NotEnoughPlayersException, PlayerNotExistsException {
		currentMatch.backToLobby(this);
	}

	/**
	 * @return  pinged
	 */
	public abstract boolean getPinged();

	public abstract void setPinged(boolean ping);
}