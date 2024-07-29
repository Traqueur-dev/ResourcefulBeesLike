package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.Saveable;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.api.constants.ConfigKeys;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.models.ResourcefulBeeType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcefulBeeTypeManager implements BeeTypeManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final Map<String, BeeType> beeTypes;

    public ResourcefulBeeTypeManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypes = new HashMap<>();
    }

    public BeeType getBeeTypeById(int id) {
        return this.beeTypes.values().stream().filter(beetype -> beetype.getId() == id).findFirst().orElse(this.getBeeType("normal_bee"));
    }

    public void setupRecipes() {
        ItemStack ingredient = new ItemStack(Material.HONEYCOMB);

        NamespacedKey key = new NamespacedKey(this.plugin, "honeycomb_production");
        ShapelessRecipe recipe = new ShapelessRecipe(key, ingredient);
        recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient));

        this.plugin.getServer().addRecipe(recipe, true);
    }

    public void registerBeeType(BeeType beeType) {
        if(this.beeTypes.containsKey(beeType.getType().toLowerCase())) {
            throw new IllegalArgumentException("Bee type " + beeType.getType() + " is already registered.");
        }
        this.beeTypes.put(beeType.getType().toLowerCase(), beeType);
        this.plugin.registerLanguageKey(beeType::getType);
        this.plugin.registerLanguageKey(() -> beeType.getType() + "_honey_name");
        this.plugin.registerLanguageKey(() -> beeType.getType() + "_honeycomb_block_name");
    }

    public BeeType getBeeType(String type) {
        return this.beeTypes.getOrDefault(type.toLowerCase(), null);
    }

    public BeeType getBeeTypeFromBee(Bee bee) {
        PersistentDataContainer container = bee.getPersistentDataContainer();
        if(!container.has(Keys.BEE)) {
            return this.getBeeType("normal_bee");
        }
        return container.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
    }

    public Map<String, BeeType> getBeeTypes() {
        return beeTypes;
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
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
            int id  = (int) map.get(ConfigKeys.ID);
            String type = (String) map.get(ConfigKeys.TYPE);
            Material food = Material.valueOf((String) map.get(ConfigKeys.FOOD));
            this.registerBeeType(new ResourcefulBeeType(id,type, food));
        });

        if(!this.beeTypes.containsKey("normal_bee")) {
            this.registerBeeType(new ResourcefulBeeType(0,"normal_bee", Material.POPPY));
        }

        BeeLogger.info("&aLoaded " + this.beeTypes.size() + " bee types.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> beetypes = this.beeTypes.values()
                .stream()
                .map(beetype -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ConfigKeys.ID, beetype.getId());
                    map.put(ConfigKeys.TYPE, beetype.getType());
                    map.put(ConfigKeys.FOOD, beetype.getFood().name());
                    return map;
                }).toList();

        config.set(ConfigKeys.BEETYPE, beetypes);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
