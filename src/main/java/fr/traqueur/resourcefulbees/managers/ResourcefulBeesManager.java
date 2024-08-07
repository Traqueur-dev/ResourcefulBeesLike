package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.api.entity.BeeEntity;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.nms.NmsVersion;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.api.utils.ReflectionUtils;
import fr.traqueur.resourcefulbees.listeners.BeeListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ResourcefulBeesManager implements BeesManager {

    private final ResourcefulBeesLikePlugin plugin;

    public ResourcefulBeesManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new BeeListener(this, plugin.getManager(BeeTypeManager.class)), plugin);
    }

    public boolean isBeeSpawnEgg(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(Keys.BEE);
    }

    public ItemStack generateBeeSpawnEgg(BeeType type) {
        ItemStack item = new ItemStack(Material.BEE_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        container.set(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, type);
        meta.setCustomModelData(type.getId());
        meta.setDisplayName(this.plugin.reset(this.plugin.translate(LangKeys.SPAWN_EGG_NAME, Formatter.beetype(type))));
        item.setItemMeta(meta);
        return item;
    }

    public BeeEntity generateBeeEntity(World world, BeeType type) {
        String className = ReflectionUtils.ENTITY.getVersioned(NmsVersion.getCurrentVersion());
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(World.class, BeeType.class);
            return (BeeEntity) constructor.newInstance(world, type);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            BeeLogger.severe(e.getMessage());
        }

        return null;
    }

    public void spawnBee(Location location, BeeType type, boolean baby, boolean nectar) {

        BeeEntity resourcefulBeeEntity = this.generateBeeEntity(location.getWorld(), type);
        resourcefulBeeEntity.setPosition(location.getX(), location.getY() + 1, location.getZ());
        resourcefulBeeEntity.setStayOutOfHive(400);
        Bee bee = resourcefulBeeEntity.getSpigotEntity();
        bee.getPersistentDataContainer().set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        bee.getPersistentDataContainer().set(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, type);

        if(!type.getType().equals("normal_bee")) {
            bee.setCustomName(this.plugin.translate(type.getType()));
            bee.setCustomNameVisible(true);
        }
        if(baby) {
            bee.setBaby();
        }
        bee.setHasNectar(nectar);
        resourcefulBeeEntity.spawn();
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }
}
