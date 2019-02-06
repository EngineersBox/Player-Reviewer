package me.engineersbox.playerrev;

import java.util.ArrayList;
import java.util.List;

import me.engineersbox.playerrev.Main;

public class InvConfig extends AbstractFile{

    public InvConfig(Main main) {
       
        super(main, "applications.yml");
       
    }
    
    public static void newApp(String pname, String rank) {
    	if (!config.contains(pname)) {
	    	List<String> criteria = new ArrayList<String>();
	    	List<String> raters = new ArrayList<String>();
	    	
	    	criteria.add("Rank-" + rank);
	    	criteria.add("Atmosphere-0-0");
	    	criteria.add("Originality-0-0");
	    	criteria.add("Skill-0-0");
	    	criteria.add("TotalRatings-0");
	    	
	    	raters.add("Name-0-0-0");
	    	
	    	config.set(pname, criteria);
	    	config.set(pname + "raters", raters);
	        saveConfig();
    	}
	        
    }
    
    public static void removeApp(String pname) throws ArrayStoreException {
    	
    	if ((config.getString(pname) != null) && (config.getString(pname + "raters") != null)) {
			config.set(pname, null);
			config.set(pname + "raters", null);
			saveConfig();
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
    
    public static void ratePlayer(String prater, String pname, Integer atmosphere, Integer originality, Integer skill) throws ArrayStoreException {
    	
    	if ((config.getString(pname) != null) && (config.getString(pname + "raters") != null)) {
    		
    		List<String> criteria = config.getStringList(pname);
        	List<String> raters = config.getStringList(pname + "raters");
        	
        	int AtCount = Integer.parseInt(criteria.get(1).substring(criteria.get(1).lastIndexOf("-") + 1)) + 1;
        	int OrCount = Integer.parseInt(criteria.get(2).substring(criteria.get(2).lastIndexOf("-") + 1)) + 1;
        	int SkCount = Integer.parseInt(criteria.get(3).substring(criteria.get(3).lastIndexOf("-") + 1)) + 1;
        	int cTotalRatings = Integer.parseInt(criteria.get(4).substring(criteria.get(4).indexOf("-") + 1)) + 1;
        	
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
        	
        	criteria.set(1, "Atmosphere-" + Float.toString((TotalAt + atmosphere) / cTotalRatings) + "-" + AtCount);
        	criteria.set(2, "Originality-" + Float.toString((TotalOr + originality) / cTotalRatings) + "-" + OrCount);
        	criteria.set(3, "Skill-" + Float.toString((TotalSk + skill) / cTotalRatings) + "-" + SkCount);
        	criteria.set(4, "TotalRatings-" + Integer.toString(cTotalRatings));
        	
        	if (raters.get(0).equalsIgnoreCase("Name-0-0-0")) {
        		raters.set(0, prater + "-" + atmosphere + "-" + originality + "-" + skill);
        	} else {
        		raters.add(prater + "-" + atmosphere + "-" + originality + "-" + skill);
        	}
        	
        	config.set(pname, criteria);
        	config.set(pname + "raters", raters);
        	saveConfig();
    		
    	} else {
    		
    		throw new ArrayStoreException();
    		
    	}
    	
    }
    
    public static ArrayList<List<String>> getRatings(String pname) throws ArrayStoreException {
    	
    	if ((config.getString(pname) != null) && (config.getString(pname + "raters") != null)) {
    		
    		List<String> criteria = config.getStringList(pname);
        	List<String> raters = config.getStringList(pname + "raters");
        	
        	String cAtmosphere = criteria.get(1).substring(criteria.get(1).indexOf("-") + 1, criteria.get(1).lastIndexOf("-"));
        	String cOriginality = criteria.get(2).substring(criteria.get(2).indexOf("-") + 1, criteria.get(2).lastIndexOf("-"));
        	String cSkill = criteria.get(3).substring(criteria.get(3).indexOf("-") + 1, criteria.get(3).lastIndexOf("-"));
        	String cTotalRatings = criteria.get(4).substring(criteria.get(4).indexOf("-") + 1);
        	
        	//raters name-0-0-0, avAt-avOr-avSk-TotalRatings
        	List<String> averages = new ArrayList<String>();
        	
        	averages.add(cAtmosphere + " ");
        	averages.add(cOriginality + " ");
        	averages.add(cSkill + " ");
        	averages.add(cTotalRatings);
        	
        	ArrayList<List<String>> retval = new ArrayList<List<String>>();
        	retval.add(raters);
        	retval.add(averages);
        	
        	return retval;
    		
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
    
    public static String getAppRank(String pname) throws ArrayStoreException {
    	
    	if ((config.getString(pname) != null) && (config.getString(pname + "raters") != null)) {
	    	List<String> criteria = config.getStringList(pname);
	    	return criteria.get(0).substring(criteria.get(0).lastIndexOf("-") + 1);
    	} else {
    		throw new ArrayStoreException();
    	}
    	
    }
   
}