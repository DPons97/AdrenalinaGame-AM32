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
	void login(String name, ClientFunctionalities client) throws RemoteException;

	/**
	 * Allow user to disconnect from server
	 * Removes remote client in Player remote
	 */
	void logout()throws RemoteException;

	/**
	 * Allow user that detects a network issue to reconnect to server
	 */
	void ping(String name)throws RemoteException;

	/**
	 * Allow user to create a game while in the lobby
	 */
	void createGame(String name, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws RemoteException;

	/**
	 * Allow to join a game while in the loby
	 */
	void joinGame(String name, int id) throws  RemoteException;

	/**
	 * Allow user to communicate that is ready
	 */
	void ready(String name) throws RemoteException;

	/**
	 * Updates the lobby view
	 * @return  JSON lobby representation to get update from
	 */
	String updateLobby() throws  RemoteException;

}