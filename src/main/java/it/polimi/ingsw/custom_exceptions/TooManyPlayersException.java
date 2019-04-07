package it.polimi.ingsw.custom_exceptions;

public class TooManyPlayersException extends Throwable{
    public TooManyPlayersException() {
        super();
    }
    public TooManyPlayersException(String s) {
        super(s);
    }
}
