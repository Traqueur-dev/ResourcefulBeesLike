package fr.traqueur.resourcefulbees.managers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeePersistentDataType;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.resourcefulbees.api.constants.ConfigKeys;
import fr.traqueur.resourcefulbees.api.constants.Constants;
import fr.traqueur.resourcefulbees.api.constants.Keys;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.datas.Saveable;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.models.Bee;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.nms.NmsVersion;
import fr.traqueur.resourcefulbees.listeners.ToolsListener;
import fr.traqueur.resourcefulbees.models.ResourcefulBee;
import org.bukkit.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourcefulToolsManager implements ToolsManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private int beeBoxMaxBees;
    private int customDataJar;
    private int customDataBox;

    public ResourcefulToolsManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        plugin.getServer().getPluginManager().registerEvents(new ToolsListener(this), plugin);
    }

    public boolean isBeesBox(ItemStack item) {
        if(item == null || item.getItemMeta() == null || item.getType() != Constants.TOOLS_MATERIAL) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasCustomModelData()) {
            return false;
        }

        return meta.getCustomModelData() == customDataBox;
    }

    public boolean isBeeJar(ItemStack item) {
        if(item == null || item.getItemMeta() == null || item.getType() != Constants.TOOLS_MATERIAL) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasCustomModelData()) {
            return false;
        }

        return meta.getCustomModelData() == customDataJar;
    }

    public ItemStack generateBeeBox() {
        ItemStack item = new ItemStack(Constants.TOOLS_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(customDataBox);
        meta.setDisplayName(this.plugin.reset(this.plugin.translate(LangKeys.BEE_BOX_NAME)));
        item.setItemMeta(meta);
        this.updateBeeBox(item);
        this.plugin.getItemUtils().setUnstackable(item);
        return item;
    }

    public ItemStack generateBeeJar() {
        ItemStack item = new ItemStack(Constants.TOOLS_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(customDataJar);
        meta.setDisplayName(this.plugin.reset(this.plugin.translate(LangKeys.BEE_JAR_NAME)));
        item.setItemMeta(meta);
        this.updateBeeJar(item);
        return this.plugin.getItemUtils().setUnstackable(item);
    }

    public boolean isBeeJarFull(ItemStack beeJar) {
        if(!this.isBeeJar(beeJar)) {
            return false;
        }

        ItemMeta meta = beeJar.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEES_INSIDE)) {
            return false;
        }
        Bee bee = container.get(Keys.BEES_INSIDE, BeePersistentDataType.INSTANCE);
        return bee != null;
    }

    public void addToBeeJar(ItemStack beeJar, org.bukkit.entity.Bee bukkitBee) {
        ItemMeta meta = beeJar.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer beeContainer = bukkitBee.getPersistentDataContainer();
        BeeType BeeType = this.beeTypeManager.getBeeType("normal_bee");
        if(beeContainer.has(Keys.BEE)) {
            BeeType = beeContainer.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
        }

        container.set(Keys.BEES_INSIDE, BeePersistentDataType.INSTANCE, new ResourcefulBee(BeeType, !bukkitBee.isAdult(), bukkitBee.hasNectar()));
        bukkitBee.remove();
        beeJar.setItemMeta(meta);
        this.updateBeeJar(beeJar);
    }

    public boolean isBeeBoxFull(ItemStack beeBox) {
        if(!this.isBeesBox(beeBox)) {
            return false;
        }

        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEES_INSIDE)) {
            return false;
        }
        List<Bee> bees = container.get(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));
        return bees.size() >= beeBoxMaxBees;
    }

    public void addToBeeBox(ItemStack beeBox, org.bukkit.entity.Bee bukkitBee) {
        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<Bee> bees = container.getOrDefault(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), new ArrayList<>());
        bees = new ArrayList<>(bees);
        PersistentDataContainer beeContainer = bukkitBee.getPersistentDataContainer();
        BeeType BeeType = this.beeTypeManager.getBeeType("normal_bee");
        if(beeContainer.has(Keys.BEE)) {
            BeeType = beeContainer.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
        }

        bees.add(new ResourcefulBee(BeeType, !bukkitBee.isAdult(), bukkitBee.hasNectar()));
        container.set(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), bees);
        bukkitBee.remove();
        beeBox.setItemMeta(meta);
        this.updateBeeBox(beeBox);
    }
    public void releaseBeeFromJar(ItemStack beeJar, Location location) {
        if(!this.isBeeJar(beeJar)) {
            return;
        }

        ItemMeta meta = beeJar.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEES_INSIDE)) {
            return;
        }
        Bee bee = container.get(Keys.BEES_INSIDE, BeePersistentDataType.INSTANCE);
        if(bee != null) {
            BeeSpawnEvent event = new BeeSpawnEvent(bee.getBeeType(), location, bee.isBaby(), bee.hasNectar(), CreatureSpawnEvent.SpawnReason.CUSTOM);
            this.plugin.getServer().getPluginManager().callEvent(event);
            container.remove(Keys.BEES_INSIDE);
            beeJar.setItemMeta(meta);
        }
        this.updateBeeJar(beeJar);
    }


    public void releaseBeeFromBox(ItemStack beebox, Location location, boolean all) {
        if(!this.isBeesBox(beebox) && !this.isBeeJar(beebox)) {
            return;
        }

        ItemMeta meta = beebox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEES_INSIDE)) {
            return;
        }
        List<Bee> bees = container.get(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));
        if(bees != null) {
            LinkedList<Bee> mutableBees = new LinkedList<>(bees);
            if(mutableBees.isEmpty()) {
                return;
            }
            int nbBees = all ? bees.size() : 1;
            for(int i = 0; i < nbBees; i++) {
                Bee bee = mutableBees.poll();
                if(bee == null) {
                    continue;
                }
                BeeSpawnEvent event = new BeeSpawnEvent(bee.getBeeType(), location, bee.isBaby(), bee.hasNectar(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                this.plugin.getServer().getPluginManager().callEvent(event);
            }
            container.set(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), mutableBees);
            beebox.setItemMeta(meta);
            this.updateBeeBox(beebox);
        }
    }

    public void updateBeeJar(ItemStack beeJar) {
        if(!this.isBeeJar(beeJar)) {
            return;
        }

        ItemMeta meta = beeJar.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Bee bee = container.get(Keys.BEES_INSIDE, BeePersistentDataType.INSTANCE);
        List<String> lore = new ArrayList<>();
        if(bee != null) {
            lore.add(ChatColor.YELLOW + this.getPlugin().translate(bee.getBeeType().getType()));
        } else {
            lore.add(ChatColor.GRAY + this.getPlugin().translate(LangKeys.BEE_JAR_EMPTY));
        }

        meta.setLore(lore);
        beeJar.setItemMeta(meta);
    }

    public void updateBeeBox(ItemStack beeBox) {
        if(!this.isBeesBox(beeBox)) {
            return;
        }

        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        List<Bee> bees = container.get(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));

        List<String> lore = new ArrayList<>();
        if(bees != null) {
            int size = bees.size();

            lore = bees.stream()
                    .collect(Collectors.groupingBy((e) -> e.getBeeType().getType(), Collectors.summingInt(e -> 1)))
                    .entrySet().stream()
                    .map(entry -> ChatColor.YELLOW + this.getPlugin().translate(entry.getKey()) + " x" + entry.getValue())
                    .collect(Collectors.toList());
            lore.add("");
            lore.add(ChatColor.GRAY + "Total: " + size + " " + this.plugin.translate("normal_bee") + (size > 1 ? "s" : ""));
        } else {
            lore.add("");
            lore.add(ChatColor.GRAY + "Total: 0 " + this.plugin.translate("normal_bee"));

        }
        meta.setLore(lore);
        beeBox.setItemMeta(meta);
    }

    private void setupRecipes() {
        YamlDocument config = this.getConfig(this.plugin);
        Section toolsCrafts = config.getSection(ConfigKeys.TOOLS_CRAFTS);
        for (String key : List.of(ConfigKeys.BEE_JAR, ConfigKeys.BEE_BOX)) {
            Section craft = toolsCrafts.getSection(key);
            ItemStack result = key.equals(ConfigKeys.BEE_BOX) ? this.generateBeeBox() : this.generateBeeJar();

            String[] pattern = craft.getList(ConfigKeys.PATTERN).toArray(new String[0]);
            List<Map<?,?>> ingredients = craft.getMapList(ConfigKeys.INGREDIENTS);

            NamespacedKey craftKey = new NamespacedKey(this.plugin, key);
            ShapedRecipe recipe = new ShapedRecipe(craftKey, result);
            recipe.shape(pattern);

            ingredients.forEach(map -> {
                String keyIngredient = (String) map.keySet().toArray()[0];
                String ingredient = (String) map.get(keyIngredient);

                Material material = Material.getMaterial(ingredient);
                if (material == null) {
                    throw new IllegalArgumentException("Material " + ingredient + " is not valid.");
                }
                recipe.setIngredient(keyIngredient.charAt(0), material);
            });
            this.plugin.getServer().addRecipe(recipe);
        }
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }

    @Override
    public String getFile() {
        return "tools.yml";
    }

    @Override
    public void loadData() {
        YamlDocument config = this.getConfig(this.plugin);
        this.beeBoxMaxBees = config.getInt(ConfigKeys.BEE_BOX_MAX_BEES);
        this.customDataBox = config.getInt(ConfigKeys.CUSTOM_DATA_BOX);
        this.customDataJar = config.getInt(ConfigKeys.CUSTOM_DATA_JAR);
        boolean craftingEnable = config.getBoolean(ConfigKeys.CRAFTING_ENABLED);
        if (craftingEnable) {
            this.getPlugin().getScheduler().runLater(this::setupRecipes, 2L);
        }
    }

    @Override
    public void saveData() {}
}
