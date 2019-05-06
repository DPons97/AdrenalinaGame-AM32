package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Cell;
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

	@Override
	public Player selectPlayer(List<Player> selectable) {
		return null;
	}

	@Override
	public Cell selectCell(List<Cell> selectable) {
		return null;
	}

	@Override
	public List<Cell> selectRoom(List<List<Cell>> selectable) { return null; }

	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		return null;
	}

	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		return null;
	}

	@Override
	public TurnAction selectAction() {
		return null;
	}
}