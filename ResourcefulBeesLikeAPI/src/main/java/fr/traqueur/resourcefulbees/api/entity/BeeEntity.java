package fr.traqueur.resourcefulbees.api.entity;

import org.bukkit.entity.Bee;

public interface BeeEntity {

    void setPosition(double x, double y, double z);

    void setStayOutOfHive(int countdown);

    Bee getSpigotEntity();

    void spawn();

}
