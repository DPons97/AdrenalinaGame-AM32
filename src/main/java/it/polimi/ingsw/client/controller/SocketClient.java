package it.polimi.ingsw.client.controller;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

	private void parseMessage(JSONObject message){
		switch (message.get("function").toString()){
			//TODO implement toJSON and fromJSON in weason selection then complete this
			case "select":
				switch (message.get("type").toString()){
					case "player":

					case "cell":

					case "load":

					case "shoot":

					default:

				}
				break;
			case "update":

			default:

		}
	}

}