package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import org.bukkit.Material;

public enum BeeTypes implements IBeeType {

        NORMAL_BEE("Bee", Material.POPPY),
        DIRT_BEE("Dirt Bee", Material.DIRT),
        COBBLESTONE_BEE("Cobblestone Bee", Material.COBBLESTONE),
        SAND_BEE("Sand Bee", Material.SAND),
        GRAVEL_BEE("Gravel Bee", Material.GRAVEL),
        WOOD_BEE("Wood Bee", Material.OAK_WOOD),
        ;

        private final String name;
        private final Material food;

        BeeTypes(String name, Material food) {
            this.name = name;
            this.food = food;
        }

        @Override
        public String getType() {
            return this.name().toLowerCase();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Material getFood() {
            return food;
        }
}