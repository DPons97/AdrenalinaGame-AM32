package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages communications about shoot/reload weapon selections
 */
public class WeaponSelection implements Serializable {
    /**
     * Weapon that was chosen
     */
    private String weapon;

    /**
     * Effect chosen. empty list if reloading
     */
    private List<Integer> effectID;

    /**
     * List of powerups used as discount to reload/shoot
     */
    private List<Powerup> powerups;

    /**
     * Default constructor
     */
    public WeaponSelection() {
        super();
        weapon = null;
        effectID = new ArrayList<>();
        powerups = new ArrayList<>();
    }

    /**
     * Builds a weapon selection from params
     * @param weapon to set
     * @param effectID to set
     * @param powerups to set
     */
    public WeaponSelection(String weapon, List<Integer> effectID, List<Powerup> powerups) {
        super();
        this.weapon = weapon;
        this.effectID = effectID;
        this.powerups = powerups;
    }

    /**
     * @return Weapon in selection
     */
    public String getWeapon() {
        return weapon;
    }

    /**
     * @param weapon weapon to set as selected
     */
    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    /**
     * @return index of selected id, -1 if reloading
     */
    public List<Integer> getEffectID() {
        return effectID;
    }

    /**
     * @param effectID index of effect to set as selected
     */
    public void setEffectID(List<Integer> effectID) {
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
        JSONObject obj = new JSONObject();

        if (this.weapon == null) obj.put("weapon", "none");
        else obj.put("weapon", this.weapon);

        JSONArray effectArray = new JSONArray();
        effectArray.addAll(effectID);
        obj.put("effectID", effectArray);

        JSONArray discArray = new JSONArray();

        for(int i = 0; i < powerups.size(); i++){
            JSONObject item = new JSONObject();
            item.put("name", powerups.get(i).getName());
            item.put("resource", powerups.get(i).getBonusResource().toString());
            discArray.add(item);
        }

        obj.put("discount", discArray);
        return obj;
    }

}
