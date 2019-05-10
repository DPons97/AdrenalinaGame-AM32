package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.custom_exceptions.InvalidSelectionTypeException;
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

import static java.lang.System.exit;

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
	 * Reader for socket stream in
	 */
	private BufferedReader input;

	/**
	 * Constructor
	 */
	public SocketClient(ClientPlayer player) {

		super(player);
	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 */
	@Override
	public void connect(String ip, int port) {
		try {
			socket= new Socket(ip, port);
			this.output = new PrintWriter(socket.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output.println(player.getNickname());
			Thread t = new Thread(this::listen);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
			exit(1);
		}

	}

	/**
	 * Disconnect client from server
	 */
	@Override
	public void disconnect() {
		output.println("disconnect");
	}

	/**
	 * listen for instructions from server
	 */
	private void listen() {
		String msg;
		while(true){
			try {
				msg = input.readLine();

				parseMessage((JSONObject) JSONValue.parse(msg));
			} catch (IOException | InvalidSelectionTypeException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Send message to server
	 * @param message to send
	 */
	private void sendAnswer(JSONObject message) {
		// TODO implement here
	}

	/**
	 * Send message to server
	 * @param message to send
	 */
	private void sendAnswer(String message) {
		// TODO implement here
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

						sendAnswer(player.reloadSelection(shootableWeapons).toJSON());
						break;
					case "action":
						sendAnswer(player.actionSelection().toString());
						break;
					default:
						throw new InvalidSelectionTypeException();
				}
				break;
			case "update":
				switch (message.get("type").toString()){
					case "match":

						break;
					case "lobby":
							player.updateLobby(message.get("lobby").toString());
						break;

					default:
				}
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

}