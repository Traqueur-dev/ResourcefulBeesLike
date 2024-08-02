package fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.goals;

import com.google.common.collect.Lists;
import fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.ResourcefulBeeEntity;
import fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.ResourcefulBeeGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class ResourcefulBeeGoToHiveGoal extends ResourcefulBeeGoal {

        private int travellingTicks;
        final List<BlockPos> blacklistedTargets;
        @Nullable
        private Path lastPath;
        private int ticksStuck;

        public ResourcefulBeeGoToHiveGoal(ResourcefulBeeEntity bee) {
            super(bee);
            this.travellingTicks = this.bee.getRandom().nextInt(10);
            this.blacklistedTargets = Lists.newArrayList();
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canBeeUse() {
            return this.bee.hivePos != null && !this.bee.hasRestriction() && this.bee.wantsToEnterHive() && !this.hasReachedTarget(this.bee.hivePos) && this.bee.level().getBlockState(this.bee.hivePos).is(BlockTags.BEEHIVES);
        }

        @Override
        public boolean canBeeContinueToUse() {
            return this.canBeeUse();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            this.bee.getNavigation().stop();
            this.bee.getNavigation().resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (this.bee.hivePos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > this.adjustedTickDelay(600)) {
                    this.dropAndBlacklistHive();
                } else if (!this.bee.getNavigation().isInProgress()) {
                    if (!this.bee.blockPosition().closerThan(this.bee.hivePos, 16)) {
                        if (this.bee.blockPosition().closerThan(this.bee.hivePos, 32)) {
                            this.dropHive();
                        } else {
                            this.bee.pathfindRandomlyTowards(this.bee.hivePos);
                        }
                    } else {
                        boolean flag = this.pathfindDirectlyTowards(this.bee.hivePos);

                        if (!flag) {
                            this.dropAndBlacklistHive();
                        } else if (this.lastPath != null && this.bee.getNavigation().getPath().sameAs(this.lastPath)) {
                            ++this.ticksStuck;
                            if (this.ticksStuck > 60) {
                                this.dropHive();
                                this.ticksStuck = 0;
                            }
                        } else {
                            this.lastPath = this.bee.getNavigation().getPath();
                        }

                    }
                }
            }
        }

        private boolean pathfindDirectlyTowards(BlockPos pos) {
            this.bee.getNavigation().setMaxVisitedNodesMultiplier(10.0F);
            this.bee.getNavigation().moveTo((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 1.0D);
            return this.bee.getNavigation().getPath() != null && this.bee.getNavigation().getPath().canReach();
        }

        public boolean isTargetBlacklisted(BlockPos pos) {
            return this.blacklistedTargets.contains(pos);
        }

        private void blacklistTarget(BlockPos pos) {
            this.blacklistedTargets.add(pos);

            while (this.blacklistedTargets.size() > 3) {
                this.blacklistedTargets.remove(0);
            }

        }

        public void clearBlacklist() {
            this.blacklistedTargets.clear();
        }

        private void dropAndBlacklistHive() {
            if (this.bee.hivePos != null) {
                this.blacklistTarget(this.bee.hivePos);
            }

            this.dropHive();
        }

        private void dropHive() {
            this.bee.hivePos = null;
            this.bee.remainingCooldownBeforeLocatingNewHive = 200;
        }

        private boolean hasReachedTarget(BlockPos pos) {
            if (this.bee.blockPosition().closerThan(pos, 2)) {
                return true;
            } else {
                Path pathentity = this.bee.getNavigation().getPath();

                return pathentity != null && pathentity.getTarget().equals(pos) && pathentity.canReach() && pathentity.isDone();
            }
        }

    public int getTravellingTicks() {
        return travellingTicks;
    }

    public List<BlockPos> getBlacklistedTargets() {
        return blacklistedTargets;
    }
}