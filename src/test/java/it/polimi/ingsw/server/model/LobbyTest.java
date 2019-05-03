package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.*;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LobbyTest {
    @Test
    public void createMatch() throws TooManyPlayersException, TooManyMatchesException, MatchAlreadyStartedException, PlayerAlreadyExistsException, PlayerNotExistsException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer");

        // Check right init of class
        assertTrue(testLobby.getLobbyMatches().isEmpty());
        assertThrows(PlayerNotExistsException.class, () -> testLobby.createMatch(testPlayer, 5, 8, 60, 2));

        // Add player to lobby
        testLobby.addPlayer(testPlayer);

        // testPlayer creates new match
        AdrenalinaMatch newMatch = testLobby.createMatch(testPlayer, 5, 8, 120, 1);

        // Match successfully created
        assertNotNull(newMatch);

        assertFalse(testLobby.getLobbyPlayers().contains(testPlayer));
        assertFalse(testLobby.getLobbyMatches().isEmpty());
        // New match is inside lobby
        assertEquals(newMatch, testLobby.getLobbyMatches().get(0));
        // New match is joinable
        assertEquals(newMatch, testLobby.getJoinableMatches().get(0));
        // New match not started yet
        assertFalse(testLobby.getLobbyMatches().get(0).isStarted());
        // Test player joined new match he created
        assertTrue(newMatch.getPlayers().contains(testPlayer));
        // Host player is indexed as 0 in match
        assertEquals(testPlayer, newMatch.getPlayers().get(0));

        // Add more matches than allowed
        for (int i = 0; i < testLobby.getMaxMatches() - 1; i++) {
            Player newMatchPlayer = new Player("Stormtrooper");
            testLobby.addPlayer(newMatchPlayer);
            testLobby.createMatch(newMatchPlayer, 5, 8, 120, 1);
        }
        Player newMatchPlayer = new Player("Stormtrooper");
        assertThrows(TooManyMatchesException.class, () -> testLobby.createMatch(newMatchPlayer, 5, 8, 60, 1));
    }

    @Test
    public void getJoinableMatches() throws TooManyMatchesException, TooManyPlayersException, PlayerNotExistsException, MatchAlreadyStartedException, PlayerAlreadyExistsException, NotEnoughPlayersException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer");

        assertTrue(testLobby.getJoinableMatches().isEmpty());

        testLobby.addPlayer(testPlayer);
        AdrenalinaMatch newMatch = testLobby.createMatch(testPlayer, 3, 5, 120, 1);

        assertEquals(newMatch, testLobby.getJoinableMatches().get(0));

        // Add enough players to match
        Player testPlayer1 = new Player("testPlayer1");
        Player testPlayer2 = new Player("testPlayer2");
        testLobby.addPlayer(testPlayer1);
        testLobby.addPlayer(testPlayer2);
        testLobby.joinMatch(testPlayer1, newMatch);
        testLobby.joinMatch(testPlayer2, newMatch);

        assertTrue(testLobby.getJoinableMatches().isEmpty());
    }

    @Test
    public void joinMatch() throws TooManyMatchesException, TooManyPlayersException, PlayerNotExistsException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer");
        Player testPlayer1 = new Player("testPlayer1");
        Player testPlayer2 = new Player("testPlayer2");
        Player testPlayer3 = new Player("testPlayer3");

        testLobby.addPlayer(testPlayer);
        testLobby.addPlayer(testPlayer1);
        testLobby.addPlayer(testPlayer2);
        testLobby.addPlayer(testPlayer3);

        AdrenalinaMatch newMatch = testLobby.createMatch(testPlayer, 3, 5, 120, 1);

        assertEquals(3, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getPlayers().contains(testPlayer));

        testLobby.joinMatch(testPlayer1, newMatch);
        assertEquals(2, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getPlayers().contains(testPlayer1));

        testLobby.joinMatch(testPlayer2, newMatch);
        assertEquals(1, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getPlayers().contains(testPlayer2));

        // Try to add more players than allowed
        assertThrows(TooManyPlayersException.class, () -> testLobby.joinMatch(testPlayer3, newMatch));
        // Check that player is still inside lobby
        assertTrue(testLobby.getLobbyPlayers().contains(testPlayer3));
    }
}