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
	 * @param nickname
	 */
	public abstract void connect(String ip, int port, String nickname);

	/**
	 * 
	 */
	public abstract void disconnect();

}