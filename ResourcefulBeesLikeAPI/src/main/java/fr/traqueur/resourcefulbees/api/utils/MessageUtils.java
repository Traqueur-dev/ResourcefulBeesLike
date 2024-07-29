package fr.traqueur.resourcefulbees.api.utils;

import org.bukkit.entity.Player;

public interface MessageUtils {

    void sendMessage(Player player, String message);

    void success(Player player, String message);

    void error(Player player, String message);

    String reset(String message);
}
