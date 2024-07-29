package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;

public record ResourcefulBeehiveUpgrade(int id, double multiplier, double reducer, BeehiveCraft craft) implements BeehiveUpgrade {

        @Override
        public int getUpgradeLevel() {
            return id;
        }

        @Override
        public double multiplierProduction() {
            return multiplier;
        }

        @Override
        public double reducerTicks() {
            return reducer;
        }

    @Override
    public BeehiveCraft getCraft() { return craft; }

}