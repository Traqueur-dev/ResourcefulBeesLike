package fr.traqueur.resourcefulbees.nms.v1_21_R1.entity.tasks;

import fr.traqueur.resourcefulbees.api.events.BeeMoveEvent;
import net.minecraft.world.entity.animal.Bee;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftBee;

import java.util.ArrayList;
import java.util.List;

public class MoveTask implements Runnable {

    @Override
    public void run() {

        List<Bee> bees = new ArrayList<>();

        for (CraftWorld world : Bukkit.getServer().getWorlds().stream().map(w -> ((CraftWorld) w)).toList()) {
            List<Bee> beeInWorld = world.getEntities()
                    .stream()
                    .filter(e -> e instanceof CraftBee)
                    .map(e -> ((CraftBee) e).getHandle()).toList();
            bees.addAll(beeInWorld);
        }

        for (Bee entity : bees) {
            if (entity.position().x != entity.xOld || entity.position().y != entity.yOld || entity.position().z != entity.zOld) {
                org.bukkit.entity.Bee bee = (org.bukkit.entity.Bee) entity.getBukkitEntity();
                BeeMoveEvent entityMoveEvent = new BeeMoveEvent(bee, new Location(bee.getWorld(), entity.xOld, entity.yOld, entity.zOld), new Location(bee.getWorld(), entity.position().x, entity.position().y, entity.position().z));
                Bukkit.getPluginManager().callEvent(entityMoveEvent);
            }
        }
        bees.clear();
    }
}
