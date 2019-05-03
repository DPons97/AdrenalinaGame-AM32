package it.polimi.ingsw.Client.Controller;

import it.polimi.ingsw.Client.Model.AdrenalinaMatch;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

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