package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores the result of a payment request from the controller to a player
 */
class PaymentResult {
    private boolean canPay;
    private List<Powerup> powerupAsResources;

    PaymentResult() {
        canPay = false;
        powerupAsResources = new ArrayList<>();
    }

    void setCanPay(boolean canPay) { this.canPay = canPay; }

    void setPowerupAsResources(List<Powerup> powerupAsResources) { this.powerupAsResources = powerupAsResources; }

    boolean isCanPay() { return canPay; }

    List<Powerup> getPowerupAsResources() { return new ArrayList<>(powerupAsResources); }
}
