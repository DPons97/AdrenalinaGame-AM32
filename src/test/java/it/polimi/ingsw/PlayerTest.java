package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.DeadPlayerException;
import it.polimi.ingsw.custom_exceptions.InsufficientResourcesException;
import it.polimi.ingsw.custom_exceptions.NoItemInInventoryException;
import it.polimi.ingsw.custom_exceptions.TooManyWeaponsException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

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
            victimPlayer.respawn(new SpawnCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0));

            // Check that reward is equal to player's default kill reward from i to end
            assertEquals(victimPlayer.getReward(), Player.getKillRewards().subList(i, Player.getKillRewards().size()));
        }

        // Check rewards if player died more times than number of rewards
        for (int j = 0; j < Player.getMaxDamage() + 1; j++) {
            victimPlayer.takeDamage(killerPlayer);
        }

        victimPlayer.respawn(new SpawnCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0));
        assertEquals(victimPlayer.getReward(), Player.getKillRewards().subList(Player.getKillRewards().size()-1, Player.getKillRewards().size()));
    }

    /**
     *
     */
    @Test
    void reload() throws TooManyWeaponsException {
        Weapon testWeaponMode = new WeaponMode();
        Weapon testWeaponEffect = new WeaponEffect();
        Player testPlayer = new Player(new AdrenalinaMatch(3,8,120,1), "TestPlayer");

        List<Resource> testCost = new ArrayList<>();

        testCost.add(Resource.RED_BOX);
        testCost.add(Resource.RED_BOX);
        testCost.add(Resource.RED_BOX);
        testWeaponEffect.setCost(testCost);
        testWeaponEffect.shoot();   // Set weapon ready to reload

        testCost.clear();
        testCost.add(Resource.YELLOW_BOX);
        testCost.add(Resource.RED_BOX);
        testWeaponMode.setCost(testCost);
        testWeaponMode.shoot();     // Set weapon ready to reload

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
        Cell fromCell = new AmmoCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0);
        Cell destCell = new AmmoCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0);

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
        assertFalse(victimPlayer.isDead());
        assertFalse(killerPlayer.isDead());

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
        victimPlayer.respawn(new SpawnCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0));
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
    void getCellAtDistance() {
    }

    @Test
    void getVisibleCells() {
    }

    @Test
    void getOutOfSightCells() {
    }

    @Test
    void pickAmmo() {
    }

    @Test
    void useAmmo() {
    }

    @Test
    void pickPerk() {
    }

    @Test
    void usePerk() {
    }

    @Test
    void pickWeapon() throws TooManyWeaponsException {
        Weapon testWeaponMode = new WeaponMode();
        Weapon testWeaponEffect = new WeaponEffect();
        Player testPlayer = new Player(new AdrenalinaMatch(3,8,120,1), "TestPlayer");

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
        Weapon testWeapon3 = new WeaponMode();
        Weapon testWeapon4 = new WeaponEffect();

        testPlayer.pickWeapon(testWeapon3);
        assertThrows(TooManyWeaponsException.class, () -> testPlayer.pickWeapon(testWeapon4));
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
        victimPlayer.respawn(new SpawnCell(Side.Free,Side.Free,Side.Free,Side.Free,Color.BLUE,0,0));
        // Check player is respawned
        assertFalse(victimPlayer.isDead());
        assertTrue(victimPlayer.getDmgPoints().isEmpty());
    }
}