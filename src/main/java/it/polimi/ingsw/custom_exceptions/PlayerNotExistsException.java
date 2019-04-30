package it.polimi.ingsw.custom_exceptions;

public class PlayerNotExistsException extends Exception {
    public PlayerNotExistsException(String errorMessage) {
        super(errorMessage);
    }
    public PlayerNotExistsException(){
        super();
    }
}
