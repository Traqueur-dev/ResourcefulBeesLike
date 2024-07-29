package fr.traqueur.resourcefulbees.api.models;

import org.bukkit.Material;

public interface Mutation {

    BeeType getParent();

    Material getBlock();

    BeeType getChild();

}
