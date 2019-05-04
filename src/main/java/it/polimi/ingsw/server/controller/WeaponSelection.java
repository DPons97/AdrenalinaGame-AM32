package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages communications about shoot/reload weapon selections
 */
class WeaponSelection {
    /**
     * Weapon that was chosen
     */
    private Weapon weapon;

    /**
     * Effect chosen. -1 if reloading
     */
    private int effectID;

    /**
     * List of powerups used as discount to reload/shoot
     */
    private List<Powerup> discount;

    WeaponSelection() {
        weapon = null;
        effectID = -1;
        discount = new ArrayList<>();
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public int getEffectID() {
        return effectID;
    }

    public void setEffectID(int effectID) {
        this.effectID = effectID;
    }

    public List<Powerup> getDiscount() {
        return discount;
    }

    public void setDiscount(List<Powerup> discount) {
        this.discount = discount;
    }
}
