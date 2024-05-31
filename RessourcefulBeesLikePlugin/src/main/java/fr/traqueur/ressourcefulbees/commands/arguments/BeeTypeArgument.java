package fr.traqueur.ressourcefulbees.commands.arguments;

import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.commands.api.arguments.ArgumentConverter;
import fr.traqueur.ressourcefulbees.commands.api.arguments.TabConverter;

import java.util.List;
import java.util.stream.Collectors;

public class BeeTypeArgument implements ArgumentConverter<IBeeType>, TabConverter {

    private final IBeeTypeManager beeTypeManager;

    public BeeTypeArgument(IBeeTypeManager beeTypeManager) {
        this.beeTypeManager = beeTypeManager;
    }

    @Override
    public IBeeType apply(String s) {
        return this.beeTypeManager.getBeeType(s);
    }

    @Override
    public List<String> onCompletion() {
        return this.beeTypeManager.getBeeTypes().keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
