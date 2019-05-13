package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
		int remainingActions = 2;

		while (remainingActions > 0) {
			// Ask player what to do (RUN, PICK, SHOOT)
			TurnAction currentAcion = playing.getConnection().selectAction();

			switch (currentAcion) {
				case MOVE:
					// MOVE management
					// Select one of cells at 1, 2 or 3 distance
					List<Cell> selectable = playing.getCellsToMove(3);
					Cell destination = playing.getConnection().selectCell(selectable);
					if (destination != null) {
						playing.move(destination);
						remainingActions--;
					}
					break;
				case PICK:
					// PICK management

					// Get cells that can be picked doing from 0 to 1 or 2 movements
					List<Cell> canPick = (playing.getDmgPoints().size() >= 3) ?
							playing.getCellsToMove(2) :
							playing.getCellsToMove(1);
					canPick.add(playing.getPosition());
					removeEmptyCells(canPick);

					// Player choosing and grabbing
					Cell pickedCell = playing.getConnection().selectCell(canPick);

					if (pickedCell.isSpawn()) remainingActions = grabWeapon(playing, remainingActions, pickedCell);
					else remainingActions = grabAmmo(playing, remainingActions, pickedCell);

					// Move player to picked cell
					playing.move(pickedCell);
					break;
				case SHOOT:
					// SHOOT management

					// Get all loaded weapons and pick one
					List<Weapon> loaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
					WeaponSelection pickedWeapon = playing.getConnection().shoot(loaded);

					remainingActions = executeShooting(playing, remainingActions, pickedWeapon);
					break;
			}
		}

		// RELOAD management
		reloadWeapon(playing);
	}

	/**
	 *	Game turn logic during frenzy (Run/Pick/Shoot + reloading)
	 */
	public void frenzyTurn(Player playing) {
		// TODO Manage turn during frenzy
	}

	/**
	 * Remove cells that have no weapons or ammos from a given list
	 * @param canPick list of cells to filter
	 */
	private void removeEmptyCells(List<Cell> canPick) {
		// Remove cells that are empty (ammo and weapons)
		List<Cell> emptyCells = new ArrayList<>();
		for (Cell pickable : canPick) {
			if (pickable.isSpawn()) {
				for (SpawnCell cell : match.getBoardMap().getSpawnPoints()) {
					if (cell.getCoordX() == pickable.getCoordX() && cell.getCoordY() == pickable.getCoordY() && cell.getWeapons().isEmpty()) emptyCells.add(pickable);
				}
			} else {
				for (AmmoCell cell : match.getBoardMap().getAmmoPoints()) {
					if (cell.getCoordX() == pickable.getCoordX() && cell.getCoordY() == pickable.getCoordY() && cell.getResource() == null) emptyCells.add(pickable);
				}
			}
		}
		canPick.removeAll(emptyCells);
	}

	/**
	 * Grab weapon or ammo from a cell
	 * @param playing player
	 * @param remainingActions number of actions this player still has during this turn
	 * @param picked cell
	 * @return new remaining actions
	 */
	private int grabAmmo(Player playing, int remainingActions, Cell picked) {
		// Pick ammo
		for (AmmoCell cell : match.getBoardMap().getAmmoPoints()) {
			if (cell.getCoordX() == picked.getCoordX() && cell.getCoordY() == picked.getCoordY()) {
				playing.pickAmmo(cell);
				remainingActions--;
			}
		}
		return remainingActions;
	}

	private int grabWeapon(Player playing, int remainingActions, Cell picked) {
		// Find right spawn cell
		SpawnCell pickedSpawn = match.getBoardMap().getSpawnCell(picked);
		if (pickedSpawn == null) return remainingActions;

		// Pick weapon
		WeaponSelection pickedWeapon = playing.getConnection().chooseWeapon(pickedSpawn.getWeapons());

		// Stop action if player can't pay weapon's cost with chosen powerups
		if (!playing.canPay(pickedWeapon.getWeapon().getCost(), pickedWeapon.getPowerups())) return remainingActions;

		try {
			playing.pickWeapon(pickedWeapon.getWeapon());
			pickedSpawn.removeWeapon(pickedWeapon.getWeapon());
			remainingActions--;
		} catch (InventoryFullException invFullE) {
			// Player has too many weapons. Ask for one to change
			WeaponSelection toChange = playing.getConnection().chooseWeapon(playing.getWeapons());
			if (toChange == null) return remainingActions;

			try {
				// Change chosen weapons
				playing.dropWeapon(toChange.getWeapon());
				playing.pickWeapon(pickedWeapon.getWeapon());
				pickedSpawn.removeWeapon(pickedWeapon.getWeapon());
				pickedSpawn.addWeapon(toChange.getWeapon());
				remainingActions--;
			} catch (NoItemInInventoryException | InventoryFullException | InsufficientResourcesException noItemE) {
				noItemE.printStackTrace();
			}
		} catch (NoItemInInventoryException | InsufficientResourcesException e) {
			e.printStackTrace();
		}

		return remainingActions;
	}

	/**
	 * Try to execute shooting
	 * @param playing player
	 * @param remainingActions during current turn
	 * @param pickedWeapon weapon selection made by player
	 * @return new actions remaining to player during this turn
	 */
	private int executeShooting(Player playing, int remainingActions, WeaponSelection pickedWeapon) {
		List<Integer> effectIds = pickedWeapon.getEffectID();

		// Check all chosen effects are valid
		if (!pickedWeapon.getWeapon().isValidActionSequence(effectIds)) return remainingActions;

		// Check player can pay all the effects' cost
		List<Resource> totalCost = new ArrayList<>();
		for (Integer id : effectIds) {
			totalCost.addAll(pickedWeapon.getWeapon().getAction(id).getCost());
		}
		if (!playing.canPay(totalCost, pickedWeapon.getPowerups())) return remainingActions;

		// IDs' order matter!
		try {
			for (Integer id : effectIds) {
				playing.shoot(pickedWeapon.getWeapon(), id, pickedWeapon.getPowerups());
			}
			remainingActions--;
		} catch (RequirementsNotMetException | InsufficientResourcesException | NoItemInInventoryException | WeaponNotLoadedException e) {
			e.printStackTrace();
		}
		return remainingActions;
	}

	/**
	 * Reload logic
	 * @param playing player
	 */
	private void reloadWeapon(Player playing) {
		List<Weapon> canBeReloaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
		while (!canBeReloaded.isEmpty()) {
			WeaponSelection toReload = playing.getConnection().reload(canBeReloaded);
			if (toReload.getWeapon() == null) return;

			// Stop action if player can't pay reload cost with chosen powerups
			if (!playing.canPay( toReload.getWeapon().getCost(), toReload.getPowerups())) {
				// Otherwise do reload
				try {
					playing.reload(toReload.getWeapon(), toReload.getPowerups());
				} catch (NoItemInInventoryException | InsufficientResourcesException e) {
					e.printStackTrace();
				}
			}

			canBeReloaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
		}
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

				Player killshot = p.getDmgPoints().get(p.getDmgPoints().size()-1);
				// Add death to match, counting overkill if present (Killshot, Death and Overkill)
				match.addDeath(killshot, p.isOverkilled());

				// Mark player who overkilled deadPlayer (Revenge mark)
				if (p.isOverkilled()) killshot.takeMark(p);

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