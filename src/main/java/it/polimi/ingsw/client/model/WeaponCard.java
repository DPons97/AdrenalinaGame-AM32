package it.polimi.ingsw.client.model;

import it.polimi.ingsw.custom_exceptions.InvalidStringException;
import it.polimi.ingsw.server.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.server.model.AdrenalinaMatch.stringToResource;

public class WeaponCard {
    private String name;
    private List<Resource> cost;
    private List<Effect> effects;
    private boolean isEffect;

    public WeaponCard(String name, List<Resource> cost, List<Effect> effects, boolean isEffect) {
        this.name = name;
        this.cost = cost;
        this.effects = effects;
        this.isEffect = isEffect;
    }

    public String getName() {
        return name;
    }

    public List<Resource> getCost() {
        return new ArrayList<>(cost);
    }

    public List<Effect> getEffects() {
        return new ArrayList<>(effects);
    }

    public boolean isEffect() {
        return isEffect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeaponCard that = (WeaponCard) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    protected static class Effect {
        private String name;
        private String description;
        private List<Resource> cost;

        public Effect(String name, String description, List<Resource> cost) {
            this.name = name;
            this.description = description;
            this.cost = cost;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<Resource> getCost() {
            return new ArrayList<>(cost);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Effect effect = (Effect) o;
            return getName().equals(effect.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName());
        }
    }

    public static WeaponCard parseJSON(JSONObject toParse){
        try {
            String cardName = toParse.get("name").toString();
            boolean isEffect = toParse.get("type").toString().equals("EFFECT");
            List<Resource> cost = parseResources(toParse);
            List<Effect> effects = parseEffects(toParse);
            return new WeaponCard(cardName, cost,effects,isEffect);
        } catch (InvalidStringException e) {
                e.printStackTrace();
                return null;
        }
    }

    private static List<Effect> parseEffects(JSONObject toParse) throws InvalidStringException {
        List<Effect> effects = new ArrayList<>();

        JSONObject effect = (JSONObject) toParse.get("base-effect");

        JSONArray costJSON = (JSONArray) effect.get("cost");
        List<Resource> effectCost = new ArrayList<>();
        for(Object res: costJSON){
            effectCost.add(stringToResource(res.toString()));
        }
        effects.add(new Effect("base effect", effect.get("description").toString(),
                effectCost));

        JSONArray optionalEffectJ = (JSONArray) toParse.get("secondaryEffects");
        if(optionalEffectJ != null) {
            for (Object effectName : optionalEffectJ) {
                effect = (JSONObject) toParse.get(effectName.toString());
                JSONArray effCostJSON = (JSONArray) effect.get("cost");
                List<Resource> effCost = new ArrayList<>();
                for(Object res: effCostJSON){
                    effCost.add(stringToResource(res.toString()));
                }
                effects.add(new Effect(effectName.toString(), effect.get("description").toString(),
                        effCost));
            }
        }
        return effects;
    }

    private static List<Resource> parseResources(JSONObject toParse) throws InvalidStringException {
        List<Resource> cost = new ArrayList<>();
        JSONArray cardCost = (JSONArray) toParse.get("cost");
        for(Object res:cardCost){

                cost.add(stringToResource(res.toString()));

        }
        return cost;
    }
}
