package me.engineersbox.playerrev;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.exceptions.FieldValueException;
import me.engineersbox.playerrev.exceptions.PlotInheritanceException;
import me.engineersbox.playerrev.methodlib.Lib;
import me.engineersbox.playerrev.mysql.SQLConfig;

public class InvConfig extends AbstractFile {

    public InvConfig(Main main) {
       
        super(main, "applications.yml");
       
    }
    
    public static void newApp(Player p, String pname, String rank) throws FieldValueException, PlotInheritanceException {
    	
    	if (!config.contains(pname)) {
	    	List<String> raters = new ArrayList<String>();
	    	String coordsstring = null;
			PlotPlayer player = PlotPlayer.wrap(p);
			if (Main.usePlotLoc) {
				coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
			} else {
				coordsstring = Lib.getCoordsString(p.getLocation());
			}
	    	
			List<String> criteriaList = SQLConfig.getCriteria();
	    	config.set(pname + ".Rank", rank);
	    	for (String current : criteriaList) {
	    		config.set(pname + "." + current, "0-0");
	    	}
	    	config.set(pname + ".PlotLoc", coordsstring);
	    	config.set(pname + ".TotalRatings", "0");
	    	raters.add("Name-0");
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
    
    public static void ratePlayer(String prater, String pname, List<Integer> criteria) throws FieldValueException {
    	
    	if (config.get(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + ".Raters");
        	List<String> configCriteria = SQLConfig.getCriteria();
        	List<Integer> currentCriteria = new ArrayList<Integer>();
        	for (String current : configCriteria) {
        		currentCriteria.add(Integer.parseInt(config.getString(pname + "." + current).substring(config.getString(pname + "." + current).lastIndexOf("-") + 1)) + 1);
        	}
        	
        	int cTotalRatings = Integer.parseInt(config.getString(pname + ".TotalRatings")) + 1;
        	
        	List<Integer> valuelist = new ArrayList<Integer>();
			try {
				valuelist = Lib.valueListCreator(raters);
			} catch (SQLException e) {
				throw new FieldValueException(pname);
			}
			
			for (int i = 0; i < currentCriteria.size(); i++) {
				config.set(pname + "." + configCriteria.get(i), Float.toString((valuelist.get(i) + criteria.get(i)) / cTotalRatings) + "-" + currentCriteria.get(i));
			}
        	config.set(pname + ".TotalRatings", Integer.toString(cTotalRatings));
        	
        	String val = "";
    		for (Integer current : criteria) {
    			val += "-" + current;
    		}
        	
        	if (raters.get(0).equalsIgnoreCase("Name-0")) {
        		raters.set(0, prater + val);
        	} else {
        		raters.add(prater + val);
        	}
        	
        	config.set(pname + ".Raters", raters);
        	saveConfig();
    		
    	} else {
    		
    		throw new FieldValueException(pname);
    		
    	}
    	
    }
    
    public static ArrayList<List<String>> getRatings(String pname) throws FieldValueException {
    	
    	if (config.get(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + ".Raters");
        	List<String> configCriteria = SQLConfig.getCriteria();
        	List<String> averages = new ArrayList<String>();
        	
        	for (String current : configCriteria) {
        		averages.add(config.getString(pname + "." + current).substring(0, config.getString(pname + "." + current).lastIndexOf("-")));
        	}
        	averages.add(config.getString(pname + ".Rank"));
        	
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