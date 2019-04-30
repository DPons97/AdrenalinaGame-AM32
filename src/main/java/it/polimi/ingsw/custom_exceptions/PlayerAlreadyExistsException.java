package it.polimi.ingsw.custom_exceptions;

public class PlayerAlreadyExistsException extends Exception {
    public PlayerAlreadyExistsException() {
        super();
    }
    public PlayerAlreadyExistsException(String s) {
        super(s);
    }
}
