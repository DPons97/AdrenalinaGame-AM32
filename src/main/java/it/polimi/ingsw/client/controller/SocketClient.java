package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.custom_exceptions.InvalidSelectionTypeException;
import it.polimi.ingsw.custom_exceptions.UsernameTakenException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class SocketClient extends ServerConnection {

	/**
	 * Socket connected to server
	 */
	private Socket socket;

	/**
	 * Writer for socket stream out
	 */
	private PrintWriter output;

	/**
	 * Stores ip for reconnections
	 */
	private String ip;

	/**
	 * Stores port for reconnections
	 */
	private int port;

	/**
	 * Reader for socket stream in
	 */
	private BufferedReader input;

	/**
	 * String with last response received from client
	 */
	private String response;
	private boolean validResponse;
	private static final Object  lock= new Object();
	/**
	 * Constructor
	 */
	public SocketClient(ClientPlayer player) {

		super(player);
		validResponse = false;
	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 */
	@Override
	public void connect(String ip, int port) throws IOException, UsernameTakenException {
		socket= new Socket(ip, port);
		this.ip = ip;
		this. port = port;
		this.output = new PrintWriter(socket.getOutputStream(), true);
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output.println(player.getNickname());
		Thread t = new Thread(this::listen);
		String confirm = input.readLine();
		if(confirm.contains("KO")) throw new UsernameTakenException();
		t.start();

	}

	/**
	 *	Gets last message sent from client or waits for it until it comes.
	 */
	private String getResponse(){
		String msg;
		synchronized (lock){
			while(!validResponse) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			}
			msg = response;
			validResponse = false;
			lock.notifyAll();
		}
		return msg;
	}

	/**
	 * Disconnect client from server
	 */
	@Override
	public void disconnect() {
		output.println("disconnect");
	}

	/**
	 * Tells server that this client is ready
	 */
	@Override
	public void setReady() {
		output.println("{\"function\":\"PUSH\", \"type\":\"ready\"}");
	}

	/**
	 * Ask server to create a new game
	 * @param maxPlayers max players to set for this match
	 * @param maxDeaths max deaths to set forthis game
	 * @param mapID id of the map to use for this game
	 */
	@Override
	public void createGame(int maxPlayers, int maxDeaths, int turnDuration, int mapID) {
		JSONObject msg = new JSONObject();
		msg.put("function", "PUSH");
		msg.put("type", "create_match");
		msg.put("max_players", maxPlayers);
		msg.put("max_deaths", maxDeaths);
		msg.put("turn_duration", turnDuration);
		msg.put("map_id", mapID);
		sendAnswer(msg);
	}

	/**
	 * Ask server to join a game
	 * @param nickname nickname of player joining the game
	 * @param id id of the match to join
	 */
	@Override
	public void joinGame(String nickname, int id) {
		JSONObject msg = new JSONObject();
		msg.put("function", "PUSH");
		msg.put("type", "join_match");
		msg.put("match_id", id);
		sendAnswer(msg);
	}

	@Override
	public String updateLobby() {
		JSONObject msg = new JSONObject();
		msg.put("function", "PUSH");
		msg.put("type", "update_lobby");
		sendAnswer(msg);
		return getResponse();
	}

	@Override
	public void backToLobby() {
		JSONObject msg = new JSONObject();
		msg.put("function", "PUSH");
		msg.put("type", "back_to_lobby");
		sendAnswer(msg);
	}

	/**
	 * listen for instructions from server
	 */
	private void listen() {
		String msg;
		while(true){
			try {
				msg = input.readLine();
				// System.out.println(msg);
				if(msg == null) {
					lostConnection();
				}
				if(!msg.equals("ping"))
					//output.println("pong");
				//else
					parseMessage((JSONObject) JSONValue.parse(msg));
			} catch (IOException | InvalidSelectionTypeException e) {
				lostConnection();
			}
		}
	}

	/**
	 * Send message to server
	 * @param message to send
	 */
	private void sendAnswer(JSONObject message) {
		output.println(message.toString());
	}

	/**
	 * Send message to server
	 * @param message to send
	 */
	private void sendAnswer(String message) {
		output.println(message);
	}

	/**
	 * Parses a JSONObject received from the server
	 * @param message to parse
	 */
	private void parseMessage(JSONObject message) throws InvalidSelectionTypeException {
		switch (message.get("function").toString()){
			//TODO implement toJSON and fromJSON in weapon selection then complete this
			case "select":
				switch (message.get("type").toString()){
					case "player":
						List<String> selectablePlayers = new ArrayList<>();
						JSONArray jsonArray= (JSONArray) message.get("list");
						for(Object o: jsonArray){
							selectablePlayers.add(o.toString());
						}
						sendAnswer(player.playerSelection(selectablePlayers));

						break;
					case "cell":
						Point selected= player.cellSelection(parseCoordinates((JSONArray) message.get("list")));
						JSONObject msg = new JSONObject();
						msg.put("x", selected.getX());
						msg.put("y", selected.getY());
						sendAnswer(msg);

						break;
					case "room":
						List<List<Point>> rooms = new ArrayList<>();
						JSONArray roomsArray = (JSONArray) message.get("list");
						for(Object o: roomsArray){
							rooms.add(parseCoordinates((JSONArray) o));
						}

						List<Point> selectedRoom = player.roomSelection(rooms);
						JSONObject response = new JSONObject();
						JSONArray room = new JSONArray();

						selectedRoom.forEach(p-> {
							JSONObject item = new JSONObject();
							item.put("x",p.getX());
							item.put("y",p.getY());
							room.add(item);
						});

						response.put("room", room);
						break;
					case "load":
						List<String> reloadableWeapons = new ArrayList<>();
						JSONArray reloadableArray = (JSONArray) message.get("list");

						for(Object o: reloadableArray){
							reloadableWeapons.add(o.toString());
						}

						sendAnswer(player.reloadSelection(reloadableWeapons).toJSON());
						break;
					case "shoot":
						List<String> shootableWeapons = new ArrayList<>();
						JSONArray shootableArray = (JSONArray) message.get("list");

						for(Object o: shootableArray){
							shootableWeapons.add(o.toString());
						}

						sendAnswer(player.shootSelection(shootableWeapons).toJSON());
						break;
					case "action":
						sendAnswer(player.actionSelection().toString());
						break;
					case "powerup":
						List<String> selectablePowerup = new ArrayList<>();
						JSONArray pArray= (JSONArray) message.get("list");
						for(Object o: pArray){
							selectablePowerup.add(o.toString());
						}
						sendAnswer(player.powerupSelection(selectablePowerup));
						break;
					case "weapon":
						List<String> selectableWeapon = new ArrayList<>();
						JSONArray wArray= (JSONArray) message.get("list");
						for(Object o: wArray){
							selectableWeapon.add(o.toString());
						}
						sendAnswer(player.weaponSelection(selectableWeapon).toJSON());
						break;
					default:
						throw new InvalidSelectionTypeException();
				}
				break;
			case "update":
				switch (message.get("type").toString()){
					case "match":
						player.updateMatch((JSONObject) message.get("match"));
						break;
					case "lobby":
						if(!validResponse) {
							synchronized (lock) {
								response = message.get("lobby").toString();
								validResponse = true;
								lock.notifyAll();
							}
						}
					default:
				}
				break;
			case "alert":
				player.alert(message.get("msg").toString());
				break;
			default:

		}
	}

	/**
	 * Parses coordinates from a JSONObject
	 * @param coords to send
	 */
	private List<Point> parseCoordinates(JSONArray coords){
		List<Point> toRet = new ArrayList<>();
		JSONObject item;
		int x;
		int y;
		for(Object o: coords){
			item = (JSONObject) o;
			x = Integer.parseInt(item.get("x").toString());
			y = Integer.parseInt(item.get("y").toString());
			toRet.add(new Point(x,y));
		}
		return toRet;
	}

	/**
	 * Handles accidental disconnection from server
	 */
	public void lostConnection(){
		System.out.println("Connection lost.");
		while(true){
			try{
				socket= new Socket(ip, port);
				this.output = new PrintWriter(socket.getOutputStream(), true);
				this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output.println(player.getNickname());
				return;
			} catch (IOException e) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}