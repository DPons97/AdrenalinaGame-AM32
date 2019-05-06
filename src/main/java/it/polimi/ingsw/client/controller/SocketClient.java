package it.polimi.ingsw.client.controller;

import org.json.simple.JSONObject;

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
	 *
	 */
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	/**
	 * Default constructor
	 */
	public SocketClient(ClientPlayer player) {
		super(player);
	}

	@Override
	public void connect(String ip, int port, String nickname) {
		try {
			socket= new Socket(ip, port);
			this.output = new PrintWriter(socket.getOutputStream(), true);
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output.println(nickname);
		} catch (IOException e) {
			e.printStackTrace();
			exit(1);
		}

	}

	@Override
	public void disconnect() {

	}



	/**
	 * 
	 */
	private void listen() {
		// TODO implement here
	}

	/**
	 * @param message to send
	 */
	private void sendAnswer(JSONObject message) {
		// TODO implement here
	}

}