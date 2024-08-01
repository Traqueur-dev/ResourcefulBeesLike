package fr.traqueur.resourcefulbees.commands.subcommands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.models.BeeTools;
import fr.traqueur.resourcefulbees.api.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class BeeToolsGiveCommand extends Command<ResourcefulBeesLikePlugin> {

    private final ToolsManager manager;

    public BeeToolsGiveCommand(ResourcefulBeesLikePlugin plugin, ToolsManager manager) {
        super(plugin, "tools");
        this.manager = manager;

        this.setPermission(Permissions.BEE_TOOLS_GIVE);
        this.addArgs("beetools:beetools");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;
        BeeTools beetools = args.get("beetools");
        LangKeys key;
        ItemStack tool;
        switch (beetools) {
            case BEE_JAR -> {
                key = LangKeys.BEE_JAR_GIVE;
                tool = this.manager.generateBeeJar();
            }
            case BEE_BOX -> {
                key = LangKeys.BEE_BOX_GIVE;
                tool = this.manager.generateBeeBox();
            }
            default -> { //never append
                return;
            }
        }

        player.getInventory().addItem(tool);
        this.getPlugin().success(player, this.manager.getPlugin().translate(key));
    }
}
