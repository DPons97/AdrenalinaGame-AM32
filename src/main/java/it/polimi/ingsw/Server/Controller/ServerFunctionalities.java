package it.polimi.ingsw.Server.Controller;

import it.polimi.ingsw.Client.Controller.ClientFunctionalities;

import java.rmi.Remote;
import java.util.*;

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