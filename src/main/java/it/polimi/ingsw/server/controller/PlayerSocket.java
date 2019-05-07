package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Weapon;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

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
	private String listen() {
		// TODO implement here
        return null;
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
	    JSONObject message = new JSONObject();
	    message.put("function", "select");
	    message.put("type", "player");
        JSONArray jArray = new JSONArray();
        selectable.forEach(s->jArray.add(s.getNickname()));
	    message.put("list", jArray);
	    this.sendInstruction(message);
	    String selected = this.listen();
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
        JSONArray jArray = new JSONArray();
        selectable.forEach(s-> {
            JSONObject coords = new JSONObject();
            coords.put("x", s.getCoordX());
            coords.put("y", s.getCoordY());
            jArray.add(coords);
        });
        message.put("list", jArray);
        this.sendInstruction(message);
        JSONObject selected = (JSONObject) JSONValue.parse(this.listen());
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
            JSONArray room = new JSONArray();
            r.forEach(s->{
                JSONObject coords = new JSONObject();
                coords.put("x", s.getCoordX());
                coords.put("y", s.getCoordY());
                room.add(coords);
            });
            jArray.add(room);
        });
        message.put("list", jArray);
        this.sendInstruction(message);

        JSONObject selected = (JSONObject) JSONValue.parse(this.listen());
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