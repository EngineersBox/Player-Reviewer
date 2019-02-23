package me.engineersbox.playerrev;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import MethodLib.FieldValueException;
import MethodLib.Lib;
import me.engineersbox.playerrev.Main;

public class InvConfig extends AbstractFile {

    public InvConfig(Main main) {
       
        super(main, "applications.yml");
       
    }
    
    public static void newApp(Player p, String pname, String rank) throws FieldValueException {
    	if (!config.contains(pname)) {
	    	List<String> raters = new ArrayList<String>();
	    	
	    	config.set(pname + ".Rank", rank);
	    	config.set(pname + ".Atmosphere", "0-0");
	    	config.set(pname + ".Originality", "0-0");
	    	config.set(pname + ".Terrain", "0-0");
	    	config.set(pname + ".Structure", "0-0");
	    	config.set(pname + ".Layout", "0-0");
	    	config.set(pname + ".PlotLoc", Lib.getCoordsString(p.getLocation()));
	    	config.set(pname + ".TotalRatings", "0");
	    	raters.add("Name-0-0-0-0-0");
	    	config.set(pname + ".Raters", raters);
	        saveConfig();
    	} else {
    		throw new FieldValueException(pname);
    	}
	        
    }
    
    public static void removeApp(String pname) throws FieldValueException {
    	
    	if (config.getList(pname) != null) {
			config.set(pname, null);
			saveConfig();
    	} else {
    		throw new FieldValueException(pname);
    	}
    	
    }
    
    public static void ratePlayer(String prater, String pname, Integer atmosphere, Integer originality, Integer terrain, Integer structure, Integer layout) throws FieldValueException {
    	
    	if (config.getList(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + ".Raters");
        	
        	int AtCount = Integer.parseInt(config.getString(pname + ".Atmosphere").substring(config.getString(pname + ".Atmosphere").lastIndexOf("-") + 1)) + 1;
        	int OrCount = Integer.parseInt(config.getString(pname + ".Originality").substring(config.getString(pname + ".Originality").lastIndexOf("-") + 1)) + 1;
        	int TrCount = Integer.parseInt(config.getString(pname + ".Terrain").substring(config.getString(pname + ".Terrain").lastIndexOf("-") + 1)) + 1;
        	int StCount = Integer.parseInt(config.getString(pname + ".Structure").substring(config.getString(pname + ".Structure").lastIndexOf("-") + 1)) + 1;
        	int LaCount = Integer.parseInt(config.getString(pname + ".Layout").substring(config.getString(pname + ".Layout").lastIndexOf("-") + 1)) + 1;
        	int cTotalRatings = Integer.parseInt(config.getString(pname + ".TotalRatings")) + 1;
        	
        	List<Integer> valuelist = new ArrayList<Integer>();
			try {
				valuelist = Lib.valueListCreator(raters);
			} catch (SQLException e) {
				throw new FieldValueException(pname);
			}
        	
        	config.set(pname + ".Atmosphere", Float.toString((valuelist.get(0) + atmosphere) / cTotalRatings) + "-" + AtCount);
        	config.set(pname + ".Originality", Float.toString((valuelist.get(1) + originality) / cTotalRatings) + "-" + OrCount);
        	config.set(pname + ".Terrain", Float.toString((valuelist.get(2) + terrain) / cTotalRatings) + "-" + TrCount);
        	config.set(pname + ".Structure", Float.toString((valuelist.get(3) + structure) / cTotalRatings) + "-" + StCount);
        	config.set(pname + ".Layout", Float.toString((valuelist.get(4) + layout) / cTotalRatings) + "-" + LaCount);
        	config.set(pname + ".TotalRatings", Integer.toString(cTotalRatings));
        	
        	if (raters.get(0).equalsIgnoreCase("Name-0-0-0-0-0")) {
        		raters.set(0, prater + "-" + atmosphere + "-" + originality + "-" + terrain + "-" + structure + "-" + layout);
        	} else {
        		raters.add(prater + "-" + atmosphere + "-" + originality + "-" + terrain + "-" + structure + "-" + layout);
        	}
        	
        	config.set(pname + ".Raters", raters);
        	saveConfig();
    		
    	} else {
    		
    		throw new FieldValueException(pname);
    		
    	}
    	
    }
    
    public static ArrayList<List<String>> getRatings(String pname) throws FieldValueException {
    	
    	if (config.getList(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + "raters");
        	
        	String cAtmosphere = config.getString(pname + ".Atmosphere").substring(0, config.getString(pname + ".Atmosphere").lastIndexOf("-"));
        	String cOriginality = config.getString(pname + ".Originality").substring(0, config.getString(pname + ".Originality").lastIndexOf("-"));
        	String cTerrain = config.getString(pname + ".Terrain").substring(0, config.getString(pname + ".Terrain").lastIndexOf("-"));
        	String cStructure = config.getString(pname + ".Structure").substring(0, config.getString(pname + ".Structure").lastIndexOf("-"));
        	String cLayout = config.getString(pname + ".Layout").substring(0, config.getString(pname + ".Layout").lastIndexOf("-"));
        	String cTotalRatings = config.getString(pname + ".TotalRatings");
        	String rank = config.getString(pname + ".Rank");
        	
        	List<String> averages = new ArrayList<String>();
        	
        	averages.add(cAtmosphere + " ");
        	averages.add(cOriginality + " ");
        	averages.add(cTerrain + " ");
        	averages.add(cStructure + " ");
        	averages.add(cLayout + " ");
        	averages.add(cTotalRatings);
        	averages.add(rank);
        	
        	ArrayList<List<String>> retval = new ArrayList<List<String>>();
        	retval.add(raters);
        	retval.add(averages);
        	
        	return retval;
    		
    	} else {
    		throw new FieldValueException(pname);
    	}
    	
    }
    
    public static Location getPlotLocation(String pname) throws FieldValueException {
    	
    	if (config.getList(pname) != null) {
    		return Lib.getLoc(config.getString(pname + ".PlotLoc"));
    	} else {
    		throw new FieldValueException(pname);
    	}
    	
    }
    
    public static String getAppRank(String pname) throws FieldValueException {
    	
    	if (config.getList(pname) != null) {
	    	return config.getString(pname + ".Rank");
    	} else {
    		throw new FieldValueException(pname);
    	}
    	
    }
   
}