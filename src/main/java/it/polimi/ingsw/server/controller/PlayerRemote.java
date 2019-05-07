package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Weapon;

import java.util.List;

/**
 *
 */
public class PlayerRemote extends PlayerConnection {
	/**
	 * Remote object of player
	 */
	public ClientFunctionalities remotePlayer;

	/**
	 * Default constructor
	 */
	public PlayerRemote(String name, ClientFunctionalities remoteClient) {
		super(name);
		this.remotePlayer = remoteClient;
	}

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
	 */
	@Override
	public Player selectPlayer(List<Player> selectable) {
		return null;
	}

	/**
	 * select a cell in a given list
	 * @param selectable list of cells
	 * @return a cell from selectable
	 */
	@Override
	public Cell selectCell(List<Cell> selectable) {
		return null;
	}

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	@Override
	public List<Cell> selectRoom(List<List<Cell>> selectable) { return null; }

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		return null;
	}

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		return null;
	}

	/**
	 * select a turn action in a given list
	 * @return an action to make
	 */
	@Override
	public TurnAction selectAction() {
		return null;
	}
}