package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.custom_exceptions.UsernameTakenException;

import java.io.IOException;
import java.rmi.NotBoundException;

/**
 * 
 */
public abstract class ServerConnection {

	/**
	 *	Reference to Client player to pass to rmi server in case of rmi connection
	 */
	protected ClientPlayer player;

	/**
	 * @param player requesting the connection
	 */
	public ServerConnection(ClientPlayer player) {
		this.player = player;
	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws UsernameTakenException
	 */
	public abstract void connect(String ip, int port) throws IOException, NotBoundException, UsernameTakenException;

	/**
	 * Disconnect client from server
	 */
	public abstract void disconnect();

    public abstract void setReady(boolean isReady);

	public abstract void createGame(int maxPlayers, int maxDeaths, int turnDuration, int mapID);

	public abstract void joinGame(String nickname, int id);

    /**
     * Updates the lobby view
	 * @param name name of the player requesting the update
     * @return JSON lobby representation to get update from
     */
    public abstract String updateLobby(String name);

    public abstract void backToLobby();
}