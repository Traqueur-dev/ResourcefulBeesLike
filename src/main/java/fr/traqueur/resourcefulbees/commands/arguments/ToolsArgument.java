package fr.traqueur.resourcefulbees.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.resourcefulbees.api.models.BeeTools;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ToolsArgument implements ArgumentConverter<BeeTools>, TabConverter {

    @Override
    public BeeTools apply(String s) {
        return Arrays.stream(BeeTools.values()).filter(tools -> tools.name().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    @Override
    public List<String> onCompletion(CommandSender sender) {
        return Arrays.stream(BeeTools.values()).map(tools -> tools.name().toLowerCase()).toList();
    }
}
