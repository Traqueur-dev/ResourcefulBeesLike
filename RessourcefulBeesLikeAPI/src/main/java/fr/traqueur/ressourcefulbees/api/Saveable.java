package fr.traqueur.ressourcefulbees.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public interface Saveable {

    String getFile();

    default FileConfiguration getConfig(JavaPlugin plugin) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), this.getFile()));
    };

    void loadData();

    void saveData();

}
