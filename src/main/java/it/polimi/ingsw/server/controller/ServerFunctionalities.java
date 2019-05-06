package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;

import java.rmi.Remote;

/**
 * 
 */
public interface ServerFunctionalities extends Remote {

	/**
	 * Allow user to connect to server.
	 * Register remote client in Player remote
	 * and adds it to Lobby Controller
	 * @param client
	 */
	public void login(String name, ClientFunctionalities client);

	/**
	 * Allow user to disconnect from server
	 * Removes remote client in Player remote
	 */
	public void logout();

}