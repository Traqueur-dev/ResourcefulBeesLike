package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeehivePersistentDataType;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeehivesManager;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.listeners.BeehivesListener;
import fr.traqueur.resourcefulbees.listeners.CraftListener;
import fr.traqueur.resourcefulbees.models.ResourcefulBeehive;
import fr.traqueur.resourcefulbees.platform.spigot.listeners.SpigotBeehivesUpdateHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;

public class ResourcefulBeehivesManager implements BeehivesManager {

    private final ResourcefulBeesLikePlugin plugin;

    public ResourcefulBeehivesManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        if(!this.plugin.isPaperVersion()) {
            Bukkit.getPluginManager().registerEvents(new SpigotBeehivesUpdateHandler(), plugin);
        }

        Bukkit.getPluginManager().registerEvents(new CraftListener(this.plugin.getManager(BeeTypeManager.class),
                this.plugin.getManager(UpgradesManager.class), this), plugin);
        pluginManager.registerEvents(new BeehivesListener(this,
                plugin.getManager(BeeTypeManager.class)), plugin);
    }

    public void addBeeToHive(Beehive hive, BeeType beeType) {
        PersistentDataContainer container = hive.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive beehive = container.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        beehive.addBee(beeType);
        container.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, beehive);
        hive.update();
    }

    public BeeType removeBeeFromHive(Beehive hive) {
        PersistentDataContainer container = hive.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive beehive = container.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        BeeType beeType = beehive.removeBee();
        container.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, beehive);
        hive.update();
        return beeType;
    }

    @Override
    public boolean isBeehive(ItemStack ingredient) {
        if(ingredient.getType() != Material.BEEHIVE || !ingredient.hasItemMeta()) {
            return false;
        }
        BlockStateMeta meta = (BlockStateMeta) ingredient.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        return itemContainer.has(Keys.BEEHIVE);
    }

    @Override
    public String getUpgradeNameFromBeehive(ItemStack ingredient) {
        if (this.isBeehive(ingredient)) {
            throw new IllegalArgumentException("ItemStack is not a beehive.");
        }
        BlockStateMeta meta = (BlockStateMeta) ingredient.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive beehive = itemContainer.get(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE);
        if (beehive == null) {
            throw new IllegalArgumentException("ItemStack is not a beehive.");
        }
        return "upgrade_" + beehive.getUpgrade().getUpgradeLevel();
    }

    @Override
    public void addHoneycombToBeehive(Beehive beehive, BeeType beetype) {
        PersistentDataContainer container = beehive.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive beehiveRessourceful = container.getOrDefault(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, new ResourcefulBeehive());
        beehiveRessourceful.addHoneycomb(beetype, 1);
        container.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, beehiveRessourceful);
        beehive.update();
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }
}
