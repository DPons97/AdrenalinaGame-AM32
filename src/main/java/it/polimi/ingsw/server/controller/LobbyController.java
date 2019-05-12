package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.Player;

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

	private static final Object lock = new Object();

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
	public void addPlayer(PlayerConnection player) {
		synchronized (lock) {
			players.add(player);
		}
		player.setServerLobby(this);
		synchronized (lobby) {
			lobby.addPlayer(new Player(player.getName(), player));
		}
		System.out.print(player.getName());
		System.out.println(" connected.");


		updatePlayers();

	}

	public void removePlayer(PlayerConnection player){
		synchronized (lock) {
			players.remove(player);
		}
		synchronized (lobby) {
			lobby.removePlayer(lobby.getPlayer(player));
		}
		updatePlayers();
	}

	/**
	 * Sends a broadcast message to all players to update their model
	 */
	private void updatePlayers() {
		synchronized (lock) {
			players.stream().forEach(p -> p.updateLobby(lobby));
		}
	}
}