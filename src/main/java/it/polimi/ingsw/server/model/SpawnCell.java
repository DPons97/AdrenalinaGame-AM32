package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.InventoryFullException;
import it.polimi.ingsw.custom_exceptions.NoItemInInventoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Single map cell containing spawn and weapons
 */
public class SpawnCell extends Cell {
	/**
	 * list of the weapons
	 */
	private List<Weapon> weapons;

	/**
	 * basic initialization of sides, color and coordinate of the Spawncell
	 * * @param north upper side of the cell
	 * 	 * @param south bottom side of the cell
	 * 	 * @param west left side of the cell
	 * 	 * @param east right side of the cell
	 * 	 * @param c cell color
	 * 	 * @param x cell x coordinate
	 * 	 * @param y cell y coordinate
	 */
	public SpawnCell(Side north, Side south, Side west, Side east, Color c, int x, int y) {
		super(north, south, west, east, c, x, y);
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
	 * @return the weapons in the spawned cell
	 */
	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * add one weapon and reload it
	 * @param toAdd weapon to add
	 */
	public void addWeapon(Weapon toAdd) throws InventoryFullException {
		if (weapons.size()==3) {
			throw new InventoryFullException();
		}
		weapons.add(toAdd);
		toAdd.reload();
	}

	/**
	 * @param toRemove weapon to remove from cell
	 * @throws NoItemInInventoryException
	 */
	public void removeWeapon(Weapon toRemove) throws NoItemInInventoryException {
		if (!weapons.contains(toRemove)) throw new NoItemInInventoryException();
		else {
			// Remove weapon
			weapons.remove(toRemove);
		}
	}
}
