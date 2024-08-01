package fr.traqueur.resourcefulbees.platform.spigot.listeners;

import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;

import java.util.HashMap;
import java.util.Map;

public class SpigotBeehivesUpdateHandler implements Listener {

    private final Map<Location, Integer> amountOfBees;
    private final Map<Location, Integer> oldLevel;

    public SpigotBeehivesUpdateHandler() {
        this.oldLevel = new HashMap<>();
        this.amountOfBees = new HashMap<>();
    }

    public void addBeehive(Location location) {
        Block block = location.getBlock();
        if(!(block.getState() instanceof Beehive beehive) ) {
            throw new IllegalArgumentException("Block is not a beehive");
        }

        if(!(beehive.getBlockData() instanceof org.bukkit.block.data.type.Beehive beehiveData)) {
            throw new IllegalArgumentException("Block is not a beehive");
        }
        this.oldLevel.put(location, beehiveData.getHoneyLevel());
        this.amountOfBees.put(location, this.amountOfBees.getOrDefault(location, 0) + 1);
    }

    public void updateBeehive(Location location, Bee bee) {
        if (oldLevel.containsKey(location)) {
            Block block = location.getBlock();
            if (!(block.getState() instanceof Beehive beehive)) {
                throw new IllegalArgumentException("Block is not a beehive");
            }

            if (!(beehive.getBlockData() instanceof org.bukkit.block.data.type.Beehive beehiveData)) {
                throw new IllegalArgumentException("Block is not a beehive");
            }

            if (beehiveData.getHoneyLevel() > this.oldLevel.getOrDefault(location, Integer.MAX_VALUE)) {
                this.oldLevel.put(location, beehiveData.getHoneyLevel());
                EntityChangeBlockEvent event = new EntityChangeBlockEvent(bee, block, beehiveData);
                Bukkit.getPluginManager().callEvent(event);
            }
            this.amountOfBees.put(location, Math.max(this.amountOfBees.getOrDefault(location, 0) - 1, 0));
        }
    }

    @EventHandler
    public void onEnter(EntityEnterBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.BEE) {
            return;
        }
        Location location = event.getBlock().getLocation();
        this.addBeehive(location);
    }

    @EventHandler
    public void onQuit(CreatureSpawnEvent event) {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BEEHIVE) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.BEE) {
            return;
        }
        Bee bee = (Bee) entity;
        Location location = bee.getHive();
        this.updateBeehive(location, bee);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(this.oldLevel.containsKey(event.getBlock().getLocation())) {
            this.oldLevel.remove(event.getBlock().getLocation());
            this.amountOfBees.remove(event.getBlock().getLocation());
        }
    }

}
