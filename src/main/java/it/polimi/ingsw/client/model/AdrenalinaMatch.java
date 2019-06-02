package it.polimi.ingsw.client.model;

import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * 
 */
public class AdrenalinaMatch {

	/**
     * match map
     */
    private Map map;

    /**
     * map id
     */
    private int mapID;

	/**
	 * state of the match
	 */
	private MatchState state;

	/**
	 * points of the spawn
	 */
	private List<SpawnCell> spawnPoints;

	/**
	 * current Deaths
	 */
	private int currentDeaths;

    /**
     * max Deaths
     */
    private int maxDeaths;

	/**
	 * track of the player's death
	 */
	private List<Player> deathTrack;

	/**
	 * number of the players
	 */
	private int nPlayers;

	/**
	 * players
	 */
	private List<Player> players;

	/**
	 * true if is frenzy
	 */
	private boolean frenzyEnabled;

	/**
	 * duration of the turn
	 */
	private int turnDuration;

	private List<WeaponCard> weapons;

	/**
	 * turn
	 */
	private int turn;


    /**
     * Default constructor
     */
    public AdrenalinaMatch() {
    }

    /**
     * @return board map
     */
    public Map getMap() {
        return map;
    }

    /**
     * @param map map to set
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * @return state of the match
     */
    public MatchState getState() {
        return state;
    }

    /**
     * @param state state to set
     */
    public void setState(MatchState state) {
        this.state = state;
    }

    /**
     * @return the spawnpoints cells
     */
    public List<SpawnCell> getSpawnPoints() {
        return spawnPoints;
    }

    /**
     * @param spawnPoints spawnPoints to set
     */
    public void setSpawnPoints(List<SpawnCell> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    /**
     * @return the currentDeaths of the players
     */
    public int getCurrentDeaths() {
        return currentDeaths;
    }

    /**
     * @param currentDeaths currentDeaths to set
     */
    public void setCurrentDeaths(int currentDeaths) {
        this.currentDeaths = currentDeaths;
    }

    /**
     * @return the DeathTrack of the player
     */
    public List<Player> getDeathTrack() {
        return deathTrack;
    }

    /**
     * @param deathTrack deathTrack to set
     */
    public void setDeathTrack(List<Player> deathTrack) {
        this.deathTrack = deathTrack;
    }

    /**
     @return the number of players
     */
    public int getnPlayers() {
        return nPlayers;
    }

    /**
     * @param nPlayers nPlayers to set
     */
    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    /**
     * @return players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param players players to set
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * @return true if is enabled
     */
    public boolean isFrenzyEnabled() {
        return frenzyEnabled;
    }

    /**
     * @param frenzyEnabled freanzyEnabled to set
     */
    public void setFrenzyEnabled(boolean frenzyEnabled) {
        this.frenzyEnabled = frenzyEnabled;
    }

    /**
     * @return the turnduration
     */
    public int getTurnDuration() {
        return turnDuration;
    }

    /**
     * @param turnDuration turnDuration to set
     */
    public void setTurnDuration(int turnDuration) {
        this.turnDuration = turnDuration;
    }

    /**
     * @return the turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * @param turn turn to set
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void update(JSONObject o) {
        this.maxDeaths = Integer.parseInt(o.get("max_deaths").toString());
        this.mapID = Integer.parseInt(o.get("mapID").toString());
        this.state = MatchState.valueOf(o.get("state").toString());
        this.map =  Map.parseJSON((JSONObject) o.get("map"), weapons, players);
    }
}