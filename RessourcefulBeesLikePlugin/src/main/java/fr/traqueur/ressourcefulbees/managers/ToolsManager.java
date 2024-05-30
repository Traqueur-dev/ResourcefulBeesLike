package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.ressourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IToolsManager;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeePersistentDataType;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.IBee;
import fr.traqueur.ressourcefulbees.api.utils.Constants;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.commands.BeeToolsGiveCommand;
import fr.traqueur.ressourcefulbees.listeners.ToolsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ToolsManager implements IToolsManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final IBeeTypeManager beeTypeManager;
    private int beeBoxMaxBees;

    public ToolsManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(IBeeTypeManager.class);

        plugin.getCommandManager().registerCommand(new BeeToolsGiveCommand(plugin, this));
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

    public ItemStack generateBeeBox() {
        ItemStack item = new ItemStack(Constants.TOOLS_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(Constants.BEE_BOX_CUSTOM_MODEL_DATA);
        meta.displayName(Component.text("Bee Box"));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isBeeBoxFull(ItemStack beeBox) {
        if(!this.isBeesBox(beeBox)) {
            return false;
        }

        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEE_BOX_BEES)) {
            return false;
        }
        List<IBee> bees = container.get(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));
        return bees.size() >= beeBoxMaxBees;
    }

    public void addToBeeBox(ItemStack beeBox, Bee bee) {
        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<IBee> bees = container.getOrDefault(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), new ArrayList<>());
        bees = new ArrayList<>(bees);
        PersistentDataContainer beeContainer = bee.getPersistentDataContainer();
        BeeType beeType = this.beeTypeManager.getBeeType("normal");
        if(beeContainer.has(Keys.BEE)) {
            beeType = beeContainer.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
        }

        bees.add(new fr.traqueur.ressourcefulbees.models.Bee(beeType, !bee.isAdult()));
        container.set(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), bees);
        bee.remove();
        beeBox.setItemMeta(meta);
        this.updateBeeBox(beeBox);
    }

    public void releaseBee(ItemStack beebox, Location location, boolean all) {
        if(!this.isBeesBox(beebox)) {
            return;
        }

        ItemMeta meta = beebox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEE_BOX_BEES)) {
            return;
        }
        List<IBee> bees = container.get(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));
        if(bees != null) {
            LinkedList<IBee> mutableBees = new LinkedList<>(bees);
            if(mutableBees.isEmpty()) {
                return;
            }
            int nbBees = all ? bees.size() : 1;
            for(int i = 0; i < nbBees; i++) {
                IBee bee = mutableBees.poll();
                if(bee == null) {
                    continue;
                }
                BeeSpawnEvent event = new BeeSpawnEvent(bee.getBeeType(), location, bee.isBaby());
                this.plugin.getServer().getPluginManager().callEvent(event);
            }
            container.set(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE), mutableBees);
            beebox.setItemMeta(meta);
            this.updateBeeBox(beebox);
        }
    }

    public void updateBeeBox(ItemStack beeBox) {
        if(!this.isBeesBox(beeBox)) {
            return;
        }

        ItemMeta meta = beeBox.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Keys.BEE_BOX_BEES)) {
            return;
        }
        List<IBee> bees = container.get(Keys.BEE_BOX_BEES, PersistentDataType.LIST.listTypeFrom(BeePersistentDataType.INSTANCE));

        if(bees != null) {
            int size = bees.size();

            List<Component> lore = bees.stream()
                    .collect(Collectors.groupingBy((e) -> e.getBeeType().getName(), Collectors.summingInt(e -> 1)))
                    .entrySet().stream()
                    .map(entry -> Component.text(entry.getKey() + " Bee x" + entry.getValue(), NamedTextColor.YELLOW))
                    .collect(Collectors.toList());
            lore.add(Component.empty());
            lore.add(Component.text("Total: " + size + " bees", NamedTextColor.GRAY));
            meta.lore(lore);
        }

        beeBox.setItemMeta(meta);
    }

    @Override
    public RessourcefulBeesLikePlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getFile() {
        return "tools.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);
        this.beeBoxMaxBees = config.getInt("bee-box-max-bees");
    }

    @Override
    public void saveData() {}
}
