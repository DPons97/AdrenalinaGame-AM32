package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void beginPlay() {
    }

    /**
     *
     */
    @Test
    void reload() throws TooManyWeaponsException, InsufficientResourcesException, NoItemInInventoryException {
        Weapon testWeaponMode = new WeaponMode();
        Weapon testWeaponEffect = new WeaponEffect();
        Player testPlayer = new Player(new AdrenalinaMatch(3,8,120,1), "TestPlayer", new SpawnCell());

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

        // Chech player cannot reload weapons he doesn't have
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
    }

    @Test
    void takeDamage() {
    }

    @Test
    void takeMark() {
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
        Player testPlayer = new Player(new AdrenalinaMatch(3,8,120,1), "TestPlayer", new SpawnCell());

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
}