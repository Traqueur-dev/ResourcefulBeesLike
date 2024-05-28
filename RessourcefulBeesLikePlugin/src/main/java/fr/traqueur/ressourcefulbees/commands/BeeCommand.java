package fr.traqueur.ressourcefulbees.commands;

import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.utils.Permissions;
import fr.traqueur.ressourcefulbees.commands.api.Command;
import fr.traqueur.ressourcefulbees.commands.arguments.Arguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BeeCommand extends Command {

    private final IBeesManager manager;

    public BeeCommand(JavaPlugin plugin, IBeesManager manager) {
        super(plugin, "bee.give");
        this.manager = manager;

        this.setPermission(Permissions.BEE_GIVE);
        this.addArgs("name:string");

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments args) {
        Player player = (Player) sender;
        String name = args.get("name");

        ItemStack beeSpawnEgg = this.manager.generateBeeSpawnEgg(name);
        player.getInventory().addItem(beeSpawnEgg);
        player.sendMessage(Component.text("Vous avez reçu un oeuf de l'abeille " + name + "!", NamedTextColor.GREEN));
    }
}
