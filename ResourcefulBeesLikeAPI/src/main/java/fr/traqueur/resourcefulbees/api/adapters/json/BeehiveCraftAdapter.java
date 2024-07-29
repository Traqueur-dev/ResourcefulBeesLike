package fr.traqueur.resourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BeehiveCraftAdapter extends TypeAdapter<BeehiveCraft> {

    private final static String PATTERN = "pattern";
    private final static String INGREDIENTS = "ingredients";

    @Override
    public void write(JsonWriter out, BeehiveCraft value) throws IOException {
        out.beginObject();
        out.name(PATTERN);
        out.beginArray();
        for (String row : value.getPattern()) {
            out.value(row);
        }
        out.endArray();
        out.name(INGREDIENTS);
        out.beginObject();
        for (Map.Entry<String, String> entry : value.getIngredients().entrySet()) {
            out.name(entry.getKey());
            out.value(entry.getValue());
        }
        out.endObject();
        out.endObject();
    }

    @Override
    public BeehiveCraft read(JsonReader in) throws IOException {
        Map<String, String> ingredients = new HashMap<>();
        String[] pattern = new String[3];

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case PATTERN:
                    in.beginArray();
                    for (int i = 0; i < 3; i++) {
                        pattern[i] = in.nextString();
                    }
                    in.endArray();
                    break;
                case INGREDIENTS:
                    in.beginObject();
                    while (in.hasNext()) {
                        String key = in.nextName();
                        String value = in.nextString();
                        ingredients.put(key, value);
                    }
                    in.endObject();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return new AdapterBeehiveCraft(pattern, ingredients);
    }

    record AdapterBeehiveCraft(String[] pattern, Map<String, String> ingredients) implements BeehiveCraft {
        @Override
        public String[] getPattern() {
            return pattern;
        }

        @Override
        public Map<String, String> getIngredients() {
            return ingredients;
        }
    }
}
