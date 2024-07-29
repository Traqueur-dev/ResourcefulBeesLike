package fr.traqueur.resourcefulbees.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class BeeTypeArgument implements ArgumentConverter<BeeType>, TabConverter {

    private final BeeTypeManager beeTypeManager;

    public BeeTypeArgument(BeeTypeManager beeTypeManager) {
        this.beeTypeManager = beeTypeManager;
    }

    @Override
    public BeeType apply(String s) {
        return this.beeTypeManager.getBeeType(s);
    }

    @Override
    public List<String> onCompletion(CommandSender sender) {
        return this.beeTypeManager.getBeeTypes().keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
