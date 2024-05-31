package fr.traqueur.ressourcefulbees.api.adapters.persistents;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeeTypePersistentDataType implements PersistentDataType<String, IBeeType> {

    public static final BeeTypePersistentDataType INSTANCE = new BeeTypePersistentDataType();
    private static final IBeeTypeManager manager = JavaPlugin.getPlugin(RessourcefulBeesLike.class).getManager(IBeeTypeManager.class);

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<IBeeType> getComplexType() {
        return IBeeType.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull IBeeType bee, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return bee.getName().toLowerCase();
    }

    @Override
    public @NotNull IBeeType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return manager.getBeeType(s);
    }
}
