package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public abstract class Weapon {
	/**
	 * weapon name
	 */
	private String name;

	/**
	 * true when weapon is ready to shoot
	 */
	private Boolean loaded;

	/**
	 * weapon description
	 */
	private String description;

	/**
	 * weapon cost
	 */
	private List<Resource> cost;

	/**
	 * Default constructor
	 */
	public Weapon() {
		reload();
	}

	/**
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 */
	public Boolean isLoaded() {
		return loaded;
	}

	/**
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 */
	public List<Resource> getCost() {
		return new ArrayList<>(cost);
	}

	/**
	 * testing purposes
	 * @param cost weapon cost
	 */
	public void setCost(List<Resource> cost) {
		this.cost = new ArrayList<>(cost);
	}

	/**
	 *
	 */
	public void reload() {
		loaded = true;
	}

	/**
	 * @return
	 */
	public void shoot() {
		// TODO implement here
		loaded = false;
	}

	/**
	 * @param fileName
	 * @return
	 */
	private void parseEffects(String fileName) {
		// TODO implement here
	}

}