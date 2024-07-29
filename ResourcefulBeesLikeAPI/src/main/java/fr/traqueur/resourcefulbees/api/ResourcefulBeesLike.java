package fr.traqueur.resourcefulbees.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;
import java.util.logging.Level;

public abstract class ResourcefulBeesLike extends JavaPlugin implements ResourcefulBeesLikeAPI {

    public boolean isPaperVersion() {
        try {
            Class.forName("net.kyori.adventure.text.Component");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void saveOrUpdateConfiguration(String resourcePath, String toPath) {
        resourcePath = resourcePath.replace('\\', '/');
        File file = new File(getDataFolder(), toPath);
        if (!file.exists()) {
            saveResource(resourcePath, toPath, false);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {

            InputStream inputStream = this.getResource(resourcePath);

            if (inputStream == null) {
                getLogger().severe("Cannot find file " + resourcePath);
                return;
            }

            Reader defConfigStream = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);


            Set<String> defaultKeys = defConfig.getKeys(true);

            boolean configUpdated = false;
            for (String key : defaultKeys) {
                if (!config.contains(key)) {
                    configUpdated = true;
                }
            }

            config.setDefaults(defConfig);
            config.options().copyDefaults(true);

            if (configUpdated) {
                getLogger().info("Update file " + toPath);
                config.save(file);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveResource(String resourcePath, String toPath, boolean replace) {
        if (resourcePath != null && !resourcePath.isEmpty()) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
            } else {
                File outFile = new File(getDataFolder(), toPath);
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = Files.newOutputStream(outFile.toPath());
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException exception) {
                    getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, exception);
                }

            }
        } else throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }
}
