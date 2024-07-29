package fr.traqueur.resourcefulbees.api.events;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BeeBreedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel = false;

    private final BeeType fatherType;
    private final BeeType motherType;
    private BeeType childType;

    public BeeBreedEvent(BeeType fatherType, BeeType motherType, BeeType childType) {
        this.fatherType = fatherType;
        this.motherType = motherType;
        this.childType = childType;
    }

    public BeeType getFatherType() {
        return fatherType;
    }

    public BeeType getMotherType() {
        return motherType;
    }

    public BeeType getChildType() {
        return childType;
    }

    public void setChildType(BeeType childType) {
        this.childType = childType;
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
