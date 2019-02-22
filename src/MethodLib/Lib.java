package MethodLib;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Range;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Lib {
	
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
	    	int TotalTr = 0;
	    	int TotalSt = 0;
	    	int TotalLa = 0;
	    	List<Integer> retval = new ArrayList<Integer>();
	    	
	    	for (String cRater : input) {
	    		String[] cSplit = cRater.split("-");
	    		for (int i = 1; i < cSplit.length; i++) {
	        		if (i == 1) {
	        			TotalAt += Integer.parseInt(cSplit[i]);
	        		} else if (i == 2) {
	        			TotalOr += Integer.parseInt(cSplit[i]);
	        		} else if (i == 3) {
	        			TotalTr += Integer.parseInt(cSplit[i]);
	        		} else if (i == 4) {
	        			TotalSt += Integer.parseInt(cSplit[i]);
	        		} else if (i == 5) {
	        			TotalLa += Integer.parseInt(cSplit[i]);
	        		}
	        	}
	    	}
	    	
	    	retval.add(TotalAt);
	    	retval.add(TotalOr);
	    	retval.add(TotalTr);
	    	retval.add(TotalSt);
	    	retval.add(TotalLa);
	    	return retval;
		}
		
	}
	
	public static int returnInRange(String value) throws NumberFormatException {
		Range<Integer> rangeInt = Range.between(0, 100);
		try {
			if (rangeInt.contains(Integer.parseInt(value))) {
				return Integer.parseInt(value);
			} else {
				return 0;
			}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(value);
		}
		
	}
	
	public static Location getLoc(World world, Double x, Double y, Double z) {
		return new Location(world, x, y, z);
	}
	
	public static Map<String, Object> getCoords(Location loc) {
		Map<String, Object> retval = new HashMap<>();
		retval.put("world", loc.getWorld());
		retval.put("x", loc.getX());
		retval.put("y", loc.getY());
		retval.put("z", loc.getZ());
		return retval;
	}
	
	public static String getCoordsString(Location loc) {
		String retval = ":w:" + loc.getWorld().toString() + ":x:" + loc.getX() + ":y:" + loc.getY() + ":z:" + loc.getZ();
		return retval;
	}
	
	public static Location getLoc(String locstring) {
		World world = Bukkit.getWorld(StringUtils.substringBetween(locstring, ":w:", ":x:"));
		Double xpos = Double.parseDouble(StringUtils.substringBetween(locstring, ":x:", "y"));
		Double ypos = Double.parseDouble(StringUtils.substringBetween(locstring, ":y:", "z"));
		Double zpos = Double.parseDouble(StringUtils.substringAfter(locstring, ":z:"));
		Location retloc = new Location(world, xpos, ypos, zpos);
		return retloc;
	}
	
	public static String capFirstLetter(String value) {
		return value.toLowerCase().substring(0, 1).toUpperCase() + value.toLowerCase().substring(1);
	}

}
