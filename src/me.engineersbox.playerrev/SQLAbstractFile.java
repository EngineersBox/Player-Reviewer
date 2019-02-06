package me.engineersbox.playerrev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.engineersbox.playerrev.Main;

public class SQLAbstractFile {
   
    protected static Main main;
    static File sqfile;
    protected static FileConfiguration sqconfig;
   
    public SQLAbstractFile(Main main, String filename) {
        SQLAbstractFile.main = main;
        SQLAbstractFile.sqfile = new File(main.getDataFolder(), filename);
        if (!sqfile.exists()) {
            try {
            	sqfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SQLAbstractFile.sqconfig = YamlConfiguration.loadConfiguration(sqfile);
        List<String> defaultDetails = new ArrayList<String>();
        defaultDetails.add("DB_URL:jdbc:mysql://localhost/db");
        defaultDetails.add("USER:username");
        defaultDetails.add("PASS:password");
        sqconfig.addDefault("User-Details", defaultDetails);
        sqconfig.options().copyDefaults(true);
    }
    
    public static void saveConfig() {
        try {
        	sqconfig.save(sqfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
}