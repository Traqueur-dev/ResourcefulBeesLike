package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.entity.BeeEntity;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public interface BeesManager extends Manager {

    ItemStack generateBeeSpawnEgg(BeeType beetype);

    boolean isBeeSpawnEgg(ItemStack item);

    void spawnBee(Location location, BeeType type, boolean baby, boolean nectar);

    BeeEntity generateBeeEntity(World world, BeeType type);
}
