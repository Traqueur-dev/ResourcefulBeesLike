package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.commands.BeeCommand;
import fr.traqueur.ressourcefulbees.commands.api.CommandManager;
import fr.traqueur.ressourcefulbees.listeners.BeeListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bee;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemFlag;
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

       pluginManager.registerEvents(new BeeListener(this), plugin);
       commandManager.registerCommand(new BeeCommand(plugin, this));
    }

    public boolean isBeeSpawnEgg(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(Keys.BEE);
    }

    public ItemStack generateBeeSpawnEgg(BeeType type) {
        //generate bee egg with data key with value name
        ItemStack item = new ItemStack(Material.BEE_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        container.set(Keys.BEE_NAME, PersistentDataType.STRING, type.getName());
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.displayName(Component.text(type.getName() + " Bee Spawn Egg"));
        item.setItemMeta(meta);
        return item;
    }


    public void spawnBee(Location location, String name, boolean baby) {
        Bee bee = location.getWorld().spawn(location.add(0.5, 1, 0.5), Bee.class, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        bee.getPersistentDataContainer().set(Keys.BEE, PersistentDataType.BOOLEAN, true);
        bee.getPersistentDataContainer().set(Keys.BEE_NAME, PersistentDataType.STRING, name);
        bee.customName(Component.text(name + " Bee"));
        bee.setCustomNameVisible(true);
        if(baby) {
            bee.setBaby();
        }
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }
}
