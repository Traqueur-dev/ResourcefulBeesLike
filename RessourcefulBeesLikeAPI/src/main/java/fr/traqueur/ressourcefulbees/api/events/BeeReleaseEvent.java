package fr.traqueur.ressourcefulbees.api.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BeeReleaseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel = false;

    private final ItemStack beebox;
    private final Location location;
    private final boolean all;

    public BeeReleaseEvent(ItemStack beebox, Location location, boolean sneak) {
        this.beebox = beebox;
        this.location = location;
        this.all = sneak;
    }

    public ItemStack getBeebox() {
        return beebox;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isAll() {
        return all;
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
