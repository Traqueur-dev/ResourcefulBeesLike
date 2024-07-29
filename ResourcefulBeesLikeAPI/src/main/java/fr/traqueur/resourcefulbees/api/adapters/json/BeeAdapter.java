package fr.traqueur.resourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.Bee;
import fr.traqueur.resourcefulbees.api.models.BeeType;

import java.io.IOException;

public class BeeAdapter extends TypeAdapter<Bee> {

    private static final String TYPE = "beetype";
    private static final String BABY = "baby";
    private static final String NECTAR = "nectar";

    private final BeeTypeManager manager;

    public BeeAdapter(BeeTypeManager manager) {
        this.manager = manager;
    }

    @Override
    public void write(JsonWriter jsonWriter, fr.traqueur.resourcefulbees.api.models.Bee bee) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(TYPE).value(bee.getBeeType().getType());
        jsonWriter.name(BABY).value(bee.isBaby());
        jsonWriter.name(NECTAR).value(bee.hasNectar());
        jsonWriter.endObject();

    }

    @Override
    public fr.traqueur.resourcefulbees.api.models.Bee read(JsonReader jsonReader) throws IOException {
        BeeType type = null;
        boolean baby = false;
        boolean nectar = false;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case TYPE -> type = this.manager.getBeeType(jsonReader.nextString());
                case BABY -> baby = jsonReader.nextBoolean();
                case NECTAR -> nectar = jsonReader.nextBoolean();
            }
        }
        jsonReader.endObject();

        return new Bee(type, baby, nectar);
    }

    private record Bee(BeeType type, boolean baby, boolean nectar) implements fr.traqueur.resourcefulbees.api.models.Bee {
        @Override
        public BeeType getBeeType() {
            return type;
        }

        @Override
        public boolean isBaby() {
            return baby;
        }

        @Override
        public boolean hasNectar() {
            return nectar;
        }
    }
}
