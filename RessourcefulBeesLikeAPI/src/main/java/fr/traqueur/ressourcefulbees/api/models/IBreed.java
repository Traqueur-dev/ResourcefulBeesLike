package fr.traqueur.ressourcefulbees.api.models;

import fr.traqueur.ressourcefulbees.api.Tuple;

public interface IBreed {

    Tuple<IBeeType, IBeeType> getParents();

    double getPercent();

    IBeeType getChild();

}
