package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;

import java.rmi.Remote;

/**
 * 
 */
public interface ServerFunctionalities extends Remote {

	/**
	 * @param client
	 */
	public void login(ClientFunctionalities client);

	/**
	 * 
	 */
	public void logout();

}