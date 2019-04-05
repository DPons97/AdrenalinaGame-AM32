package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AdrenalinaMatchTest {

    @BeforeEach
    void setUp() {

    }

    /**
     *  tests add death method
     */
    @Test
    void addDeath() throws PlayerNotExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        testMatch.addPlayer(new Player(testMatch, "testPlayer1", new SpawnCell(Side.Border, Side.Door, Side.Free, Side.Wall, Color.BLUE, 3, 5)));
        testMatch.addPlayer(new Player(testMatch, "testPlayer2", new SpawnCell(Side.Border, Side.Door, Side.Free, Side.Wall, Color.BLUE, 3, 5)));
        assertEquals(0, testMatch.getCurrentDeaths());

        testMatch.addDeath(testMatch.getFirstPlayer(), false);
        assertEquals(1, testMatch.getCurrentDeaths());
        assertEquals(testMatch.getFirstPlayer(), testMatch.getDeathTrack().get(testMatch.getDeathTrack().size()-1));

        assertThrows(PlayerNotExistsException.class, ()-> testMatch.addDeath(new Player(testMatch, "unknownPlayer", new SpawnCell(Side.Border, Side.Door, Side.Free, Side.Wall, Color.BLUE, 3, 5)), true));

        testMatch.addDeath(testMatch.getPlayers().get(1), true);
        assertEquals(2, testMatch.getCurrentDeaths());
        assertEquals(2, Collections.frequency(testMatch.getDeathTrack(),testMatch.getPlayers().get(1)));
        assertEquals(1, Collections.frequency(testMatch.getDeathTrack(),testMatch.getPlayers().get(0)));

        testMatch.addDeath(testMatch.getPlayers().get(1), true);
        testMatch.addDeath(testMatch.getPlayers().get(0), true);
        testMatch.addDeath(testMatch.getPlayers().get(1), false);
        assertEquals(5, testMatch.getCurrentDeaths());
        assertEquals(5, Collections.frequency(testMatch.getDeathTrack(),testMatch.getPlayers().get(1)));
        assertEquals(3, Collections.frequency(testMatch.getDeathTrack(),testMatch.getPlayers().get(0)));
        assertTrue(testMatch.isFrenzyEnabled());
    }
}