package it.polimi.ingsw.custom_exceptions;

public class MatchAlreadyStartedException extends Throwable{
    public MatchAlreadyStartedException() {
        super();
    }
    public MatchAlreadyStartedException(String s) {
        super(s);
    }
}
