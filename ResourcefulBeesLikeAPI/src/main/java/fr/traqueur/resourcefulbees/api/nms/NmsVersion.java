package fr.traqueur.resourcefulbees.api.nms;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NmsVersion {

    V_1_20(1200, "1_20_R1"),
    V_1_20_1(1201, "1_20_R1"),
    V_1_20_2(1202, "1_20_R2"),
    V_1_20_3(1203, "1_20_R3"),
    V_1_20_4(1204, "1_20_R3"),
    V_1_20_5(1205, "1_20_R4"),
    V_1_20_6(1206, "1_20_R4"),
    V_1_21(1210, "1_21_R1"),

    ;

    private final int version;
    private final String NMSVersion;

    NmsVersion(int version, String NMSVersion) {
        this.version = version;
        this.NMSVersion = NMSVersion;
    }

    /**
     * Gets the current version of the Bukkit server.
     *
     * @return The NmsVersion instance corresponding to the current version.
     */
    public static NmsVersion getCurrentVersion() {
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        int currentVersion = matcher.find() ? Integer.parseInt(matcher.group("version").replace(".", "") + (matcher.group("patch") != null ? matcher.group("patch").replace(".", "") : "0")) : 0;

        // Returns the version closest to the current version
        return java.util.Arrays.stream(values()).min(java.util.Comparator.comparingInt(v -> Math.abs(v.version - currentVersion))).orElse(V_1_21);
    }

    public String getNMSVersion() {
        return NMSVersion;
    }
}