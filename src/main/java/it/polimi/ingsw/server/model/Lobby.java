package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.controller.MatchController;
import it.polimi.ingsw.server.controller.PlayerConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Waiting lobby in which players are put before entering in a match (or create one)
 */
public class Lobby {

    private int maxMatches;

    private List<Player> players;

    private List<MatchController> matches;

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
    public List<MatchController> getLobbyMatches() { return new ArrayList<>(matches); }

    /**
     * @return max number of matches that can be played on same server
     */
    public int getMaxMatches() { return maxMatches; }

    /**
     * Get reference to player that has defined nickname
     * @param connection of player to find
     * @return reference to player found
     */
    public Player getPlayer(PlayerConnection connection) {
        for (Player p : players) {
            if (p.getConnection().equals(connection)) return p;
        }
        return null;
    }

    /**
     * @return all matches that can be joined by a player
     */
    public List<MatchController> getJoinableMatches() {
        List<MatchController> toReturn = new ArrayList<>();
        for (MatchController match : matches) {
            if (match.getMatch().getPlayers().size() < match.getMatch().getPlayerNumber()) toReturn.add(match);
        }

        return toReturn;
    }

    /**
     * Create and add a new match to the lobby
     * @return Controller to new match
     */
    public MatchController createMatch(PlayerConnection host, int maxPlayers, int maxDeaths, int turnDuration, int mapID) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, TooManyMatchesException, PlayerNotExistsException {
        if (matches.size() >= maxMatches) throw new TooManyMatchesException();
        if (!players.contains(getPlayer(host))) throw new PlayerNotExistsException();

        // Create model of new match for controller
        AdrenalinaMatch newMatchModel = new AdrenalinaMatch(maxPlayers, maxDeaths, turnDuration, mapID);

        // Returns controller for new match
        MatchController newMatchController = new MatchController(newMatchModel, this);

        joinMatch(getPlayer(host), newMatchController);
        matches.add(newMatchController);

        return newMatchController;
    }

    /**
     * Destroy one of server's match
     * @param toDestroy match to destroy
     */
    public void destroyMatch(MatchController toDestroy) {
        matches.remove(toDestroy);
    }

    /**
     * Add new client to lobby
     * @param toAdd
     */
    public void addPlayer(Player toAdd) {
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
    protected void joinMatch(Player player, MatchController toJoin) throws TooManyPlayersException, MatchAlreadyStartedException, PlayerAlreadyExistsException, PlayerNotExistsException {
        if (!players.contains(player)) throw new PlayerNotExistsException();

        toJoin.getMatch().addPlayer(player);
        player.setMatch(toJoin.getMatch());
        player.getConnection().setCurrentMatch(toJoin);
        players.remove(player);
    }

    /**
     * @return JSON representation of this
     * */
    public JSONObject toJSON(){
        JSONObject toRet = new JSONObject();
        toRet.put("n_players", players.size());

        JSONArray matches = new JSONArray();
        this.matches.forEach(m -> {
            JSONObject match = new JSONObject();
            match.put("n_players", m.getMatch().getPlayerNumber());
            match.put("mapID", m.getMatch().getMapID());
            match.put("max_deaths", m.getMatch().getMaxDeaths());
            JSONArray players = new JSONArray();
            m.getMatch().getPlayers().forEach(p-> players.add(p.getNickname()));
            match.put("players", players);
            matches.add(match);
        });
        toRet.put("matches", matches);
        return toRet;
    }
}
