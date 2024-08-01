package fr.traqueur.resourcefulbees.platform.spigot;

import fr.traqueur.resourcefulbees.api.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpigotUtils implements MessageUtils {

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void success(Player player, String s) {
        this.sendMessage(player, ChatColor.translateAlternateColorCodes('&', "&a" + s));
    }

    @Override
    public void error(Player player, String s) {
        this.sendMessage(player, ChatColor.translateAlternateColorCodes('&', "&c" + s));
    }

    @Override
    public String reset(String s) {
        return ChatColor.translateAlternateColorCodes('&', "&r" + s);
    }
}
