package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.events.BeeMoveEvent;
import fr.traqueur.resourcefulbees.api.events.BeeMutationEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.MutationsManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Mutation;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MutationsListener implements Listener {

    private final MutationsManager mutationsManager;
    private final BeeTypeManager beeTypeManager;

    public MutationsListener(ResourcefulBeesLikePlugin plugin, MutationsManager mutationsManager) {
        this.mutationsManager = mutationsManager;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
    }

    @EventHandler
    public void onEntityMove(BeeMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo().clone().subtract(0, 1,0);
        if(to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) {
            return;
        }

        if(to.getBlock().isEmpty()) {
            return;
        }

        Bee bee = event.getEntity();
        if(!bee.hasNectar()) {
            return;
        }

        BeeType type = this.beeTypeManager.getBeeTypeFromBee(bee);
        Mutation mutation = this.mutationsManager.getMutation(type, to.getBlock().getType());
        BeeMutationEvent beeMutationEvent = new BeeMutationEvent(bee, to, mutation.getParent(), mutation.getChild());
        Bukkit.getPluginManager().callEvent(beeMutationEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBeeMutation(BeeMutationEvent event) {
        this.mutationsManager.mutateBee(event.getLocation(), event.getChild());
        event.getBee().setHasNectar(false);
    }

}
