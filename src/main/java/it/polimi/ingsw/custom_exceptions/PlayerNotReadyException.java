package it.polimi.ingsw.custom_exceptions;

public class PlayerNotReadyException extends Exception {
    public PlayerNotReadyException(){
        super();
    }
    public PlayerNotReadyException(String s){
        super(s);
    }
}
