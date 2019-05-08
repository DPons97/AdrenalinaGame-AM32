package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.MatchAlreadyStartedException;
import it.polimi.ingsw.custom_exceptions.NotEnoughPlayersException;
import it.polimi.ingsw.custom_exceptions.PlayerNotExistsException;
import it.polimi.ingsw.custom_exceptions.PlayerNotReadyException;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
	 * @return this match's host's nickname
	 */
	public String getHostName() { return match.getPlayers().get(0).getNickname(); }

	/**
	 * Start controlling match
	 * @throws PlayerNotReadyException if there is at least one player that is not ready to start match
	 */
	public void startMatch() throws PlayerNotReadyException, NotEnoughPlayersException, MatchAlreadyStartedException, PlayerNotExistsException {
		for (Player player : match.getPlayers()) {
			if (!player.isReadyToStart()) throw new PlayerNotReadyException();
		}
		match.startMatch();

		int currentTurn;
		Player currentPlayer;

		while (match.getMatchState() != MatchState.ENDED) {
			// Start round logic
			currentTurn = match.getTurn();
			currentPlayer = match.getTurnPlayer();

			if (currentTurn/match.getPlayers().size() == 0) {
				// Manage first turn

				// First spawn:
				// Draw 2 powerups from deck
				List<Powerup> spawnCards = new ArrayList<>();
				spawnCards.add(match.getPowerupDeck().drawCard());
				spawnCards.add(match.getPowerupDeck().drawCard());

				// Make player choose one to keep
				Powerup toKeep = currentPlayer.getConnection().choosePowerup(spawnCards);
				currentPlayer.addPowerup(toKeep);
				spawnCards.remove(toKeep);

				// Spawn player in right cell
				Color spawnColor;
				spawnColor = resourceToColor(spawnCards.get(0).getBonusResource());
				for (SpawnCell spawn : match.getBoardMap().getSpawnPoints()) {
					if (spawn.getColor() == spawnColor) currentPlayer.respawn(spawn);
				}

				// Discard other card
				match.getPowerupDeck().discardCard(spawnCards.get(0));

				// Start usual turn
				beginTurn(currentPlayer);
				resolveDeaths(currentPlayer);

				// TODO: Repopulate Map

				match.nextTurn();
			} else {
				beginTurn(currentPlayer);
				resolveDeaths(currentPlayer);

				// TODO: Repopulate Map

				match.nextTurn();
			}
		}


	}

	/**
	 * @param resource to convert
	 * @return corresponding color. Null if resource's color is not inside {RED_BOX, BLUE_BOX, YELLOW_BOX}
	 */
	private Color resourceToColor(Resource resource) {
		Color spawnColor;
		switch (resource) {
			case RED_BOX:
				spawnColor = Color.RED;
				break;
			case BLUE_BOX:
				spawnColor = Color.BLUE;
				break;
			case YELLOW_BOX:
				spawnColor = Color.YELLOW;
				break;
			default:
				spawnColor = null;
		}
		return spawnColor;
	}

	/**
	 *	Game turn logic (Run/Pick/Shoot + reloading)
	 */
	public void beginTurn(Player playing) {

	}

	/**
	 * Resolve deaths occurred during current turn
	 * @param currentPlayer player who has just finished his turn
	 * @throws PlayerNotExistsException if player doesn't exists inside match
	 */
	private void resolveDeaths(Player currentPlayer) throws PlayerNotExistsException {
		int deadPlayers = 0;
		for (Player p : match.getPlayers()) {
			if (p.isDead()) {
				// First to damage deadPlayer gets 1 point (First Blood)
				p.getDmgPoints().get(0).addScore(1);
				rewardPlayers(p.getDmgPoints(), p.getReward());

				// Add death to match, counting overkill if present (Killshot, Death and Overkill)
				match.addDeath(p.getDmgPoints().get(p.getDmgPoints().size()-1), p.isOverkilled());

				// Mark player who overkilled deadPlayer (Revenge mark)
				if (p.isOverkilled()) p.getDmgPoints().get(p.getDmgPoints().size() - 1).takeMark(p);

				deadPlayers++;
			}
		}

		// Manage double kill: give +1 additional point to killer
		if (deadPlayers > 1)  currentPlayer.addScore(1);
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
	 * Distribution of victory points based on a defined reward list
	 * @param track to an analyze
	 * @param rewards current reward
	 */
	private void rewardPlayers(List<Player> track, List<Integer> rewards) {
		/* This list contains a reference to every player in match.
			Every player's index is equal to the number of damage he dealt to the dead player
			Multiple players can share the same index */
		List<List<Player>> toReward = new ArrayList<>();

		for (Player matchPlayer : match.getPlayers()) {
			if(toReward.get(Collections.frequency(track, matchPlayer)) == null)
				toReward.set(Collections.frequency(track, matchPlayer), new ArrayList<>());

			toReward.get(Collections.frequency(track, matchPlayer)).add(matchPlayer);
		}

		int reward = 0;
		for (int i = toReward.size()-1; i > 0; i-- ) {
			List<Player> damagers = toReward.get(i);

			if (damagers != null) {
				if (damagers.size() == 1) {
					damagers.get(0).addScore(rewards.get(reward));
				} else {
					for (Player damager : track) {
						if (damagers.contains(damager)) {
							damager.addScore(rewards.get(reward));
							damagers.remove(damager);
							i++;
							break;
						}
					}
				}
				reward++;
			}
		}
	}
}