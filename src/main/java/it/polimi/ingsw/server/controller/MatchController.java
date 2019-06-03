package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.model.*;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 */
public class MatchController {

	/**
	 * Loading/Match countdown time
	 */
	static private int firstWait = 5;

	/**
	 * Maximum time match waits all player to be connected
	 */
	static private int waitingTime = 120;

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
	 * @return this match's host's nickname
	 */
	public String getHostName() { return match.getPlayers().get(0).getNickname(); }

	/**
	 * Start controlling match
	 * @return List representing leaderboard
	 * @throws PlayerNotReadyException if there is at least one player that is not ready to start match
	 */
	public List<Player> startMatch() throws PlayerNotReadyException, NotEnoughPlayersException, MatchAlreadyStartedException {
		for (Player player : match.getPlayers()) {
			if (!player.isReadyToStart()) throw new PlayerNotReadyException();
		}

		// Start model's side
		match.startMatch();

		// Make players load
		loadAndStart();

		// Create singleton of turn
		Turn playerTurn = new Turn(match);

		// Wait 5 seconds
		try {
			TimeUnit.SECONDS.sleep(firstWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Start turns logic
		while (match.getMatchState() != MatchState.ENDED) {
			Player currentPlayer = match.getTurnPlayer();
			ExecutorService turnExecutor = Executors.newSingleThreadExecutor();
			turnExecutor.submit(playerTurn::startNewTurn);
			turnExecutor.shutdown();
			try {
				turnExecutor.awaitTermination(match.getTurnDuration(), TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!turnExecutor.isTerminated()) {
				currentPlayer.getConnection().alert("Time's over");
				turnExecutor.shutdownNow();
			}

			match.nextTurn();
			playerTurn.updatePlayers();
		}

		return finalScore();
	}

	/**
	 * Make player load,
	 * synchronize server and players,
	 * start match
	 */
	private void loadAndStart() {
		// Make clients start loading
		ExecutorService loadingExecutor = Executors.newSingleThreadExecutor();

		loadingExecutor.submit(() -> {
			for (Player p : match.getPlayers()) {
				p.setReady(false);
				p.getConnection().beginLoading();
			}
		});
		loadingExecutor.shutdown();
		try {
			loadingExecutor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!loadingExecutor.isTerminated()) {
			// Send all client message to stop loading / game interrupted
			for (Player p : match.getPlayers())
				if (p.getConnection() != null) p.getConnection().alert("Game interrupted");
		}
		loadingExecutor.shutdownNow();

		// Wait players to load (max 120s)
		boolean allReady = false;
		long currTimeStamp = System.currentTimeMillis();
		while (System.currentTimeMillis() - currTimeStamp <= waitingTime) {
			allReady = true;
			for (Player p : match.getPlayers()) {
				if (!p.isReadyToStart()) {
					allReady = false;
					break;
				}
			}
			if (allReady) break;
		}
		if (!allReady) {
			for (Player p : match.getPlayers()) p.getConnection().alert("Game interrupted");
		} else {
			// Send all client message that all player connected and match is starting
			for (Player p : match.getPlayers())
				if (p.getConnection() != null) p.getConnection().beginMatch();
		}
	}

	/**
	 * Make player leave match and go back to server lobby
	 * @param playerLeaving player that leaves
	 */
	public void backToLobby(PlayerConnection playerLeaving) throws MatchAlreadyStartedException, NotEnoughPlayersException, PlayerNotExistsException {
		if (playerLeaving == match.getPlayers().get(0).getConnection()) {
			//  Move all players in lobby
			for (Player p : match.getPlayers()) {
				match.kickPlayer(p);
				serverLobby.addPlayer(p);
				p.getConnection().setCurrentMatch(null);
			}

			// All players kicked from match. Destroying
			match.setMatchState(MatchState.ENDED);
			match = null;
			serverLobby.destroyMatch(this);
		} else {
			// Make player leave match without destroying it
			for (Player p : match.getPlayers()) {
				if (p.getNickname().equals(playerLeaving.getName())) {
					match.kickPlayer(p);
					serverLobby.addPlayer(p);
					p.getConnection().setCurrentMatch(null);
				}
			}
		}
	}

	/**
	 * Resolve final scoring of not dead players
	 */
	private List<Player> finalScore() {
		// Final scoring
		match.setMatchState(MatchState.FINAL_SCORING);
		for (Player p : match.getPlayers()) {
			// First to damage deadPlayer gets 1 point (First Blood)
			if (!p.isFrenzyPlayer()) p.getDmgPoints().get(0).addScore(1);
			match.rewardPlayers(p.getDmgPoints(), p.getReward());
		}

		// Give points to players in death track
		match.rewardPlayers(match.getDeathTrack(), match.getRewards());

		// Final leaderboard
		List<Player> leaderBoard = new ArrayList<>(match.getPlayers());
		leaderBoard.sort(Comparator.comparing(Player::getScore));

		// Manage player with same score
		boolean modifiedLeaderboard;
		int i = 0;
		while (i < leaderBoard.size() - 1) {
			modifiedLeaderboard = false;
			Player p1 = leaderBoard.get(i);
			Player p2 = leaderBoard.get(i+1);
			if (p1.getScore() == p2.getScore() &&
					match.getDeathTrack().indexOf(p1) > match.getDeathTrack().indexOf(p2)) {
				// Swap players in leaderboard
				Collections.swap(leaderBoard, i, i+1);
				modifiedLeaderboard = true;
			}
			if (modifiedLeaderboard) i = 0;
			else i++;
		}
		match.setMatchState(MatchState.ENDED);
		return leaderBoard;
	}

	/**
	 *	set correspondent player object state to ready
	 * @param player player connection object of the player to set ready
	 */
	public synchronized void setPlayerReady(PlayerConnection player) {
		getPlayer(player.getName()).setReady(true);
		match.getPlayers().forEach(p->p.getConnection().updateMatch(match));
	}

}