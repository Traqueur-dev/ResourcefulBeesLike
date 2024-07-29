package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface UpgradesManager extends Manager {

    Map<Integer, BeehiveUpgrade> getUpgrades();

    void registerUpgrade(BeehiveUpgrade upgrade);

    ItemStack generateBeehive(BeehiveUpgrade name);

    BeehiveUpgrade getBeehiveUpgradeFromName(String ingredient);

    BeehiveUpgrade getUpgrade(int level);

    BeehiveUpgrade getBeehiveUpgradeFromItem(ItemStack ingredient);
}
