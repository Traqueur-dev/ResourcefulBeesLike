package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.models.IBreed;

public interface IBreedsManager {

    RessourcefulBeesLikeAPI getPlugin();

    IBreed getBreed(IBeeType fatherType, IBeeType motherType);
}
