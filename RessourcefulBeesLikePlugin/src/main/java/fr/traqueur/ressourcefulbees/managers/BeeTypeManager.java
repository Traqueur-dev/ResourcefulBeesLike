package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;

import java.util.HashMap;

public class BeeTypeManager implements IBeeTypeManager {

    private final RessourcefulBeesLikePlugin plugin;
    private final HashMap<String, BeeType> beeTypes;

    public BeeTypeManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypes = new HashMap<>();

        for (BeeType beeType : BeeTypes.values()) {
            this.registerBeeType(beeType);
        }
    }

    public void registerBeeType(BeeType beeType) {
        this.beeTypes.put(beeType.getName().toLowerCase(), beeType);
    }

    public BeeType getBeeType(String name) {
        return this.beeTypes.getOrDefault(name.toLowerCase(), null);
    }

    public HashMap<String, BeeType> getBeeTypes() {
        return beeTypes;
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }

    private enum BeeTypes implements BeeType {

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
