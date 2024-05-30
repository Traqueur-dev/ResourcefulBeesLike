package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.IBee;

public record Bee(BeeType type, boolean baby) implements IBee {
    @Override
    public BeeType getBeeType() {
        return type;
    }

    @Override
    public boolean isBaby() {
        return baby;
    }
}
