package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

public class BeeListener implements Listener {

    private final BeesManager manager;
    private final BeeTypeManager beeTypeManager;

    public BeeListener(BeesManager manager, BeeTypeManager beeTypeManager) {
        this.manager = manager;
        this.beeTypeManager = beeTypeManager;
    }

    @EventHandler
    public void onTryToSpawnBeeOnEntity(PlayerInteractEntityEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity.getType() != EntityType.BEE) {
            return;
        }
        this.parseInteraction(event, player, entity.getLocation(), true);
    }

    @EventHandler
    public void onTryToSpawnBee(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if(action != Action.RIGHT_CLICK_BLOCK || block == null) {
            return;
        }
        this.parseInteraction(event, player, block.getLocation(), false);
    }

    private void parseInteraction(Cancellable event, Player player, Location location, boolean baby) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!this.manager.isBeeSpawnEgg(item)) {
            return;
        }

        event.setCancelled(true);
        if(player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
        BeeType bee = item.getItemMeta().getPersistentDataContainer().getOrDefault(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, this.beeTypeManager.getBeeType("normal"));
        BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(bee,location, baby, false, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        Bukkit.getPluginManager().callEvent(beeSpawnEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnBee(BeeSpawnEvent event) {
        this.manager.spawnBee(event.getLocation(), event.getType(), event.isBaby(), event.hasNectar());
    }

    @EventHandler
    public void onLoad(EntitiesLoadEvent event) {
        List<Bee> bees = event.getEntities().stream()
                .filter(entity -> entity.getType() == EntityType.BEE)
                .map(entity -> (Bee) entity)
                .filter(bee -> bee.getPersistentDataContainer().has(Keys.BEE))
                .toList();

        Iterator<Bee> iterator = bees.iterator();
        while(iterator.hasNext()) {
            Bee bee = iterator.next();
            Location location = bee.getLocation();
            BeeType type = bee.getPersistentDataContainer()
                    .getOrDefault(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, this.beeTypeManager.getBeeType("normal_bee"));

            BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(type, location, !bee.isAdult(), bee.hasNectar(), CreatureSpawnEvent.SpawnReason.NATURAL);
            Bukkit.getPluginManager().callEvent(beeSpawnEvent);
            bee.remove();
        }
    }

}
