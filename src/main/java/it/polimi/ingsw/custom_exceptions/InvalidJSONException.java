package it.polimi.ingsw.custom_exceptions;

public class InvalidJSONException extends Throwable {
    public InvalidJSONException(){
        super();
    }
    public InvalidJSONException(String s){
        super(s);
    }
}
