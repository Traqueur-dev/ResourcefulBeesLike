package fr.traqueur.resourcefulbees.platform.paper;

import fr.traqueur.resourcefulbees.api.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class PaperUtils implements MessageUtils {

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public void success(Player player, String s) {
        player.sendMessage(Component.text(s, NamedTextColor.GREEN));
    }

    @Override
    public void error(Player player, String s) {
        player.sendMessage(Component.text(s, NamedTextColor.RED));
    }

    @Override
    public String reset(String s) {
        return Component.text(s).decoration(TextDecoration.ITALIC, false).toString();
    }
}
