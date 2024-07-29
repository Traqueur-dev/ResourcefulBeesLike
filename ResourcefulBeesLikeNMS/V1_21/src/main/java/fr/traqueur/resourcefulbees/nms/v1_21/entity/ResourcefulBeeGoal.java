package fr.traqueur.resourcefulbees.nms.v1_21.entity;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class ResourcefulBeeGoal extends Goal {

    protected final ResourcefulBeeEntity bee;

    public ResourcefulBeeGoal(ResourcefulBeeEntity bee) {
        this.bee = bee;
    }

    public abstract boolean canBeeUse();

    public abstract boolean canBeeContinueToUse();

    @Override
    public boolean canUse() {
        return this.canBeeUse() && !bee.isAngry();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canBeeContinueToUse() && !bee.isAngry();
    }

}
