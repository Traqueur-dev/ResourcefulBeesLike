package fr.traqueur.ressourcefulbees.api.adapters.persistents;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeeTypePersistentDataType implements PersistentDataType<String, BeeType> {

    public static final BeeTypePersistentDataType INSTANCE = new BeeTypePersistentDataType();
    private static final IBeeTypeManager manager = JavaPlugin.getPlugin(RessourcefulBeesLike.class).getManager(IBeeTypeManager.class);

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<BeeType> getComplexType() {
        return BeeType.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull BeeType bee, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return bee.getName().toLowerCase();
    }

    @Override
    public @NotNull BeeType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return manager.getBeeType(s);
    }
}
