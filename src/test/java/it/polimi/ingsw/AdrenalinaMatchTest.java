package it.polimi.ingsw;

import it.polimi.ingsw.custom_exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AdrenalinaMatchTest {

    /**
     *  tests add death method
     */
    @Test
    void addDeath() throws PlayerNotExistsException, MatchAlreadyStartedException, TooManyPlayersException, PlayerAlreadyExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        testMatch.addPlayer(new Player(testMatch, "testPlayer1", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)));
        testMatch.addPlayer(new Player(testMatch, "testPlayer2", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)));
        assertEquals(0, testMatch.getCurrentDeaths());

        testMatch.addDeath(testMatch.getPlayers().get(0), false);
        assertEquals(1, testMatch.getCurrentDeaths());
        assertEquals(testMatch.getPlayers().get(0), testMatch.getDeathTrack().get(testMatch.getDeathTrack().size()-1));

        assertThrows(PlayerNotExistsException.class, ()-> testMatch.addDeath(new Player(testMatch, "unknownPlayer", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)), true));

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

    @Test
    void checkMaps(){
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        assertEquals(3, testMatch.getSpawnPoints().size());
        testMatch = new AdrenalinaMatch(3, 5,120, 2);
        assertEquals(3, testMatch.getSpawnPoints().size());
        testMatch = new AdrenalinaMatch(3, 5,120, 3);
        assertEquals(3, testMatch.getSpawnPoints().size());
    }

    @Test
    void startMatch() throws MatchAlreadyStartedException, TooManyPlayersException, NotEnoughPlayersException, PlayerAlreadyExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        assertFalse(testMatch.isStarted());
        testMatch.addPlayer(new Player(testMatch, "testPlayer1", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)));
        testMatch.addPlayer(new Player(testMatch, "testPlayer2", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)));
        assertThrows(NotEnoughPlayersException.class, () -> testMatch.startMatch(41));
        testMatch.addPlayer(new Player(testMatch, "testPlayer3", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5)));
        testMatch.startMatch(14);
        assertTrue(testMatch.isStarted());
        assertThrows(MatchAlreadyStartedException.class, () -> testMatch.startMatch(1));
    }

    @Test
    public void addPlayer(){
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        assertDoesNotThrow(()->testMatch.addPlayer(new Player(testMatch, "testPlayer1", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
        assertThrows(PlayerAlreadyExistsException.class,()->testMatch.addPlayer(new Player(testMatch, "testPlayer1", new SpawnCell(Side.WALL, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
        assertDoesNotThrow(()->testMatch.addPlayer(new Player(testMatch, "testPlayer2", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
        assertDoesNotThrow(()->testMatch.addPlayer(new Player(testMatch, "testPlayer3", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
        assertThrows(TooManyPlayersException.class, ()-> testMatch.addPlayer(new Player(testMatch, "testPlayer4", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
        assertDoesNotThrow(()->testMatch.startMatch(77));
        assertThrows(MatchAlreadyStartedException.class, ()-> testMatch.addPlayer(new Player(testMatch, "testPlayer5", new SpawnCell(Side.BORDER, Side.DOOR, Side.FREE, Side.WALL, Color.BLUE, 3, 5))));
    }


}