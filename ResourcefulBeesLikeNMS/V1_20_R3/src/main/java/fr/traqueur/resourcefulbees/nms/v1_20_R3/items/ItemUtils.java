package fr.traqueur.resourcefulbees.nms.v1_20_R3.items;

import fr.traqueur.resourcefulbees.api.ResourcefulBeesLike;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ItemUtils implements fr.traqueur.resourcefulbees.api.utils.ItemUtils {

    public ItemStack setUnstackable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(JavaPlugin.getProvidingPlugin(ResourcefulBeesLike.class), "unstackable"), PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(meta);
        return item;
    }

}
