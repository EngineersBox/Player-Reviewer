package me.engineersbox.playerrev.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			return true;
		} else {
			Bukkit.getLogger().info("[Player Reviewer] SQL Interfacting Disabled, Using Local File: " + config.getName());
			return false;
		}
	}
	
	public static void InitRankConfig() {
		Main.useConfigRanks = config.getBoolean("Application-Settings.Use-Config-Ranks");
    	if (Main.useConfigRanks == true) {
    		Main.configRankString = config.getString("Application-Settings.Application-Ranks");
    		for (String rankName : Main.configRankString.split(",")) {
        		if ((rankName.contains("[")) && (rankName.contains("]"))) {
        			List<String> splitName = new ArrayList<String>(Arrays.asList(rankName.substring(rankName.indexOf("["), rankName.indexOf("]")).split(".")));
        			for (String nameVal : splitName) {
        				if (nameVal.equals(rankName.substring(0, rankName.indexOf("]")).toUpperCase())) {
        					splitName.remove(nameVal);
        				}
        			}
        			splitName.add(rankName.substring(0, rankName.indexOf("]")).toUpperCase());
        			Main.ranksEnum.put(rankName.substring(0, rankName.indexOf("]")), splitName);
        		} else {
        			Main.ranksEnum.put(rankName, Arrays.asList(rankName.toUpperCase()));
        		}
        		
        	}
    	}
	}

}
