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
    private List<Player> playedFrenzy;

    private boolean skipTurn;

    /**
     * Default constructor
     * @param match where this turn takes place
     */
    public Turn(AdrenalinaMatch match) {
        this.match = match;
        playedFrenzy = new ArrayList<>();
    }

    /**
     * Start new turn
     */
    public void startNewTurn() {
        if (match.getMatchState() == MatchState.ENDED) return;

        // Get current playing player and check that he's not disconnected
        Player currentPlayer = match.getTurnPlayer();
        if (currentPlayer.getConnection() == null) return;

        if (!match.getMatchState().equals(MatchState.FRENZY_TURN)) {
            // Start round logic
            // Manage first turn
            if (match.getTurn()/match.getPlayers().size() == 0) respawnPlayer(currentPlayer);

            repopulateMap();

            // Start usual turn, or manage frenzy
            beginTurn(currentPlayer);

            try {
                resolveDeaths(currentPlayer);
            } catch (PlayerNotExistsException e) {
                e.printStackTrace();
            }
            repopulateMap();
        } else {
            System.out.println("STARTING FRENZY TURN");
            repopulateMap();
            frenzyTurn(currentPlayer);

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

    public boolean isSkipTurn() {
        return skipTurn;
    }

    public void setSkipTurn(boolean skipTurn) {
        this.skipTurn = skipTurn;
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

        for (Player p : match.getPlayers())
            p.setDamagedThisTurn(false);

        while (remainingActions > 0) {
            // if player disconnected, skip turn
            if (playing.getConnection() == null) return;

            if(isSkipTurn()){
                skipTurn =false;
                return;
            }
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
                    if (playing.getDmgPoints().size() >= 6) {
                        // Move 0 or 1 cell
                        List<Cell> canMove = playing.getCellsToMove(1);
                        canMove.add(playing.getPosition());
                        playing.move(playing.getConnection().selectCell(canMove));
                    }

                    // Get all loaded weapons and pick one
                    List<Weapon> loaded = playing.getWeapons().stream().filter(Weapon::isLoaded).collect(Collectors.toList());
                    if(!loaded.isEmpty()) {
                        WeaponSelection pickedWeapon = playing.getConnection().shoot(loaded);

                        if (executeShooting(playing, pickedWeapon)) {
                            remainingActions--;
                        }
                    }
                    break;
                case POWERUP:
                    // POWERUP EFFECT management

                    // Get all powerups that can be used during common turn
                    List<Powerup> selectables = playing.getPowerups().stream()
                            .filter(powerup -> (powerup.getName().equals("Newton") || powerup.getName().equals("Teleporter")))
                            .collect(Collectors.toList());

                    usePowerupEffect(playing, selectables);

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
     */
    public void frenzyTurn(Player playing) {
        int remainingActions = 2;

        // Calculate available actions
        if (playing.equals(match.getFirstPlayer())) {
            match.setFirstPlayedFrenzy(true);
        }

        if (match.isFirstPlayedFrenzy()) remainingActions = 1;

        while (remainingActions > 0) {
            if (playing.getConnection() == null) return;

            if(isSkipTurn()){
                skipTurn =false;
                return;
            }

            // Ask player what to do (RUN, PICK, SHOOT)
            TurnAction currentAcion = playing.getConnection().selectAction();

            if (!match.isFirstPlayedFrenzy()) {
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
                        playing.move(playing.getConnection().selectCell(canMove));

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
                        playing.move(playing.getConnection().selectCell(canMove));

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

        if (playingBeforeFirst) canPick = playing.getCellsToMove(2);
        else canPick = playing.getCellsToMove(3);

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

        // Add only pickable weapons
        List<Weapon> pickable = pickedSpawn.getWeapons().stream()
                .filter(weapon -> playing.canPay(weapon.getCost(), playing.getPowerups())).collect(Collectors.toList());

        // Pick weapon
        WeaponSelection pickedWeapon = playing.getConnection().chooseWeapon(pickable);
        Weapon selectedWeapon = pickedSpawn.getWeapons().stream()
                .filter(weapon -> weapon.getName().equals(pickedWeapon.getWeapon()))
                .collect(Collectors.toList()).get(0);

        try {
            playing.pickWeapon(selectedWeapon, pickedWeapon.getPowerups());
            pickedSpawn.removeWeapon(selectedWeapon);
            return true;
        } catch (InventoryFullException invFullE) {
            // Player has too many weapons. Ask for one to change
            WeaponSelection toChange = playing.getConnection().chooseWeaponFree(playing.getWeapons());

            if (toChange == null) return false;

            Weapon weaponToChange = getWeapon(toChange.getWeapon(), playing);

            try {
                // Change chosen weapons
                playing.dropWeapon(weaponToChange);
                playing.pickWeapon(selectedWeapon, pickedWeapon.getPowerups());
                pickedSpawn.removeWeapon(selectedWeapon);
                pickedSpawn.addWeapon(weaponToChange);
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

        // Remove cells with no pickable weapons
        List<Cell> cNotPickable = new ArrayList<>();
        for (Cell cell : canPick) {
            if (cell.isSpawn()) {
                List<Weapon> wNotPickable = new ArrayList<>();
                SpawnCell spawn = (SpawnCell) cell;

                for (Weapon weapon : spawn.getWeapons()) {
                    if (!playing.canPay(weapon.getCost(), playing.getPowerups())) {
                        wNotPickable.add(weapon);
                    }
                }

                if (wNotPickable.containsAll(spawn.getWeapons())) cNotPickable.add(cell);
            }
        }

        canPick.removeAll(cNotPickable);


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
        if(pickedWeapon.getWeapon()==null || pickedWeapon.getWeapon().equals("")) return false;
        if (!getWeapon(pickedWeapon.getWeapon(), playing).isValidActionSequence(effectIds)) return false;

        // Check player can pay all the effects' cost
        List<Resource> totalCost = new ArrayList<>();
        for (Integer id : effectIds) {
            totalCost.addAll(getWeapon(pickedWeapon.getWeapon(), playing).getAction(id).getCost());
        }
        if (!playing.canPay(totalCost, pickedWeapon.getPowerups())) return false;

        // IDs' order matter!
        try {
            for (Integer id : effectIds) {
                playing.shoot(getWeapon(pickedWeapon.getWeapon(), playing), id, pickedWeapon.getPowerups());
            }

            // Reset ids
            for (Player p : match.getPlayers()) p.setID(-1);
            for (Cell c : match.getBoardMap().getAmmoPoints()) c.setID(-1);
            for (Cell c : match.getBoardMap().getSpawnPoints()) c.setID(-1);

            // Weapon has to be reloaded
            getWeapon(pickedWeapon.getWeapon(), playing).setLoaded(false);

            powerupManagerAfterShooting(playing);

            return true;
        } catch (RequirementsNotMetException | InsufficientResourcesException | NoItemInInventoryException | WeaponNotLoadedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Manages powerup usages after shooting
     * @param shooter player that shot
     */
    private void powerupManagerAfterShooting(Player shooter) {
        // Source can use Targetting Scopes
        boolean canUsePowerup = false;
        for (Player p : match.getPlayers()) {
            if (p.isDamagedThisTurn()) {
                // Damaged player can use Tagback Grenade (Only if he sees damager)
                List<Cell> visibleCells = p.getVisibleCellsAtDistance(0, -1);

                for (Cell c : visibleCells) {
                    if (c.getPlayers().contains(shooter)) {
                        List<Powerup> grenades = p.getPowerups().stream()
                                .filter(powerup -> powerup.getName().equals("Tagback grenade")).collect(Collectors.toList());

                        usePowerupEffect(p, grenades);
                        break;
                    }
                }

                canUsePowerup = true;

            }
        }

        if (!canUsePowerup) return;

        List<Powerup> targScopes = shooter.getPowerups().stream()
                .filter(powerup -> powerup.getName().equals("Targetting scopes")).collect(Collectors.toList());

        usePowerupEffect(shooter, targScopes);
    }

    /**
     * Select one of given powerups to use as effect
     * @param playing player
     * @param selectables list of selectable powerups
     */
    private void usePowerupEffect(Player playing, List<Powerup> selectables) {
        if (selectables.isEmpty()) return;
        Powerup selected = playing.getConnection().choosePowerup(selectables);
        if (selected != null) {
            try {
                playing.usePowerupEffect(selected);
            } catch (NoItemInInventoryException e) {
                playing.getConnection().alert("You do not own that powerup!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Reload logic
     * @param playing player
     */
    private void reloadWeapon(Player playing) {
        List<Weapon> canBeReloaded = playing.getWeapons().stream().filter(weapon -> !weapon.isLoaded()).collect(Collectors.toList());

        // Remove all weapons that cannot be loaded due to lack of resources
        canBeReloaded = canBeReloaded.stream()
                .filter(weapon -> playing.canPay(weapon.getCost(), playing.getPowerups())).collect(Collectors.toList());

        while (!canBeReloaded.isEmpty()) {
            WeaponSelection toReload = playing.getConnection().reload(canBeReloaded);
            if (toReload.getWeapon() == null) return;

            // Stop action if player can't pay reload cost with chosen powerups
            if (playing.canPay(getWeapon(toReload.getWeapon(), playing).getCost(), toReload.getPowerups())) {
                // Do reload
                try {
                    Weapon weaponToReaload = getWeapon(toReload.getWeapon(), playing);
                    playing.reload(weaponToReaload, toReload.getPowerups());
                    canBeReloaded.remove(weaponToReaload);

                    canBeReloaded = canBeReloaded.stream()
                            .filter(weapon -> playing.canPay(weapon.getCost(), playing.getPowerups())).collect(Collectors.toList());
                } catch (NoItemInInventoryException | InsufficientResourcesException e) {
                    e.printStackTrace();
                }
            }
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
            if (p.isDead() && !p.getDmgPoints().isEmpty()) {
                // First to damage deadPlayer gets 1 point (First Blood)
                if (!p.isFrenzyPlayer()) p.getDmgPoints().get(0).addScore(1);
                match.rewardPlayers(p.getDmgPoints(), p.getReward());

                Player killshot = p.getDmgPoints().get(p.getDmgPoints().size() - 1);
                // Add death to match, counting overkill if present (Killshot, Death and Overkill)
                match.addDeath(killshot, p.isOverkilled());

                // Mark player who overkilled deadPlayer (Revenge mark)
                if (p.isOverkilled()) killshot.takeMark(p);

                // Respawn player
                respawnPlayer(p);

                // If frenzy enabled, flip dead player's board
                if (match.getMatchState() == MatchState.FRENZY_TURN) p.enableFrenzy();
                updatePlayers();
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
        if(toDiscard == null) {
            // Player disconnected. Choose first weapon
            toDiscard = spawnCards.get(0);
        }
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

        // Update clients to view new player's position
        updatePlayers();
    }

    /**
     * Get a weapon by its name
     * @param weaponName name of searched weapon
     * @param playing player who has this weapon
     * @return Searched weapon
     */
    private Weapon getWeapon(String weaponName, Player playing){
        return match.getPlayers().stream().
                filter(p-> p.getNickname().equals(playing.getNickname())).map(Player::getWeapons).
                flatMap(List::stream).filter(w->w.getName().equals(weaponName)).
                collect(Collectors.toList()).get(0);
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
        match.getPlayers().stream().filter(player -> player.getConnection() != null)
                .forEach(p -> p.getConnection().updateMatch(match));
    }
}
