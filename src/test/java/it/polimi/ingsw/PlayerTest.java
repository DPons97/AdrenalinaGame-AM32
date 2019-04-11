package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {


    private static String testName = "testPlayer";
    private static String victimName = "Victim";
    private static String killerName = "Killer";

    @Test
    void getReward() throws DeadPlayerException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player victimPlayer = new Player(testMatch, victimName);
        Player killerPlayer = new Player(testMatch, killerName);

        // Victim never died
        assertTrue(victimPlayer.getReward().isEmpty());

        // Kill victim until reward list is constant
        for (int i=0; i < Player.getKillRewards().size(); i++) {
            // Kill victim
            for (int j = 0; j < Player.getMaxDamage() + 1; j++) {
                victimPlayer.takeDamage(killerPlayer);
            }
            victimPlayer.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0));

            // Check that reward is equal to player's default kill reward from i to end
            assertEquals(victimPlayer.getReward(), Player.getKillRewards().subList(i, Player.getKillRewards().size()));
        }

        // Check rewards if player died more times than number of rewards
        for (int j = 0; j < Player.getMaxDamage() + 1; j++) {
            victimPlayer.takeDamage(killerPlayer);
        }

        victimPlayer.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0));
        assertEquals(victimPlayer.getReward(), Player.getKillRewards().subList(Player.getKillRewards().size()-1, Player.getKillRewards().size()));
    }

    /**
     *
     */
    @Test
    void reload() throws InventoryFullException {
        AdrenalinaMatch newMatch = new AdrenalinaMatch(3,8,120,1);
        Weapon testWeaponMode = newMatch.getWeaponDeck().drawCard();
        Weapon testWeaponEffect = newMatch.getWeaponDeck().drawCard();
        Player testPlayer = new Player(newMatch, testName);

        List<Resource> testCost = new ArrayList<>();

        testCost.add(Resource.RED_BOX);
        testCost.add(Resource.RED_BOX);
        testCost.add(Resource.RED_BOX);
        testWeaponEffect.setCost(testCost);
        testWeaponEffect.shoot(0, testPlayer);   // Set weapon ready to reload

        testCost.clear();
        testCost.add(Resource.YELLOW_BOX);
        testCost.add(Resource.RED_BOX);
        testWeaponMode.setCost(testCost);
        testWeaponMode.shoot(0, testPlayer);     // Set weapon ready to reload

        // Check player cannot reload weapons he doesn't have
        assertThrows(NoItemInInventoryException.class, () -> testPlayer.reload(testWeaponEffect));

        // Add weapons
        testPlayer.pickWeapon(testWeaponEffect);
        testPlayer.pickWeapon(testWeaponMode);

        // Check player cannot reaload if he doesn't have ammos
        assertThrows(InsufficientResourcesException.class, () -> testPlayer.reload(testWeaponEffect));

        // TODO: Add resources to player's inventory

        // Check if weapons load
        //testPlayer.reload(testWeaponEffect);
        //assertTrue(testWeaponMode.isLoaded());
    }

    @Test
    void move() {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, "Player1");
        Cell fromCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);
        Cell destCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);

        assertThrows(NullPointerException.class, () -> testPlayer.move(null));

        // Check correct first move
        testPlayer.move(fromCell);
        assertEquals(testPlayer.getPosition(), fromCell);
        assertTrue(fromCell.getPlayers().contains(testPlayer));

        // Check correct movement from one cell to another
        testPlayer.move(destCell);
        assertEquals(testPlayer.getPosition(), destCell);
        assertFalse(fromCell.getPlayers().contains(testPlayer));
        assertTrue(destCell.getPlayers().contains(testPlayer));
    }

    @Test
    void takeDamage() throws DeadPlayerException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player victimPlayer = new Player(testMatch, victimName);
        Player killerPlayer = new Player(testMatch, killerName);

        // Check that player isn't already dead
        assertTrue(victimPlayer.isDead());
        assertTrue(killerPlayer.isDead());

        // Victim takes 1 damage, then check that he isn't dead
        assertFalse(victimPlayer.takeDamage(killerPlayer));

        // Victim takes another 8 damage
        for (int i=0; i < Player.getMaxDamage() - 2; i++) {
            assertFalse(victimPlayer.takeDamage(killerPlayer));
        }

        // Victim takes 1 damage and check he's dead
        assertTrue(victimPlayer.takeDamage(killerPlayer));

        // Victim takes another 1 damage and check he's still killed and overkilled
        assertTrue(victimPlayer.takeDamage(killerPlayer));
        assertEquals(victimPlayer.getDmgPoints().size(), Player.getMaxDamage() + 1);

        // Check damage from marks is correctly dealt
        victimPlayer.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0));
        for (int i = 0; i < Player.getMaxMarks(); i++) {
            victimPlayer.takeMark(killerPlayer);
        }
        victimPlayer.takeDamage(killerPlayer);
        assertEquals(victimPlayer.getDmgPoints().size(), Player.getMaxMarks() + 1);
    }

    @Test
    void takeMark() {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player victimPlayer = new Player(testMatch, victimName);
        Player killerPlayer = new Player(testMatch, killerName);

        victimPlayer.takeMark(killerPlayer);
        assertEquals(victimPlayer.getMarks().get(0), killerPlayer);

        // Add more marks than maximum allowed
        for (int i = 0; i < Player.getMaxMarks(); i++) {
            victimPlayer.takeMark(killerPlayer);
        }

        assertEquals(victimPlayer.getMarks().size(), Player.getMaxMarks());

    }

    @Test
    void getCellAtDistance() throws IllegalArgumentException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, testName);

        testPlayer.respawn(testMatch.getSpawnPoints().get(0));
        assertThrows(IllegalArgumentException.class, () -> testPlayer.getCellAtDistance(-1, -1));
        assertThrows(IllegalArgumentException.class, () -> testPlayer.getCellAtDistance(0, -2));

        List<Cell> mapCells = testPlayer.getCellAtDistance(0, -1);
        Cell[][] matchMap = testMatch.getMap();

        // Check every map's cell exists in mapCells
        for (Cell[] col : matchMap) {
            for (Cell cell : col) {
                if (cell != null) assertTrue(mapCells.contains(cell));
            }
        }
        // Number of not null cells = 10 (see Map1.json)
        assertEquals(mapCells.size(), 10);

        mapCells = testPlayer.getCellAtDistance(1, 3);
        // Check every map's cell that is inside 1 and 3 distance exists in mapCells
        for (Cell[] col : matchMap) {
            for (Cell cell : col) {
                if (cell != null) {
                    int dist = Math.abs(cell.getCoordX()-testPlayer.getPosition().getCoordX()) + Math.abs(cell.getCoordY()-testPlayer.getPosition().getCoordY());
                    if (dist <= 3 && dist >= 1) {
                        assertTrue(mapCells.contains(cell));
                    }
                }
            }
        }
    }

    @Test
    void getVisibleCells() {
    }

    @Test
    void getOutOfSightCells() {
    }

    @Test
    void pickAmmo() throws AmmoAlreadyOnCellException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, testName);
        testPlayer.respawn(testMatch.getSpawnPoints().get(0));

        // Get first ammo cell
        AmmoCell newAmmoCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);
        Ammo cellAmmo = new Ammo(Resource.RED_BOX, Resource.RED_BOX, Resource.RED_BOX);
        newAmmoCell.setAmmo(cellAmmo);

        // Check player has no ammos
        assertTrue(testPlayer.getAmmos().isEmpty());

        // Check player's inventory has been updated after picking ammos
        List<Resource> playerAmmos = new ArrayList<>();
        playerAmmos.add(Resource.RED_BOX);
        playerAmmos.add(Resource.RED_BOX);
        playerAmmos.add(Resource.RED_BOX);
        testPlayer.pickAmmo(newAmmoCell);
        assertEquals(playerAmmos, testPlayer.getAmmos());

        // Try to add 4 ammos of same type
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.BLUE_BOX));
        testPlayer.pickAmmo(newAmmoCell);

        // Count how many RED_BOXes there are in player's inventory and check there aren't more than 3
        playerAmmos = testPlayer.getAmmos();
        int i = 0;
        for (Resource res : playerAmmos) {
            if (res.equals(Resource.RED_BOX)) i++;
        }
        assertEquals(3, i);

        // Check powerup has been added
        assertEquals(testPlayer.getPowerups().size(), 1);

        // Try to add more than 3 powerups
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        newAmmoCell.setAmmo(new Ammo(Resource.BLUE_BOX, Resource.BLUE_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        newAmmoCell.setAmmo(new Ammo(Resource.YELLOW_BOX, Resource.YELLOW_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        assertEquals(3, testPlayer.getPowerups().size());


        // Try add only resources that player already has, and check cell's ammo is taken
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        playerAmmos = testPlayer.getAmmos();
        i=0;
        for (Resource res : playerAmmos) {
            if (res.equals(Resource.RED_BOX)) i++;
        }
        assertEquals(3, i);
        assertNull(newAmmoCell.getResource());
    }

    @Test
    void usePowerupEffect() throws AmmoAlreadyOnCellException, NoItemInInventoryException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, testName);

        // Try to use powerup that player doesn't own
        assertThrows(NoItemInInventoryException.class, () -> testPlayer.usePowerupEffect(testMatch.getPowerupDeck().drawCard()));

        // Give player one powerup and use it
        AmmoCell newAmmoCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        Powerup toBeUsed = testPlayer.getPowerups().get(0);
        testPlayer.usePowerupEffect(toBeUsed);

        // Check powerup has been discarded
        assertFalse(testPlayer.getPowerups().contains(toBeUsed));
    }

    @Test
    void usePowerupResource() throws AmmoAlreadyOnCellException, NoItemInInventoryException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, testName);

        // Try to use powerup that player doesn't own
        assertThrows(NoItemInInventoryException.class, () -> testPlayer.usePowerupEffect(testMatch.getPowerupDeck().drawCard()));

        // Give player one powerup and use it
        AmmoCell newAmmoCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        Powerup toBeUsed = testPlayer.getPowerups().get(0);
        testPlayer.usePowerupResource(toBeUsed);

        // Check powerup has been discarded
        assertFalse(testPlayer.getPowerups().contains(toBeUsed));
    }

    @Test
    void getAllPowerupByResource()  throws AmmoAlreadyOnCellException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player testPlayer = new Player(testMatch, testName);

        // Get resources if player not having any powerups
        assertTrue(testPlayer.getAllPowerupByResource(Resource.RED_BOX).isEmpty());

        // Give player one powerup and use it
        AmmoCell newAmmoCell = new AmmoCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0);
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);

        // Check return value
        assertEquals(testPlayer.getPowerups(), testPlayer.getAllPowerupByResource(testPlayer.getPowerups().get(0).getBonusResource()));

        // Add different type of resources but get only the RED ones
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);
        newAmmoCell.setAmmo(new Ammo(Resource.RED_BOX, Resource.RED_BOX));
        testPlayer.pickAmmo(newAmmoCell);

        // Check for all types of resources
        for (Resource res : Resource.values()) {
            int count = 0;
            for (Powerup p : testPlayer.getPowerups()) if (p.getBonusResource() == res) count++;

            // Check count of resources and that all have the right type
            assertEquals(count, testPlayer.getAllPowerupByResource(res).size());
            for (Powerup p : testPlayer.getAllPowerupByResource(res)) assertEquals(res, p.getBonusResource());
        }
    }

    @Test
    void pickWeapon() throws InventoryFullException {
        AdrenalinaMatch newMatch = new AdrenalinaMatch(3,8,120,1);
        Weapon testWeaponMode = newMatch.getWeaponDeck().drawCard();
        Weapon testWeaponEffect = newMatch.getWeaponDeck().drawCard();
        Player testPlayer = new Player(newMatch, testName);

        // Add weapon (effect) and check successful add
        testPlayer.pickWeapon(testWeaponEffect);
        assertTrue(testPlayer.getWeapons().contains(testWeaponEffect));

        // Add weapon (mode) and check successful add
        testPlayer.pickWeapon(testWeaponMode);
        assertTrue(testPlayer.getWeapons().contains(testWeaponMode));

        // Try to add 2 same weapons
        testPlayer.pickWeapon(testWeaponEffect);
        assertFalse(Collections.frequency(testPlayer.getWeapons(), testWeaponEffect) > 1);

        // Try to add 4 weapons
        Weapon testWeapon3 = newMatch.getWeaponDeck().drawCard();
        Weapon testWeapon4 = newMatch.getWeaponDeck().drawCard();

        testPlayer.pickWeapon(testWeapon3);
        assertThrows(InventoryFullException.class, () -> testPlayer.pickWeapon(testWeapon4));
    }

    @Test
    void shoot() {
    }

    @Test
    void respawn() throws DeadPlayerException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 8, 60, 1);
        Player victimPlayer = new Player(testMatch, victimName);
        Player killerPlayer = new Player(testMatch, killerName);

        // Kill player
        for (int j = 0; j < Player.getMaxDamage() + 1; j++) {
            victimPlayer.takeDamage(killerPlayer);
        }
        assertTrue(victimPlayer.isDead());
        victimPlayer.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE,Color.BLUE,0,0));
        // Check player is respawned
        assertFalse(victimPlayer.isDead());
        assertTrue(victimPlayer.getDmgPoints().isEmpty());
    }
}