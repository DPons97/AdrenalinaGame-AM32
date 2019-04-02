package it.polimi.ingsw;

public class PlayerNotExistsException extends Exception{
    public PlayerNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
