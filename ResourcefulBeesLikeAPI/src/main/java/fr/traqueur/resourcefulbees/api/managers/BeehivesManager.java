package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.block.Beehive;
import org.bukkit.inventory.ItemStack;

public interface BeehivesManager extends Manager {

    void addBeeToHive(Beehive hive, BeeType beeType);

    void addHoneycombToBeehive(Beehive beehive, BeeType beetype);

    BeeType removeBeeFromHive(Beehive hive);

    boolean isBeehive(ItemStack ingredient);

    String getUpgradeNameFromBeehive(ItemStack ingredient);
}
