package fr.traqueur.resourcefulbees.api.adapters.persistents;

import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class MaterialPersistentDataType implements PersistentDataType<String, Material> {

    public static final MaterialPersistentDataType INSTANCE = new MaterialPersistentDataType();

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Material> getComplexType() {
        return Material.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Material material, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return material.name();
    }

    @Override
    public @NotNull Material fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return Material.valueOf(s);
    }
}
