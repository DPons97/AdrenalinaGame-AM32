package it.polimi.ingsw;
import it.polimi.ingsw.custom_exceptions.TooManyWeaponsException;

import java.util.*;

/**
 * 
 */
public class SpawnCell extends Cell {
	/**
	 * list of the weapons
	 */
	private List<Weapon> weapons;

	/**
	 * basic initialization of sides, color and coordinate of the Spawncell
	 * * @param north upper side of the cell
	 * 	 * @param sud bottom side of the cell
	 * 	 * @param west left side of the cell
	 * 	 * @param east right side of the cell
	 * 	 * @param c cell color
	 * 	 * @param x cell x coordinate
	 * 	 * @param y cell y coordinate
	 */
	public SpawnCell(Side north, Side sud, Side west, Side east, Color c, int x, int y) {
		super(north, sud, west, east, c, x, y);
		weapons = new ArrayList<>();
	}

	/**
	 * @return true because the cell is spawned
	 */
	@Override
	public boolean isSpawn() {
		return true;
	}

	/**
	 * @return the weapons in the spowned cell
	 */
	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * add one weapon
	 * @param w weapon to add
	 */
	public void addWeapon(Weapon w) throws TooManyWeaponsException {
		if (weapons.size()==3) {
			throw new TooManyWeaponsException();
		}
		weapons.add(w);
	}
}
