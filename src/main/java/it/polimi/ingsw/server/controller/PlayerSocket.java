package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

	/**
	 * select a player in a given list
	 * @param selectable list of players
	 * @return a player from selectable
	 */
	@Override
	public Player selectPlayer(List<Player> selectable) {
		return null;
	}

	/**
	 * select a cell in a given list
	 * @param selectable list of cells
	 * @return a cell from selectable
	 */
	@Override
	public Cell selectCell(List<Cell> selectable) {
		return null;
	}

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	@Override
	public List<Cell> selectRoom(List<List<Cell>> selectable) { return null; }

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	@Override
	public WeaponSelection reload(List<Weapon> canLoad) {
		return null;
	}

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection shoot(List<Weapon> loaded) {
		return null;
	}

	/**
	 * select a turn action in a given list
	 * @return an action to make
	 */
	@Override
	public TurnAction selectAction() {
		return null;
	}
}