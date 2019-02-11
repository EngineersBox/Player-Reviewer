package me.engineersbox.playerrev;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class lib {
	
	public static List<String> ratingListCreator(String input) {
		
		if (input.length() == 0) {
			List<String> retval = new ArrayList<String>();
			return retval;
		} else {
			boolean eos = false;
			int count = 0;
			List<String> retval = new ArrayList<String>();
			
			while (eos == false) {
				
				if ((input.contains(":" + count + ":")) && (input.contains(":" + (count + 1) + ":"))) {
					retval.add(StringUtils.substringBetween(input, ":" + count + ":", ":" + (count + 1) + ":"));
					count += 1;
				} else if ((input.contains(":" + count + ":")) && (!input.contains(":" + (count + 1) + ":"))) {
					retval.add(StringUtils.substringAfter(input, ":" + count + ":"));
					count += 1;
					eos = true;
				}
				
			}
			return retval;
		}
		
	}
	
	public static List<Integer> valueListCreator(List<String> input) throws SQLException {
		
		if (input.size() == 0) {
			throw new SQLException();
		} else {
			int TotalAt = 0;
	    	int TotalOr = 0;
	    	int TotalSk = 0;
	    	List<Integer> retval = new ArrayList<Integer>();
	    	
	    	for (String cRater : input) {
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
	    	
	    	retval.add(TotalAt);
	    	retval.add(TotalOr);
	    	retval.add(TotalSk);
	    	return retval;
		}
		
	}

}
