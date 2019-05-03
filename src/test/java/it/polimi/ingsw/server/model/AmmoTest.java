package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoTest {

    @Test
    void ammoTestConstructor1(){
        Ammo a = new Ammo(Resource.RED_BOX, Resource.YELLOW_BOX, Resource.BLUE_BOX);
        assertFalse(a.hasPowerup());
        assertTrue(a.getResources().contains(Resource.RED_BOX));
        assertTrue(a.getResources().contains(Resource.YELLOW_BOX));
        assertTrue(a.getResources().contains(Resource.BLUE_BOX));
        assertEquals(3,a.getResources().size());
    }

    @Test
    void ammoTestConstructor2(){
        Ammo a = new Ammo(Resource.RED_BOX, Resource.YELLOW_BOX);
        assertTrue(a.hasPowerup());
        assertTrue(a.getResources().contains(Resource.RED_BOX));
        assertTrue(a.getResources().contains(Resource.YELLOW_BOX));
        assertFalse(a.getResources().contains(Resource.BLUE_BOX));
        assertEquals(2,a.getResources().size());
    }
}