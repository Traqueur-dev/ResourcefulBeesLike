package fr.traqueur.resourcefulbees.api.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BeeType {

    int getId();

    String getType();

    Material getFood();

    ItemStack getHoney(Integer amount);
}
