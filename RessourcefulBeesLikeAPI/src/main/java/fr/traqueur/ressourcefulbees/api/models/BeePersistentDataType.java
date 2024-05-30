package fr.traqueur.ressourcefulbees.api.models;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeePersistentDataType implements PersistentDataType<String, BeeType> {

    public static final BeePersistentDataType INSTANCE = new BeePersistentDataType();

    private final IBeeTypeManager manager = JavaPlugin.getPlugin(RessourcefulBeesLike.class).getManager(IBeeTypeManager.class);

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<BeeType> getComplexType() {
        return BeeType.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull BeeType beeType, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return beeType.getName().toLowerCase();
    }

    @Override
    public @NotNull BeeType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return manager.getBeeType(s);
    }
}
