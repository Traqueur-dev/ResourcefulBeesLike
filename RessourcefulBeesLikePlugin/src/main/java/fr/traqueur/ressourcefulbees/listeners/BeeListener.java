package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.ressourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeePersistentDataType;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.IBee;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.models.Bee;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BeeListener implements Listener {

    private final IBeesManager manager;
    private final IBeeTypeManager beeTypeManager;
    private boolean isEntityInteraction;

    public BeeListener(IBeesManager manager, IBeeTypeManager beeTypeManager) {
        this.manager = manager;
        this.beeTypeManager = beeTypeManager;
    }

    @EventHandler
    public void onTryToSpawnBeeOnEntity(PlayerInteractEntityEvent event) {
        isEntityInteraction = true;

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
        if(isEntityInteraction) {
            isEntityInteraction = false;
            return;
        }
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
            item.subtract();
        }
        BeeType bee = item.getItemMeta().getPersistentDataContainer().getOrDefault(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, this.beeTypeManager.getBeeType("normal"));
        BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(bee,location, baby);
        Bukkit.getPluginManager().callEvent(beeSpawnEvent);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
    public void onSpawnBee(BeeSpawnEvent event) {
        this.manager.spawnBee(event.getLocation(), event.getType(), event.isBaby());
    }

}
