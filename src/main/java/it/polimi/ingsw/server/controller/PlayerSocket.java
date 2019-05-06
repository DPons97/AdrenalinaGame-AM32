package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.List;

/**
 * 
 */
public class PlayerSocket extends PlayerConnection {

	/**
	 * Socket to communicate with player
	 */
	private PrintWriter output;
	private BufferedReader input;

	/**
	 * Default constructor
	 */
	public PlayerSocket(PrintWriter output, BufferedReader input) throws IOException {
		super(input.readLine());
		this.input= input;
		this.output= output;
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
	public void sendInstruction(JSONObject message) {
		// TODO implement here
	}

	/**
	 * 
	 */
	private void disconnect() {
		// TODO implement here
	}

	@Override
	public Player selectPlayer(List<Player> selectable) {
		return null;
	}

	@Override
	public Cell selectCell(List<Cell> selectable) {
		return null;
	}

	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		return null;
	}

	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		return null;
	}

	@Override
	public TurnAction selectAction() {
		return null;
	}
}