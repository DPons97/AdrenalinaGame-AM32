package it.polimi.ingsw.client.controller;

/**
 * 
 */
public abstract class ServerConnection {

	/**
	 *	Reference to Client player to pass to rmi server in case of rmi connection
	 */
	protected ClientPlayer player;

	/**
	 * Default constructor
	 */
	public ServerConnection(ClientPlayer player) {
		this.player = player;
	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 */
	public abstract void connect(String ip, int port);

	/**
	 * Disconnect client from server
	 */
	public abstract void disconnect();

    public abstract void setReady();

	public abstract void createGame(String nickname, int maxPlayers, int maxDeaths, int turnDuration, int mapID);

	public abstract void joinGame(String nickname, int id);
}