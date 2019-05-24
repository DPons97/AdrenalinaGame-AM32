package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton Turn class
 */
public class Turn {
    /**
     * Match where turn is ran
     */
    private AdrenalinaMatch match;

    /**
     * Players that already played their frenzy turn
     */
    private List<Player> playedFrenzy = new ArrayList<>();

    /**
     * Default constructor
     * @param match where this turn takes place
     */
    public Turn(AdrenalinaMatch match) {
        this.match = match;
        List<Player> playedFrenzy = new ArrayList<>();
    }

    /**
     * Start new turn
     */
    public void startNewTurn() {
        if (match.getMatchState() == MatchState.ENDED) return;

        // Get current playing player and check that he's not disconnected
        Player currentPlayer = match.getTurnPlayer();
        if (currentPlayer.getConnection() == null) return;

        if (match.getMatchState() != MatchState.FRENZY_TURN) {
            // Start round logic
            // Manage first turn
            if (match.getTurn()/match.getPlayers().size() == 0) respawnPlayer(currentPlayer);

            // Start usual turn, or manage frenzy
            beginTurn(currentPlayer);

            try {
                resolveDeaths(currentPlayer);
            } catch (PlayerNotExistsException e) {
                e.printStackTrace();
            }
            repopulateMap();
        } else {
            frenzyTurn(currentPlayer, playedFrenzy);

            try {
                resolveDeaths(currentPlayer);
            } catch (PlayerNotExistsException e) {
                e.printStackTrace();
            }

            repopulateMap();

            if (playedFrenzy.size() == match.getPlayers().size()){
                // All player finished their frenzy turn. End game
                match.setMatchState(MatchState.ENDED);
            }
        }

        // Update player's model after turn
        updatePlayers();
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
        int remainingActions = 2;

        while (remainingActions > 0) {
            // Ask player what to do (RUN, PICK, SHOOT)
            TurnAction currentAcion = playing.getConnection().selectAction();

            switch (currentAcion) {
                case MOVE:
                    if (movePlayer(playing, 3)) remainingActions--;
                    break;
                case PICK:
                    // PICK management

                    // Get cells that can be picked doing from 0 to 1 or 2 movements
                    if (playerPick(playing)) remainingActions--;
                    break;
                case SHOOT:
                    // SHOOT management

                    // Get all loaded weapons and pick one
                    List<Weapon> loaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
                    WeaponSelection pickedWeapon = playing.getConnection().shoot(loaded);

                    if (executeShooting(playing, pickedWeapon)) remainingActions--;
                    break;
            }
            updatePlayers();
        }

        // RELOAD management
        reloadWeapon(playing);
    }

    /**
     *	Game turn logic during frenzy (Run/Pick/Shoot + reloading)
     * @param playing player
     * @param playedFrenzy players that already played their frenzy turn
     */
    public void frenzyTurn(Player playing,List<Player> playedFrenzy) {
        int remainingActions = 2;
        boolean playingBeforeFirst = true;

        // Calculate available actions
        if (playing.equals(match.getFirstPlayer()) ||
                playedFrenzy.contains(match.getFirstPlayer())) {
            playingBeforeFirst = false;
            remainingActions = 1;
        }

        while (remainingActions > 0) {
            // Ask player what to do (RUN, PICK, SHOOT)
            TurnAction currentAcion = playing.getConnection().selectAction();

            if (playingBeforeFirst) {
                switch (currentAcion) {
                    case MOVE:
                        if (movePlayer(playing, 4)) remainingActions--;
                        break;
                    case PICK:
                        if (frenzyPick(playing, true)) remainingActions--;
                        break;
                    case SHOOT:
                        // Move 0 or 1 cell
                        List<Cell> canMove = playing.getCellsToMove(1);
                        canMove.add(playing.getPosition());
                        playing.getConnection().selectCell(canMove);

                        // Ask reload
                        reloadWeapon(playing);

                        // Get all loaded weapons and pick one
                        List<Weapon> loaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
                        WeaponSelection pickedWeapon = playing.getConnection().shoot(loaded);

                        if (executeShooting(playing, pickedWeapon)) remainingActions--;
                        break;
                }
            } else {
                switch (currentAcion) {
                    case PICK:
                        // (Useful only if turrets mode are implemented)
                        // Get cells that can be picked doing from 0 to 1 or 2 movements
                        if (frenzyPick(playing, false)) remainingActions--;
                        break;
                    case SHOOT:
                        // Move 0, 1 or 2 cell
                        List<Cell> canMove = playing.getCellsToMove(2);
                        canMove.add(playing.getPosition());
                        playing.getConnection().selectCell(canMove);

                        // Ask reload
                        reloadWeapon(playing);

                        // Get all loaded weapons and pick one
                        List<Weapon> loaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
                        WeaponSelection pickedWeapon = playing.getConnection().shoot(loaded);

                        if (executeShooting(playing, pickedWeapon)) remainingActions--;
                        break;
                    default:
                }
            }
            updatePlayers();
        }
        playedFrenzy.add(playing);
    }

