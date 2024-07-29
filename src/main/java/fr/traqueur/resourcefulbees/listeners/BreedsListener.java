package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.api.events.BeeBreedEvent;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BreedsManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Breed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedsListener implements Listener {

    private final BreedsManager breedsManager;
    private final BeeTypeManager beeTypeManager;

    public BreedsListener(BreedsManager manager) {
        this.breedsManager = manager;
        this.beeTypeManager = manager.getPlugin().getManager(BeeTypeManager.class);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getEntity() instanceof Bee)) {
            return;
        }

        Bee father = (Bee) event.getFather();
        Bee mother = (Bee) event.getMother();
        event.getEntity().remove();

        BeeType fatherType = beeTypeManager.getBeeTypeFromBee(father);
        BeeType motherType = beeTypeManager.getBeeTypeFromBee(mother);
        Breed breed = breedsManager.getBreed(fatherType, motherType);
        BeeType childType;

        double random = Math.random();
        if(breed == null || random > breed.getPercent()) {
            childType = Math.random() > 0.5 ? fatherType : motherType;
        } else {
            childType = breed.getChild();
        }
        BeeBreedEvent beeBreedEvent = new BeeBreedEvent(fatherType, motherType, childType);
        Bukkit.getPluginManager().callEvent(beeBreedEvent);
        if (beeBreedEvent.isCancelled()) {
            return;
        }
        BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(beeBreedEvent.getChildType(), mother.getLocation(), true, false, CreatureSpawnEvent.SpawnReason.BREEDING);
        Bukkit.getPluginManager().callEvent(beeSpawnEvent);
    }
}
