package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.Tuple;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.models.IBreed;

public class Breed implements IBreed {

    private final Tuple<IBeeType, IBeeType> parents;
    private final double percent;
    private final IBeeType child;

    public Breed(IBeeType parent1, IBeeType parent2, double percent, IBeeType child) {
        this.parents = new Tuple<>(parent1, parent2);
        this.percent = percent;
        this.child = child;
    }

    @Override
    public Tuple<IBeeType, IBeeType> getParents() {
        return this.parents;
    }

    @Override
    public double getPercent() {
        return this.percent;
    }

    @Override
    public IBeeType getChild() {
        return this.child;
    }
}
