package it.polimi.ingsw.custom_exceptions;

public class MatchAlreadyStartedException extends Exception {
    public MatchAlreadyStartedException() {
        super();
    }
    public MatchAlreadyStartedException(String s) {
        super(s);
    }
}
