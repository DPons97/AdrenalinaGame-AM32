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


}