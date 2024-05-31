package fr.traqueur.ressourcefulbees.api.events;

import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
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

    private final IBeeType fatherType;
    private final IBeeType motherType;
    private IBeeType childType;

    public BeeBreedEvent(IBeeType fatherType, IBeeType motherType, IBeeType childType) {
        this.fatherType = fatherType;
        this.motherType = motherType;
        this.childType = childType;
    }

    public IBeeType getFatherType() {
        return fatherType;
    }

    public IBeeType getMotherType() {
        return motherType;
    }

    public IBeeType getChildType() {
        return childType;
    }

    public void setChildType(IBeeType childType) {
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
