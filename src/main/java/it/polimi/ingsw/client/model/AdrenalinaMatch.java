package it.polimi.ingsw.client.model;

import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        initWeapons();
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

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public int getMaxDeaths() {
        return maxDeaths;
    }

    public void setMaxDeaths(int maxDeaths) {
        this.maxDeaths = maxDeaths;
    }

    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<WeaponCard> weapons) {
        this.weapons = weapons;
    }

    public void update(JSONObject o) {
        this.maxDeaths = Integer.parseInt(o.get("max_deaths").toString());
        this.mapID = Integer.parseInt(o.get("mapID").toString());
        this.turnDuration = Integer.parseInt(o.get("turnDuration").toString());
        this.turn = Integer.parseInt(o.get("turn").toString());
        this.nPlayers = Integer.parseInt((o.get("nPlayers").toString()));
        JSONArray playersArray = (JSONArray) o.get("players");
        if(state == MatchState.NOT_STARTED||players == null) {
            initPlayers(playersArray);
        }

        for(Object player: playersArray){
            players.stream().filter(p->p.getNickname().equals(((JSONObject) player).get("name").toString())).collect(Collectors.toList()).get(0).update((JSONObject) player);
        }

        this.state = MatchState.valueOf(o.get("state").toString());
        this.map =  Map.parseJSON((JSONObject) o.get("map"), weapons, players);

        JSONArray deathTrackArray = (JSONArray) o.get("deathTrack");
        deathTrack = new ArrayList<>();
        for(Object player: deathTrackArray){
            deathTrack.add(players.stream().filter(p->p.getNickname().equals(player.toString())).collect(Collectors.toList()).get(0));
        }
        System.out.println("match model updated");
    }

    private void initPlayers(JSONArray playersArray) {
        this.players = new ArrayList<>();
        for (Object p : playersArray) {
            Player toAdd = new Player();
            toAdd.setNickname(((JSONObject) p).get("name").toString());
            toAdd.setMatch(this);
            this.players.add(toAdd);
        }
    }

    private void initWeapons(){
        weapons = new ArrayList<>();
        JSONParser parser = new JSONParser();
        String name;
        try {
            Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/json/weapons.json"), StandardCharsets.UTF_8)));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray weaponCards = (JSONArray) jsonObject.get("Weapons");

            for(Object weaponCard: weaponCards){
                weapons.add(WeaponCard.parseJSON((JSONObject) weaponCard));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}