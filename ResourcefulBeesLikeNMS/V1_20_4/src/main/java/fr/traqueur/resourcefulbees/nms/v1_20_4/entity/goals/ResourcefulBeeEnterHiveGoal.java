package fr.traqueur.resourcefulbees.nms.v1_20_4.entity.goals;

import fr.traqueur.resourcefulbees.nms.v1_20_4.entity.ResourcefulBeeEntity;
import fr.traqueur.resourcefulbees.nms.v1_20_4.entity.ResourcefulBeeGoal;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResourcefulBeeEnterHiveGoal extends ResourcefulBeeGoal {

        public ResourcefulBeeEnterHiveGoal(ResourcefulBeeEntity bee) {
            super(bee);
        }

        @Override
        public boolean canBeeUse() {
            if (this.bee.hasHive() && this.bee.wantsToEnterHive() && this.bee.hivePos.closerToCenterThan(this.bee.position(), 2.0D)) {
                if (!this.isLoadedAndInBounds(this.bee.level(), this.bee.hivePos)) return false; // Paper - Do not allow bees to load chunks for beehives
                BlockEntity tileentity = this.bee.level().getBlockEntity(this.bee.hivePos);

                if (tileentity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity tileentitybeehive = (BeehiveBlockEntity) tileentity;

                    if (!tileentitybeehive.isFull()) {
                        return true;
                    }

                    this.bee.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            if (!this.isLoadedAndInBounds(this.bee.level(), this.bee.hivePos)) return; // Paper - Do not allow bees to load chunks for beehives
            BlockEntity tileentity = this.bee.level().getBlockEntity(this.bee.hivePos);

            if (tileentity instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity tileentitybeehive = (BeehiveBlockEntity) tileentity;

                tileentitybeehive.addOccupant(this.bee, this.bee.hasNectar());
            }

        }
    }