package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.custom_exceptions.*;
import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Socket implementation of PlayerConnection
 * Builds a JSON formatted string with instructions for client which executes ClientPlayer functions
 * and responds with json formatted string
 */
public class PlayerSocket extends PlayerConnection {
	/**
	 * Printer to write on socket stream out
	 */
	private PrintWriter output;

	/**
	 * BufferReader to read from socket stream out
	 */
	private BufferedReader input;

	/**
	 * String with last response received from client
	 */
	private String response;

	private boolean validResponse;


	/**
	 * Default constructor
	 */
	public PlayerSocket(String name, PrintWriter output, BufferedReader input) throws IOException {
		super(name);
		this.input= input;
		this.output= output;
		validResponse = false;
		Thread t = new Thread(this::listen);
		t.start();
	}

	/**
	 *	listens for communications from client.
	 *  Intercepts disconnection requests.
	 */
	private void listen() {
		String message;
		while(true){
			try {
				message = input.readLine();
				if (message == null) continue;

				if(message.equals("disconnect")){
					Thread t = new Thread(this::disconnect);
					t.start();
					return;
				} else if(message.contains("PUSH")) {
					parseMessage(message);
				}else if(!validResponse){
					synchronized (this) {
						response = message;
						validResponse = true;
						notifyAll();
					}
				}

			} catch (IOException e) {
				Thread t = new Thread(this::disconnect);
				t.start();
				try {
					t.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				synchronized (this) {
					response = null;
					validResponse = true;
					notifyAll();
				}
				return;
			}
		}
	}


	/**
	 *	parses message in case it's a push message from client
	 */
	private void parseMessage(String message) {
		JSONObject msg  = (JSONObject) JSONValue.parse(message);
		switch(msg.get("type").toString()){
			case "join_match":
				int id = Integer.parseInt(msg.get("match_id").toString());
				try {
					getServerLobby().joinMatch(this, id);
				} catch (TooManyPlayersException e) {
					this.alert("Cannot join match: match is full");
				} catch (MatchAlreadyStartedException e) {
					this.alert("Cannot join match: match already started");
				} catch (PlayerAlreadyExistsException e) {
					e.printStackTrace();
				} catch (PlayerNotExistsException e) {
					e.printStackTrace();
				}
				break;
			case "create_match":
				int maxPlayers = Integer.parseInt(msg.get("max_players").toString());
				int maxDeaths = Integer.parseInt(msg.get("max_deaths").toString());
				int turnDuration = Integer.parseInt(msg.get("turn_duration").toString());
				int mapID = Integer.parseInt(msg.get("map_id").toString());
				try {
					getServerLobby().hostMatch(this,maxPlayers,maxDeaths,turnDuration,mapID);
				} catch (TooManyMatchesException e) {
					this.alert("Cannot create match: server is full");
				} catch (PlayerNotExistsException e) {
					e.printStackTrace();
				} catch (MatchAlreadyStartedException e) {
					e.printStackTrace();
				} catch (PlayerAlreadyExistsException e) {
					e.printStackTrace();
				} catch (TooManyPlayersException e) {
					e.printStackTrace();
				}
				break;
			case "ready":
				getCurrentMatch().setPlayerReady(this, true);
				break;
			case "not_ready":
				getCurrentMatch().setPlayerReady(this, false);
				break;
			case "update_lobby":
				if(getServerLobby()!=null) {
					this.updateLobby(getServerLobby().lobby);
				}
				break;
			case "back_to_lobby":
				try {
					this.backToLobby();
				} catch (MatchAlreadyStartedException | PlayerNotExistsException e) {
					alert("Error leaving waiting room");
				}
				break;
			default:
		}
	}

	/**
	 *	Gets last message sent from client or waits for it until it comes.
	 */
	private String getResponse(){
		String msg;
		synchronized (this){
			while(!validResponse) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
			msg = response;
			validResponse = false;
			notifyAll();
		}
		return msg;
	}

	/**
	 * @param message to send
	 */
	public void sendInstruction(JSONObject message) {
		//System.out.println("Sending "+ message.toString());
		output.println(message.toString());

	}

	/**
	 * Disconnects this player
	 */
	private void disconnect() {
		System.out.println(name + " disconnected.");
		if(getServerLobby() != null)
			getServerLobby().removePlayer(this);
		if(getCurrentMatch()!= null)
			getCurrentMatch().getPlayer(getName()).setConnection(null);
	}

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
	 */
	@Override
	public Player selectPlayer(List<Player> selectable) {
	    JSONObject message = new JSONObject();
	    message.put("function", "select");
	    message.put("type", "player");
        JSONArray jArray = new JSONArray();
        selectable.forEach(s->jArray.add(s.getNickname()));
	    message.put("list", jArray);
	    this.sendInstruction(message);

	    String selected = this.getResponse();

		if (selected == null || selected.isEmpty()) return null;
		return selectable.stream().filter(p->p.getNickname().equals(selected))
                            .collect(Collectors.toList()).get(0);
	}

	/**
	 * select a cell in a given list
	 * @param selectable list of cells
	 * @return a cell from selectable
	 */
	@Override
	public Cell selectCell(List<Cell> selectable) {
        JSONObject message = new JSONObject();
        message.put("function", "select");
        message.put("type", "cell");
		JSONArray jArray = createJSONCoordinateList(selectable);
		message.put("list", jArray);
        this.sendInstruction(message);
        JSONObject selected = (JSONObject) JSONValue.parse(this.getResponse());

        if (selected.get("x").equals("none")) return null;

        return selectable.stream().filter(c->c.getCoordX() == Integer.parseInt(selected.get("x").toString()) &&
                c.getCoordY() == Integer.parseInt(selected.get("y").toString()))
                .collect(Collectors.toList()).get(0);
	}

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	@Override
	public List<Cell> selectRoom(List<List<Cell>> selectable) {
        JSONObject message = new JSONObject();
        message.put("function", "select");
        message.put("type", "room");
        JSONArray jArray = new JSONArray();
        selectable.forEach(r -> {
			JSONArray room = createJSONCoordinateList(r);
			jArray.add(room);
        });
        message.put("list", jArray);
        this.sendInstruction(message);

        JSONObject selected = (JSONObject) JSONValue.parse(this.getResponse());
        JSONArray room = (JSONArray) selected.get("room");
        for(List<Cell> r : selectable){
            for(Cell c : r){
                if(c.getCoordX() == Integer.parseInt((((JSONObject) room.get(0)).get("x").toString())) &&
						c.getCoordY() == Integer.parseInt((((JSONObject) room.get(0)).get("y").toString())))
                	return r;
            }
        }
        return null;
    }

	/**
     * builds a JSONArray of coordinates
     * @param r list of cells
	 * @return JSONArray of coordinates
     */
	private JSONArray createJSONCoordinateList(List<Cell> r) {
		JSONArray room = new JSONArray();
		r.forEach(s -> {
			JSONObject coords = new JSONObject();
			coords.put("x", s.getCoordX());
			coords.put("y", s.getCoordY());
			room.add(coords);
		});
		return room;
	}


	/**
	 * select a powerup card from a given list
	 * @param selectable list of powerups
	 * @return a powerup from selectable
	 */
	@Override
	public Powerup choosePowerup(List<Powerup> selectable) {
		JSONObject message = new JSONObject();
		message.put("function", "select");
		message.put("type", "powerup");
		JSONArray jArray = new JSONArray();
		selectable.forEach(s->jArray.add(s.toJSON()));
		message.put("list", jArray);
		this.sendInstruction(message);

		String selected = this.getResponse();

		if (selected == null || selectable.isEmpty()) return null;
		return selectable.stream().filter(p->p.toJSON().toString().equals(selected))
				.collect(Collectors.toList()).get(0);
	}

	/**
	 * select a weapon card from a given list
	 * @param selectable list of weapon
	 * @return a powerup from selectable
	 */
	@Override
	public WeaponSelection chooseWeapon(List<Weapon> selectable) {
		JSONObject message = new JSONObject();
		message.put("function", "select");
		message.put("type", "weapon");
		JSONArray jArray = new JSONArray();
		selectable.forEach(s->jArray.add(s.getName()));
		message.put("list", jArray);
		this.sendInstruction(message);
		JSONObject selected = (JSONObject) JSONValue.parse(this.getResponse());

		return parseWeaponSelection(selected);
	}

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		JSONObject message = new JSONObject();
		message.put("function", "select");
		message.put("type", "load");
		return getWeaponSelection(canLoad, message);
	}

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		JSONObject message = new JSONObject();
		message.put("function", "select");
		message.put("type", "shoot");
		return getWeaponSelection(loaded, message);
	}

	/**
	 * Completes the message and parses client response
	 * @param weapons list of weapons to put in the message
	 * @return WeaponSelection parsed from JSON received from client
	 */
	private WeaponSelection getWeaponSelection(List<Weapon> weapons, JSONObject message) {
		JSONArray jArray = createJSONWeaponList(weapons);
		message.put("list", jArray);
		this.sendInstruction(message);

		JSONObject selected = (JSONObject) JSONValue.parse(this.getResponse());

		return parseWeaponSelection(selected);
	}

	/**
	 * creats a JSONArray with weapons in a given list
	 * @param weapons list of weapons
	 * @return JSONArray with weapons in weapons
	 */
	private JSONArray createJSONWeaponList(List<Weapon> weapons) {
		JSONArray jArray = new JSONArray();
		weapons.forEach(s -> jArray.add(s.getName()));
		return jArray;
	}

	/**
	 * select a turn action in a given list
	 * @return an action to make
	 */
	@Override
	public TurnAction selectAction() {
		JSONObject message = new JSONObject();
		message.put("function", "select");
		message.put("type", "action");

		this.sendInstruction(message);
		String selected = this.getResponse();
		return TurnAction.valueOf(selected);
	}

	/**
	 * Updates the client match view
	 * @param toGetUpdateFrom  match to get update from
	 */
	@Override
	public void updateMatch(AdrenalinaMatch toGetUpdateFrom) {
		JSONObject message = new JSONObject();
		message.put("function", "update");
		message.put("type", "match");
		message.put("match", toGetUpdateFrom.toJSON());
		this.sendInstruction(message);
	}

	/**
	 * Updates the client lobby view
	 * @param toGetUpdateFrom  lobby to get update from
	 */
	public void updateLobby(Lobby toGetUpdateFrom) {
		JSONObject message = new JSONObject();
		message.put("function", "update");
		message.put("type", "lobby");
		message.put("lobby", toGetUpdateFrom.toJSON());
		this.sendInstruction(message);
	}

	@Override
	public Thread ping() {
		output.println("ping");
		/*try {
			if(!input.readLine().equals("pong")) {
				Thread t = new Thread(this::disconnect);
				t.start();
				return t;
			}
		} catch (IOException e) {
			Thread t = new Thread(this::disconnect);
			t.start();
			return t;
		}*/
		return null;
	}

	@Override
	public void beginLoading() {
		
	}

	@Override
	public void beginMatch() {

	}

	/**
	 * Socket doesn't use ping to detect disconnections but methods are provided to prevent usless casts
	 */
	@Override
	public boolean getPinged() {
		return true;
	}
	@Override
	public void setPinged(boolean ping) {

	}

	@Override
	public void alert(String s) {
		JSONObject msg = new JSONObject();
		msg.put("function", "alert");
		msg.put("msg", s);
		this.sendInstruction(msg);
	}

	private Powerup getPowerup(String name, Resource r){
		return  getCurrentMatch().getMatch().getPlayers().stream().
				filter(p-> p.getNickname().equals(getName())).map(Player::getPowerups).
				flatMap(List::stream).filter(powerup->powerup.getName().equals(name) && powerup.getBonusResource() == r).
				collect(Collectors.toList()).get(0);
	}

	private WeaponSelection parseWeaponSelection(JSONObject weaponJSON){
		String weapon = weaponJSON.get("weapon").toString();

		if (weapon.equals("none")) weapon = null;

		// Parse effect ids
		List<Integer> effectID = new ArrayList<>();
		JSONArray effectIDArray = (JSONArray) weaponJSON.get("effectID");
		for (Object o : effectIDArray) {
			Integer id = Integer.parseInt(o.toString());
			effectID.add(id);
		}

		// Parse powerups
		List<Powerup> powerups = new ArrayList<>();
		JSONArray powerupsArray = (JSONArray) weaponJSON.get("discount");
		for(Object o: powerupsArray){
			JSONObject item = (JSONObject) o;
			powerups.add(getPowerup(item.get("name").toString(), Resource.valueOf(item.get("resource").toString())));
		}

		return new WeaponSelection(weapon, effectID, powerups);
	}
}