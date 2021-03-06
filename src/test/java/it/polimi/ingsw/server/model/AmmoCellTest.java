package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.AmmoAlreadyOnCellException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoCellTest {

    /**
     * checks set ammo logic
     * @throws AmmoAlreadyOnCellException
     */
    @Test
    void setAmmo() throws AmmoAlreadyOnCellException {
        AmmoCell cellTest = new AmmoCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5);
        assertNull(cellTest.getResource());
        Ammo a = new Ammo(Resource.RED_BOX, Resource.BLUE_BOX, Resource.BLUE_BOX);
        cellTest.setAmmo(a);
        assertThrows(AmmoAlreadyOnCellException.class, () ->cellTest.setAmmo(new Ammo(Resource.RED_BOX, Resource.BLUE_BOX, Resource.BLUE_BOX)));
        assertEquals(a,cellTest.pickResource());
        assertNull(cellTest.getResource());
    }
}