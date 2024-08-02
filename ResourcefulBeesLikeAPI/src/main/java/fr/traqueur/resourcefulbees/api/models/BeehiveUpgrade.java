package fr.traqueur.resourcefulbees.api.models;

public interface BeehiveUpgrade {

    int getUpgradeLevel();

    double multiplierProduction();

    double reducerTicks();

    boolean produceBlocks();

    BeehiveCraft getCraft();

}
