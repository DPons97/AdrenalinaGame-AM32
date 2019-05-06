package it.polimi.ingsw.client.controller;

/**
 * 
 */
public abstract class ServerConnection {

	/**
	 *
	 */
	protected ClientPlayer player;

	/**
	 * Default constructor
	 */
	public ServerConnection(ClientPlayer player) {
		this.player = player;
	}






	/**
	 * @param ip 
	 * @param port 
	 */
	public abstract void connect(String ip, int port);

	/**
	 * 
	 */
	public abstract void disconnect();

}