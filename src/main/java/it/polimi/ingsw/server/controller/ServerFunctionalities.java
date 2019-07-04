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
	 * @return true if login was successful
	 * @throws RemoteException
	 */
	boolean login(String name, ClientFunctionalities client) throws RemoteException;

	/**
	 * Allow user to disconnect from server
	 * Removes remote client in Player remote
	 * @throws RemoteException
	 */
	void logout()throws RemoteException;

	/**
	 * Allow user that detects a network issue to reconnect to server
	 * @param name player name
	 * @throws RemoteException
	 */
	void ping(String name)throws RemoteException;

	/**
	 * Allow user to create a game while in the lobby
	 * @param name player name
	 * @param maxPlayers in the game
	 * @param maxDeaths in the game
	 * @param turnDuration in seconds
	 * @param mapID between 1 and 4
	 * @throws RemoteException
	 */
	void createGame(String name, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws RemoteException;

	/**
	 * Allow to join a game while in the loby
	 * @param name player name
	 * @param id match id
	 * @throws RemoteException
	 */
	void joinGame(String name, int id) throws  RemoteException;

	/**
	 * Allow user to leave a waiting room and go back to the lobby
	 * @param name player name
	 * @throws RemoteException
	 */
	void backToLobby(String name) throws  RemoteException;

	/**
	 * Allow user to communicate that is ready
	 * @param name player name
	 * @param isReady value to set
	 * @throws RemoteException
	 */
	void ready(String name, boolean isReady) throws RemoteException;

	/**
	 * Updates the lobby view
	 * @return  JSON lobby representation to get update from
	 * @param name player name
	 * @throws RemoteException
	 */
	String updateLobby(String name) throws  RemoteException;

}