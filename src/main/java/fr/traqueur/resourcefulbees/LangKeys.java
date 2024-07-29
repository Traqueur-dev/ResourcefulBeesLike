package fr.traqueur.resourcefulbees;

import fr.traqueur.resourcefulbees.api.lang.LangKey;

public enum LangKeys implements LangKey {

    BEE_GIVE("bee_give"),
    BEE_GIVE_BEEHIVE("bee_give_beehive"),
    BEE_BOX_GIVE("bee_box_give"),
    BEE_JAR_GIVE("bee_jar_give"),
    BEE_BOX_NAME("bee_box_name"),
    BEE_JAR_NAME("bee_jar_name"),
    BEE_BOX_FULL("bee_box_full"),
    BEE_JAR_FULL("bee_jar_full"),
    BEE_JAR_EMPTY("bee_jar_empty"),
    BEEHIVE_NAME("beehive_name"),
    SPAWN_EGG_NAME("spawn_egg_name"),
    NO_PERMISSION("no_permission"),
    ONLY_PLAYER("only_player"),
    MISSING_ARGS("missing_args"),
    ARG_NOT_RECOGNIZED("arg_not_recognized"),
    ;

    private final String key;

    LangKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
