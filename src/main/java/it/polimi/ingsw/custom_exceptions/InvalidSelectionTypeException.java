package it.polimi.ingsw.custom_exceptions;

public class InvalidSelectionTypeException extends Exception {
    public InvalidSelectionTypeException(){
        super();
    }
    public InvalidSelectionTypeException(String s){
        super(s);
    }
}
