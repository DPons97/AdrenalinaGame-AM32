package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 */
public interface ServerFunctionalities extends Remote {

	/**
	 * Allow user to connect to server.
	 * Register remote client in Player remote
	 * and adds it to Lobby Controller
	 * @param client remote reference to client connecting
	 * @param name client name
	 */
	public void login(String name, ClientFunctionalities client) throws RemoteException;

	/**
	 * Allow user to disconnect from server
	 * Removes remote client in Player remote
	 */
	public void logout()throws RemoteException;

	/**
	 * Allow user that detects a network issue to reconnect to server
	 */
	public void ping(String name)throws RemoteException;
}