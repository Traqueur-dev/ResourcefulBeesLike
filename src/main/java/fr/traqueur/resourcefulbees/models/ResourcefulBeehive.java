package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Beehive;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ResourcefulBeehive implements Beehive {

    private BeehiveUpgrade upgrade;
    private final Map<BeeType, List<Long>> bees;
    private final Map<BeeType, Integer> honeycombs;

    public ResourcefulBeehive() {
        this.bees = new HashMap<>();
        this.honeycombs = new HashMap<>();
        this.upgrade = JavaPlugin.getPlugin(ResourcefulBeesLikePlugin.class)
                .getManager(UpgradesManager.class)
                .getUpgrade(1);
    }

    @Override
    public BeehiveUpgrade getUpgrade() {
        return this.upgrade;
    }

    @Override
    public void setUpgrade(BeehiveUpgrade upgrade) {
        this.upgrade = upgrade;
    }

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
