package fr.traqueur.resourcefulbees.api.managers;

import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;

public interface ToolsManager extends Manager {

    ItemStack generateBeeJar();

    boolean isBeeJar(ItemStack item);

    boolean isBeeJarFull(ItemStack beeBox);

    void addToBeeJar(ItemStack beeBox, Bee bee);

    void updateBeeJar(ItemStack beeBox);

    void releaseBeeFromJar(ItemStack beebox, Location location);

    ItemStack generateBeeBox();

    boolean isBeesBox(ItemStack item);

    boolean isBeeBoxFull(ItemStack beeBox);

    void addToBeeBox(ItemStack beeBox, Bee bee);

    void updateBeeBox(ItemStack beeBox);

    void releaseBeeFromBox(ItemStack beebox, Location location, boolean all);

}
