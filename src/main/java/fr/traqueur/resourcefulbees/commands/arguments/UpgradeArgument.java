package fr.traqueur.resourcefulbees.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class UpgradeArgument implements ArgumentConverter<BeehiveUpgrade>, TabConverter {

    private final UpgradesManager upgradesManager;

    public UpgradeArgument(UpgradesManager upgradesManager) {
        this.upgradesManager = upgradesManager;
    }

    @Override
    public BeehiveUpgrade apply(String s) {
        String levelStr = s.replace("upgrade_", "");
        try {
            int level = Integer.parseInt(levelStr);
            return this.upgradesManager.getUpgrade(level);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<String> onCompletion(CommandSender sender) {
        return this.upgradesManager.getUpgrades().values().stream().map(upgrade -> "upgrade_" + upgrade.getUpgradeLevel()).collect(Collectors.toList());
    }
}