    /**
     * Player's movement logic
     * @param playing player
     * @param maxMoves maximum movements player can execute
     * @return True if movement is successful
     */
    private boolean movePlayer(Player playing, int maxMoves) {
        // MOVE management
        // Select one of cells at 1, 2, 3 or 4 distance
        List<Cell> selectable = playing.getCellsToMove(maxMoves);
        Cell destination = playing.getConnection().selectCell(selectable);
        if (destination != null) {
            playing.move(destination);
            return true;
        }
        return false;
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

    /** Player pick logic
     * @param playing player
     * @return true if pick was successful
     */
    private boolean playerPick(Player playing) {
        // Get cells that can be picked doing from 0 to 1 or 2 movements
        List<Cell> canPick = playing.getCellsToMove((playing.getDmgPoints().size() >= 3) ? 2 : 1);

        return grabSomething(playing, canPick);
    }

    /** Player pick logic during frenzy
     * @param playing player
     * @return true if pick was successful
     */
    private boolean frenzyPick(Player playing, boolean playingBeforeFirst) {
        // PICK management during frenzy

        // Get cells that can be picked doing from 0 to 1 or 2 movements
        List<Cell> canPick;
        boolean bonusMovement = !playing.isFrenzyPlayer() && (playing.getDmgPoints().size() >= 3);

        if (playingBeforeFirst) canPick = playing.getCellsToMove((bonusMovement) ? 3 : 2);
        else canPick = playing.getCellsToMove((bonusMovement) ? 4 : 3);

        return grabSomething(playing, canPick);
    }

    /**
     * Grab weapon or ammo from a cell
     * @param playing player
     * @param picked cell
     * @return true if grab was successful
     */
    private boolean grabAmmo(Player playing, Cell picked) {
        // Pick ammo
        for (AmmoCell cell : match.getBoardMap().getAmmoPoints()) {
            if (cell.getCoordX() == picked.getCoordX() && cell.getCoordY() == picked.getCoordY()) {
                playing.pickAmmo(cell);
                return true;
            }
        }
        return false;
    }

    /**
     * Grab a weapon
     * @param playing player
     * @param picked weapon
     * @return true if pick was successful
     */
    private boolean grabWeapon(Player playing, Cell picked) {
        // Find right spawn cell
        SpawnCell pickedSpawn = match.getBoardMap().getSpawnCell(picked);
        if (pickedSpawn == null) return false;

        // Pick weapon
        WeaponSelection pickedWeapon = playing.getConnection().chooseWeapon(pickedSpawn.getWeapons());

        // Stop action if player can't pay weapon's cost with chosen powerups
        if (!playing.canPay(pickedWeapon.getWeapon().getCost(), pickedWeapon.getPowerups())) return false;

        try {
            playing.pickWeapon(pickedWeapon.getWeapon());
            pickedSpawn.removeWeapon(pickedWeapon.getWeapon());
            return true;
        } catch (InventoryFullException invFullE) {
            // Player has too many weapons. Ask for one to change
            WeaponSelection toChange = playing.getConnection().chooseWeapon(playing.getWeapons());
            if (toChange == null) return false;

            try {
                // Change chosen weapons
                playing.dropWeapon(toChange.getWeapon());
                playing.pickWeapon(pickedWeapon.getWeapon());
                pickedSpawn.removeWeapon(pickedWeapon.getWeapon());
                pickedSpawn.addWeapon(toChange.getWeapon());
                return true;
            } catch (NoItemInInventoryException | InventoryFullException | InsufficientResourcesException noItemE) {
                noItemE.printStackTrace();
            }
        } catch (NoItemInInventoryException | InsufficientResourcesException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Make player grab something
     * @param playing player
     * @param canPick list of cell player can reach
     * @return true if grab was successful
     */
    private boolean grabSomething(Player playing, List<Cell> canPick) {
        // PICK management
        canPick.add(playing.getPosition());
        removeEmptyCells(canPick);

        // Player choosing and grabbing
        Cell pickedCell = playing.getConnection().selectCell(canPick);

        if ((pickedCell.isSpawn() && grabWeapon(playing, pickedCell)) || grabAmmo(playing, pickedCell)) {
            // Move player to picked cell
            playing.move(pickedCell);
            return true;
        }
        return false;
    }

    /**
     * Try to execute shooting
     * @param playing player
     * @param pickedWeapon weapon selection made by player
     * @return True if shooting was successful
     */
    private boolean executeShooting(Player playing, WeaponSelection pickedWeapon) {
        List<Integer> effectIds = pickedWeapon.getEffectID();

        // Check all chosen effects are valid
        if (!pickedWeapon.getWeapon().isValidActionSequence(effectIds)) return false;

        // Check player can pay all the effects' cost
        List<Resource> totalCost = new ArrayList<>();
        for (Integer id : effectIds) {
            totalCost.addAll(pickedWeapon.getWeapon().getAction(id).getCost());
        }
        if (!playing.canPay(totalCost, pickedWeapon.getPowerups())) return false;

        // IDs' order matter!
        try {
            for (Integer id : effectIds) {
                playing.shoot(pickedWeapon.getWeapon(), id, pickedWeapon.getPowerups());
            }
            return true;
        } catch (RequirementsNotMetException | InsufficientResourcesException | NoItemInInventoryException | WeaponNotLoadedException e) {
            e.printStackTrace();
        }
        return false;
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
            if (playing.canPay( toReload.getWeapon().getCost(), toReload.getPowerups())) {
                // Do reload
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
                if (!p.isFrenzyPlayer()) p.getDmgPoints().get(0).addScore(1);
                match.rewardPlayers(p.getDmgPoints(), p.getReward());

                Player killshot = p.getDmgPoints().get(p.getDmgPoints().size() - 1);
                // Add death to match, counting overkill if present (Killshot, Death and Overkill)
                match.addDeath(killshot, p.isOverkilled());

                // Mark player who overkilled deadPlayer (Revenge mark)
                if (p.isOverkilled()) killshot.takeMark(p);

                // Respawn player
                respawnPlayer(currentPlayer);

                // If frenzy enabled, flip dead player's board
                if (match.getMatchState() == MatchState.FRENZY_TURN) p.enableFrenzy();

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
    public void updatePlayers() {
        match.getPlayers().forEach(p-> p.getConnection().updateMatch(match));
    }
}