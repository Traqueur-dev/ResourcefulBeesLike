package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Breed;

public interface BreedsManager extends Manager {

    void registerBreed(Breed breed);

    Breed getBreed(BeeType fatherType, BeeType motherType);
}
