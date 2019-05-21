package it.polimi.ingsw.server.model;

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
        testMatch.addPlayer(new Player(testMatch, "testPlayer1"));
        testMatch.addPlayer(new Player(testMatch, "testPlayer2"));
        assertEquals(0, testMatch.getCurrentDeaths());

        testMatch.addDeath(testMatch.getPlayers().get(0), false);
        assertEquals(1, testMatch.getCurrentDeaths());
        assertEquals(testMatch.getPlayers().get(0), testMatch.getDeathTrack().get(testMatch.getDeathTrack().size()-1));

        assertThrows(PlayerNotExistsException.class, ()-> testMatch.addDeath(new Player(testMatch, "unknownPlayer"), true));

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
        assertSame(MatchState.FRENZY_TURN, testMatch.getMatchState());
    }

    @Test
    void startMatch() throws MatchAlreadyStartedException, TooManyPlayersException, NotEnoughPlayersException, PlayerAlreadyExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        assertFalse(testMatch.isStarted());
        testMatch.addPlayer(new Player(testMatch, "testPlayer1"));
        testMatch.addPlayer(new Player(testMatch, "testPlayer2"));
        assertThrows(NotEnoughPlayersException.class, testMatch::startMatch);
        testMatch.addPlayer(new Player(testMatch, "testPlayer3"));
        testMatch.startMatch();
        assertTrue(testMatch.isStarted());
        assertThrows(MatchAlreadyStartedException.class, testMatch::startMatch);
    }

    @Test
    public void addPlayer() {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        Player p1 = new Player(testMatch, "testPlayer1");
        Player p2 = new Player(testMatch, "testPlayer1");

        assertDoesNotThrow(()->testMatch.addPlayer(p1));
        assertThrows(PlayerAlreadyExistsException.class,()->testMatch.addPlayer(p2));
        assertDoesNotThrow(()->testMatch.addPlayer(new Player(testMatch, "testPlayer2")));
        assertDoesNotThrow(()->testMatch.addPlayer(new Player(testMatch, "testPlayer3")));
        assertThrows(TooManyPlayersException.class, ()-> testMatch.addPlayer(new Player(testMatch, "testPlayer4")));
        assertDoesNotThrow(testMatch::startMatch);
        assertThrows(MatchAlreadyStartedException.class, ()-> testMatch.addPlayer(new Player(testMatch, "testPlayer5")));
    }

    @Test
    public void kickPlayer() throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 5,120, 1);
        Player p1 = new Player(testMatch, "testPlayer1");
        Player p2 = new Player(testMatch, "testPlayer2");
        Player p3 = new Player(testMatch, "testPlayer3");
        Player p4 = new Player(testMatch, "testPlayer4");

        testMatch.addPlayer(p1);
        assertThrows(NotEnoughPlayersException.class,()->testMatch.kickPlayer(p2));

        testMatch.addPlayer(p2);
        testMatch.addPlayer(p3);
        testMatch.addPlayer(p4);
        assertDoesNotThrow(()->testMatch.kickPlayer(p1));

        assertThrows(NotEnoughPlayersException.class, ()-> testMatch.kickPlayer(p2));

        testMatch.addPlayer(p1);
        assertDoesNotThrow(testMatch::startMatch);

        assertThrows(MatchAlreadyStartedException.class, ()-> testMatch.kickPlayer(p2));
    }

    @Test
    public void rewardPlayer() throws DeadPlayerException, PlayerNotExistsException, TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
        AdrenalinaMatch testMatch = new AdrenalinaMatch(4, 6,120, 1);
        Player p1 = new Player(testMatch, "testPlayer1");
        testMatch.addPlayer(p1);
        p1.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE, Color.BLUE,0,0));
        Player p2 = new Player(testMatch, "testPlayer2");
        testMatch.addPlayer(p2);
        p2.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE, Color.BLUE,0,0));
        Player p3 = new Player(testMatch, "testPlayer3");
        testMatch.addPlayer(p3);
        p3.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE, Color.BLUE,0,0));
        Player p4 = new Player(testMatch, "testPlayer4");
        testMatch.addPlayer(p4);
        p4.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE, Color.BLUE,0,0));

        // Test reward for kills
        p2.takeDamage(p1);
        p2.takeDamage(p1);
        p2.takeDamage(p3);
        p2.takeDamage(p1);
        p2.takeDamage(p4);
        p2.takeDamage(p3);
        p2.takeDamage(p3);
        p2.takeDamage(p3);
        p2.takeDamage(p3);
        p2.takeDamage(p1);
        p2.takeDamage(p1);

        testMatch.addDeath(p1, true);

        // Rewards should be:
        // p1 -> 8pt, p3 -> 6pt, p4 -> 4pt
        testMatch.rewardPlayers(p2.getDmgPoints(), p2.getReward());
        assertEquals(8, p1.getScore());
        assertEquals(6, p3.getScore());
        assertEquals(4, p4.getScore());
        assertEquals(0, p2.getScore());

        p2.respawn(new SpawnCell(Side.FREE,Side.FREE,Side.FREE,Side.FREE, Color.BLUE,0,0));

        // Test reward for death track
        testMatch.addDeath(p1, false);
        testMatch.addDeath(p2, false);
        testMatch.addDeath(p2, false);
        testMatch.addDeath(p1, false);
        testMatch.addDeath(p3, false);

        testMatch.rewardPlayers(testMatch.getDeathTrack(), testMatch.getRewards());

        // New player's points expected:
        // p1 -> 8pt+8pt, p2 -> 6pt, p3 -> 6pt+4pt, p4 -> 4pt
        assertEquals(16, p1.getScore());
        assertEquals(6, p2.getScore());
        assertEquals(10, p3.getScore());
        assertEquals(4, p4.getScore());
    }

}