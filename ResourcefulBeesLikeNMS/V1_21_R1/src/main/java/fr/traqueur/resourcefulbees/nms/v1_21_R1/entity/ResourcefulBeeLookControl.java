package fr.traqueur.resourcefulbees.nms.v1_21_R1.entity;

import net.minecraft.world.entity.ai.control.LookControl;

public class ResourcefulBeeLookControl extends LookControl {

    private final ResourcefulBeeEntity bee;

    public ResourcefulBeeLookControl(ResourcefulBeeEntity entity) {
        super(entity);
        this.bee = entity;
    }

    @Override
    public void tick() {
        if (!bee.isAngry()) {
            super.tick();
        }
    }

    @Override
    protected boolean resetXRotOnTick() {
        return !bee.getPollinateGoal().isPollinating();
    }
}
