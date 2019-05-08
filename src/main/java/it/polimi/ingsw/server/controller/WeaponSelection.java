package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages communications about shoot/reload weapon selections
 */
public class WeaponSelection {
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
    private List<Powerup> powerups;

    /**
     * Default constructor
     */
    WeaponSelection() {
        weapon = null;
        effectID = -1;
        powerups = new ArrayList<>();
    }

    /**
     * constructor for JSON parsing
     */
    public WeaponSelection(Weapon weapon, int effectID, List<Powerup> powerups) {
        this.weapon = weapon;
        this.effectID = effectID;
        this.powerups = powerups;
    }

    /**
     * @return Weapon in selection
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * @param weapon weapon to set as selected
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * @return index of selected id, -1 if reloading
     */
    public int getEffectID() {
        return effectID;
    }

    /**
     * @param effectID index of effect to set as selected
     */
    public void setEffectID(int effectID) {
        this.effectID = effectID;
    }

    /**
     * @return list of powerups selected to use as resources
     */
    public List<Powerup> getPowerups() {
        return powerups;
    }

    /**
     * @param powerups list of powerups to set to use as resources
     */
    public void setDiscount(List<Powerup> powerups) {
        this.powerups = powerups;
    }

    /**
     * Returns the JSON representation of this
     * @return json representation of object
     */
    public JSONObject toJSON(){
        JSONObject repr = new JSONObject();
        repr.put("weapon", this.weapon);
        repr.put("effectID", this.effectID);

        JSONArray discArray = new JSONArray();

        for(int i = 0; i < powerups.size(); i ++){
            JSONObject item = new JSONObject();
            item.put("name", powerups.get(i));
            item.put("resource", powerups.get(i).getBonusResource().toString());
            discArray.add(item);
        }

        repr.put("discount", discArray);
        return repr;
    }

}
