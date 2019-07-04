package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Player;
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
	 * Ping client to check connection status
	 * @throws RemoteException
	 */
	void ping() throws RemoteException;

	/**
	 * Select a player from a given list
	 * @param selectable list of players
	 * @return player from selectable
	 * @throws RemoteException
	 */
	String playerSelection(List<String> selectable) throws RemoteException;

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 * @throws RemoteException
	 */
	Point cellSelection(List<Point> selectable) throws RemoteException;

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 * @throws RemoteException
	 */
	List<Point> roomSelection(List<List<Point>> selectable) throws RemoteException;

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 * @throws RemoteException
	 */
	WeaponSelection reloadSelection(List<String> canLoad) throws RemoteException;

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 * @throws RemoteException
	 */
	WeaponSelection shootSelection(List<String> loaded) throws RemoteException;

	/**
	 * select a weapon from a list
	 * @param weapon list of selectable weapons
	 * @return WeaponSelection with weapon to shoot with
	 * @throws RemoteException
	 */
	WeaponSelection weaponSelection(List<String> weapon) throws RemoteException;

	/**
	 * select a powerup from a list
	 * @param powerup list of selectable powerup
	 * @return selected powerup
	 * @throws RemoteException
	 */
	String powerupSelection(List<String> powerup) throws RemoteException;

	/**
	 * Select an action to make
	 * @return action to make
	 * @throws RemoteException
	 */
	TurnAction actionSelection() throws RemoteException;

	/**
	 * Shows leaderboard
	 * @param leaderboard list of players ordered by victory points
	 * @throws RemoteException
	 */
	void showLeaderboard(List<String> leaderboard) throws RemoteException;

	/**
	 * select a powerup from a list without making user pay its cost
	 * @param weapons list of selectable powerup
	 * @return selected powerup
	 * @throws RemoteException
	 */
	WeaponSelection weaponFreeSelection(List<String> weapons) throws RemoteException;

	/**
	 * Updates the match view
	 * @param toGetUpdateFrom JSON match representation to get update from
	 * @throws RemoteException
	 */
	void updateMatch(JSONObject toGetUpdateFrom) throws  RemoteException;

	void alert(String message) throws  RemoteException;

}