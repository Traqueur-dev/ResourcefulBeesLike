package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.api.models.RessourcefulBeeEntity;
import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.commands.BeeCommand;
import fr.traqueur.ressourcefulbees.commands.api.CommandManager;
import fr.traqueur.ressourcefulbees.listeners.BeeListener;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

public class BeesManager implements IBeesManager {

    private final RessourcefulBeesLikePlugin plugin;

    public BeesManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        CommandManager commandManager = plugin.getCommandManager();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new BeeListener(this, plugin.getManager(IBeeTypeManager.class)), plugin);
        commandManager.registerCommand(new BeeCommand(plugin, this));


    }

    public boolean isBeeSpawnEgg(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(Keys.BEE);
    }

    public ItemStack generateBeeSpawnEgg(IBeeType type) {
        //generate bee egg with data key with value name
        ItemStack item = new ItemStack(Material.BEE_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        container.set(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, type);
        meta.displayName(Component.text(type.getName() + " Spawn Egg"));
        item.setItemMeta(meta);
        return item;
    }


    public void spawnBee(Location location, IBeeType type, boolean baby) {

        RessourcefulBeeEntity test = new RessourcefulBeeEntity(location.getWorld(), new ItemStack(type.getFood()));
        test.setPos(location.getX(), location.getY() + 1, location.getZ());
        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(test);

        Bee bee = (Bee) test.getBukkitEntity();
        bee.getPersistentDataContainer().set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        bee.getPersistentDataContainer().set(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE, type);

        if(!type.getType().equals("normal_bee")) {
            bee.customName(Component.text(type.getName()));
            bee.setCustomNameVisible(true);
        }
        if(baby) {
            bee.setBaby();
        }
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }
}
