package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;

import java.util.HashMap;

public interface IBeeTypeManager {

    void registerBeeType(BeeType beeType);

    BeeType getBeeType(String name);

    HashMap<String, BeeType> getBeeTypes();

    RessourcefulBeesLikeAPI getPlugin();
}
