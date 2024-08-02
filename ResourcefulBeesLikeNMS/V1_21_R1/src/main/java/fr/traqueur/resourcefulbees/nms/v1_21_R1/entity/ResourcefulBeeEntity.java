package fr.traqueur.resourcefulbees.nms.v1_21_R1.entity;

import fr.traqueur.resourcefulbees.api.entity.BeeEntity;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.nms.v1_21_R1.entity.goals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;

import java.util.List;

public class ResourcefulBeeEntity extends Bee implements BeeEntity {

    private final World world;
    private final org.bukkit.inventory.ItemStack food;
    private final ResourcefulBeeGoToKnownFlowerGoal goToKnownFlowerGoal;
    private final ResourcefulBeePollinateGoal pollinateGoal;
    private final ResourcefulBeeGoToHiveGoal goToHiveGoal;
    private final Material flowerType;

    public int ticksWithoutNectarSinceExitingHive;
    public int remainingCooldownBeforeLocatingNewFlower;
    public int remainingCooldownBeforeLocatingNewHive;

    public ResourcefulBeeEntity(World world, BeeType type) {
        super(EntityType.BEE, ((CraftWorld) world).getHandle());
        this.world = world;
        this.food = new org.bukkit.inventory.ItemStack(type.getFood());
        this.flowerType = type.getFlower();
        this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
        Ingredient ingredient = Ingredient.of(CraftItemStack.asNMSCopy(food));
        this.goToKnownFlowerGoal = new ResourcefulBeeGoToKnownFlowerGoal(this);
        this.pollinateGoal = new ResourcefulBeePollinateGoal(this);
        this.goToHiveGoal = new ResourcefulBeeGoToHiveGoal(this);

        this.goalSelector.addGoal(1, new ResourcefulBeeEnterHiveGoal(this));
        this.goalSelector.addGoal(3, new ResourcefulBeeTemptGoal(this, 1.25D, ingredient, false));
        this.goalSelector.addGoal(5, new ResourcefulBeeLocateHiveGoal(this));
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, this.goToHiveGoal);
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.lookControl = new ResourcefulBeeLookControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof TemptGoal && !(wrappedGoal.getGoal() instanceof ResourcefulBeeTemptGoal));
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof BeeGoToKnownFlowerGoal);
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getPriority() == 4 && !(wrappedGoal.getGoal() instanceof ResourcefulBeePollinateGoal)); //pollinateGoal is only goal with 4 priority
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof BeeGoToHiveGoal);
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> {
            Goal goal = wrappedGoal.getGoal();
            return wrappedGoal.getPriority() == 5 && !(goal instanceof FollowParentGoal) && !(goal instanceof ResourcefulBeeGoToHiveGoal);
        }); // remove LocateHiveGoal because is private so we can't access it
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getPriority() == 1 && !(wrappedGoal.getGoal() instanceof ResourcefulBeeEnterHiveGoal));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return CraftItemStack.asCraftMirror(stack).isSimilar(food);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.remainingCooldownBeforeLocatingNewHive > 0) {
                --this.remainingCooldownBeforeLocatingNewHive;
            }

            if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
                --this.remainingCooldownBeforeLocatingNewFlower;
            }
        }
    }

    @Override
    public int getTravellingTicks() {
        return Math.max(this.goToHiveGoal.getTravellingTicks(), this.goToKnownFlowerGoal.getTravellingTicks());
    }

    @Override
    public List<BlockPos> getBlacklistedHives() {
        return this.goToHiveGoal.getBlacklistedTargets();
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigationflying = new FlyingPathNavigation(this, world) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }

            @Override
            public void tick() {
                if (!ResourcefulBeeEntity.this.pollinateGoal.isPollinating()) {
                    super.tick();
                }
            }
        };

        navigationflying.setCanOpenDoors(false);
        navigationflying.setCanFloat(false);
        navigationflying.setCanPassDoors(true);
        return navigationflying;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean result = super.hurt(source, amount);
            if (result && !this.level().isClientSide) {
                this.pollinateGoal.stopPollinating();
            }

            return result;
        }
    }

    public void addAdditionalSaveData(CompoundTag nbttagcompound) {
        this.addAdditionalSaveData(nbttagcompound, true);
    }

    public void addAdditionalSaveData(CompoundTag nbttagcompound, boolean includeAll) {
        super.addAdditionalSaveData(nbttagcompound, includeAll);
        nbttagcompound.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.ticksWithoutNectarSinceExitingHive = nbt.getInt("TicksSincePollination");
    }

    private boolean isTiredOfLookingForNectar() {
        return this.ticksWithoutNectarSinceExitingHive > 3600;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.hasNectar()) {
            ++this.ticksWithoutNectarSinceExitingHive;
        }
    }

    public void resetTicksWithoutNectarSinceExitingHive() {
        this.ticksWithoutNectarSinceExitingHive = 0;
    }



    public final boolean isLoadedAndInBounds(Level level, BlockPos blockposition) {
        return level.getWorldBorder().isWithinBounds(blockposition) &&  ((ServerLevel) level).getChunkSource().getChunkNow(blockposition.getX() >> 4, blockposition.getZ() >> 4) != null;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        } else {
            if (!this.isLoadedAndInBounds(this.level(), this.hivePos)) return false;
            BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);

            return tileentity instanceof BeehiveBlockEntity && ((BeehiveBlockEntity) tileentity).isFireNearby();
        }
    }

    public boolean doesHiveHaveSpace(BlockPos pos) {
        if (!this.isLoadedAndInBounds(this.level(), pos)) return false; // Paper - Do not allow bees to load chunks for beehives
        BlockEntity tileentity = this.level().getBlockEntity(pos);

        return tileentity instanceof BeehiveBlockEntity && !((BeehiveBlockEntity) tileentity).isFull();
    }

    public boolean isFlowerValid(BlockPos pos) {
        return this.level().isLoaded(pos) &&
                this.getBukkitMaterial(this.level().getBlockState(pos)) == this.flowerType;
    }

    public final org.bukkit.Material getBukkitMaterial(BlockState state) {;
        return org.bukkit.craftbukkit.v1_21_R1.block.CraftBlockType.minecraftToBukkit(state.getBlock());
    }

    public boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = this.ticksWithoutNectarSinceExitingHive > 3600 || this.level().isRaining() || this.level().isNight() || this.hasNectar();

            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    public void pathfindRandomlyTowards(BlockPos pos) {
        Vec3 vec3d = Vec3.atBottomCenterOf(pos);
        byte b0 = 0;
        BlockPos blockposition1 = this.blockPosition();
        int i = (int) vec3d.y - blockposition1.getY();

        if (i > 2) {
            b0 = 4;
        } else if (i < -2) {
            b0 = -4;
        }

        int j = 6;
        int k = 8;
        int l = blockposition1.distManhattan(pos);

        if (l < 15) {
            j = l / 2;
            k = l / 2;
        }

        Vec3 vec3d1 = AirRandomPos.getPosTowards(this, j, k, b0, vec3d, 0.3141592741012573D);

        if (vec3d1 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
        }
    }

    public org.bukkit.inventory.ItemStack getFood() {
        return food;
    }

    public ResourcefulBeeGoToHiveGoal getGoToHiveGoal() {
        return goToHiveGoal;
    }

    public ResourcefulBeePollinateGoal getPollinateGoal() {
        return pollinateGoal;
    }

    @Override
    public void setStayOutOfHive(int cannotEnterHiveTicks) {
        this.setStayOutOfHiveCountdown(cannotEnterHiveTicks);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.setPos(x, y, z);
    }

    @Override
    public org.bukkit.entity.Bee getSpigotEntity() {
        return (org.bukkit.entity.Bee) this.getBukkitEntity();
    }

    @Override
    public void spawn() {
        ((CraftWorld) world).getHandle().addFreshEntity(this);
    }
}
