package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
	private List<PlayerConnection> players;

	/*
	 *	Keeps track of disconnected players' nicknames
	 */
	private List<String> disconnectedPlayers;



	/**
	 * Default constructor
	 * initializes new lobby and new list of players
	 */
	public LobbyController() {
		this.lobby = new Lobby(maxMatches);
		this.players = new ArrayList<>();
		this.disconnectedPlayers = new ArrayList<>();
	}


	/**
	 * @return list of current players inside lobby and waiting to join
	 */
	public List<PlayerConnection> getPlayers() { return new ArrayList<>(players); }

	/**
	 * @return list of current players inside lobby and waiting to join
	 */
	public List<String> getPlayersNames() {
		return getPlayers().stream().map(p->p.name).collect(Collectors.toList());
	}

	/**
	 * @param name of player to get
	 * @return Player with given name
	 */
	public PlayerConnection getPlayerByName(String name) {
		if(!getPlayersNames().contains(name))
			return null;
		return getPlayers().stream().filter(p-> p.name.equals(name)).collect(Collectors.toList()).get(0);
	}

	/**
	 * @return list of current players inside a game
	 */
	public List<PlayerConnection> getPlayersInGame(){
		return lobby.getPlayersInGame().stream().map(Player::getConnection).collect(Collectors.toList());
	}

	/**
	 * @return list of current players inside a game
	 */
	public List<String> getPlayersNameInGame(){
		return lobby.getPlayersInGame().stream().map(Player::getNickname).collect(Collectors.toList());
	}

	/**
	 * @param name of player to get from a game
	 * @return Player with given name
	 */
	public PlayerConnection getPlayerInGameByName(String name){
		if(!getPlayersNames().contains(name))
			return null;
		return getPlayersInGame().stream().filter(p-> p.name.equals(name)).collect(Collectors.toList()).get(0);
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
		player.setServerLobby(this);
		lobby.addPlayer(new Player(player.getName(), player));

		System.out.print(player.getName());
		System.out.println(" connected.");


		updatePlayers();

	}

	public synchronized List<String> getDisconnectedPlayers() {
		return new ArrayList<>(disconnectedPlayers);
	}

	public synchronized void removePlayer(PlayerConnection player){
		players.remove(player);
		disconnectedPlayers.add(player.getName());
		lobby.removePlayer(lobby.getPlayer(player));
		updatePlayers();
	}

	/**
	 * Sends a broadcast message to all players to update their model
	 */
	private synchronized void updatePlayers() {
		players.forEach(p -> p.updateLobby(lobby));
	}

	/**
	 * Sends a broadcast message to all players to update their model
	 * @return list of threads that ned to be joined if there is need of being sure that all disconnected clientes have been removed
	 */
	public synchronized List<Thread> pingALl() {
		List<Thread> toJoin= new ArrayList<>();
		players.forEach(p-> toJoin.add(p.ping()));

		return toJoin;

	}

	public synchronized void reconnectPlayer(PlayerConnection player){
		System.out.println("Reconnecting "+player.name);
		player.setServerLobby(this);
		players.add(player);
		disconnectedPlayers.remove(player.getName());
		lobby.reconnect(player);
		updatePlayers();
	}
}