package it.polimi.ingsw.Client.Controller;

import java.util.*;

/**
 * 
 */
public abstract class ServerConnection {

	/**
	 * Default constructor
	 */
	public ServerConnection() {
	}

	/**
	 * 
	 */
	private String ip;

	/**
	 * 
	 */
	private int port;

	/**
	 * 
	 */
	private ClientPlayer player;




	/**
	 * @param ip 
	 * @param port 
	 * @param nickname
	 */
	public void connect(String ip, int port, String nickname) {
		// TODO implement here
	}

	/**
	 * 
	 */
	public void disconnect() {
		// TODO implement here
	}

}