package fr.traqueur.ressourcefulbees.api;

public interface RessourcefulBeesLikeAPI {

    <T> T getManager(Class<T> clazz);

    <I, T extends I> void registerManager(T instance, Class<I> clazz);
}
