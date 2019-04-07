package it.polimi.ingsw.custom_exceptions;

public class PlayerAlreadyExistsException extends Throwable{
    public PlayerAlreadyExistsException() {
        super();
    }
    public PlayerAlreadyExistsException(String s) {
        super(s);
    }
}
