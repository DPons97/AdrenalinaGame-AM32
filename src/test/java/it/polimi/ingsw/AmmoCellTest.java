package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoCellTest {

    @Test
    void setAmmo() {
        AmmoCell cellTest = new AmmoCell(Side.Border, Side.Door, Side.Free, Side.Wall, Color.BLUE, 3, 5);
        assertNull(cellTest.getResource());
        Ammo a = new Ammo();
        cellTest.setAmmo(a);
        assertEquals(a,cellTest.getResource());

    }
}