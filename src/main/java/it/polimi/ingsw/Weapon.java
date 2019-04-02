package it.polimi.ingsw;

import java.util.*;

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
	 *
	 * @param cost
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