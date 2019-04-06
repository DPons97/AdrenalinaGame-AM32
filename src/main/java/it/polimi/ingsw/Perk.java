package it.polimi.ingsw;
import java.util.*;

/**
 * 
 */
public class Perk {
	/**
	 * Perk name
	 */
	private String name;

	/**
	 * Perk description
	 */
	private String description;

	/**
	 * Bonus resource given by the perk
	 */
	private Resource bonusResource;

	/**
	 * Perk effect
	 */
	private Action effect;

	/**
	 * Default constructor
	 */
	public Perk(Resource bonusResource, Action effect) {
		this.bonusResource = bonusResource;
		this.effect = effect;
	}

	/**
	 * 
	 */
	public void usePerk() {
		// TODO implement here
	}

	/**
	 * @return bonus resource
	 */
	public Resource useResource() {
		return bonusResource;
	}


}