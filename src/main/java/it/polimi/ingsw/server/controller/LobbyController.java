package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.*;
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
	 * @param player connectio to look for in the game
	 * @return Player with given name
	 */
	public MatchController getMatchByPlayerConnection(PlayerConnection player){
		return lobby.getLobbyMatches().stream().filter(m-> m.getPlayer(player.name)!= null).collect(Collectors.toList()).get(0);
	}

	/**
	 * Allow user to create a game while in the lobby
	 */
	public synchronized void joinMatch(PlayerConnection player, int gameID) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, PlayerNotExistsException {
		lobby.joinMatch(lobby.getPlayer(player),lobby.getJoinableMatches().get(gameID));
		players.remove(player);
		lobby.removePlayer(lobby.getPlayer(player));
	}

	/**
	 * Allow to join a game while in the lobby
	 */
	public synchronized void hostMatch(PlayerConnection host, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws TooManyMatchesException, TooManyPlayersException, PlayerNotExistsException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
		lobby.createMatch(host, maxPlayers,maxDeaths,turnDuration,mapID);
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


	}

	/**
	 * Gets all in game and disconnected player's nicknames
	 * @return list with players nickname that are in game and disconnected
	 */
	public synchronized List<String> getDisconnectedPlayersInGame(){
		return lobby.getLobbyMatches().stream().map(m->m.getMatch().getPlayers()).
				flatMap(List::stream).filter(p->p.getConnection()==null).map(Player::getNickname).collect(Collectors.toList());
	}

	public synchronized List<String> getDisconnectedPlayers() {
		return new ArrayList<>(disconnectedPlayers);
	}

	public synchronized void removePlayer(PlayerConnection player){
		if(players.contains(player)) {
			players.remove(player);
			disconnectedPlayers.add(player.getName());
			lobby.removePlayer(lobby.getPlayer(player));
		}else {
			getMatchByPlayerConnection(player).getPlayer(player.name).setConnection(null);
		}
	}

	/**
	 * @return representation of lobby
	 */
	public synchronized String updatePlayer() {
		return lobby.toJSON().toString();
	}

	/**
	 * Sends a broadcast message to all players to update their model
	 * @return list of threads that ned to be joined if there is need of being sure that all disconnected clients have been removed
	 */
	public synchronized List<Thread> pingALl() {
		List<Thread> toJoin= new ArrayList<>();
		players.forEach(p-> toJoin.add(p.ping()));

		return toJoin;

	}

	/**
	 *	Reconnects a player
	 * @param player to reconnect
	 */
	public synchronized void reconnectPlayer(PlayerConnection player){
		System.out.println("Reconnecting "+player.name);
		if(getDisconnectedPlayers().contains(player.name)) {
			player.setServerLobby(this);
			players.add(player);
			disconnectedPlayers.remove(player.getName());
			lobby.reconnect(player);

		} else {
			getMatchByPlayerConnection(player).getPlayer(player.name).setConnection(player);
		}
	}

	/**
	 *	set correspondent player object state to ready
	 * @param player player connection object of the player to set ready
	 */
	public synchronized void setPlayerReady(PlayerConnection player) {
		lobby.getPlayer(player).setReady(true);
	}
}