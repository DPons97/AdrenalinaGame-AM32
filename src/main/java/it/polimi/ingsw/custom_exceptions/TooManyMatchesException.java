package it.polimi.ingsw.custom_exceptions;

public class TooManyMatchesException extends Exception {
    public TooManyMatchesException() {super();}
    public TooManyMatchesException(String s) {super(s);}
}
