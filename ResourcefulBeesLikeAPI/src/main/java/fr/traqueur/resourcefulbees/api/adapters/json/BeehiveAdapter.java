package fr.traqueur.resourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Beehive;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class BeehiveAdapter extends TypeAdapter<Beehive> {

    private static final String UPGRADE = "upgrade";
    private static final String BEES = "bees";
    private static final String HONEYCOMBS = "honeycombs";

    private final BeeTypeManager manager;
    private final BeehiveUpgradeAdapter upgradeAdapter;

    public BeehiveAdapter(BeeTypeManager manager) {
        this.manager = manager;
        this.upgradeAdapter = new BeehiveUpgradeAdapter();
    }

    @Override
    public void write(JsonWriter jsonWriter, Beehive beehive) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(UPGRADE);
        upgradeAdapter.write(jsonWriter, beehive.getUpgrade());
        jsonWriter.name(BEES);
        jsonWriter.beginObject();
        for (Map.Entry<BeeType, List<Long>> entry : beehive.getBees().entrySet()) {
            jsonWriter.name(entry.getKey().getType());
            jsonWriter.beginArray();
            for (Long timestamp : entry.getValue()) {
                jsonWriter.value(timestamp);
            }
            jsonWriter.endArray();
        }
        jsonWriter.endObject();
        jsonWriter.name(HONEYCOMBS);
        jsonWriter.beginObject();
        for (Map.Entry<BeeType, Integer> entry : beehive.getHoneycombs().entrySet()) {
            jsonWriter.name(entry.getKey().getType());
            jsonWriter.value(entry.getValue());
        }
        jsonWriter.endObject();
        jsonWriter.endObject();
    }

    @Override
    public Beehive read(JsonReader jsonReader) throws IOException {
        BeehiveUpgrade upgrade = null;
        Map<BeeType, List<Long>> bees = new HashMap<>();
        Map<BeeType, Integer> honeycombs = new HashMap<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case UPGRADE -> upgrade = upgradeAdapter.read(jsonReader);
                case BEES -> {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        List<Long> timestamps = new ArrayList<>();
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            timestamps.add(jsonReader.nextLong());
                        }
                        jsonReader.endArray();
                        BeeType beeType = this.manager.getBeeType(key);
                        bees.put(beeType, timestamps);
                    }
                    jsonReader.endObject();
                }
                case HONEYCOMBS -> {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        int value = jsonReader.nextInt();
                        BeeType beeType = this.manager.getBeeType(key);
                        honeycombs.put(beeType, value);
                    }
                    jsonReader.endObject();
                }
            }
        }
        jsonReader.endObject();
        return new AdapterBeehive(upgrade, bees, honeycombs);
    }

    private static class AdapterBeehive implements Beehive {

        private final Map<BeeType, List<Long>> bees;
        private final Map<BeeType, Integer> honeycombs;
        private BeehiveUpgrade upgrade;

        public AdapterBeehive(BeehiveUpgrade upgrade, Map<BeeType, List<Long>> bees, Map<BeeType, Integer> honeycombs) {
            this.upgrade = upgrade;
            this.bees = bees;
            this.honeycombs = honeycombs;
        }

        @Override
        public BeehiveUpgrade getUpgrade() {
            return this.upgrade;
        }

        @Override
        public void setUpgrade(BeehiveUpgrade upgrade) {
            this.upgrade = upgrade;
        }

        @Override
        public Map<BeeType, List<Long>> getBees() {
            bees.remove(null);
            return this.bees;
        }

        @Override
        public Map<BeeType, Integer> getHoneycombs() {
            honeycombs.remove(null);
            return this.honeycombs;
        }

        public void addBee(BeeType beeType, int amount) {
            List<Long> timestamps = this.bees.getOrDefault(beeType, new ArrayList<>());
            for (int i = 0; i < amount; i++) {
                timestamps.add(System.currentTimeMillis());
            }
            this.bees.put(beeType, timestamps);
        }

        public List<BeeType> removeBee(int amount) {
            // Collect all bees in a sorted list
            List<Map.Entry<BeeType, Long>> allBees = this.bees.entrySet().stream()
                    .flatMap(entry -> entry.getValue()
                            .stream()
                            .map(timestamp -> new AbstractMap.SimpleEntry<>(entry.getKey(), timestamp)))
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toList());
            allBees = new CopyOnWriteArrayList<>(allBees);

            // Prepare the list of removed bee types
            List<BeeType> removedBeeTypes = new CopyOnWriteArrayList<>();

            // Remove the specified amount of older bees
            for (int i = 0; i < amount && !allBees.isEmpty(); i++) {
                Map.Entry<BeeType, Long> beeEntry = allBees.removeFirst();
                BeeType beeType = beeEntry.getKey();
                Long timestamp = beeEntry.getValue();

                // Remove the timestamp from the original bees map
                List<Long> timestamps = bees.get(beeType);
                timestamps.remove(timestamp);
                if (timestamps.isEmpty()) {
                    bees.remove(beeType);
                } else {
                    bees.put(beeType, timestamps);
                }

                // Add the bee type to the list of removed bees
                removedBeeTypes.add(beeType);
            }
            return removedBeeTypes;
        }

        @Override
        public void addHoneycomb(BeeType beeType, int amount) {
            int prevAmount = this.honeycombs.getOrDefault(beeType, 0);
            this.honeycombs.put(beeType, prevAmount + amount);
        }

        @Override
        public void removeHoneycomb(BeeType beeType, int amount) {
            int prevAmount = this.honeycombs.getOrDefault(beeType, -1);
            if(prevAmount == -1) {
                return;
            }
            int newAmount = prevAmount - amount;
            if(newAmount <= 0) {
                this.honeycombs.remove(beeType);
            } else {
                this.honeycombs.put(beeType, newAmount);
            }
        }
    }
}
