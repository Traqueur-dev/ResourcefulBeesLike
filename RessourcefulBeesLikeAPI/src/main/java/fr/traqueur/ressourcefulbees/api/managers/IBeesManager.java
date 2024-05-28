package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface IBeesManager {

    ItemStack generateBeeSpawnEgg(BeeType beetype);

    boolean isBeeSpawnEgg(ItemStack item);

    void spawnBee(Location location, String name);

    RessourcefulBeesLikeAPI getPlugin();
}
