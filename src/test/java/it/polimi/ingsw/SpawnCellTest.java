package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.TooManyWeaponsException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpawnCellTest {

    @Test
    void addWeapons() throws TooManyWeaponsException {
        SpawnCell cellTest = new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5);
        assertTrue(cellTest.getWeapons().isEmpty());
        WeaponMode w = new WeaponMode("WeaponName",new ArrayList<Resource>(),new JSONObject());
        cellTest.addWeapon(w);
        assertTrue(cellTest.getWeapons().contains(w));
        assertFalse(cellTest.getWeapons().contains(new WeaponMode("WeaponName",new ArrayList<Resource>(),new JSONObject())));
        cellTest.addWeapon(new WeaponMode("WeaponName",new ArrayList<Resource>(),new JSONObject()));
        cellTest.addWeapon(new WeaponMode("WeaponName",new ArrayList<Resource>(),new JSONObject()));
        assertThrows(TooManyWeaponsException.class, ()-> cellTest.addWeapon(new WeaponMode("WeaponName",new ArrayList<Resource>(),new JSONObject())));


    }
}