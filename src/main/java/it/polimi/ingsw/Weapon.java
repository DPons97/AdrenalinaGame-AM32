package it.polimi.ingsw;

/**
 * 
 */
public abstract class Weapon {
	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private Boolean loaded;

	/**
	 * 
	 */
	private String description;

	/**
	 * 
	 */
	private Perk[] cost;

	/**
	 * Default constructor
	 */
	public Weapon() {
	}

	/**
	 * @return
	 */
	public void shoot() {
		// TODO implement here
	}

	/**
	 * @param String fileName
	 * @return
	 */
	private void parseEffects(String fileName) {
		// TODO implement here
	}

}