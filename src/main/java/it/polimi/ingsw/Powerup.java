package it.polimi.ingsw;

/**
 * 
 */
public class Powerup {
	/**
	 * Powerup name
	 */
	private String name;

	/**
	 * Powerup description
	 */
	private String description;

	/**
	 * Bonus resource given by the perk
	 */
	private Resource bonusResource;

	/**
	 * Powerup effect
	 */
	private Action effect;

	/**
	 * Default constructor
	 */
	public Powerup(Resource bonusResource, Action effect) {
		this.bonusResource = bonusResource;
		this.effect = effect;
	}

	/**
	 * 
	 */
	public void useAsEffect() {
		// TODO implement here
	}

	/**
	 * @return bonus resource
	 */
	public Resource useAsResource() {
		return bonusResource;
	}


}