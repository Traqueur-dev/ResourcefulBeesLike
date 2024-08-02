package fr.traqueur.resourcefulbees.nms.v1_21_R1.entity.goals;

import fr.traqueur.resourcefulbees.nms.v1_21_R1.entity.ResourcefulBeeEntity;
import fr.traqueur.resourcefulbees.nms.v1_21_R1.entity.ResourcefulBeeGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcefulBeeLocateHiveGoal extends ResourcefulBeeGoal {
    
    public ResourcefulBeeLocateHiveGoal(ResourcefulBeeEntity bee) {
        super(bee);
    }

        @Override
        public boolean canBeeUse() {
            return this.bee.remainingCooldownBeforeLocatingNewHive == 0 && !this.bee.hasHive() && this.bee.wantsToEnterHive();
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.bee.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPos> list = this.findNearbyHivesWithSpace();

            if (!list.isEmpty()) {
                Iterator<BlockPos> iterator = list.iterator();

                BlockPos blockposition;

                do {
                    if (!iterator.hasNext()) {
                        this.bee.getGoToHiveGoal().clearBlacklist();
                        this.bee.hivePos = list.get(0);
                        return;
                    }

                    blockposition = iterator.next();
                } while (this.bee.getGoToHiveGoal().isTargetBlacklisted(blockposition));

                this.bee.hivePos = blockposition;
            }
        }

        private List<BlockPos> findNearbyHivesWithSpace() {
            BlockPos blockposition = this.bee.blockPosition();
            PoiManager villageplace = ((ServerLevel) this.bee.level()).getPoiManager();
            Stream<PoiRecord> stream = villageplace.getInRange((holder) -> {
                return holder.is(PoiTypeTags.BEE_HOME);
            }, blockposition, 20, PoiManager.Occupancy.ANY);

            return stream.map(PoiRecord::getPos).filter(this.bee::doesHiveHaveSpace).sorted(Comparator.comparingDouble((blockposition1) -> {
                return blockposition1.distSqr(blockposition);
            })).collect(Collectors.toList());
        }
    }