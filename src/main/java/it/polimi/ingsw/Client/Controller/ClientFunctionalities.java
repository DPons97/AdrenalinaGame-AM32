package it.polimi.ingsw.Client.Controller;

import it.polimi.ingsw.Client.Model.Player;
import it.polimi.ingsw.Server.Controller.TurnAction;
import it.polimi.ingsw.Server.Model.Cell;

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