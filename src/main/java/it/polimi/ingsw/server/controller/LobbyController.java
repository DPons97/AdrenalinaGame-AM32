package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class LobbyController {

	/**
	 *
	 */
	public Lobby lobby;
	private int maxMatches = 10;
	/**
	 *
	 */
	public List<PlayerConnection> players;

	/**
	 * Default constructor
	 */
	public LobbyController() {
		this.lobby = new Lobby(maxMatches);
		this.players = new ArrayList<>();
	}


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
	public synchronized void addPlayer(PlayerConnection player) {
		players.add(player);
		System.out.println(player.getName());
		System.out.print(" connected.");
	}
}