package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;

public abstract class Manager implements IManager {

    private final RessourcefulBeesLikeAPI plugin;

    public Manager(RessourcefulBeesLikeAPI plugin) {
        this.plugin = plugin;
    }
}
