package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.AdrenalinaMatch;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 */
public abstract class ClientPlayer extends UnicastRemoteObject implements ClientFunctionalities{

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
	 * Default constructor
	 */
	public ClientPlayer(String nickname) throws RemoteException {
		super();
		this.nickname = nickname;
	}



	/**
	 * 
	 */
	public static void main(){

	}

}