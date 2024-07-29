package fr.traqueur.resourcefulbees.api.events;

import fr.traqueur.resourcefulbees.api.models.BeeTools;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BeeCatchEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean cancel;

    private final ItemStack beeCatcher;
    private final Bee bee;
    private final Player player;
    private final BeeTools beeTools;

    public BeeCatchEvent(ItemStack beeCatcher, Bee bee, Player player, BeeTools beeTools) {
        this.beeCatcher = beeCatcher;
        this.bee = bee;
        this.player = player;
        this.cancel = false;
        this.beeTools = beeTools;
    }

    public ItemStack getBeeCatcher() {
        return beeCatcher;
    }

    public BeeTools getBeeTools() {
        return beeTools;
    }

    public Bee getBee() {
        return bee;
    }

    public Player getPlayer() {
        return player;
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
