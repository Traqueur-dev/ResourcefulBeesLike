package fr.traqueur.resourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;

import java.io.IOException;

public class BeehiveUpgradeAdapter extends TypeAdapter<BeehiveUpgrade> {

    private final static String UPGRADE_LEVEL = "upgrade_level";
    private final static String MULTIPLIER = "multiplier";
    private final static String REDUCER = "reducer";
    private final static String CRAFT = "craft";
    private final static String PRODUCE_BLOCK = "produce_block";

    private final BeehiveCraftAdapter craftAdapter;

    public BeehiveUpgradeAdapter() {
        this.craftAdapter = new BeehiveCraftAdapter();
    }

    @Override
    public void write(JsonWriter out, BeehiveUpgrade value) throws IOException {
        out.beginObject();
        out.name(UPGRADE_LEVEL).value(value.getUpgradeLevel());
        out.name(MULTIPLIER).value(value.multiplierProduction());
        out.name(REDUCER).value(value.reducerTicks());
        out.name(PRODUCE_BLOCK).value(value.produceBlocks());
        out.name(CRAFT);
        craftAdapter.write(out, value.getCraft());
        out.endObject();
    }

    @Override
    public BeehiveUpgrade read(JsonReader in) throws IOException {
        in.beginObject();
        int level = 0;
        double multiplier = 0, reducer = 0;
        boolean produceBlock = false;
        BeehiveCraft craft = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case UPGRADE_LEVEL:
                    level = in.nextInt();
                    break;
                case MULTIPLIER:
                    multiplier = in.nextDouble();
                    break;
                case REDUCER:
                    reducer = in.nextDouble();
                    break;
                case CRAFT:
                    craft = craftAdapter.read(in);
                    break;
                case PRODUCE_BLOCK:
                    produceBlock = in.nextBoolean();
                    break;
                default:
                    in.skipValue(); // Skip unknown keys
                    break;
            }
        }
        in.endObject();
        return new AdapterBeehiveUpgrade(level, multiplier, reducer, produceBlock, craft);
    }

    private record AdapterBeehiveUpgrade(int level, double multiplier, double reducer, boolean produceBlock, BeehiveCraft craft) implements BeehiveUpgrade {


        @Override
        public int getUpgradeLevel() {
            return level;
        }

        @Override
        public double multiplierProduction() {
            return multiplier;
        }

        @Override
        public double reducerTicks() {
            return reducer;
        }

        @Override
        public boolean produceBlocks() { return produceBlock;}

        @Override
        public BeehiveCraft getCraft() {
            return craft;
        }
    }
}
