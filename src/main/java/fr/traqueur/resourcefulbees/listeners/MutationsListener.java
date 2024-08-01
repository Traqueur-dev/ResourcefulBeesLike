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

    public MutationsListener(ResourcefulBeesLikePlugin plugin, MutationsManager mutationsManager) {
        this.mutationsManager = mutationsManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBeeMutation(BeeMutationEvent event) {
        this.mutationsManager.mutateBee(event.getLocation(), event.getChild());
        event.getBee().setHasNectar(false);
    }

}
