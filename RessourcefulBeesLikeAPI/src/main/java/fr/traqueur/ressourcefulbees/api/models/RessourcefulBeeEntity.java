package fr.traqueur.ressourcefulbees.api.models;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;

public class RessourcefulBeeEntity extends Bee {

    private final org.bukkit.inventory.ItemStack food;

    public RessourcefulBeeEntity(World world, org.bukkit.inventory.ItemStack food) {
        super(EntityType.BEE, ((CraftWorld) world).getHandle());
        this.food = food;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof TemptGoal);
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(CraftItemStack.asNMSCopy(food)), false));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.asBukkitCopy().isSimilar(food);
    }
}
