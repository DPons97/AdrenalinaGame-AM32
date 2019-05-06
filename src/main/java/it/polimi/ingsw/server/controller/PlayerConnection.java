package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Weapon;

import java.util.List;

/**
 * 
 */
public abstract class PlayerConnection {
	/**
	 * Player's name
	 */
	protected String name;

	/**
	 * Default constructor
	 */
	public PlayerConnection(String name) {
		this.name=name;

	}

	public String getName(){
	    return name;
    }
	/**
	 * @param selectable
	 * @return
	 */
	public abstract Player selectPlayer(List<Player> selectable);
	/**
	 * @param selectable 
	 * @return
	 */
	public abstract Cell selectCell(List<Cell> selectable);

	/**
	 * @return
	 */
	public abstract WeaponSelection reload(List<Weapon> canLoad);

	/**
	 * @return
	 */
	public abstract WeaponSelection shoot(List<Weapon> loaded);

	/**
	 * @return
	 */
	public abstract TurnAction selectAction();

}