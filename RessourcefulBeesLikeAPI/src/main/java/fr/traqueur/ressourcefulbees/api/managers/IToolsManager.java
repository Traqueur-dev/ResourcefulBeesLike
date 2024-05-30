package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;

public interface IToolsManager {

    ItemStack generateBeeBox();

    boolean isBeesBox(ItemStack item);

    boolean isBeeBoxFull(ItemStack beeBox);

    void addToBeeBox(ItemStack beeBox, Bee bee);

    void updateBeeBox(ItemStack beeBox);

    void releaseBee(ItemStack beebox, Location location, boolean all);

    RessourcefulBeesLikeAPI getPlugin();
}
