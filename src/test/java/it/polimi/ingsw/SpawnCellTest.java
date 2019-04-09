package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.TooManyWeaponsException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpawnCellTest {

    @Test
    void addWeapons() throws TooManyWeaponsException {
        AdrenalinaMatch newMatch = new AdrenalinaMatch(3,8,120,1);

        SpawnCell cellTest = new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5);
        assertTrue(cellTest.getWeapons().isEmpty());

        Weapon w = newMatch.getWeaponDeck().drawCard();
        cellTest.addWeapon(w);
        assertTrue(cellTest.getWeapons().contains(w));
        assertFalse(cellTest.getWeapons().contains(newMatch.getWeaponDeck().drawCard()));

        cellTest.addWeapon(newMatch.getWeaponDeck().drawCard());
        cellTest.addWeapon(newMatch.getWeaponDeck().drawCard());
        assertThrows(TooManyWeaponsException.class, ()-> cellTest.addWeapon(newMatch.getWeaponDeck().drawCard()));


    }
}