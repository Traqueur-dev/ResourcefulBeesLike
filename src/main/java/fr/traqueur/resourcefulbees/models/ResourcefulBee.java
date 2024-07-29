package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.api.models.Bee;
import fr.traqueur.resourcefulbees.api.models.BeeType;

public record ResourcefulBee(BeeType type, boolean baby, boolean nectar) implements Bee {
    @Override
    public BeeType getBeeType() {
        return type;
    }

    @Override
    public boolean isBaby() {
        return baby;
    }

    @Override
    public boolean hasNectar() {
        return nectar;
    }
}
