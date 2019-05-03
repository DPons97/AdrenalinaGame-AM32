package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.AdrenalinaMatch;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 */
public abstract class ClientPlayer extends UnicastRemoteObject implements ClientFunctionalities{

	/**
	 * Default constructor
	 */
	public ClientPlayer() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private String nickname;

	/**
	 * 
	 */
	private AdrenalinaMatch match;

	/**
	 * 
	 */
	private ServerConnection server;



	/**
	 * 
	 */
	public static void main(){

	}

}