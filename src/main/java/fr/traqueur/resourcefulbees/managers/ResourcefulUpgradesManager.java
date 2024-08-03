package fr.traqueur.resourcefulbees.managers;

import dev.dejvokep.boostedyaml.YamlDocument;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeehivePersistentDataType;
import fr.traqueur.resourcefulbees.api.constants.ConfigKeys;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.datas.Saveable;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.models.ResourcefulBeehive;
import fr.traqueur.resourcefulbees.models.ResourcefulBeehiveCraft;
import fr.traqueur.resourcefulbees.models.ResourcefulBeehiveUpgrade;
import fr.traqueur.resourcefulbees.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcefulUpgradesManager implements UpgradesManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final Map<Integer, BeehiveUpgrade> upgrades;

    public ResourcefulUpgradesManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.upgrades = new HashMap<>();
    }

    @Override
    public void registerUpgrade(BeehiveUpgrade upgrade) {
        if(this.upgrades.containsKey(upgrade.getUpgradeLevel())) {
            throw new IllegalArgumentException("Upgrade " + upgrade.getUpgradeLevel() + " is already registered.");
        }
        this.upgrades.put(upgrade.getUpgradeLevel(), upgrade);
        this.plugin.registerLanguageKey(() -> "upgrade_" + upgrade.getUpgradeLevel() + "_name");
    }

    @Override
    public ItemStack generateBeehive(BeehiveUpgrade upgrade) {
        ItemStack item = new ItemStack(Material.BEEHIVE);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();

        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        fr.traqueur.resourcefulbees.api.models.Beehive resourcefulBeehive = new ResourcefulBeehive();
        resourcefulBeehive.setUpgrade(upgrade);
        meta.setDisplayName(this.plugin.reset(this.plugin.translate(LangKeys.BEEHIVE_NAME, Formatter.upgrade(upgrade))));
        itemContainer.set(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE, resourcefulBeehive);
        itemContainer.set(Keys.HONEY_LEVEL, PersistentDataType.INTEGER, 0);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public Map<Integer, BeehiveUpgrade> getUpgrades() {
        return this.upgrades;
    }

    @Override
    public BeehiveUpgrade getBeehiveUpgradeFromName(String ingredient) {
        String levelStr = ingredient.replace("upgrade_", "");
        try {
            int level = Integer.parseInt(levelStr);
            return this.getUpgrade(level);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public BeehiveUpgrade getUpgrade(int level) {
        return this.upgrades.getOrDefault(level, null);
    }

    @Override
    public BeehiveUpgrade getBeehiveUpgradeFromItem(ItemStack ingredient) {
        if(ingredient == null || !ingredient.hasItemMeta()) {
            return null;
        }
        PersistentDataContainer container = ingredient.getItemMeta().getPersistentDataContainer();
        if(!container.has(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE)) {
            return null;
        }

        return container.get(Keys.BEEHIVE, BeehivePersistentDataType.INSTANCE).getUpgrade();
    }

    private void setupRecipes() {
        for (BeehiveUpgrade upgrade : this.upgrades.values()) {

            BeehiveCraft craft = upgrade.getCraft();
            ItemStack result = this.generateBeehive(upgrade);
            String[] pattern = craft.getPattern();
            Map<String, String> ingredients = craft.getIngredients();

            NamespacedKey key = new NamespacedKey(this.plugin, "beehive_upgrade_" + upgrade.getUpgradeLevel());
            ShapedRecipe recipe = new ShapedRecipe(key,result);
            recipe.shape(pattern);
            ingredients.forEach((keyIngredient, value) -> {
                BeehiveUpgrade upgradeInner = this.getBeehiveUpgradeFromName(value);
                Material ingredient;
                if (upgradeInner != null) {
                    ingredient = Material.BEEHIVE;
                } else {
                    Material material = Material.getMaterial(value);
                    if (material == null) {
                        throw new IllegalArgumentException("Material " + value + " is not valid.");
                    }
                    ingredient = material;
                }
                recipe.setIngredient(keyIngredient.charAt(0), ingredient);
            });
            this.plugin.getServer().addRecipe(recipe);

        }
    }

    @Override
    public String getFile() {
        return "upgrades.yml";
    }

    @Override
    public void loadData() {
        YamlDocument config = this.getConfig(this.plugin);

        config.getMapList(ConfigKeys.UPGRADES).forEach(map -> {
            int level  = (int) map.get(ConfigKeys.UPGRADE_LEVEL);
            double reducer  = NumberUtils.castDouble(map.get(ConfigKeys.REDUCER));
            double multiplier = NumberUtils.castDouble(map.get(ConfigKeys.MULTIPLIER));
            boolean produceBlocks = (boolean) map.get(ConfigKeys.PRODUCE_BLOCKS);

            Map<String, Object> craftMap = (Map<String, Object>) map.get(ConfigKeys.CRAFT);
            String[] pattern = ((List<String>) craftMap.get(ConfigKeys.PATTERN)).toArray(new String[0]);
            Map<String, String> ingredients = (Map<String, String>) craftMap.get(ConfigKeys.INGREDIENTS);

            ResourcefulBeehiveCraft craft = new ResourcefulBeehiveCraft(pattern, ingredients);
            this.registerUpgrade(new ResourcefulBeehiveUpgrade(level, multiplier, reducer, produceBlocks, craft));
        });

        if(!this.upgrades.containsKey(1)) {
            this.registerUpgrade(new ResourcefulBeehiveUpgrade(1,1,1, false,new ResourcefulBeehiveCraft(new String[] {
                    "XXX",
                    "XOX",
                    "XXX"
            }, Map.of(
                    "X", "SHORT_GRASS",
                    "O", "BEEHIVE"
            ))));
        }

        boolean craftingEnable = config.getBoolean(ConfigKeys.CRAFTING_ENABLED);
        if (craftingEnable) {
            this.getPlugin().getScheduler().runLater(this::setupRecipes, 2L);
        }

        BeeLogger.info("&aLoaded " + this.upgrades.size() + " upgrades.");
    }

    @Override
    public void saveData() {
        YamlDocument config = this.getConfig(this.plugin);

        List<Map<String, Object>> upgrades = this.upgrades.values()
                .stream()
                .map(upgrade -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ConfigKeys.UPGRADE_LEVEL, upgrade.getUpgradeLevel());
                    map.put(ConfigKeys.MULTIPLIER, upgrade.multiplierProduction());
                    map.put(ConfigKeys.REDUCER, upgrade.reducerTicks());
                    map.put(ConfigKeys.PRODUCE_BLOCKS, upgrade.produceBlocks());

                    BeehiveCraft craft = upgrade.getCraft();
                    Map<String, Object> craftMap = new HashMap<>();
                    craftMap.put(ConfigKeys.PATTERN, Arrays.asList(craft.getPattern()));
                    craftMap.put(ConfigKeys.INGREDIENTS, craft.getIngredients());
                    map.put(ConfigKeys.CRAFT, craftMap);
                    return map;
                }).toList();

        config.set(ConfigKeys.UPGRADES, upgrades);
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }
}
