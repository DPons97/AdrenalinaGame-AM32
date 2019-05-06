package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.MatchAlreadyStartedException;
import it.polimi.ingsw.custom_exceptions.NotEnoughPlayersException;
import it.polimi.ingsw.custom_exceptions.PlayerNotExistsException;
import it.polimi.ingsw.custom_exceptions.PlayerNotReadyException;
import it.polimi.ingsw.server.model.AdrenalinaMatch;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.Player;

/**
 *
 */
public class MatchController {

	/**
	 * Match that is controlled
	 */
	private AdrenalinaMatch match;

	/**
	 * Lobby of this server
	 */
	private Lobby serverLobby;

	/**
	 * Default constructor
	 */
	public MatchController(AdrenalinaMatch toControl, Lobby serverLobby) {
		this.match = toControl;
		this.serverLobby = serverLobby;
	}

	/**
	 * @return controlled match
	 */
	public AdrenalinaMatch getMatch() { return match; }

	/**
	 * Get reference to player that has defined nickname
	 * @param nickname to find
	 * @return reference to player found
	 */
	public Player getPlayer(String nickname) {
		for (Player p : match.getPlayers()) {
			if (p.getNickname().equals(nickname)) return p;
		}
		return null;
	}

	/**
	 * Start controlling match
	 * @throws PlayerNotReadyException if there is at least one player that is not ready to start match
	 */
	public void startMatch() throws PlayerNotReadyException, NotEnoughPlayersException, MatchAlreadyStartedException {
		for (Player player : match.getPlayers()) {
			if (!player.isReadyToStart()) throw new PlayerNotReadyException();
		}
		match.startMatch();
		// TODO: Start turn
	}

	/**
	 *
	 */
	public void beginTurn() {
		// TODO: Manage turn logic
	}

	/**
	 * Make player leave match and go back to server lobby
	 * @param playerLeaving player that leaves
	 */
	public void backToLobby(PlayerConnection playerLeaving) throws MatchAlreadyStartedException, NotEnoughPlayersException, PlayerNotExistsException {
		if (playerLeaving == match.getPlayers().get(0).getConnection()) {
			// Destroy match
			for (Player p : match.getPlayers()) {
				if (p.getNickname().equals(playerLeaving.getName())) {
					match.kickPlayer(p);
					serverLobby.addPlayer(p);
				}
			}
			// TODO: Destroy AdrenalinaMatch
		} else {
			// Make player leave match without destroying it
			for (Player p : match.getPlayers()) {
				if (p.getNickname().equals(playerLeaving.getName())) {
					match.kickPlayer(p);
					serverLobby.addPlayer(p);
				}
			}
		}
	}
}