package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.model.Cell;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

/**
 * 
 */
public interface ClientFunctionalities extends Remote {

	/**
	 * @param selectable
	 * @return
	 */
	public Cell playerSelection(List<Player> selectable) throws RemoteException;

	/**
	 * @param selectable 
	 * @return
	 */
	public Cell cellSelection(List<Cell> selectable) throws RemoteException;

	/**
	 * @return
	 */
	public TurnAction actionSelection() throws RemoteException;

}