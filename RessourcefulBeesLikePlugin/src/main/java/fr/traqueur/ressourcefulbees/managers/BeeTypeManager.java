package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;

import java.util.HashMap;

public class BeeTypeManager implements IBeeTypeManager {

    private final RessourcefulBeesLikePlugin plugin;
    private final HashMap<String, IBeeType> beeTypes;

    public BeeTypeManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypes = new HashMap<>();

        for (IBeeType IBeeType : BeeTypes.values()) {
            this.registerBeeType(IBeeType);
        }
    }

    public void registerBeeType(IBeeType IBeeType) {
        this.beeTypes.put(IBeeType.getName().toLowerCase(), IBeeType);
    }

    public IBeeType getBeeType(String name) {
        return this.beeTypes.getOrDefault(name.toLowerCase(), null);
    }

    public HashMap<String, IBeeType> getBeeTypes() {
        return beeTypes;
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }

    private enum BeeTypes implements IBeeType {

        NORMAL("normal"),
        DIRT("Dirt"),
        COBBLESTONE("Cobblestone"),
        SAND("Sand"),
        GRAVEL("Gravel"),
        WOOD("Wood"),
        ;

        private final String name;

        BeeTypes(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
