package fr.traqueur.ressourcefulbees.api.models;

import org.bukkit.Material;

public interface IBeeType {

    String getType();

    String getName();

    Material getFood();
}
