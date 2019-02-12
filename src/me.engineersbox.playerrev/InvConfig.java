package me.engineersbox.playerrev;

import java.util.ArrayList;
import java.util.List;

import me.engineersbox.playerrev.Main;

public class InvConfig extends AbstractFile {

    public InvConfig(Main main) {
       
        super(main, "applications.yml");
       
    }
    
    public static boolean SQLEnabled() {
    	if (config.contains("Config.Use-SQL")) {
    		return config.getBoolean("Config.Use-SQL");
    	} else {
    		return false;
    	}
    }
    
    public static void newApp(String pname, String rank) throws ArrayStoreException {
    	if (!config.contains(pname)) {
	    	List<String> raters = new ArrayList<String>();
	    	
	    	config.set(pname + ".Rank", rank);
	    	config.set(pname + ".Atmosphere", "0-0");
	    	config.set(pname + ".Originality", "0-0");
	    	config.set(pname + ".Skill", "0-0");
	    	config.set(pname + ".TotalRatings", "0");
	    	raters.add("Name-0-0-0");
	    	config.set(pname + ".Raters", raters);
	        saveConfig();
    	} else {
    		throw new ArrayStoreException();
    	}
	        
    }
    
    public static void removeApp(String pname) throws ArrayStoreException {
    	
    	if (config.getList(pname) != null) {
			config.set(pname, null);
			saveConfig();
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
    
    public static void ratePlayer(String prater, String pname, Integer atmosphere, Integer originality, Integer skill) throws ArrayStoreException {
    	
    	if (config.getList(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + ".Raters");
        	
        	int AtCount = Integer.parseInt(config.getString(pname + ".Atmosphere").substring(config.getString(pname + ".Atmosphere").lastIndexOf("-") + 1)) + 1;
        	int OrCount = Integer.parseInt(config.getString(pname + ".Originality").substring(config.getString(pname + ".Originality").lastIndexOf("-") + 1)) + 1;
        	int SkCount = Integer.parseInt(config.getString(pname + ".Skill").substring(config.getString(pname + ".Skill").lastIndexOf("-") + 1)) + 1;
        	int cTotalRatings = Integer.parseInt(config.getString(pname + ".TotalRatings")) + 1;
        	
        	int TotalAt = 0;
        	int TotalOr = 0;
        	int TotalSk = 0;
        	for (String cRater : raters) {
        		String[] cSplit = cRater.split("-");
        		for (int i = 1; i < cSplit.length; i++) {
            		if (i == 1) {
            			TotalAt += Integer.parseInt(cSplit[i]);
            		} else if (i == 2) {
            			TotalOr += Integer.parseInt(cSplit[i]);
            		} else if (i == 3) {
            			TotalSk += Integer.parseInt(cSplit[i]);
            		}
            	}
        	}
        	
        	config.set(pname + ".Atmosphere", Float.toString((TotalAt + atmosphere) / cTotalRatings) + "-" + AtCount);
        	config.set(pname + ".Originality", Float.toString((TotalOr + originality) / cTotalRatings) + "-" + OrCount);
        	config.set(pname + ".Skill", Float.toString((TotalSk + skill) / cTotalRatings) + "-" + SkCount);
        	config.set(pname + ".TotalRatings", Integer.toString(cTotalRatings));
        	
        	if (raters.get(0).equalsIgnoreCase("Name-0-0-0")) {
        		raters.set(0, prater + "-" + atmosphere + "-" + originality + "-" + skill);
        	} else {
        		raters.add(prater + "-" + atmosphere + "-" + originality + "-" + skill);
        	}
        	
        	config.set(pname + ".Raters", raters);
        	saveConfig();
    		
    	} else {
    		
    		throw new ArrayStoreException();
    		
    	}
    	
    }
    
    public static ArrayList<List<String>> getRatings(String pname) throws ArrayStoreException {
    	
    	if (config.getList(pname) != null) {
    		
        	List<String> raters = config.getStringList(pname + "raters");
        	
        	String cAtmosphere = config.getString(pname + ".Atmosphere").substring(0, config.getString(pname + ".Atmosphere").lastIndexOf("-"));
        	String cOriginality = config.getString(pname + ".Originality").substring(0, config.getString(pname + ".Originality").lastIndexOf("-"));
        	String cSkill = config.getString(pname + ".Skill").substring(0, config.getString(pname + ".Skill").lastIndexOf("-"));
        	String cTotalRatings = config.getString(pname + ".TotalRatings");
        	String rank = config.getString(pname + ".Rank");
        	
        	//raters name-0-0-0, avAt-avOr-avSk-TotalRatings
        	List<String> averages = new ArrayList<String>();
        	
        	averages.add(cAtmosphere + " ");
        	averages.add(cOriginality + " ");
        	averages.add(cSkill + " ");
        	averages.add(cTotalRatings);
        	averages.add(rank);
        	
        	ArrayList<List<String>> retval = new ArrayList<List<String>>();
        	retval.add(raters);
        	retval.add(averages);
        	
        	return retval;
    		
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
    
    public static String getAppRank(String pname) throws ArrayStoreException {
    	
    	if (config.getList(pname) != null) {
	    	return config.getString(pname + ".Rank");
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
   
}