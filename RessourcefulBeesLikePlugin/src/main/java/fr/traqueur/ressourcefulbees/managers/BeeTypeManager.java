package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.models.BeeTypes;
import fr.traqueur.ressourcefulbees.models.Breed;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BeeTypeManager implements IBeeTypeManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final HashMap<String, IBeeType> beeTypes;

    public BeeTypeManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypes = new HashMap<>();
    }

    public void registerBeeType(IBeeType IBeeType) {
        this.beeTypes.put(IBeeType.getType().toLowerCase(), IBeeType);
    }

    public IBeeType getBeeType(String type) {
        return this.beeTypes.getOrDefault(type.toLowerCase(), null);
    }

    public IBeeType getBeeTypeFromBee(Bee bee) {
        PersistentDataContainer container = bee.getPersistentDataContainer();
        if(!container.has(Keys.BEE)) {
            return this.getBeeType("normal_bee");
        }
        return container.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
    }

    public HashMap<String, IBeeType> getBeeTypes() {
        return beeTypes;
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }

    @Override
    public String getFile() {
        return "beetypes.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        config.getMapList(ConfigKeys.BEETYPE).forEach(map -> {
            String type = (String) map.get(ConfigKeys.TYPE);
            String name = (String) map.get(ConfigKeys.NAME);
            Material food = Material.valueOf((String) map.get(ConfigKeys.FOOD));
            this.beeTypes.put(type, new IBeeType() {
                @Override
                public String getType() {
                    return type;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Material getFood() {
                    return food;
                }
            });
        });

        if(this.beeTypes.isEmpty()) {
            Stream.of(BeeTypes.values()).forEach(this::registerBeeType);
        }

        BeeLogger.info("&aLoaded " + this.beeTypes.size() + " bee types.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> beetypes = this.beeTypes.values()
                .stream()
                .map(beetype -> (Map<String, Object>) new HashMap<String, Object>() {{
                    put(ConfigKeys.TYPE, beetype.getType());
                    put(ConfigKeys.NAME, beetype.getName());
                    put(ConfigKeys.FOOD, beetype.getFood().name());
                }}).toList();

        config.set(ConfigKeys.BEETYPE, beetypes);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
