package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores the result of a payment request from the controller to a player
 */
public class PaymentResult {
    private boolean canPay;
    private List<Powerup> powerupAsResources;

    PaymentResult() {
        canPay = false;
        powerupAsResources = new ArrayList<>();
    }

    public void setCanPay(boolean canPay) { this.canPay = canPay; }

    public void setPowerupAsResources(List<Powerup> powerupAsResources) { this.powerupAsResources = powerupAsResources; }

    public boolean isCanPay() { return canPay; }

    public List<Powerup> getPowerupAsResources() { return new ArrayList<>(powerupAsResources); }
}
