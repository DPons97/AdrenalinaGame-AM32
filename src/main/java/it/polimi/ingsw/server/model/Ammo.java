package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Ammo {
	/**
	 * List of resources on the card
	 */
	private List<Resource> resources;

	/**
	 * Identifies presence of powerup on the card. if present resources.size()==2
	 */
	private boolean powerup;

	/**
	 * Constructor for ammo card with 3 resources
	 */
	public Ammo(Resource res1, Resource res2, Resource res3) {
		this.resources = new ArrayList<>();
		this.resources.add(res1);
		this.resources.add(res2);
		this.resources.add(res3);
		this.powerup  = false;
	}

    /**
     * Constructor for ammo card with 2 resources and a perk card
     */
	public Ammo(Resource res1, Resource res2) {
		this.resources = new ArrayList<>();
		this.resources.add(res1);
		this.resources.add(res2);
		this.powerup = true;
	}

	/**
	 * @return list of resources
	 */
	public List<Resource> getResources() {
		return new ArrayList<>(this.resources);
	}

	/**
	 * @return true if perk is present, otherwise false
	 */
	public boolean hasPowerup() {
		return powerup;
	}

}