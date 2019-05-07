package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.model.Cell;

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
	public Player playerSelection(List<Player> selectable) throws RemoteException;

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 */
	public Cell cellSelection(List<Cell> selectable) throws RemoteException;

	/**
	 * Select an action to make
	 * @return action to make
	 */
	public TurnAction actionSelection() throws RemoteException;

}