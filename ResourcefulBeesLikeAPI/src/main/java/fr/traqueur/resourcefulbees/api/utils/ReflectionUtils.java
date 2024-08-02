package fr.traqueur.resourcefulbees.api.utils;

import fr.traqueur.resourcefulbees.api.nms.NmsVersion;

public enum ReflectionUtils {

    ENTITY("fr.traqueur.resourcefulbees.nms.%s.entity", "ResourcefulBeeEntity"),
    MOVE_TASK("fr.traqueur.resourcefulbees.nms.%s.entity.tasks", "MoveTask")
    ;

    private final String packageName;
    private final String className;

    ReflectionUtils(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getVersioned(NmsVersion version) {
        return String.format(this.packageName, "v" + version.getNMSVersion()) + "." + this.className;
    }

}
