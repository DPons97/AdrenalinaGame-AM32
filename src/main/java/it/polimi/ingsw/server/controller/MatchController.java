package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.MatchAlreadyStartedException;
import it.polimi.ingsw.custom_exceptions.NotEnoughPlayersException;
import it.polimi.ingsw.custom_exceptions.PlayerNotExistsException;
import it.polimi.ingsw.custom_exceptions.PlayerNotReadyException;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.Collections;
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

		Player currentPlayer = match.getTurnPlayer();

		while (match.getMatchState() != MatchState.ENDED) {
			if (match.getMatchState() == MatchState.FRENZY_TURN) break;

			// Start round logic
			currentPlayer = match.getTurnPlayer();

			// Manage first turn
			if (match.getTurn()/match.getPlayers().size() == 0) respawnPlayer(currentPlayer);

			// Start usual turn, or manage frenzy
			beginTurn(currentPlayer);

			resolveDeaths(currentPlayer);
			repopulateMap();

			match.nextTurn();
		}

		// Frenzy initialization
		Player lastFrenzyPlayer = currentPlayer;
		currentPlayer = match.getTurnPlayer();

		// All players without damage change their rewards to frenzy, and reset their deaths to 0
		for (Player p : match.getPlayers()) {
			if (p.getDmgPoints().isEmpty()) p.enableFrenzy();
		}

		// Frenzy turns
		while (currentPlayer != lastFrenzyPlayer) {
			currentPlayer = match.getTurnPlayer();

			frenzyTurn(currentPlayer);
			resolveDeaths(currentPlayer);
			repopulateMap();

			match.nextTurn();
		}

		// TODO Do last frenzy turn (last player has to play)


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
	 *	Game turn logic (Run/Pick/Shoot + reloading)
	 */
	public void beginTurn(Player playing) {
		// TODO Manage turn as usual
        // Ask player what to do (RUN, PICK, SHOOT)
        TurnAction currentAcion = playing.getConnection().selectAction();

        switch (currentAcion) {
            case MOVE:
                // MOVE management
                // Select one of cells at 1, 2 or 3 distance
                List<Cell> selectable = playing.getCellsToMove();
                playing.move(playing.getConnection().selectCell(selectable));
                break;
            case PICK:
                // PICK management

                break;
            case SHOOT:
                // SHOOT management

                break;
        }
	}

	/**
	 *	Game turn logic during frenzy (Run/Pick/Shoot + reloading)
	 */
	public void frenzyTurn(Player playing) {
		// TODO Manage turn during frenzy
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

				// Respawn player
				respawnPlayer(currentPlayer);

				deadPlayers++;
			}
		}

		// Manage double kill: give +1 additional point to killer
		if (deadPlayers > 1)  currentPlayer.addScore(1);
	}

	/**
	 * Respawn mechanics
	 * @param currentPlayer that is playing
	 */
	private void respawnPlayer(Player currentPlayer) {
		// Draw a powerups from deck. Draw 2 if it's first player's turn
		List<Powerup> spawnCards = new ArrayList<>();

		if (match.getTurn()/match.getPlayers().size() == 0 && currentPlayer.getDeaths() == 0) spawnCards.add(match.getPowerupDeck().drawCard());
		spawnCards.add(match.getPowerupDeck().drawCard());

		// Add cards that player have. During first turn this will add nothing
		spawnCards.addAll(currentPlayer.getPowerups());

		// Make player choose one to discard. This card's color is the spawn's cell color
		Powerup toDiscard = currentPlayer.getConnection().choosePowerup(spawnCards);
		spawnCards.remove(toDiscard);

		// Update player's powerups
		for (Powerup powerup : spawnCards) {
			if (!currentPlayer.getPowerups().contains(powerup)) currentPlayer.addPowerup(powerup);
		}


		// Spawn player in right cell
		Color spawnColor;
		spawnColor = resourceToColor(toDiscard.getBonusResource());
		for (SpawnCell spawn : match.getBoardMap().getSpawnPoints()) {
			if (spawn.getColor() == spawnColor) currentPlayer.respawn(spawn);
		}

		// Discard chosen card
		match.getPowerupDeck().discardCard(toDiscard);
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

	/**
	 * Replace ammos and weapons in map
	 */
	private void repopulateMap() {
		// Replace taken ammo in map
		match.getBoardMap().initAmmoCells(match.getAmmoDeck());

		// Replace taken weapons in map
		match.getBoardMap().initSpawnCells(match.getWeaponDeck());
	}

	/**
	 * Sends a broadcast message to all players to update their model
	 */
	private void updatePlayers() {
		match.getPlayers().forEach(p-> p.getConnection().updateMatch(match));
	}
}