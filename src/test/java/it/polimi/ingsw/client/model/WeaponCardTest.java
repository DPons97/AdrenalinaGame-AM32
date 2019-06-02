package it.polimi.ingsw.client.model;

import it.polimi.ingsw.server.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class WeaponCardTest {

    @Test
    void parseJSON() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/json/weapons.json"), StandardCharsets.UTF_8)));
            JSONObject toParse = (JSONObject) ((JSONArray)((JSONObject) obj).get("Weapons")).get(0);
            WeaponCard weapon = WeaponCard.parseJSON(toParse);
            assertEquals(weapon.getName(),"Lock Rifle");
            assertEquals(weapon.getCost().size(),2);
            assertEquals(weapon.getCost().get(0), Resource.BLUE_BOX);
            assertEquals(weapon.getCost().get(1), Resource.BLUE_BOX);
            assertTrue(weapon.isEffect());
            assertEquals(weapon.getEffects().size(), 2);
            assertEquals(weapon.getEffects().get(0).getName(), "base effect");
            assertEquals(weapon.getEffects().get(0).getDescription(), "Deal 2 damage and 1 mark to 1 target you can see.");
            assertEquals(weapon.getEffects().get(0).getCost().size(), 0);
            assertEquals(weapon.getEffects().get(1).getName(), "second-lock");
            assertEquals(weapon.getEffects().get(1).getDescription(), "Deal 1 mark to a different target you can see.");
            assertEquals(weapon.getEffects().get(1).getCost().size(),1);
            assertEquals(weapon.getEffects().get(1).getCost().get(0), Resource.RED_BOX);

        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        } catch (ParseException e) {
            e.printStackTrace();
            assert false;
        }


    }
}