package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.api.events.BeeCatchEvent;
import fr.traqueur.ressourcefulbees.api.events.BeeReleaseEvent;
import fr.traqueur.ressourcefulbees.api.managers.IToolsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private final IToolsManager manager;
    private boolean isEntityInteraction;

    public ToolsListener(IToolsManager manager) {
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
        if (bee.getType() != EntityType.BEE || !this.manager.isBeesBox(beebox)) {
            return;
        }

        event.setCancelled(true);
        BeeCatchEvent beeCatchEvent = new BeeCatchEvent(beebox, (Bee) bee, player);
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
        Location location = event.getInteractionPoint();
        ItemStack beebox = player.getInventory().getItemInMainHand();
        if (!this.manager.isBeesBox(beebox)) {
            return;
        }

        boolean sneak = player.isSneaking();
        event.setCancelled(true);

        BeeReleaseEvent beeReleaseEvent = new BeeReleaseEvent(beebox, location == null ? player.getLocation() : location, sneak);
        Bukkit.getPluginManager().callEvent(beeReleaseEvent);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCatch(BeeCatchEvent event) {
        if(this.manager.isBeeBoxFull(event.getBeeBox())) {
            event.getPlayer().sendMessage(Component.text("Bee Box remplie!", NamedTextColor.RED));
            return;
        }

        this.manager.addToBeeBox(event.getBeeBox(), event.getBee());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRelease(BeeReleaseEvent event) {
        this.manager.releaseBee(event.getBeebox(), event.getLocation(), event.isAll());
    }

}
