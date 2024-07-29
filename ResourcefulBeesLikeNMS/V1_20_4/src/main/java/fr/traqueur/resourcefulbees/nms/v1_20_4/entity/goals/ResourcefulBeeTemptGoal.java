package fr.traqueur.resourcefulbees.nms.v1_20_4.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class ResourcefulBeeTemptGoal extends TemptGoal {

    public ResourcefulBeeTemptGoal(PathfinderMob entity, double speed, Ingredient food, boolean canBeScared) {
        super(entity, speed, food, canBeScared);
    }

}
