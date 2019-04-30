package it.polimi.ingsw.custom_exceptions;

public class NoItemInInventoryException extends Exception {
    public NoItemInInventoryException() {super();}
    public NoItemInInventoryException(String s) {super(s);}
}
