package fr.traqueur.ressourcefulbees.api;

import fr.traqueur.ressourcefulbees.api.managers.Manager;

public interface RessourcefulBeesLikeAPI {

    <T extends Manager> T getManager(Class<T> clazz);

    <T extends Manager> void registerManager(Class<T> clazz);
}
