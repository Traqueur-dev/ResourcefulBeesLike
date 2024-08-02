package fr.traqueur.resourcefulbees.commands.subcommands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BeeTypeGiveCommand extends Command<ResourcefulBeesLikePlugin> {

    private final BeesManager manager;

    public BeeTypeGiveCommand(ResourcefulBeesLikePlugin plugin, BeesManager manager) {
        super(plugin, "bee");
        this.manager = manager;

        this.setPermission(Permissions.BEE_BEETYPE_GIVE);
        this.addArgs("name:beetype");

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;
        BeeType name = args.get("name");

        ItemStack beeSpawnEgg = this.manager.generateBeeSpawnEgg(name);
        player.getInventory().addItem(beeSpawnEgg);
        this.getPlugin().success(player, this.manager.getPlugin().translate(LangKeys.BEE_GIVE, Formatter.beetype(name)));
    }
}
