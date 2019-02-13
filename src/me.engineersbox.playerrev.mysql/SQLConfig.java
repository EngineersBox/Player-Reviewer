package me.engineersbox.playerrev.mysql;

import org.bukkit.Bukkit;

import me.engineersbox.playerrev.AbstractFile;
import me.engineersbox.playerrev.Main;

public class SQLConfig extends AbstractFile {

    public SQLConfig(Main main) {
        
        super(main, "applications.yml");
       
    }
    
    public static String getHOSTNAME() {
    	return config.getString("User-Details.HOSTNAME");
    }
    
    public static String getPORT() {
    	return config.getString("User-Details.PORT");
    }
    
    public static String getDATABASE() {
    	return config.getString("User-Details.DATABASE");
    }
    
    public static String getUSER() {
    	return config.getString("User-Details.USER");
    }
    
    public static String getPASS() {
    	return config.getString("User-Details.PASS");
    }

	public static boolean SQLEnabled() {
		if (config.getBoolean("User-Details.UseSQL") == true) {
			Bukkit.getLogger().info("[Player Reviewer] SQL Interfacting Enabled");
			return config.getBoolean("User-Details.UseSQL");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] SQL Interfacting Disabled, Using Local File: " + config.getName());
			return config.getBoolean("User-Details.UseSQL");
		}
	}

}
