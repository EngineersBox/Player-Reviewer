package me.engineersbox.playerrev;

import java.util.ArrayList;
import java.util.List;

import me.engineersbox.playerrev.Main;

public class InvConfig extends AbstractFile{

    public InvConfig(Main main) {
       
        super(main, "invconfig.yml");
       
    }
    //Criteria: atmosphere, originality, skill
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
    
    public static void removeApp(String pname) {
    	
    	config.set(pname, null);
    	config.set(pname + "raters", null);
    	saveConfig();
    	
    }
    
    public static void ratePlayer(String prater, String pname, Integer atmosphere, Integer originality, Integer skill) {
    	
    	List<String> criteria = config.getStringList(pname);
    	List<String> raters = config.getStringList(pname + "raters");
    	
    	int cAtmosphere = Integer.parseInt(criteria.get(1).substring(criteria.get(1).indexOf("-") + 1, criteria.get(1).lastIndexOf("-")));
    	int AtCount = Integer.parseInt(criteria.get(1).substring(criteria.get(1).lastIndexOf("-") + 1));
    	int cOriginality = Integer.parseInt(criteria.get(2).substring(criteria.get(2).indexOf("-") + 1, criteria.get(2).lastIndexOf("-")));
    	int OrCount = Integer.parseInt(criteria.get(2).substring(criteria.get(2).lastIndexOf("-") + 1));
    	int cSkill = Integer.parseInt(criteria.get(3).substring(criteria.get(3).indexOf("-") + 1, criteria.get(3).lastIndexOf("-")));
    	int SkCount = Integer.parseInt(criteria.get(3).substring(criteria.get(3).lastIndexOf("-") + 1));
    	int cTotalRatings = Integer.parseInt(criteria.get(4).substring(criteria.get(4).indexOf("-"), +1));
    	
    	int newAt;
    	int newOr;
    	int newSk;
    	
    	if (cAtmosphere == 0) {
    		newAt = atmosphere;
    	} else {
    		newAt = (cAtmosphere + atmosphere) / (AtCount += 1);
    	}
    	
    	if (cOriginality == 0) {
    		newOr = originality;
    	} else {
    		newOr = (cOriginality + originality) / (OrCount += 1);
    	}
    	
    	if (cSkill == 0) {
    		newSk = skill;
    	} else {
    		newSk = (cSkill + skill) / (SkCount += 1);
    	}
    	
    	criteria.set(1, "Atmosphere-" + newAt + "-" + AtCount);
    	criteria.set(2, "Originality-" + newOr + "-" + OrCount);
    	criteria.set(3, "Skill-" + newSk + "-" + SkCount);
    	criteria.set(4, Integer.toString(cTotalRatings += 1));
    	
    	if (raters.get(0).equalsIgnoreCase("Name-0-0-0")) {
    		raters.set(0, prater + "-" + atmosphere + "-" + originality + "-" + skill);
    	} else {
    		raters.add(prater + "-" + atmosphere + "-" + originality + "-" + skill);
    	}
    	
    	config.set(pname, criteria);
    	config.set(pname + "raters", raters);
    	saveConfig();
    	
    }
    
    public static ArrayList<List<String>> getRatings(String pname) {
    	
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
    	
    }
    
    public static String getAppRank(String pname) {
    	
    	List<String> criteria = config.getStringList(pname);
    	return criteria.get(0).substring(criteria.get(0).lastIndexOf("-") + 1);
    	
    }
   
}