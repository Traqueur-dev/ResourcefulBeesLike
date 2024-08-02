package fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.goals;

import fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.ResourcefulBeeEntity;
import fr.traqueur.resourcefulbees.nms.v1_20_R1.entity.ResourcefulBeeGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class ResourcefulBeePollinateGoal extends ResourcefulBeeGoal {

        private final Predicate<BlockState> VALID_POLLINATION_BLOCKS;
        private int successfulPollinatingTicks;
        private int lastSoundPlayedTick;
        private boolean pollinating;
        @Nullable
        private Vec3 hoverPos;
        private int pollinatingTicks;
        
        public ResourcefulBeePollinateGoal(ResourcefulBeeEntity bee) {
            super(bee);
            this.setFlags(EnumSet.of(Flag.MOVE));
            VALID_POLLINATION_BLOCKS = (iblockdata) -> (!iblockdata.hasProperty(BlockStateProperties.WATERLOGGED)
                    || !((Boolean) iblockdata.getValue(BlockStateProperties.WATERLOGGED)))
                    &&  this.getBukkitMaterial(iblockdata) == bee.getFood().getType();
        }
        
        public boolean canBeeUse() {
            if (bee.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            } else if (bee.hasNectar()) {
                return false;
            } else if (bee.level().isRaining()) {
                return false;
            } else {
                Optional<BlockPos> optional = this.findNearbyFlower();

                if (optional.isPresent()) {
                    bee.setSavedFlowerPos(optional.get());
                    bee.getNavigation().moveTo((double) bee.getSavedFlowerPos().getX() + 0.5D, (double) bee.getSavedFlowerPos().getY() + 0.5D, (double) bee.getSavedFlowerPos().getZ() + 0.5D, 1.2000000476837158D);
                    return true;
                } else {
                    bee.remainingCooldownBeforeLocatingNewFlower = bee.getRandom().nextInt(20, 60);
                    return false;
                }
            }
        }
        
        public boolean canBeeContinueToUse() {
            if (!this.pollinating) {
                return false;
            } else if (!bee.hasSavedFlowerPos()) {
                return false;
            } else if (bee.level().isRaining()) {
                return false;
            } else if (this.hasPollinatedLongEnough()) {
                return bee.getRandom().nextFloat() < 0.2F;
            } else if (bee.tickCount % 20 == 0 && !bee.isFlowerValid(bee.getSavedFlowerPos())) {
                bee.setSavedFlowerPos(null);
                return false;
            } else {
                return true;
            }
        }

        private boolean hasPollinatedLongEnough() {
            return this.successfulPollinatingTicks > 400;
        }

        public boolean isPollinating() {
            return this.pollinating;
        }

        public void stopPollinating() {
            this.pollinating = false;
        }

        @Override
        public void start() {
            this.successfulPollinatingTicks = 0;
            this.pollinatingTicks = 0;
            this.lastSoundPlayedTick = 0;
            this.pollinating = true;
            bee.resetTicksWithoutNectarSinceExitingHive();
        }

        @Override
        public void stop() {
            if (this.hasPollinatedLongEnough()) {
                bee.setHasNectar(true);
            }

            this.pollinating = false;
            bee.getNavigation().stop();
            bee.remainingCooldownBeforeLocatingNewFlower = 200;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.pollinatingTicks;
            if (this.pollinatingTicks > 600) {
                bee.setSavedFlowerPos(null);
            } else if (bee.getSavedFlowerPos() != null) { // Paper - add null check since API can manipulate this
                Vec3 vec3d = Vec3.atBottomCenterOf(bee.getSavedFlowerPos()).add(0.0D, 0.6000000238418579D, 0.0D);

                if (vec3d.distanceTo(bee.position()) > 1.0D) {
                    this.hoverPos = vec3d;
                    this.setWantedPos();
                } else {
                    if (this.hoverPos == null) {
                        this.hoverPos = vec3d;
                    }

                    boolean flag = bee.position().distanceTo(this.hoverPos) <= 0.1D;
                    boolean flag1 = true;

                    if (!flag && this.pollinatingTicks > 600) {
                        bee.setSavedFlowerPos(null);
                    } else {
                        if (flag) {
                            boolean flag2 = bee.getRandom().nextInt(25) == 0;

                            if (flag2) {
                                this.hoverPos = new Vec3(vec3d.x() + (double) this.getOffset(), vec3d.y(), vec3d.z() + (double) this.getOffset());
                                bee.getNavigation().stop();
                            } else {
                                flag1 = false;
                            }

                            bee.getLookControl().setLookAt(vec3d.x(), vec3d.y(), vec3d.z());
                        }

                        if (flag1) {
                            this.setWantedPos();
                        }

                        ++this.successfulPollinatingTicks;
                        if (bee.getRandom().nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                            this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                            bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }

        private void setWantedPos() {
            bee.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.3499999940395355D);
        }

        private float getOffset() {
            return (bee.getRandom().nextFloat() * 2.0F - 1.0F) * 0.33333334F;
        }

        private Optional<BlockPos> findNearbyFlower() {
            return this.findNearestBlock(this.VALID_POLLINATION_BLOCKS, 5.0D);
        }

        private Optional<BlockPos> findNearestBlock(Predicate<BlockState> predicate, double searchDistance) {
            BlockPos blockposition = bee.blockPosition();
            BlockPos.MutableBlockPos blockposition_mutableblockposition = new BlockPos.MutableBlockPos();

            for (int i = 0; (double) i <= searchDistance; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < searchDistance; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.setWithOffset(blockposition, k, i - 1, l);
                            if (blockposition.closerThan(blockposition_mutableblockposition, searchDistance) && predicate.test(bee.level().getBlockState(blockposition_mutableblockposition))) {
                                return Optional.of(blockposition_mutableblockposition);
                            }
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }