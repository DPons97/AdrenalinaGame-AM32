package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles incoming players and put them in the lobby
 */
public class LobbyController {

	/**
	 * Reference to lobby
	 */
	public Lobby lobby;

    /**
     * Maximum number of matches in the lobby
     */
	private int maxMatches = 10;
	/**
	 * List of players connected
	 */
	public List<PlayerConnection> players;

	/**
	 * Default constructor
     * initializes new lobby and new list of players
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
     * Add a new player to connected players
	 * @param player player to add
	 */
	public synchronized void addPlayer(PlayerConnection player) {
		players.add(player);
		// TODO set reference to lobby in player

		// TODO create new Player and add to lobby

		System.out.print(player.getName());
		System.out.println(" connected.");
	}
}