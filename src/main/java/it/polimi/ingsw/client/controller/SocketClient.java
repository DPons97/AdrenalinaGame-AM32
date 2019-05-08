package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
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
			} catch (IOException e) {
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

	private void parseMessage(JSONObject message){
		switch (message.get("function").toString()){
			//TODO implement toJSON and fromJSON in weapon selection then complete this
			case "select":
				switch (message.get("type").toString()){
					case "player":
						List<String> selectblePlayers = new ArrayList<>();
						JSONArray jsonArray= (JSONArray) message.get("list");
						for(Object o: jsonArray){
							selectblePlayers.add(o.toString());
						}
						try {
							sendAnswer(player.playerSelection(selectblePlayers));

						} catch (RemoteException e) {
							e.printStackTrace();
							return;
						}
						break;
					case "cell":
						try {
							Point selected= player.cellSelection(parseCoordinates((JSONArray) message.get("list")));
							JSONObject msg = new JSONObject();
							msg.put("x", selected.getX());
							msg.put("y", selected.getY());
							sendAnswer(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

						break;
					case "room":
						List<List<Point>> rooms = new ArrayList<>();
						JSONArray roomsArray = (JSONArray) message.get("list");
						for(Object o: roomsArray){
							rooms.add(parseCoordinates((JSONArray) o));
						}

						try {
							List<Point> selected = player.roomSelection(rooms);
							JSONObject msg = new JSONObject();
							JSONArray room = new JSONArray();

							selected.forEach(p-> {
								JSONObject item = new JSONObject();
								item.put("x",p.getX());
								item.put("y",p.getY());
								room.add(item);
							});

							msg.put("room", room);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					case "load":

					case "shoot":

					default:

				}
				break;
			case "update":

			default:

		}
	}

	private List<Point> parseCoordinates(JSONArray coords){
		List<Point> toRet = new ArrayList<>();
		JSONObject item;
		int x, y;
		for(Object o: coords){
			item = (JSONObject) o;
			x = Integer.parseInt(item.get("x").toString());
			y = Integer.parseInt(item.get("y").toString());
			toRet.add(new Point(x,y));
		}
		return toRet;
	}

}