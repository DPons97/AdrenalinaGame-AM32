package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class AmmoCell extends Cell {
	/**
	 *
	 */
	private Ammo resource = null;

	/**
	 * Default constructor
	 */
	public AmmoCell(Side north, Side sud, Side weast, Side east, Color c, int x, int y) {
		super(north, sud, weast, east, c, x, y);
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
		Ammo a = resource;
		resource = null;
		return a;
	}

	/**
	 * @param toPlace
	 */
	public void setAmmo(Ammo toPlace) throws AmmoAlreadyOnCellException {
		if(resource==null){
			resource = toPlace;}
		else throw new AmmoAlreadyOnCellException();

	}


}