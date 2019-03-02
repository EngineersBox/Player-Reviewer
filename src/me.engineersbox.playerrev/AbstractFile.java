package me.engineersbox.playerrev;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.engineersbox.playerrev.Main;

public class AbstractFile {
   
    protected static Main main;
    static File file;
    protected static FileConfiguration config;
   
    public AbstractFile(Main main, String filename) {
        AbstractFile.main = main;
        AbstractFile.file = new File(main.getDataFolder(), filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AbstractFile.config = YamlConfiguration.loadConfiguration(file);
        config.addDefault("User-Details.UseSQL", false);
        config.addDefault("User-Details.HOSTNAME", "localhost");
        config.addDefault("User-Details.DATABASE", "db");
        config.addDefault("User-Details.PORT", "3306");
        config.addDefault("User-Details.USER", "username");
        config.addDefault("User-Details.PASS", "password");
        config.addDefault("Application-Settings.Use-Plot-Locations", true);
        config.addDefault("Application-Settings.Criteria", "atmosphere,originality,terrain,structure,layout");
        config.addDefault("Application-Settings.Use-Config-Ranks", true);
        config.addDefault("Application-Settings.Application-Ranks", "guest[GUEST.guest],squire,knight,baron,builder,head_builder,senior_builder");
        config.options().copyDefaults(true);
        saveConfig();
    }
    
    public static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
}