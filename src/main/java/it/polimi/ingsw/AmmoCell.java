package it.polimi.ingsw;
import it.polimi.ingsw.custom_exceptions.AmmoAlreadyOnCellException;

/**
 * 
 */
public class AmmoCell extends Cell {
	/**
	 *
	 */
	private Ammo resource = null;

	/**
	 * @param north upper side of the cell
	 * @param sud bottom side of the cell
	 * @param west left side of the cell
	 * @param east right side of the cell
	 * @param c cell color
	 * @param x cell x coordinate
	 * @param y cell y coordinate
	 */
	public AmmoCell(Side north, Side sud, Side west, Side east, Color c, int x, int y) {
		super(north, sud, west, east, c, x, y);
	}


	/**
	 * @return false since ammo cell is not a spawn cell
	 */
	public boolean isSpawn() {
		return false;
	}

	/**
	 * @return ammoCard on cell and removes it from the cell
	 */
	public Ammo getResource() {
		Ammo a = resource;
		resource = null;
		return a;
	}

	/**
	 * @param toPlace ammo card to place on cell
	 */
	public void setAmmo(Ammo toPlace) throws AmmoAlreadyOnCellException {
		if(resource==null){
			resource = toPlace;}
		else throw new AmmoAlreadyOnCellException();

	}


}