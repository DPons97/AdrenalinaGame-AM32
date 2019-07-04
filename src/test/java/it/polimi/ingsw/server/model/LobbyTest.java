package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.controller.MatchController;
import it.polimi.ingsw.server.controller.PlayerRemote;
import it.polimi.ingsw.server.controller.PlayerSocket;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class LobbyTest {

    /**
     * Tests lobby logic in match creation
     * @throws TooManyPlayersException
     * @throws TooManyMatchesException
     * @throws MatchAlreadyStartedException
     * @throws PlayerAlreadyExistsException
     * @throws PlayerNotExistsException
     */
    @Test
    public void createMatch() throws TooManyPlayersException, TooManyMatchesException, MatchAlreadyStartedException, PlayerAlreadyExistsException, PlayerNotExistsException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer", new PlayerRemote("testPlayer", null));

        // Check right init of class
        assertTrue(testLobby.getLobbyMatches().isEmpty());
        assertThrows(PlayerNotExistsException.class, () -> testLobby.createMatch(null, testPlayer.getConnection(), 5, 8, 60, 2));

        // Add player to lobby
        testLobby.addPlayer(testPlayer);

        // testPlayer creates new match
        MatchController newMatch = testLobby.createMatch(null,testPlayer.getConnection(), 5, 8, 120, 1);

        // Match successfully created
        assertNotNull(newMatch);

        assertFalse(testLobby.getLobbyPlayers().contains(testPlayer));
        assertFalse(testLobby.getLobbyMatches().isEmpty());
        // New match is inside lobby
        assertEquals(newMatch, testLobby.getLobbyMatches().get(0));
        // New match is joinable
        assertEquals(newMatch, testLobby.getJoinableMatches().get(0));
        // New match not started yet
        assertFalse(testLobby.getLobbyMatches().get(0).getMatch().isStarted());
        // Test player joined new match he created
        assertTrue(newMatch.getMatch().getPlayers().contains(testPlayer));
        // Host player is indexed as 0 in match
        assertEquals(testPlayer, newMatch.getMatch().getPlayers().get(0));

        // Add more matches than allowed
        for (int i = 0; i < testLobby.getMaxMatches() - 1; i++) {
            Player newMatchPlayer = new Player("Stormtrooper", new PlayerRemote("Stormtrooper", null));
            testLobby.addPlayer(newMatchPlayer);
            testLobby.createMatch(null,newMatchPlayer.getConnection(), 5, 8, 120, 1);
        }
        Player newMatchPlayer = new Player("ObiWan", new PlayerRemote("ObiWan", null));
        assertThrows(TooManyMatchesException.class, () -> testLobby.createMatch(null,newMatchPlayer.getConnection(), 5, 8, 60, 1));
    }

    /**
     * Tests correctness of jaoinable matches getter
     * @throws TooManyMatchesException
     * @throws TooManyPlayersException
     * @throws PlayerNotExistsException
     * @throws MatchAlreadyStartedException
     * @throws PlayerAlreadyExistsException
     * @throws NotEnoughPlayersException
     */
    @Test
    public void getJoinableMatches() throws TooManyMatchesException, TooManyPlayersException, PlayerNotExistsException, MatchAlreadyStartedException, PlayerAlreadyExistsException, NotEnoughPlayersException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer", new PlayerRemote("testPlayer", null));

        assertTrue(testLobby.getJoinableMatches().isEmpty());

        testLobby.addPlayer(testPlayer);
        MatchController newMatch = testLobby.createMatch(null,testPlayer.getConnection(), 3, 5, 120, 1);

        assertEquals(newMatch, testLobby.getJoinableMatches().get(0));

        // Add enough players to match
        Player testPlayer1 = new Player("testPlayer1", new PlayerRemote("testPlayer1", null));
        Player testPlayer2 = new Player("testPlayer2", new PlayerRemote("testPlayer2", null));
        testLobby.addPlayer(testPlayer1);
        testLobby.addPlayer(testPlayer2);
        testLobby.joinMatch(testPlayer1, newMatch);
        testLobby.joinMatch(testPlayer2, newMatch);

        assertTrue(testLobby.getJoinableMatches().isEmpty());
    }

    /**
     * Tests joinMatch logic
     * @throws TooManyMatchesException
     * @throws TooManyPlayersException
     * @throws PlayerNotExistsException
     * @throws MatchAlreadyStartedException
     * @throws PlayerAlreadyExistsException
     */
    @Test
    public void joinMatch() throws TooManyMatchesException, TooManyPlayersException, PlayerNotExistsException, MatchAlreadyStartedException, PlayerAlreadyExistsException {
        Lobby testLobby = new Lobby(3);
        Player testPlayer = new Player("testPlayer", new PlayerRemote("testPlayer", null));
        Player testPlayer1 = new Player("testPlayer1", new PlayerRemote("testPlayer1", null));
        Player testPlayer2 = new Player("testPlayer2", new PlayerRemote("testPlayer2", null));
        Player testPlayer3 = new Player("testPlayer3", new PlayerRemote("testPlayer3", null));

        testLobby.addPlayer(testPlayer);
        testLobby.addPlayer(testPlayer1);
        testLobby.addPlayer(testPlayer2);
        testLobby.addPlayer(testPlayer3);

        MatchController newMatch = testLobby.createMatch(null,testPlayer.getConnection(), 3, 5, 120, 1);

        assertEquals(3, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getMatch().getPlayers().contains(testPlayer));

        testLobby.joinMatch(testPlayer1, newMatch);
        assertEquals(2, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getMatch().getPlayers().contains(testPlayer1));

        testLobby.joinMatch(testPlayer2, newMatch);
        assertEquals(1, testLobby.getLobbyPlayers().size());
        assertTrue(newMatch.getMatch().getPlayers().contains(testPlayer2));

        // Try to add more players than allowed
        assertThrows(TooManyPlayersException.class, () -> testLobby.joinMatch(testPlayer3, newMatch));
        // Check that player is still inside lobby
        assertTrue(testLobby.getLobbyPlayers().contains(testPlayer3));
    }
}