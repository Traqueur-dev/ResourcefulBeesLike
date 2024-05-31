package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.IBeeType;

public enum BeeTypes implements IBeeType {

        NORMAL_BEE("Bee"),
        DIRT_BEE("Dirt Bee"),
        COBBLESTONE_BEE("Cobblestone Bee"),
        SAND_BEE("Sand Bee"),
        GRAVEL_BEE("Gravel Bee"),
        WOOD_BEE("Wood Bee"),
        ;

        private final String name;

        BeeTypes(String name) {
            this.name = name;
        }

        @Override
        public String getType() {
            return this.name().toLowerCase();
        }

        @Override
        public String getName() {
            return this.name;
        }
    }