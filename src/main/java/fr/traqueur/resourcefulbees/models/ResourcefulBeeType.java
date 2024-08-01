package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.adapters.persistents.MaterialPersistentDataType;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

public record ResourcefulBeeType(int id, String type, Material food) implements BeeType {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Material getFood() {
        return this.food;
    }

    @Override
    public ItemStack getHoney(Integer amount) {
        ItemStack item = new ItemStack(Material.HONEYCOMB, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(this.id);
        meta.setDisplayName(JavaPlugin.getPlugin(ResourcefulBeesLikePlugin.class).reset(JavaPlugin.getPlugin(ResourcefulBeesLikePlugin.class).translate(this.type + "_honey_name")));
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (type.equals("normal_bee")) {
            container.set(Keys.HONEY_PRODUCTION, MaterialPersistentDataType.INSTANCE, Material.AIR);
        } else {
            container.set(Keys.HONEY_PRODUCTION, MaterialPersistentDataType.INSTANCE, this.food);
        }
        item.setItemMeta(meta);
        return item;
    }
}
