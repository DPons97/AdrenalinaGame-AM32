package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpawnCellTest {

    @Test
    void addWeapons() throws TooManyWeaponsException {
        SpawnCell cellTest = new SpawnCell(Side.Border, Side.Door, Side.Free, Side.Wall, Color.BLUE, 3, 5);
        assertTrue(cellTest.getWeapons().isEmpty());
        WeaponEffect w = new WeaponEffect();
        cellTest.addWeapon(w);
        assertTrue(cellTest.getWeapons().contains(w));
        assertFalse(cellTest.getWeapons().contains(new WeaponEffect()));
        cellTest.addWeapon(new WeaponEffect());
        cellTest.addWeapon(new WeaponEffect());
        assertThrows(TooManyWeaponsException.class, ()-> cellTest.addWeapon(new WeaponEffect()));


    }
}