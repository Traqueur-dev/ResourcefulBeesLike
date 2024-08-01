package fr.traqueur.resourcefulbees.api.adapters.persistents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLike;
import fr.traqueur.resourcefulbees.api.adapters.json.BeeAdapter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.Bee;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BeePersistentDataType implements PersistentDataType<String, Bee> {

    public static final BeePersistentDataType INSTANCE = new BeePersistentDataType();
    private static final BeeTypeManager manager = JavaPlugin.getPlugin(ResourcefulBeesLike.class).getManager(BeeTypeManager.class);
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(Bee.class, new BeeAdapter(manager));
        gson = builder.create();
    }

    @Override
    public  Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public  Class<Bee> getComplexType() {
        return Bee.class;
    }

    @Override
    public  String toPrimitive( Bee bee,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(bee);
    }

    @Override
    public  Bee fromPrimitive( String s,  PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.fromJson(s, Bee.class);
    }
}
