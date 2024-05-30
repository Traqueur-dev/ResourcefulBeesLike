package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BeeListener implements Listener {

    private final IBeesManager manager;

    public BeeListener(IBeesManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onTryToSpawnBee(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if(action != Action.RIGHT_CLICK_BLOCK || block == null) {
            return;
        }
        this.parseInteraction(event, player, block.getLocation(), false);
    }

    @EventHandler
    public void onTryToSpawnBeeOnEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity.getType() != EntityType.BEE) {
            return;
        }
        this.parseInteraction(event, player, entity.getLocation(), true);
    }

    private <T extends Cancellable> void parseInteraction(T event, Player player, Location location, boolean baby) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!this.manager.isBeeSpawnEgg(item)) {
            return;
        }

        event.setCancelled(true);
        BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent();
        Bukkit.getPluginManager().callEvent(beeSpawnEvent);
        if(beeSpawnEvent.isCancelled()) {
            return;
        }
        if(player.getGameMode() != GameMode.CREATIVE) {
            item.subtract();
        }
        String name = item.getItemMeta().getPersistentDataContainer().getOrDefault(Keys.BEE_NAME, PersistentDataType.STRING, "Bee");
        this.manager.spawnBee(location, name, baby);
    }

}
