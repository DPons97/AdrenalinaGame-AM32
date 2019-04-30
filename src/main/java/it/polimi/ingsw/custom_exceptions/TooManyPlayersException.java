package it.polimi.ingsw.custom_exceptions;

public class TooManyPlayersException extends Exception {
    public TooManyPlayersException() {
        super();
    }
    public TooManyPlayersException(String s) {
        super(s);
    }
}
