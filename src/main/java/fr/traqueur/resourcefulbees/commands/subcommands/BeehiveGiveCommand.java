package fr.traqueur.resourcefulbees.commands.subcommands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import fr.traqueur.resourcefulbees.api.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BeehiveGiveCommand extends Command<ResourcefulBeesLikePlugin> {

    private final UpgradesManager manager;

    public BeehiveGiveCommand(ResourcefulBeesLikePlugin plugin, UpgradesManager manager) {
        super(plugin, "beehive");
        this.manager = manager;

        this.setPermission(Permissions.BEE_BEEHIVE_GIVE);
        this.addArgs("name:upgrade");

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;
        BeehiveUpgrade name = args.get("name");

        ItemStack beehive = this.manager.generateBeehive(name);
        player.getInventory().addItem(beehive);
        this.getPlugin().sendMessage(player, this.manager.getPlugin().translate(LangKeys.BEE_GIVE_BEEHIVE, Formatter.upgrade(name)));
    }
}
