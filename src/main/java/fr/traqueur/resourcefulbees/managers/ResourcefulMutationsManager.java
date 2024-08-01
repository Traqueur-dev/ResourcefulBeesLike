package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.constants.ConfigKeys;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BeesManager;
import fr.traqueur.resourcefulbees.api.managers.MutationsManager;
import fr.traqueur.resourcefulbees.api.managers.Saveable;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Mutation;
import fr.traqueur.resourcefulbees.api.nms.NmsVersion;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.api.utils.ReflectionUtils;
import fr.traqueur.resourcefulbees.listeners.MutationsListener;
import fr.traqueur.resourcefulbees.models.ResourcefulMutation;
import fr.traqueur.resourcefulbees.platform.paper.listeners.PaperEntityMoveListener;
import fr.traqueur.resourcefulbees.platform.spigot.listeners.SpigotEntityMoveListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class ResourcefulMutationsManager implements MutationsManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private final BeesManager beesManager;
    private final Map<Material, Set<Mutation>> mutations;

    public ResourcefulMutationsManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        this.beesManager = plugin.getManager(BeesManager.class);
        this.mutations = new HashMap<>();

        if(plugin.isPaperVersion()) {
            plugin.getServer().getPluginManager().registerEvents(new PaperEntityMoveListener(plugin, this), plugin);
        } else {
            String version = NmsVersion.getCurrentVersion().name().replace("V_", "v");
            String className = ReflectionUtils.MOVE_TASK.getVersioned(version);
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor();
                this.plugin.getScheduler().runTimer((Runnable) constructor.newInstance(), 0L, 1L);
            } catch (Exception exception) {
                BeeLogger.severe("Cannot create a new instance for the class " + className);
                BeeLogger.severe(exception.getMessage());
            }
            plugin.getServer().getPluginManager().registerEvents(new SpigotEntityMoveListener(plugin, this), plugin);
        }

        plugin.getServer().getPluginManager().registerEvents(new MutationsListener(plugin, this), plugin);
    }

    @Override
    public void registerMutation(Mutation mutation) {
        Set<Mutation> mutations = this.mutations.getOrDefault(mutation.getBlock(), new HashSet<>());
        for (Mutation m : mutations) {
            if (m.getParent().getType().equals(mutation.getParent().getType())) {
                throw new IllegalArgumentException("Mutation already exists");
            }
        }
        mutations.add(mutation);
        this.mutations.put(mutation.getBlock(), mutations);
    }

    @Override
    public Set<Mutation> getMutationsForBlock(Material block) {
        return this.mutations.getOrDefault(block, new HashSet<>());
    }

    @Override
    public Set<Mutation> getMutationsForParent(BeeType parent) {
        return this.mutations.values().stream().flatMap(Set::stream).filter(mutation -> mutation.getParent().getType().equals(parent.getType())).collect(Collectors.toSet());
    }

    @Override
    public Mutation getMutation(BeeType parent, Material block) {
        return this.mutations.getOrDefault(block, new HashSet<>()).stream().filter(mutation -> mutation.getParent().getType().equals(parent.getType())).findFirst().orElseThrow();
    }

    @Override
    public void mutateBee(Location to, BeeType child) {
        to.getBlock().setType(Material.AIR);
        ItemStack item = this.beesManager.generateBeeSpawnEgg(child);
        to.getWorld().dropItem(to.add(0, 0.6, 0), item);
    }

    @Override
    public Map<Material, Set<Mutation>> getMutations() {
        return this.mutations;
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }

    @Override
    public String getFile() {
        return "mutations.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        for (Map<?, ?> map : config.getMapList(ConfigKeys.MUTATIONS)) {
            BeeType parent = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.PARENT)));
            BeeType child = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.CHILD)));
            Material block = Material.valueOf((String) map.get(ConfigKeys.BLOCK));

            try {
                ResourcefulMutation mutation = new ResourcefulMutation(parent, block, child);
                this.registerMutation(mutation);
            } catch (IllegalArgumentException e) {
                BeeLogger.severe("Invalid block type for mutation: " + block);
            }
        }

        BeeLogger.info("&aLoaded " + this.mutations.size() + " mutations.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> mutations = this.mutations.values().stream()
                .flatMap(mutationsSet -> mutationsSet.stream()
                        .map(mutation -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put(ConfigKeys.PARENT, mutation.getParent().getType());
                            map.put(ConfigKeys.CHILD, mutation.getChild().getType());
                            map.put(ConfigKeys.BLOCK, mutation.getBlock().name());
                            return map;
                        })).toList();

        config.set(ConfigKeys.MUTATIONS, mutations);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
