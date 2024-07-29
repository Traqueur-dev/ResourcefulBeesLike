package fr.traqueur.resourcefulbees.api.models;

import java.util.List;
import java.util.Map;

public interface Beehive {

    BeehiveUpgrade getUpgrade();

    void setUpgrade(BeehiveUpgrade upgrade);

    Map<BeeType, List<Long>> getBees();

    Map<BeeType, Integer> getHoneycombs();

    void addBee(BeeType beeType, int amount);

    List<BeeType> removeBee(int amount);

    default void addBee(BeeType beeType) {
        this.addBee(beeType, 1);
    }

    default BeeType removeBee() {
        return this.removeBee(1).getFirst();
    }

    void addHoneycomb(BeeType beeType, int amount);

    void removeHoneycomb(BeeType beeType, int amount);

    default void addHoneycomb(BeeType beeType) {
        this.addHoneycomb(beeType, 1);
    }

    default void removeHoneycomb(BeeType beeType) {
        this.removeHoneycomb(beeType, 1);
    }

}
