package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeaponTest {
    @Test
    void weaponTest(){
        AdrenalinaMatch tMatch = new AdrenalinaMatch(3,8, 120,1);
        Player p = new Player(tMatch,"Aldo");
        p.respawn(tMatch.getBoardMap().getSpawnPoints().get(0));
        do{
            Weapon w = tMatch.getWeaponDeck().drawCard();
            int i = 0;
            for (Action a : w.getShootActions()) {
                int finalI = i;
                assertDoesNotThrow(() -> w.shoot(finalI, p));
                w.reload();
                i++;
            }
            int finalI1 = i;
            assertThrows(IllegalArgumentException.class, () -> w.shoot(finalI1 + 1, p));
        } while(!tMatch.getWeaponDeck().isDeckEmpty());
    }
}