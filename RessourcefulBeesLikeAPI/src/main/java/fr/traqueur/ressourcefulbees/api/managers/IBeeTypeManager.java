package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;

import java.util.HashMap;

public interface IBeeTypeManager {

    void registerBeeType(IBeeType IBeeType);

    IBeeType getBeeType(String type);

    HashMap<String, IBeeType> getBeeTypes();

    RessourcefulBeesLikeAPI getPlugin();
}
