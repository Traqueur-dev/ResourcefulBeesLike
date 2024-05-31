package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IBreedsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.IBreed;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.models.Bee;
import fr.traqueur.ressourcefulbees.models.Breed;
import net.minecraft.util.Tuple;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BreedsManager implements IBreedsManager, Saveable {

    private final RessourcefulBeesLike plugin;
    private final IBeeTypeManager beeTypeManager;
    private final Set<IBreed> breeds;

    public BreedsManager(RessourcefulBeesLike plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(IBeeTypeManager.class);
        this.breeds = new HashSet<>();
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }

    @Override
    public String getFile() {
        return "breeds.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);
        if (config.getConfigurationSection(ConfigKeys.BREEDS) == null) {
            return;
        }

        config.getMapList(ConfigKeys.BREEDS).forEach(map -> {
            String parents = (String) map.get(ConfigKeys.PARENTS);
            List<BeeType> parentsArray = Stream.of(parents.split(",")).map(s-> s.replace("_bee", "")).map(beeTypeManager::getBeeType).toList();
            BeeType child = this.beeTypeManager.getBeeType((String) map.get(ConfigKeys.CHILD));
            double chance = (double) map.get(ConfigKeys.CHANCE);
            this.breeds.add(new Breed(parentsArray.get(0), parentsArray.get(1), chance, child));
        });
    }

    @Override
    public void saveData() {}
}
