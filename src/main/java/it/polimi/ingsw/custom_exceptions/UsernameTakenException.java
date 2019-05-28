package it.polimi.ingsw.custom_exceptions;

public class UsernameTakenException extends Exception {
    public UsernameTakenException() {super();}
    public UsernameTakenException(String s) {super(s);}
}
