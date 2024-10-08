package fr.traqueur.resourcefulbees.listeners;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeehivesManager;
import fr.traqueur.resourcefulbees.api.managers.UpgradesManager;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class CraftListener implements Listener {

    private final BeeTypeManager beeTypeManager;
    private final UpgradesManager upgradesManager;
    private final BeehivesManager beehivesManager;

    public CraftListener(BeeTypeManager beeTypeManager, UpgradesManager upgradesManager, BeehivesManager beehivesManager) {
        this.beeTypeManager = beeTypeManager;
        this.upgradesManager = upgradesManager;
        this.beehivesManager = beehivesManager;
    }

    @EventHandler
    public void onCraftHoneyBlock(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }
        ItemStack itemStack = recipe.getResult();
        if (itemStack.getType() != Material.HONEYCOMB_BLOCK) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        long distinctCustomModelDataCount = Arrays.stream(matrix)
                .filter(item -> item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData())
                .mapToInt(item -> item.getItemMeta().getCustomModelData())
                .distinct()
                .count();

        long noCustomModelDataCount = Arrays.stream(matrix)
                .filter(item -> item != null && (!item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()))
                .count();

        boolean allItemsWithoutCustomModelData = Arrays.stream(matrix)
                .filter(Objects::nonNull)
                .allMatch(item -> !item.hasItemMeta() || !item.getItemMeta().hasCustomModelData());

        if (allItemsWithoutCustomModelData) {
            return;
        }

        if(distinctCustomModelDataCount > 1) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        if(noCustomModelDataCount > 0 && distinctCustomModelDataCount > 0) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }


        int customModelData = Arrays.stream(matrix)
                .filter(item -> item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData())
                .mapToInt(item -> item.getItemMeta().getCustomModelData())
                .findFirst()
                .orElse(0);

        BeeType type = this.beeTypeManager.getBeeTypeById(customModelData);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(customModelData);
        meta.setDisplayName(this.beehivesManager.getPlugin().reset(JavaPlugin.getPlugin(ResourcefulBeesLikePlugin.class)
                .translate(type.getType().toLowerCase() + "_honeycomb_block_name")));
        itemStack.setItemMeta(meta);
        event.getInventory().setResult(itemStack);
    }

    @EventHandler
    public void onCraftHoneyProduction(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }

        if(!this.getKey(recipe).contains("honeycomb_production")) {
            return;
        }

        ItemStack itemStack = recipe.getResult();
        if (itemStack.getType() != Material.HONEYCOMB) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        int customModelData = Arrays.stream(matrix)
                .filter(item -> item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData())
                .mapToInt(item -> item.getItemMeta().getCustomModelData())
                .findFirst()
                .orElse(0);

        if(customModelData == 0) {
            return;
        }

        BeeType type = this.beeTypeManager.getBeeTypeById(customModelData);
        itemStack = new ItemStack(type.getFood());
        event.getInventory().setResult(itemStack);
    }

    @EventHandler
    public void onCraftHoneyReverser(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }


        if(!this.getKey(recipe).contains("honeycomb_reverse")) {
            return;
        }

        ItemStack itemStack = recipe.getResult();
        if (itemStack.getType() != Material.HONEYCOMB) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        //check if matrix have single item and it's honeycomb_block
        if (Arrays.stream(matrix).filter(Objects::nonNull).count() != 1) {
            return;
        }

        if (Arrays.stream(matrix).filter(Objects::nonNull).noneMatch(item -> item.getType() == Material.HONEYCOMB_BLOCK)) {
            return;
        }

        int customModelData = Arrays.stream(matrix)
                .filter(item -> item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData())
                .mapToInt(item -> item.getItemMeta().getCustomModelData())
                .findFirst()
                .orElse(0);

        if(customModelData == 0) {
            return;
        }

        BeeType type = this.beeTypeManager.getBeeTypeById(customModelData);
        itemStack = type.getHoney(4);
        event.getInventory().setResult(itemStack);
    }

    @EventHandler
    public void onCraftBeehive(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }

        if(!this.getKey(recipe).contains("beehive_upgrade_")) {
            return;
        }

        ItemStack itemStack = recipe.getResult();
        if (itemStack.getType() != Material.BEEHIVE) {
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        List<BeehiveUpgrade> upgrades = new ArrayList<>(this.upgradesManager.getUpgrades().values());
        for (BeehiveUpgrade upgrade : upgrades) {
            boolean upgradeError = false;
            BeehiveCraft craft = upgrade.getCraft();
            if (!this.isUpgradeCraft(craft, matrix)) {
                continue;
            }

            Map<Integer, ItemStack> beehives = Arrays.stream(matrix).filter(Objects::nonNull)
                    .filter(item -> item.getType() == Material.BEEHIVE)
                    .collect(Collectors.toMap(item -> Arrays.asList(matrix).indexOf(item), item -> item));

            if (!beehives.isEmpty()) {
                List<String> patternInline = new ArrayList<>();
                for (String s : craft.getPattern()) {
                    patternInline.addAll(Arrays.asList(s.split("")));
                }
                List<ItemStack> pattern = patternInline.stream()
                        .map(s -> craft.getIngredients().get(s))
                        .map(s -> {
                            BeehiveUpgrade upgrade1 = this.upgradesManager.getBeehiveUpgradeFromName(s);
                            if (upgrade1 != null) {
                                return this.upgradesManager.generateBeehive(upgrade1);
                            }
                            return new ItemStack(Material.valueOf(s));
                        }).toList();

                for (Map.Entry<Integer, ItemStack> integerItemStackEntry : beehives.entrySet()) {
                    int index = integerItemStackEntry.getKey();
                    ItemStack beehive = integerItemStackEntry.getValue();
                    ItemStack beehiveSource = pattern.get(index);
                    BeehiveUpgrade sourceUpgrade = this.upgradesManager.getBeehiveUpgradeFromItem(beehiveSource);
                    BeehiveUpgrade targetUpgrade = this.upgradesManager.getBeehiveUpgradeFromItem(beehive);

                    if ((sourceUpgrade == null && targetUpgrade != null) ||
                            (sourceUpgrade != null && targetUpgrade == null) ||
                            (sourceUpgrade != null && sourceUpgrade.getUpgradeLevel() != targetUpgrade.getUpgradeLevel())) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        upgradeError = true;
                        break;
                    }
                }
            }
            if(!upgradeError) {
                event.getInventory().setResult(this.upgradesManager.generateBeehive(upgrade));
            }
        }
    }

    private boolean isUpgradeCraft(BeehiveCraft craft, ItemStack[] matrix) {
        String[] pattern = craft.getPattern();
        Map<String, String> ingredientsMap = craft.getIngredients();
        //from pattern and ingredients, create string to compare with matrix
        List<Material> ingredients = new ArrayList<>();
        for (String line : pattern) {
            String[] split = line.split("");
            for (String s : split) {
                if(s.equals(" ")) {
                    continue;
                }
                if (ingredientsMap.containsKey(s)) {
                    String ingredient = ingredientsMap.get(s);
                    BeehiveUpgrade upgrade = this.upgradesManager.getBeehiveUpgradeFromName(ingredient);
                    if (upgrade != null) {
                        ingredients.add(Material.BEEHIVE);
                    } else {
                        ingredients.add(Material.valueOf(ingredient));
                    }
                }
            }
        }
        //compare matrix with ingredients
        List<Material> matrixList = Arrays.stream(matrix).filter(Objects::nonNull).map(ItemStack::getType).toList();
        return new HashSet<>(matrixList).containsAll(ingredients);
    }

    private String getKey(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return ((ShapedRecipe) recipe).getKey().getKey();
        }
        if(recipe instanceof ShapelessRecipe) {
            return ((ShapelessRecipe) recipe).getKey().getKey();
        }
        throw new IllegalArgumentException("Recipe type not supported.");
    }
}