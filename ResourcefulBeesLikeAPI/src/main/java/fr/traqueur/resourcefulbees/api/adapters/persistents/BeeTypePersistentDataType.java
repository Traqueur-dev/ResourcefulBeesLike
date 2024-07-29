package fr.traqueur.resourcefulbees.api.adapters.persistents;

import fr.traqueur.resourcefulbees.api.ResourcefulBeesLike;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeeTypePersistentDataType implements PersistentDataType<String, BeeType> {

    public static final BeeTypePersistentDataType INSTANCE = new BeeTypePersistentDataType();
    private static final BeeTypeManager manager = JavaPlugin.getPlugin(ResourcefulBeesLike.class).getManager(BeeTypeManager.class);

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
        return bee.getType().toLowerCase();
    }

    @Override
    public @NotNull BeeType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return manager.getBeeType(s);
    }
}
