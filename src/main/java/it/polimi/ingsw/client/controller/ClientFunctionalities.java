package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import org.json.simple.JSONObject;

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
	String playerSelection(List<String> selectable) throws RemoteException;

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 */
	Point cellSelection(List<Point> selectable) throws RemoteException;

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	List<Point> roomSelection(List<List<Point>> selectable) throws RemoteException;

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	WeaponSelection reloadSelection(List<String> canLoad) throws RemoteException;

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	WeaponSelection shootSelection(List<String> loaded) throws RemoteException;

	/**
	 * select a weapon from a list
	 * @param weapon list of selectable weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	String weaponSelection(List<String> weapon) throws RemoteException;

	/**
	 * select a weapon from a list
	 * @param powerup list of selectable weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	String powerupSelection(List<String> powerup) throws RemoteException;

	/**
	 * Select an action to make
	 * @return action to make
	 */
	TurnAction actionSelection() throws RemoteException;

	/**
	 * Updates the lobby view
	 * @param toGetUpdateFrom JSON lobby representation to get update from
	 */
	void updateLobby(String toGetUpdateFrom) throws  RemoteException;

	/**
	 * Updates the match view
	 * @param toGetUpdateFrom JSON match representation to get update from
	 */
	void updateMatch(JSONObject toGetUpdateFrom) throws  RemoteException;

}