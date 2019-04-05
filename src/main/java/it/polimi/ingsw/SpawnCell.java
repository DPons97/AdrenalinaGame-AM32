package it.polimi.ingsw;
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
	 */
	public SpawnCell(Side north, Side sud, Side weast, Side east, Color c, int x, int y) {
		super(north, sud, weast, east, c, x, y);
		weapons = new ArrayList<>();
	}

	/**
	 * @return true because the cell is spawned
	 */
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
	 * @return add one weapon
	 */
	public void addWeapon(Weapon w) throws TooManyWeaponsException{
		if (weapons.size()==3) {
			throw new TooManyWeaponsException();
		}
		weapons.add(w);

	}
}
