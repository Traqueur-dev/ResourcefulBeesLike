package fr.traqueur.resourcefulbees.api.managers;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Mutation;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

public interface MutationsManager extends Manager {

    void registerMutation(Mutation mutation);

    Set<Mutation> getMutationsForBlock(Material block);

    Set<Mutation> getMutationsForParent(BeeType parent);

    Mutation getMutation(BeeType parent, Material block);

    void mutateBee(Location to, BeeType child);

    Map<Material, Set<Mutation>> getMutations();
}
