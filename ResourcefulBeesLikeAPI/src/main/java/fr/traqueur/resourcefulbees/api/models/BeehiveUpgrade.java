package fr.traqueur.resourcefulbees.api.models;

public interface BeehiveUpgrade {

    int getUpgradeLevel();

    double multiplierProduction();

    double reducerTicks();

    BeehiveCraft getCraft();

}
