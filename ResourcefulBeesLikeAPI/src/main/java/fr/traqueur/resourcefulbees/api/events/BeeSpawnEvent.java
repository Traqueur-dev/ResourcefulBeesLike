package fr.traqueur.resourcefulbees.api.events;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class BeeSpawnEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel = false;
    private final BeeType type;
    private final Location location;
    private final boolean baby;
    private final boolean nectar;
    private final CreatureSpawnEvent.SpawnReason spawnReason;

    public BeeSpawnEvent(BeeType type, Location location, boolean baby, boolean nectar,
                         CreatureSpawnEvent.SpawnReason spawnReason) {
        this.type = type;
        this.location = location;
        this.baby = baby;
        this.nectar = nectar;
        this.spawnReason = spawnReason;
    }

    public CreatureSpawnEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    public BeeType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isBaby() {
        return baby;
    }

    public boolean hasNectar() {
        return nectar;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
