package it.polimi.ingsw.custom_exceptions;

public class WeaponNotLoadedException extends Throwable {
    public WeaponNotLoadedException(){
        super();
    }
    public WeaponNotLoadedException(String s){
        super(s);
    }
}
