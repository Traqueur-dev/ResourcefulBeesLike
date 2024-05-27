package fr.traqueur.ressourcefulbees.api;

public interface RessourcefulBeesLikeAPI {

    <T> T getManager(Class<T> clazz);

    <T> void registerManager(T instance, Class<T> clazz);
}
