package fr.traqueur.resourcefulbees.api.events;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class BeeMoveEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Location from;
    private Location to;
    private boolean cancelled;
    
    public BeeMoveEvent(Bee entity, Location from, Location to) {
        super(entity);
        this.from = from;
        this.to = to;
    }

    public  Bee getEntity() {
        return (Bee) super.getEntity();
    }

    public  Location getFrom() {
        return this.from;
    }

    public void setFrom( Location from) {
        this.validateLocation(from);
        this.from = from;
    }

    public  Location getTo() {
        return this.to;
    }

    public void setTo(Location to) {
        this.validateLocation(to);
        this.to = to;
    }

    public boolean hasChangedPosition() {
        return this.hasExplicitlyChangedPosition() || !this.from.getWorld().equals(this.to.getWorld());
    }

    public boolean hasExplicitlyChangedPosition() {
        return this.from.getX() != this.to.getX() || this.from.getY() != this.to.getY() || this.from.getZ() != this.to.getZ();
    }

    public boolean hasChangedBlock() {
        return this.hasExplicitlyChangedBlock() || !this.from.getWorld().equals(this.to.getWorld());
    }

    public boolean hasExplicitlyChangedBlock() {
        return this.from.getBlockX() != this.to.getBlockX() || this.from.getBlockY() != this.to.getBlockY() || this.from.getBlockZ() != this.to.getBlockZ();
    }

    public boolean hasChangedOrientation() {
        return this.from.getPitch() != this.to.getPitch() || this.from.getYaw() != this.to.getYaw();
    }

    private void validateLocation( Location loc) {
        Preconditions.checkArgument(loc != null, "Cannot use null location!");
        Preconditions.checkArgument(loc.getWorld() != null, "Cannot use null location with null world!");
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public  HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static  HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
