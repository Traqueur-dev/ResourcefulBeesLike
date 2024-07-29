package fr.traqueur.resourcefulbees.api.adapters.persistents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLike;
import fr.traqueur.resourcefulbees.api.adapters.json.BeehiveAdapter;
import fr.traqueur.resourcefulbees.api.adapters.json.BeehiveCraftAdapter;
import fr.traqueur.resourcefulbees.api.adapters.json.BeehiveUpgradeAdapter;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.models.Beehive;
import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BeehivePersistentDataType implements PersistentDataType<String, Beehive> {

    public static final BeehivePersistentDataType INSTANCE = new BeehivePersistentDataType();
    private static final BeeTypeManager manager = JavaPlugin.getPlugin(ResourcefulBeesLike.class).getManager(BeeTypeManager.class);
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.registerTypeHierarchyAdapter(Beehive.class, new BeehiveAdapter(manager));
        builder.registerTypeHierarchyAdapter(BeehiveUpgrade.class, new BeehiveUpgradeAdapter());
        builder.registerTypeHierarchyAdapter(BeehiveCraft.class, new BeehiveCraftAdapter());
        gson = builder.create();
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Beehive> getComplexType() {
        return Beehive.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Beehive bee, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(bee);
    }

    @Override
    public @NotNull Beehive fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.fromJson(s, Beehive.class);
    }
}
