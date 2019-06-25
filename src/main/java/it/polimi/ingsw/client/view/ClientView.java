package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.controller.ClientPlayer;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.Powerup;

import java.util.List;

public abstract class ClientView {
    protected static final int ALERT_DURATION = 5;

    protected ClientPlayer player;

    public ClientView(ClientPlayer player){
        this.player = player;
    }

    /**
     * Shows the lobby
     */
    public abstract void showLobby(String lobby);

    /**
     * Shows the launcher options
     */
    public abstract void showMatch();

    /**
     * Lets client select a player from a list
     * @param selectables list of players
     * @return selected player
     */
    public abstract String selectPlayer (List<String> selectables);

    /**
     * Lets client select a cell from a list
     * @param selectables list of points
     * @return selected point
     */
    public abstract Point selectCell (List<Point> selectables);

    /**
     * Lets client select a room from a list
     * @param selectables list of rooms
     * @return selected room
     */
    public abstract List<Point> selectRoom (List<List<Point>> selectables);

    /**
     * Lets client select a weapon and effect from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    public abstract WeaponSelection selectShoot(List<String> selectables);

    /**
     * Lets client select a weapon to reload from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    public abstract WeaponSelection selectReload (List<String> selectables);

    /**
     * Lets client select a weapon  from a list
     * @param selectables list of weapons
     * @return selected weapon and effect
     */
    public abstract String selectWeapon (List<String> selectables);

    /**
     * Lets client select a powerup from a list
     * @param selectables list of powerups
     * @return selected powerup
     */
    public abstract Powerup selectPowerup (List<Powerup> selectables);

    /**
     * Select an action to make
     * @return action to make
     */
    public abstract TurnAction actionSelection();


    public abstract void createNewGame();

    public abstract void showAlert(String message);
}
