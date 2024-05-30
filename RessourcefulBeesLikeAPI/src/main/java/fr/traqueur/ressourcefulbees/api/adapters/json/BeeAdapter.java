package fr.traqueur.ressourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.IBee;

import java.io.IOException;

public class BeeAdapter extends TypeAdapter<IBee> {

    private static final String TYPE = "beetype";
    private static final String BABY = "baby";

    private final IBeeTypeManager manager;

    public BeeAdapter(IBeeTypeManager manager) {
        this.manager = manager;
    }

    @Override
    public void write(JsonWriter jsonWriter, IBee iBee) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(TYPE).value(iBee.getBeeType().getName());
        jsonWriter.name(BABY).value(iBee.isBaby());
        jsonWriter.endObject();

    }

    @Override
    public IBee read(JsonReader jsonReader) throws IOException {
        BeeType type = null;
        boolean baby = false;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case TYPE -> type = this.manager.getBeeType(jsonReader.nextString());
                case BABY -> baby = jsonReader.nextBoolean();
            }
        }
        jsonReader.endObject();

        return new Bee(type, baby);
    }

    private record Bee(BeeType type, boolean baby) implements IBee {
        @Override
        public BeeType getBeeType() {
            return type;
        }

        @Override
        public boolean isBaby() {
            return baby;
        }
    }
}
