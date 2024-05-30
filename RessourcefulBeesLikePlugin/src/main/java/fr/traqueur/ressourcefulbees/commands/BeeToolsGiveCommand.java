package fr.traqueur.ressourcefulbees.commands;

import fr.traqueur.ressourcefulbees.api.managers.IToolsManager;
import fr.traqueur.ressourcefulbees.api.utils.Permissions;
import fr.traqueur.ressourcefulbees.commands.api.Command;
import fr.traqueur.ressourcefulbees.commands.api.arguments.Arguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BeeToolsGiveCommand extends Command {

    private final IToolsManager manager;

    public BeeToolsGiveCommand(JavaPlugin plugin, IToolsManager manager) {
        super(plugin, "bee.tools.give");
        this.manager = manager;

        this.setPermission(Permissions.BEE_TOOLS_GIVE);

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;

        ItemStack beeBox = this.manager.generateBeeBox();
        player.getInventory().addItem(beeBox);
        player.sendMessage(Component.text("Vous avez reçu une bee box !", NamedTextColor.GREEN));
    }
}
