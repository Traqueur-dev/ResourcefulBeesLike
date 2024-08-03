package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import org.bukkit.entity.Bee;

import java.util.Map;

public interface BeeTypeManager extends Manager {

    BeeType getNaturalType();

    void registerBeeType(BeeType BeeType);

    void setupRecipes();

    BeeType getBeeTypeById(int id);

    BeeType getBeeType(String type);

    BeeType getBeeTypeFromBee(Bee bee);

    Map<String, BeeType> getBeeTypes();
}
