package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PowerupTest {
    @Test
    void powerupTest(){
        AdrenalinaMatch tMatch = new AdrenalinaMatch(3,8, 120,1);
        Player p = new Player(tMatch,"Aldo");
        Powerup up= tMatch.getPowerupDeck().drawCard();

        assertDoesNotThrow(()->up.useAsEffect(p));

    }
}