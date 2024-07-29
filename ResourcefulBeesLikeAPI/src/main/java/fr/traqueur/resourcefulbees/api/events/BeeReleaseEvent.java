package fr.traqueur.resourcefulbees.api.events;

import fr.traqueur.resourcefulbees.api.models.BeeTools;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BeeReleaseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public  HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel = false;

    private final ItemStack beeCatcher;
    private final Location location;
    private final BeeTools beeTools;
    private final boolean all;

    public BeeReleaseEvent(ItemStack beeCatcher, Location location, boolean sneak, BeeTools beeTools) {
        this.beeCatcher = beeCatcher;
        this.location = location;
        this.all = sneak;
        this.beeTools = beeTools;
    }

    public BeeTools getBeeTools() {
        return beeTools;
    }

    public ItemStack getBeeCatcher() {
        return beeCatcher;
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
