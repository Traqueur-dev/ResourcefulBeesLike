package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.api.events.BeeCatchEvent;
import fr.traqueur.resourcefulbees.api.events.BeeReleaseEvent;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.models.BeeTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ToolsListener implements Listener {

    private final ToolsManager manager;
    private boolean isEntityInteraction;

    public ToolsListener(ToolsManager manager) {
        this.manager = manager;
        this.isEntityInteraction = false;
    }

    @EventHandler
    public void onTryCatchBee(PlayerInteractEntityEvent event) {
        isEntityInteraction = true;

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Entity bee = event.getRightClicked();
        ItemStack beebox = player.getInventory().getItemInMainHand();
        BeeTools beeTools = this.manager.isBeeJar(beebox) ? BeeTools.BEE_JAR : BeeTools.BEE_BOX;
        if (bee.getType() != EntityType.BEE || (!this.manager.isBeesBox(beebox) && !this.manager.isBeeJar(beebox))) {
            return;
        }

        event.setCancelled(true);
        BeeCatchEvent beeCatchEvent = new BeeCatchEvent(beebox, (Bee) bee, player, beeTools);
        Bukkit.getPluginManager().callEvent(beeCatchEvent);
    }

    @EventHandler
    public void onTryReleaseBee(PlayerInteractEvent event) {
        if(isEntityInteraction) {
            isEntityInteraction = false;
            return;
        }

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Location location = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : player.getLocation();
        ItemStack beebox = player.getInventory().getItemInMainHand();
        BeeTools beeTools = this.manager.isBeeJar(beebox) ? BeeTools.BEE_JAR : BeeTools.BEE_BOX;
        if (!this.manager.isBeesBox(beebox) && !this.manager.isBeeJar(beebox)) {
            return;
        }

        boolean sneak = player.isSneaking();
        event.setCancelled(true);

        BeeReleaseEvent beeReleaseEvent = new BeeReleaseEvent(beebox, location == null ? player.getLocation() : location, sneak, beeTools);
        Bukkit.getPluginManager().callEvent(beeReleaseEvent);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCatch(BeeCatchEvent event) {
        if(this.manager.isBeeBoxFull(event.getBeeCatcher())) {
            this.manager.getPlugin().error(event.getPlayer(), this.manager.getPlugin().translate(LangKeys.BEE_BOX_FULL));
            return;
        }

        if(this.manager.isBeeJarFull(event.getBeeCatcher())) {
            this.manager.getPlugin().error(event.getPlayer(), this.manager.getPlugin().translate(LangKeys.BEE_JAR_FULL));
            return;
        }

        if(event.getBeeTools() == BeeTools.BEE_BOX) {
            this.manager.addToBeeBox(event.getBeeCatcher(), event.getBee());
        } else if(event.getBeeTools() == BeeTools.BEE_JAR) {
            this.manager.addToBeeJar(event.getBeeCatcher(), event.getBee());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRelease(BeeReleaseEvent event) {
        if(event.getBeeTools() == BeeTools.BEE_BOX) {
            this.manager.releaseBeeFromBox(event.getBeeCatcher(), event.getLocation(), event.isAll());
        } else if(event.getBeeTools() == BeeTools.BEE_JAR) {
            this.manager.releaseBeeFromJar(event.getBeeCatcher(),event.getLocation());
        }


    }

}
