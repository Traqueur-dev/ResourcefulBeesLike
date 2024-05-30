package fr.traqueur.ressourcefulbees.api.events;

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

    private boolean cancel = false;

    private final ItemStack beeBox;
    private final Bee bee;
    private final Player player;

    public BeeCatchEvent(ItemStack beeBox, Bee bee, Player player) {
        this.beeBox = beeBox;
        this.bee = bee;
        this.player = player;
    }

    public ItemStack getBeeBox() {
        return beeBox;
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
