package fr.traqueur.resourcefulbees.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BooleanArgument implements ArgumentConverter<Boolean>, TabConverter {

    @Override
    public Boolean apply(String s) {
        if(s.equalsIgnoreCase("false") || s.equalsIgnoreCase("true")) {
            return Boolean.parseBoolean(s);
        }
        return null;
    }

    @Override
    public List<String> onCompletion(CommandSender commandSender) {
        return List.of("true", "false");
    }
}
