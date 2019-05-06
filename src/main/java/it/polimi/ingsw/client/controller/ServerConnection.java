package it.polimi.ingsw.client.controller;

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
	public abstract void connect(String ip, int port, String nickname);

	/**
	 * 
	 */
	public abstract void disconnect();

}