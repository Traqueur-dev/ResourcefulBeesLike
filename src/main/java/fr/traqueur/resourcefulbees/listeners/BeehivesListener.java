package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeehivePersistentDataType;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeehivesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.models.ResourcefulBeehive;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

public class BeehivesListener implements Listener {

    private final BeeTypeManager beeTypeManager;
    private final BeehivesManager beehivesManager;

    public BeehivesListener(BeehivesManager beehivesManager, BeeTypeManager beeTypeManager) {
        this.beeTypeManager = beeTypeManager;
        this.beehivesManager = beehivesManager;
    }

    @EventHandler
    public void onBeehiveBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(!(block.getState() instanceof Beehive beehive)) {
            return;
        }
        if(block.getDrops().isEmpty()) {
            return;
        }
        ItemStack item = new ArrayList<>(block.getDrops()).getFirst();
        if(item.getType() != Material.BEEHIVE && item.getType() != Material.BEE_NEST) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta blockStateMeta)) {
            return;
        }

        event.setCancelled(true);
        event.setDropItems(false);

        int honeyLevel = ((org.bukkit.block.data.type.Beehive) beehive.getBlockData()).getHoneyLevel();

        PersistentDataContainer itemContainer = blockStateMeta.getPersistentDataContainer();
        PersistentDataContainer container = beehive.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive resourcefulBeehive = container.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        itemContainer.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, resourcefulBeehive);
        itemContainer.set(Keys.HONEY_LEVEL, PersistentDataType.INTEGER, honeyLevel);
        blockStateMeta.setDisplayName(this.beehivesManager.getPlugin().reset(this.beehivesManager.getPlugin().translate(LangKeys.BEEHIVE_NAME, Formatter.upgrade(resourcefulBeehive.getUpgrade()))));
        blockStateMeta.setBlockState(beehive);
        item.setItemMeta(blockStateMeta);

        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), item);
    }

    @EventHandler
    public void onBeehivePlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();
        if(!(block.getState() instanceof Beehive beehive)) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if(!(meta instanceof BlockStateMeta blockStateMeta)) {
            return;
        }

        PersistentDataContainer itemContainer = blockStateMeta.getPersistentDataContainer();
        PersistentDataContainer container = beehive.getPersistentDataContainer();

        fr.traqueur.resourcefulbees.api.models.Beehive resourcefulBeehive = itemContainer.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        container.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, resourcefulBeehive);

        org.bukkit.block.data.type.Beehive beehiveData = (org.bukkit.block.data.type.Beehive) beehive.getBlockData();
        beehiveData.setHoneyLevel(itemContainer.getOrDefault(Keys.HONEY_LEVEL, PersistentDataType.INTEGER, 0));
        beehive.setBlockData(beehiveData);

        beehive.update();
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.BEE) {
            return;
        }
        Block block = event.getBlock();
        Bee bee = (Bee) event.getEntity();
        BeeType beeType = this.beeTypeManager.getBeeTypeFromBee(bee);
        this.beehivesManager.getPlugin().getScheduler().runNextTick((task) -> {
            if(!(block.getState() instanceof Beehive beehive)) {
                return;
            }

            if(!(beehive.getBlockData() instanceof org.bukkit.block.data.type.Beehive beehiveData)) {
                return;
            }

            if(beehiveData.getHoneyLevel() > beehiveData.getMaximumHoneyLevel()) {
                return;
            }

            this.beehivesManager.addHoneycombToBeehive(beehive, beeType);
        });
    }


    @EventHandler
    public void onEnterBeehive(EntityEnterBlockEvent event) {
        if(event.getEntity().getType() != EntityType.BEE) {
            return;
        }
        Bee bee = (Bee) event.getEntity();
        BeeType beeType = this.beeTypeManager.getBeeTypeFromBee(bee);
        this.beehivesManager.getPlugin().getScheduler().runNextTick((task) -> {
            if(!(event.getBlock().getState() instanceof Beehive beehive)) {
                return;
            }
            this.beehivesManager.addBeeToHive(beehive, beeType);
        });
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BEEHIVE) {
            return;
        }

        Bee bee = (Bee) event.getEntity();
        Location hiveLocation = bee.getHive();
        if(hiveLocation == null) {
            BeeLogger.log(Level.WARNING, "&cBeehive location is null, can't remove bee to hive.");
            return;
        }
        Block block = hiveLocation.getBlock();

        this.beehivesManager.getPlugin().getScheduler().runNextTick((task) -> {
            if(!(block.getState() instanceof Beehive beehive)) {
                return;
            }
            BeeType type = this.beehivesManager.removeBeeFromHive(beehive);
            BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(type, bee.getLocation(),!bee.isAdult(), bee.hasNectar(), CreatureSpawnEvent.SpawnReason.BEEHIVE);
            Bukkit.getPluginManager().callEvent(beeSpawnEvent);
            bee.remove();
        });
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if(!(event.getClickedBlock().getState() instanceof Beehive beehive)) {
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SHEARS) {
            return;
        }

        org.bukkit.block.data.type.Beehive beehiveData = (org.bukkit.block.data.type.Beehive) event.getClickedBlock().getBlockData();
        if(beehiveData.getHoneyLevel() < beehiveData.getMaximumHoneyLevel()) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);

        PersistentDataContainer container = beehive.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive beehiveRessourceful = container.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        Map<BeeType, Integer> honeyCombs = Map.copyOf(beehiveRessourceful.getHoneycombs());
        double multiplier = beehiveRessourceful.getUpgrade().multiplierProduction();
        honeyCombs.forEach((key, value) -> {
            beehiveRessourceful.removeHoneycomb(key, value);
            int amount = (int) (value * multiplier);
            do {
                ItemStack honey = key.getHoney(Math.min(amount, 64));
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), honey);
                amount -= honey.getAmount();
            } while (amount > 0);
        });
        container.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, beehiveRessourceful);
        beehiveData.setHoneyLevel(0);
        beehive.setBlockData(beehiveData);
        beehive.update();
    }
}
