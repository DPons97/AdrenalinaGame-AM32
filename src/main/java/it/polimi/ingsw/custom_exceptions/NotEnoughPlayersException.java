package it.polimi.ingsw.custom_exceptions;

public class NotEnoughPlayersException extends Throwable{
    public NotEnoughPlayersException() {
        super();
    }
    public NotEnoughPlayersException(String s) {
        super(s);
    }
}
