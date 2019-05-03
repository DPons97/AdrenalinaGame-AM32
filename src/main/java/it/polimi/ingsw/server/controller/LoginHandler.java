package it.polimi.ingsw.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 */
public abstract class LoginHandler extends UnicastRemoteObject implements ServerFunctionalities{

	/**
	 * Default constructor
	 */
	public LoginHandler() throws RemoteException {
		super();
	}

	/**
	 *  max connections number
	 */
	private int maxConnections;

	/**
	 * ip address
	 */
	private String ip;

	/**
	 * socket port
	 */
	private int socketPort;

	/**
	 * rmi port
	 */
	private int rmiPort;

	/**
	 * controller of the lobby
	 */
	private LobbyController lobby;


	/**
	 * 
	 */
	public abstract void main();

	/**
	 * listen socket connection
	 */
	public void listenSocketConnection() {
		// TODO implement here
	}

}