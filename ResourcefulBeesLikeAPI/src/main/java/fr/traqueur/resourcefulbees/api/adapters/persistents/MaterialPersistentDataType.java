package fr.traqueur.resourcefulbees.api.adapters.persistents;

import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class MaterialPersistentDataType implements PersistentDataType<String, Material> {

    public static final MaterialPersistentDataType INSTANCE = new MaterialPersistentDataType();

    @Override
    public  Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public  Class<Material> getComplexType() {
        return Material.class;
    }

    @Override
    public  String toPrimitive( Material material,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return material.name();
    }

    @Override
    public  Material fromPrimitive( String s,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return Material.valueOf(s);
    }
}
