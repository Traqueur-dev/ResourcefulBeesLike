package fr.traqueur.ressourcefulbees;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;

public class BeesManager implements IBeesManager {

    private final RessourcefulBeesLikeAPI plugin;

    public BeesManager(RessourcefulBeesLikeAPI plugin) {
       this.plugin = plugin;
    }


    @Override
    public void test() {
        BeeLogger.info("&cTest");
    }
}
