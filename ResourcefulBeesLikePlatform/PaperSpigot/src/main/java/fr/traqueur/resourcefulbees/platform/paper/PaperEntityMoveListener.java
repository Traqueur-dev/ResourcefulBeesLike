package fr.traqueur.resourcefulbees.platform.paper;

import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.events.BeeMutationEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.MutationsManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Mutation;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperEntityMoveListener implements Listener {

    private final MutationsManager mutationsManager;
    private final BeeTypeManager beeTypeManager;

    public PaperEntityMoveListener(ResourcefulBeesLikeAPI plugin, MutationsManager mutationsManager) {
        this.mutationsManager = mutationsManager;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if(event.getEntity().getType() != EntityType.BEE) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo().clone().subtract(0, 1,0);
        if(to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) {
            return;
        }

        if(to.getBlock().isEmpty()) {
            return;
        }

        Bee bee = (Bee) event.getEntity();
        if(!bee.hasNectar()) {
            return;
        }

        BeeType type = this.beeTypeManager.getBeeTypeFromBee(bee);
        Mutation mutation = this.mutationsManager.getMutation(type, to.getBlock().getType());
        BeeMutationEvent beeMutationEvent = new BeeMutationEvent(bee, to, mutation.getParent(), mutation.getChild());
        Bukkit.getPluginManager().callEvent(beeMutationEvent);
    }
}
