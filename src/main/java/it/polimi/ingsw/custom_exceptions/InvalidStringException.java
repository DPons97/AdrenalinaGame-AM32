package it.polimi.ingsw.custom_exceptions;

public class InvalidStringException extends Throwable {
    public InvalidStringException() {super();}
    public InvalidStringException(String s) {super(s);}
}
