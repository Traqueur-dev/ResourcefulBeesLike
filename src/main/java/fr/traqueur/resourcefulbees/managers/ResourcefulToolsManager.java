package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeePersistentDataType;
import fr.traqueur.resourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.resourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.Saveable;
import fr.traqueur.resourcefulbees.api.managers.ToolsManager;
import fr.traqueur.resourcefulbees.api.models.Bee;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.resourcefulbees.api.utils.Constants;
import fr.traqueur.resourcefulbees.api.utils.Keys;
import fr.traqueur.resourcefulbees.listeners.ToolsListener;
import fr.traqueur.resourcefulbees.models.ResourcefulBee;
import fr.traqueur.resourcefulbees.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcefulToolsManager implements ToolsManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private int beeBoxMaxBees;

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

        return meta.getCustomModelData() == Constants.BEE_BOX_CUSTOM_MODEL_DATA;
    }

    public boolean isBeeJar(ItemStack item) {
        if(item == null || item.getItemMeta() == null || item.getType() != Constants.TOOLS_MATERIAL) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasCustomModelData()) {
            return false;
        }

        return meta.getCustomModelData() == Constants.BEE_JAR_CUSTOM_MODEL_DATA;
    }

    public ItemStack generateBeeBox() {
        ItemStack item = new ItemStack(Constants.TOOLS_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(Constants.BEE_BOX_CUSTOM_MODEL_DATA);
        meta.displayName(ComponentUtils.of(this.plugin.translate(LangKeys.BEE_BOX_NAME)));
        item.setItemMeta(meta);
        this.updateBeeBox(item);
        return item;
    }

    public ItemStack generateBeeJar() {
        ItemStack item = new ItemStack(Constants.TOOLS_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(Constants.BEE_JAR_CUSTOM_MODEL_DATA);
        meta.displayName(ComponentUtils.of(this.plugin.translate(LangKeys.BEE_JAR_NAME)));
        item.setItemMeta(meta);
        this.updateBeeJar(item);
        return item;
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
        List<Component> lore = new ArrayList<>();
        if(bee != null) {
            lore.add(Component.text(this.getPlugin().translate(bee.getBeeType().getType()), NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text(this.getPlugin().translate(LangKeys.BEE_JAR_EMPTY), NamedTextColor.GRAY));
        }

        meta.lore(lore);
        beeJar.setItemMeta(meta);
    }

    public void updateBeeBox(ItemStack beeBox) {
        if(!this.isBeesBox(beeBox)) {
            return;
        }

        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        List<Bee> bees = container.get(Keys.BEES_INSIDE, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));

        if(bees != null) {
            int size = bees.size();

            List<Component> lore = bees.stream()
                    .collect(Collectors.groupingBy((e) -> e.getBeeType().getType(), Collectors.summingInt(e -> 1)))
                    .entrySet().stream()
                    .map(entry -> Component.text(this.getPlugin().translate(entry.getKey()) + " x" + entry.getValue(), NamedTextColor.YELLOW))
                    .collect(Collectors.toList());
            lore.add(Component.empty());
            lore.add(Component.text("Total: " + size + " " + this.plugin.translate("normal_bee") + (size > 1 ? "s" : ""), NamedTextColor.GRAY));
            meta.lore(lore);
        } else {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Total: 0 " + this.plugin.translate("normal_bee"), NamedTextColor.GRAY));
            meta.lore(lore);
        }

        beeBox.setItemMeta(meta);
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
        FileConfiguration config = this.getConfig(this.plugin);
        this.beeBoxMaxBees = config.getInt(ConfigKeys.BEE_BOX_MAX_BEES);
    }

    @Override
    public void saveData() {}
}
