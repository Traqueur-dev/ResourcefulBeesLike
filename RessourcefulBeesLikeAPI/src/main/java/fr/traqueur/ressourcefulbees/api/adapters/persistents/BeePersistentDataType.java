package fr.traqueur.ressourcefulbees.api.adapters.persistents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.adapters.json.BeeAdapter;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.IBee;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeePersistentDataType implements PersistentDataType<String, IBee> {

    public static final BeePersistentDataType INSTANCE = new BeePersistentDataType();
    private static final IBeeTypeManager manager = JavaPlugin.getPlugin(RessourcefulBeesLike.class).getManager(IBeeTypeManager.class);
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(IBee.class, new BeeAdapter(manager));
        gson = builder.create();
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<IBee> getComplexType() {
        return IBee.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull IBee bee, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(bee);
    }

    @Override
    public @NotNull IBee fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.fromJson(s, IBee.class);
    }
}
