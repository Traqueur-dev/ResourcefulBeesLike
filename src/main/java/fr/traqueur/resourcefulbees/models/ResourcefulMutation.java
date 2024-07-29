package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.Mutation;
import org.bukkit.Material;

public record ResourcefulMutation(BeeType parent, Material block, BeeType child) implements Mutation {

    public ResourcefulMutation {
        if(!block.isBlock()) {
            throw new IllegalArgumentException("Material must be a block");
        }
    }

    @Override
    public BeeType getParent() {
        return this.parent;
    }

    @Override
    public Material getBlock() {
        return this.block;
    }

    @Override
    public BeeType getChild() {
        return this.child;
    }
}
