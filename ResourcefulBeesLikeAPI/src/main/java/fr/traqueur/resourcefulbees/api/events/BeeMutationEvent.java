package fr.traqueur.resourcefulbees.api.events;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BeeMutationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final Bee bee;
    private final Location location;
    private final BeeType parent;
    private BeeType child;
    private boolean cancel;

    public BeeMutationEvent(Bee bee, Location location, BeeType parent, BeeType child) {
        this.bee = bee;
        this.location = location;
        this.parent = parent;
        this.child = child;
        this.cancel = false;
    }

    public Bee getBee() {
        return bee;
    }

    public Location getLocation() {
        return this.location;
    }

    public BeeType getParent() {
        return this.parent;
    }

    public BeeType getChild() {
        return this.child;
    }

    public void setChild(BeeType child) {
        this.child = child;
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
