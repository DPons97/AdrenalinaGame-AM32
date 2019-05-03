package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.custom_exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private int maxMatches;

    private int maxPlayers;

    private List<Player> players;

    private List<AdrenalinaMatch> matches;

    protected Lobby(int maxMatches, int maxPlayers) {
        this.players = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.maxMatches = maxMatches;
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return all matches that can be joined by a player
     */
    protected List<AdrenalinaMatch> getJoinableMatches() {
        List<AdrenalinaMatch> toReturn = new ArrayList<>();
        for (AdrenalinaMatch match : matches) {
            if (!match.isStarted() && match.getPlayers().size() < match.getMaxPlayers()) toReturn.add(match);
        }

        return toReturn;
    }

    /**
     * Create and add a new match to the lobby
     */
    protected AdrenalinaMatch createMatch(Player host, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, TooManyMatchesException {
        if (matches.size() >= maxMatches) throw new TooManyMatchesException();

        AdrenalinaMatch newMatch = new AdrenalinaMatch(maxPlayers, maxDeaths, turnDuration, mapID);
        newMatch.addPlayer(host);
        matches.add(newMatch);
        return newMatch;
    }

    /**
     * Add new client to lobby
     * @param toAdd
     */
    protected void addPlayer(Player toAdd) throws TooManyPlayersException {
        if (players.size() >= maxPlayers) throw new TooManyPlayersException();

        players.add(toAdd);
    }

    /**
     * Add player to match
     * @param player that joins
     * @param toJoin match that is joined
     * @throws TooManyPlayersException if match is full
     * @throws MatchAlreadyStartedException if match already started
     * @throws PlayerAlreadyExistsException if player already inside toJoin
     * @throws PlayerNotExistsException if player is not in lobby
     */
    protected void joinMatch(Player player, AdrenalinaMatch toJoin) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, PlayerNotExistsException {
        if (!players.contains(player)) throw new PlayerNotExistsException();

        toJoin.addPlayer(player);
        player.setMatch(toJoin);
        players.remove(player);
    }
}
