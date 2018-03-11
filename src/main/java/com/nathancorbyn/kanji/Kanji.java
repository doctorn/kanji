package com.nathancorbyn.kanji;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Kanji {
    private List<Kanji> components;
    private String character;
    private boolean radical;

    public Kanji(String character, List<Kanji> components) {
        this.character = character;
        this.components = new ArrayList<>(components);
        if (components.size() == 0)
            this.radical = true;
    }

    public String getCharacter() {
        return character;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("character", character);
        object.put("radical", radical); 
        if (!radical) {
            JSONArray componentArray = new JSONArray();
            for (Kanji kanji : components)
                componentArray.put(kanji.toJSON());
            object.put("components", componentArray);
        }

        return object;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (character != null ? character.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kanji object = (Kanji) o;

        return !(character != null ? !character.equals(object.character) : object.character != null);
    }
}
