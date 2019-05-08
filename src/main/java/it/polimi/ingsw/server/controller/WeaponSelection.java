package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Powerup;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.Weapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages communications about shoot/reload weapon selections
 */
public class WeaponSelection {
    /**
     * Weapon that was chosen
     */
    private String weapon;

    /**
     * Effect chosen. -1 if reloading
     */
    private int effectID;

    /**
     * List of powerups names used as discount to reload/shoot
     */
    private List<String> powerups;

    /**
     * List of powerups resources used as discount to reload/shoot
     */
    private List<Resource> resources;

    /**
     * Default constructor
     */
    WeaponSelection() {
        weapon = null;
        effectID = -1;
        powerups = new ArrayList<>();
        resources = new ArrayList<>();
    }

    /**
     * Private constructor for JSON static parser
     */
    private WeaponSelection(String weapon, int effectID, List<String> powerups, List<Resource> resources) {
        weapon = weapon;
        effectID = effectID;
        this.powerups = powerups;
        this.resources = resources;
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
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon.getName();
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
    public List<String> getPowerups() {
        return powerups;
    }

    /**
     * @param powerups list of powerups to set to use as resources
     */
    public void setDiscount(List<Powerup> powerups) {
        this.powerups = powerups.stream().map(Powerup::getName).collect(Collectors.toList());
        this.resources = powerups.stream().map(Powerup::getBonusResource).collect(Collectors.toList());
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
            item.put("resource", resources.get(i).toString());
            discArray.add(item);
        }

        repr.put("discount", discArray);
        return repr;
    }

    /**
     * Creates a WeaponSelection from a JSONObject
     * @param toParse JSONObject to parse
     * @return parsed WeaponSelection
     */
    public static WeaponSelection fromJSON(JSONObject toParse){
        String weapon = toParse.get("weapon").toString();
        int effectID  = Integer.parseInt(toParse.get("effectID").toString());
        JSONArray discArray = (JSONArray) toParse.get("discount");
        List<String> powerups = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();

        for(Object o: discArray){
            JSONObject item = (JSONObject) o;
            powerups.add(item.get("name").toString());
            resources.add(Resource.valueOf(item.get("resource").toString()));
        }


        return new WeaponSelection(weapon, effectID,powerups, resources);
    }

}
