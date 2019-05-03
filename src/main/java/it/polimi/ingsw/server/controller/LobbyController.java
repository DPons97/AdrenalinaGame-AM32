package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;

import java.util.*;

/**
 * 
 */
public class LobbyController implements Observer {

	/**
	 * Default constructor
	 */
	public LobbyController() {
	}

	/**
	 * 
	 */
	public Lobby lobby;

	/**
	 * 
	 */
	public List<PlayerConnection> players;



	/**
	 * 
	 */
	public void joinMatch() {
		// TODO implement here
	}

	/**
	 * 
	 */
	public void hostMatch() {
		// TODO implement here
	}

	/**
	 * @param player
	 */
	public void addPlayer(PlayerConnection player) {
		// TODO implement here
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}