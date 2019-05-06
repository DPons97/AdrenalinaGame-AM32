package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Waiting lobby in which players are put before entering in a match (or create one)
 */
public class Lobby {

    private int maxMatches;

    private List<Player> players;

    private List<AdrenalinaMatch> matches;

    public Lobby(int maxMatches) {
        this.players = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.maxMatches = maxMatches;
    }

    /**
     * @return list of current players inside lobby and waiting to join
     */
    public List<Player> getLobbyPlayers() { return new ArrayList<>(players); }

    /**
     * @return list of all current matches inside lobby (joinable, full and begun)
     */
    public List<AdrenalinaMatch> getLobbyMatches() { return new ArrayList<>(matches); }

    /**
     * @return max number of matches that can be played on same server
     */
    public int getMaxMatches() { return maxMatches; }

    /**
     * @return all matches that can be joined by a player
     */
    protected List<AdrenalinaMatch> getJoinableMatches() {
        List<AdrenalinaMatch> toReturn = new ArrayList<>();
        for (AdrenalinaMatch match : matches) {
            if (match.getPlayers().size() < match.getnPlayers()) toReturn.add(match);
        }

        return toReturn;
    }

    /**
     * Create and add a new match to the lobby
     */
    protected AdrenalinaMatch createMatch(Player host, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, TooManyMatchesException, PlayerNotExistsException {
        if (matches.size() >= maxMatches) throw new TooManyMatchesException();
        if (!players.contains(host)) throw new PlayerNotExistsException();

        AdrenalinaMatch newMatch = new AdrenalinaMatch(maxPlayers, maxDeaths, turnDuration, mapID);
        joinMatch(host, newMatch);
        matches.add(newMatch);

        return newMatch;
    }

    /**
     * Add new client to lobby
     * @param toAdd
     */
    protected void addPlayer(Player toAdd) {
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
