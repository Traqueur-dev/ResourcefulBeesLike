package fr.traqueur.resourcefulbees.api.lang;

import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;

import java.util.function.Function;

public class Formatter {

    private final String pattern;
    private final Function<ResourcefulBeesLikeAPI, String> supplier;

    private Formatter(String pattern, Object supplier) {
        this.pattern = pattern;
        this.supplier = (api) -> supplier.toString();
    }

    private Formatter(String pattern, Function<ResourcefulBeesLikeAPI, String> supplier) {
        this.pattern = pattern;
        this.supplier = supplier;
    }

    public static Formatter format(String pattern, Object supplier) {
        return new Formatter(pattern, supplier);
    }

    public static Formatter format(String pattern, Function<ResourcefulBeesLikeAPI, String> supplier) {
        return new Formatter(pattern, supplier);
    }

    public static Formatter upgrade(BeehiveUpgrade upgrade) {
        return format("&upgrade&", (ressourcefulBeesLikeAPI -> ressourcefulBeesLikeAPI.translate("upgrade_" + upgrade.getUpgradeLevel() + "_name")));
    }

    public static Formatter beetype(BeeType beetype) {
        return format("&beetype&", (ressourcefulBeesLikeAPI -> ressourcefulBeesLikeAPI.translate(beetype.getType())));
    }

    public String handle(ResourcefulBeesLikeAPI api, String message) {
        return message.replaceAll(this.pattern, String.valueOf(this.supplier.apply(api)));
    }
}
