package fr.traqueur.resourcefulbees.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.utils.Permissions;
import fr.traqueur.resourcefulbees.commands.subcommands.BeeToolsGiveCommand;
import fr.traqueur.resourcefulbees.commands.subcommands.BeeTypeGiveCommand;
import fr.traqueur.resourcefulbees.commands.subcommands.BeehiveGiveCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BeeSummonCommand extends Command<ResourcefulBeesLikePlugin> {

    public BeeSummonCommand(ResourcefulBeesLikePlugin plugin) {
        super(plugin, "bee.summon");

        this.setPermission(Permissions.BEE_SUMMON);
        this.setUsage("bee summon <type> [baby] [nectar]");
        this.addArgs("name:beetype");
        this.addOptinalArgs("baby:boolean", "nectar:boolean");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;
        BeeType name = args.get("name");
        Optional<Boolean> baby = args.getOptional("baby");
        Optional<Boolean> nectar = args.getOptional("nectar");

        BeeSpawnEvent event = new BeeSpawnEvent(name, player.getLocation(), baby.orElse(false), nectar.orElse(false), CreatureSpawnEvent.SpawnReason.COMMAND);
        Bukkit.getPluginManager().callEvent(event);
        this.getPlugin().success(player, this.getPlugin().translate(LangKeys.BEE_SUMMON, Formatter.beetype(name)));
    }
}
