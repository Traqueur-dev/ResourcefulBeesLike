package fr.traqueur.resourcefulbees.nms.v1_20_R4.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

    public final org.bukkit.Material getBukkitMaterial(BlockState state) {;
        return org.bukkit.craftbukkit.v1_20_R4.block.CraftBlockType.minecraftToBukkit(state.getBlock());
    }

    public final boolean isLoadedAndInBounds(Level level, BlockPos blockposition) {
        return level.getWorldBorder().isWithinBounds(blockposition) &&  ((ServerLevel) level).getChunkSource().getChunkNow(blockposition.getX() >> 4, blockposition.getZ() >> 4) != null;
    }

}
