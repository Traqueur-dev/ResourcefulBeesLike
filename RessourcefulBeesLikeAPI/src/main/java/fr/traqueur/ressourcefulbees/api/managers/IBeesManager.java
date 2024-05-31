package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface IBeesManager {

    ItemStack generateBeeSpawnEgg(IBeeType beetype);

    boolean isBeeSpawnEgg(ItemStack item);

    void spawnBee(Location location, IBeeType type, boolean baby);

    RessourcefulBeesLikeAPI getPlugin();
}
