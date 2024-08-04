package fr.traqueur.resourcefulbees.nms.v1_20_R4.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils implements fr.traqueur.resourcefulbees.api.utils.ItemUtils {

    public ItemStack setUnstackable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

}
