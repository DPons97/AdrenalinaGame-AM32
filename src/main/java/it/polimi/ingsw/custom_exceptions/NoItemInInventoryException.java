package it.polimi.ingsw.custom_exceptions;

public class NoItemInInventoryException extends Throwable {
    public NoItemInInventoryException() {super();}
    public NoItemInInventoryException(String s) {super(s);}
}
