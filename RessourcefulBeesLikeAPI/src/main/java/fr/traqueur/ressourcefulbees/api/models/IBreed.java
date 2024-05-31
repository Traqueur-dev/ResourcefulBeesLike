package fr.traqueur.ressourcefulbees.api.models;

import net.minecraft.util.Tuple;

public interface IBreed {

    Tuple<BeeType, BeeType> getParents();

    double getPercent();

    BeeType getChild();

}
