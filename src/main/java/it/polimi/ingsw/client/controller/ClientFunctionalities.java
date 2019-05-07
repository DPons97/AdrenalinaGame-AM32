package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Weapon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 
 */
public interface ClientFunctionalities extends Remote {

	/**
	 * Select a player from a given list
	 * @param selectable list of players
	 * @return player from selectable
	 */
	Player playerSelection(List<Player> selectable) throws RemoteException;

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 */
	Cell cellSelection(List<Cell> selectable) throws RemoteException;

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	List<Cell> roomSelection(List<List<Cell>> selectable) throws RemoteException;

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	WeaponSelection reloadSelection(List<Weapon> canLoad) throws RemoteException;

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	WeaponSelection shootSelection(List<Weapon> loaded) throws RemoteException;

	/**
	 * Select an action to make
	 * @return action to make
	 */
	TurnAction actionSelection() throws RemoteException;

}