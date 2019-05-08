package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Weapon;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *	RMI implementation of player connection
 *	calls clientPlayer methods directly
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

	private Player getPlayerByName(String name){
		return getCurrentMatch().getMatch().getPlayers().stream().
				filter(p->p.getNickname().equals(name)).collect(Collectors.toList()).get(0);
	}

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
	 */
	@Override
	public Player selectPlayer(List<Player> selectable) {
		try {
			return getPlayerByName(
					remotePlayer.playerSelection(
						selectable.stream().map(Player::getNickname).collect(Collectors.toList())
					)
			);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * select a cell in a given list
	 * @param selectable list of cells
	 * @return a cell from selectable
	 */
	@Override
	public Cell selectCell(List<Cell> selectable) {
		try {
			Point p =  remotePlayer.cellSelection(
				selectable.stream().map(c->new Point(c.getCoordX(), c.getCoordY())).collect(Collectors.toList())
			);
			return selectable.stream().filter(c->c.getCoordX() == p.getX() && c.getCoordY() == p.getY()).
					collect(Collectors.toList()).get(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	@Override
	public List<Cell> selectRoom(List<List<Cell>> selectable) {
		try {
			List<List<Point>> points = new ArrayList<>();
			selectable.forEach(list -> points.add(list.stream().map(c->new Point(c.getCoordX(), c.getCoordY())).collect(Collectors.toList())));
			List<Point> selected =  remotePlayer.roomSelection(points);
			for(List<Cell> r : selectable){
				for(Cell c : r){
					if(c.getCoordX() ==  selected.get(0).getX()&&
							c.getCoordY() == selected.get(0).getY() )
						return r;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * select a powerup card from a given list
	 * @param selectable list of powerups
	 * @return a powerup from selectable
	 */
	@Override
	public Powerup choosePowerup(List<Powerup> selectable) {
		return null;
	}

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		try {
			return remotePlayer.reloadSelection(canLoad);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		try {
			return remotePlayer.shootSelection(loaded);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * select a turn action in a given list
	 * @return an action to make
	 */
	@Override
	public TurnAction selectAction() {
		try {
			return remotePlayer.actionSelection();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
}