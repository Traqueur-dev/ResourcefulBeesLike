package fr.traqueur.resourcefulbees.api;

import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.lang.LangKey;
import org.bukkit.entity.Player;

public interface ResourcefulBeesLikeAPI {

    void sendMessage(Player player, String message);

    void success(Player player, String message);

    void error(Player player, String message);

    String reset(String message);

    boolean isPaperVersion();

    <T> T getManager(Class<T> clazz);

    <I, T extends I> void registerManager(T instance, Class<I> clazz);

    void registerLanguageKey(LangKey langKey);

    void registerLanguage(String key, String path);

    String translate(String key, Formatter... formatters);

    default String translate(LangKey langKey, Formatter... formatters) {
        return this.translate(langKey.getKey(), formatters);
    }

}
