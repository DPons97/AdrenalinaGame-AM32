package it.polimi.ingsw.custom_exceptions;

public class InvalidJSONException extends Exception {
    public InvalidJSONException(){
        super();
    }
    public InvalidJSONException(String s){
        super(s);
    }
}
