package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class AmmoCell extends Cell {
	/**
	 *
	 */
	private Ammo resource;

	/**
	 * Default constructor
	 */
	public AmmoCell(Side north, Side sud, Side weast, Side east, Color c, int x, int y) {
		super(north, sud, weast, east, c, x, y);
		this.resource = null;
	}


	/**
	 * @return
	 */
	public boolean isSpawn() {
		return false;
	}

	/**
	 * @return
	 */
	public Ammo getResource() {

		return resource;
	}

	/**
	 * @param toPlace
	 */
	public void setAmmo(Ammo toPlace) {
		resource = toPlace;
	}

}