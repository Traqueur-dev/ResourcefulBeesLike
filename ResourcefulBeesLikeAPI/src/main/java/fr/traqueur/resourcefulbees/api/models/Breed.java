package fr.traqueur.resourcefulbees.api.models;

import fr.traqueur.resourcefulbees.api.utils.Tuple;

public interface Breed {

    Tuple<BeeType, BeeType> getParents();

    double getPercent();

    BeeType getChild();

}
