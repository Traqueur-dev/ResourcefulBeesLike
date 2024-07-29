package fr.traqueur.resourcefulbees.managers;

import fr.traqueur.resourcefulbees.ResourcefulBeesLikePlugin;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.resourcefulbees.api.managers.BreedsManager;
import fr.traqueur.resourcefulbees.api.managers.Saveable;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Breed;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.resourcefulbees.listeners.BreedsListener;
import fr.traqueur.resourcefulbees.models.ResourcefulBreed;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class ResourcefulBreedsManager implements BreedsManager, Saveable {

    private final ResourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private final Set<Breed> breeds;

    public ResourcefulBreedsManager(ResourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        this.breeds = new HashSet<>();

        this.plugin.getServer().getPluginManager().registerEvents(new BreedsListener(this), this.plugin);
    }

    @Override
    public void registerBreed(Breed breed) {
        for (Breed b : this.breeds) {
            if(b.getParents().getLeft().getType().equals(breed.getParents().getLeft().getType())
                    && b.getParents().getRight().getType().equals(breed.getParents().getRight().getType()) ||
                    b.getParents().getLeft().getType().equals(breed.getParents().getRight().getType())
                    && b.getParents().getRight().getType().equals(breed.getParents().getLeft().getType())) {
                throw new IllegalArgumentException("Breed already exists");
            }
        }
        this.breeds.add(breed);
    }

    @Override
    public Breed getBreed(BeeType fatherType, BeeType motherType) {
        for (Breed breed : this.breeds) {
            if(breed.getParents().getLeft().getType().equals(fatherType.getType())
                    && breed.getParents().getRight().getType().equals(motherType.getType()) ||
                    breed.getParents().getLeft().getType().equals(motherType.getType())
                    && breed.getParents().getRight().getType().equals(fatherType.getType())) {
                return breed;
            }
        }
        return null;
    }

    @Override
    public ResourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }

    @Override
    public String getFile() {
        return "breeds.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        config.getMapList(ConfigKeys.BREEDS).forEach(map -> {
            String parents = (String) map.get(ConfigKeys.PARENTS);
            List<BeeType> parentsArray = Stream.of(parents.split(",")).map(beeTypeManager::getBeeType).toList();
            BeeType child = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.CHILD)));
            double chance = (double) map.get(ConfigKeys.CHANCE);
            this.registerBreed(new ResourcefulBreed(parentsArray.get(0), parentsArray.get(1), chance, child));
        });

       BeeLogger.info("&aLoaded " + this.breeds.size() + " breeds.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> breeds = this.breeds
                .stream()
                .map(breed -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ConfigKeys.PARENTS, breed.getParents().getLeft().getType() + "," + breed.getParents().getRight().getType());
                    map.put(ConfigKeys.CHANCE, breed.getPercent());
                    map.put(ConfigKeys.CHILD, breed.getChild().getType());
                    return map;
                }).toList();

        config.set(ConfigKeys.BREEDS, breeds);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
