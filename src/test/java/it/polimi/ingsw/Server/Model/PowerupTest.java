package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.Model.AdrenalinaMatch;
import it.polimi.ingsw.Server.Model.Player;
import it.polimi.ingsw.Server.Model.Powerup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PowerupTest {
    @Test
    void powerupTest(){
        AdrenalinaMatch tMatch = new AdrenalinaMatch(3,8, 120,1);
        Player p = new Player(tMatch,"Aldo");
        while(!tMatch.getPowerupDeck().isDeckEmpty()) {
            Powerup up = tMatch.getPowerupDeck().drawCard();
            assertDoesNotThrow(() -> up.useAsEffect(p));
        }
    }
}