package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.model.*;

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
	 *
	 */
	private boolean pinged;

	/**
	 * Default constructor
	 */
	public PlayerRemote(String name, ClientFunctionalities remoteClient) {
		super(name);
		this.remotePlayer = remoteClient;
		pinged = true;
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
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
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

			if (p == null) return null;

			return selectable.stream().filter(c->c.getCoordX() == p.getX() && c.getCoordY() == p.getY()).
					collect(Collectors.toList()).get(0);
		} catch (RemoteException e) {
			e.printStackTrace();
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
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
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
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
		try {
			String selection = remotePlayer.powerupSelection(selectable.stream()
					.map(p->p.toJSON().toString()).collect(Collectors.toList()));

			if (selection.isEmpty()) return null;

			return selectable.stream().filter(s->selection.equals(s.toJSON().toString())).collect(Collectors.toList()).get(0);
		} catch (RemoteException e) {
			e.printStackTrace();
			Thread t = new Thread(this::disconnectPlayer);
			t.start();		}
		return null;
	}

	/**
	 * Select a weapon from a given list
	 * @param selectable list of weapons
	 * @return a WeaponSelection with only the selected weapons
	 */
	@Override
	public WeaponSelection chooseWeapon(List<Weapon> selectable) {
		try {
			return remotePlayer.weaponSelection(
					selectable.stream().map(Weapon::getName).collect(Collectors.toList()));
		} catch (RemoteException e) {
			e.printStackTrace();
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
		}
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
			return remotePlayer.reloadSelection(
					canLoad.stream().map(Weapon::getName).collect(Collectors.toList()));
		} catch (RemoteException e) {
			e.printStackTrace();
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
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
			return remotePlayer.shootSelection(
					loaded.stream().map(Weapon::getName).collect(Collectors.toList()));
		} catch (RemoteException e) {
			e.printStackTrace();

			Thread t = new Thread(this::disconnectPlayer);
			t.start();
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
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
		}
		return null;
	}

	/**
	 * Updates the client match view
	 * @param toGetUpdateFrom  match to get update from
	 */
	@Override
	public void updateMatch(AdrenalinaMatch toGetUpdateFrom) {
		try {
			if (remotePlayer != null) remotePlayer.updateMatch(toGetUpdateFrom.toJSON());
		} catch (RemoteException e) {
			e.printStackTrace();
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
		}
	}

	@Override
	public Thread ping() {
		try {
			remotePlayer.ping();
		} catch (RemoteException e) {
			Thread t = new Thread(this::disconnectPlayer);
			t.start();
			return t;
		}
		return null;
	}

	@Override
	public void beginLoading() {

	}

	@Override
	public void beginMatch() {

	}

	@Override
	public boolean getPinged() {
		return pinged;
	}

	@Override
	public void setPinged(boolean ping) {
		pinged = ping;
	}

	@Override
	public void alert(String s) {
		try {
			remotePlayer.alert(s);
		} catch (RemoteException e) {
			Thread t = new Thread(this::disconnectPlayer);
			t.start();		}
	}

	private void disconnectPlayer(){
		System.out.println(name+" disconnected");
		if(getServerLobby() != null)
			getServerLobby().removePlayer(this);
		if(getCurrentMatch()!= null)
			getCurrentMatch().getPlayer(getName()).setConnection(null);
	}
}