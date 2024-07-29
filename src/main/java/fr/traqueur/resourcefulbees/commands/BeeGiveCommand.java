package fr.traqueur.resourcefulbees.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.utils.Permissions;
import fr.traqueur.resourcefulbees.commands.subcommands.BeeToolsGiveCommand;
import fr.traqueur.resourcefulbees.commands.subcommands.BeeTypeGiveCommand;
import fr.traqueur.resourcefulbees.commands.subcommands.BeehiveGiveCommand;
import org.bukkit.command.CommandSender;

public class BeeGiveCommand extends Command<ResourcefulBeesLikePlugin> {

    public BeeGiveCommand(ResourcefulBeesLikePlugin plugin) {
        super(plugin, "bee.give");

        this.setPermission(Permissions.BEE_GIVE);

        this.addSubCommand(new BeeToolsGiveCommand(plugin, plugin.getManager(ToolsManager.class)));
        this.addSubCommand(new BeeTypeGiveCommand(plugin, plugin.getManager(BeesManager.class)));
        this.addSubCommand(new BeehiveGiveCommand(plugin, plugin.getManager(UpgradesManager.class)));

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {}
}
