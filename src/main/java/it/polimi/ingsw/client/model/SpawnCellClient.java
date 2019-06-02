package it.polimi.ingsw.client.model;

import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.Side;

import java.util.List;

public class SpawnCellClient extends Cell {

    /**
     * list of the weapons
     */
    private List<WeaponCard> weapons;

    /**
     * @param north upper side of the cell
     * @param south bottom side of the cell
     * @param west  left side of the cell
     * @param east  right side of the celle
     * @param c     cell color
     * @param x     cell x coordinate
     * @param y     cell y coordinate
     */
    public SpawnCellClient(Side north, Side south, Side west, Side east, Color c, int x, int y) {
        super(north, south, west, east, c, x, y);
    }

    @Override
    public boolean isSpawn() {
        return true;
    }

    /**
     * @return the weapons in the spawned cell
     */
    public List<WeaponCard> getWeapons() {
        return weapons;
    }

    /**
     * add one weapon and reload it
     * @param toAdd weapon to add
     */
    public void addWeapon(WeaponCard toAdd) {
        weapons.add(toAdd);
    }
}
