package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        item.subtract();
        String name = item.getItemMeta().getPersistentDataContainer().getOrDefault(Keys.BEE_NAME, PersistentDataType.STRING, "Bee");
        this.manager.spawnBee(block.getLocation(), name);
    }

}
