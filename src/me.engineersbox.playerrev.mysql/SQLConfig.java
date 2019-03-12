package me.engineersbox.playerrev.mysql;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

import me.engineersbox.playerrev.AbstractFile;
import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.methodlib.DynamicEnum;

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
			Bukkit.getLogger().info("[PlayerReviewer] SQL Interfacting Enabled");
			return true;
		} else {
			Bukkit.getLogger().info("[PlayerReviewer] SQL Interfacting Disabled, Using Local File: " + config.getName());
			return false;
		}
	}
	
	public static void InitRankConfig() {
		Main.useConfigRanks = config.getBoolean("Application-Settings.Use-Config-Ranks");
    	if (Main.useConfigRanks == true) {
    		Main.configRankString = config.getString("Application-Settings.Application-Ranks");
    		Main.ranksEnum = new DynamicEnum<String, List<String>>(1);
    		for (String rankSplit : Main.configRankString.split(",")) {
    			if (StringUtils.containsAny(rankSplit, "[]")) {
    				String alias = StringUtils.substringBetween(rankSplit, "[", "]");
    				String[] aliasList = alias.split(":");
    				Main.ranksEnum.put(StringUtils.substringBefore(rankSplit, "["), Arrays.asList(aliasList));
    			} else {
    				Main.ranksEnum.put(rankSplit, Arrays.asList(rankSplit.toUpperCase()));
    			}
    		}
    	}
	}
	
	public static boolean usePlotLoc() {
		return config.getBoolean("Application-Settings.Use-Plot-Locations");
	}
	
	public static List<String> getCriteria() {
		return config.getStringList("Application-Settings.Criteria");
	}

}
