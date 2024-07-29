package fr.traqueur.resourcefulbees.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentUtils {

    public static Component of(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }

}
