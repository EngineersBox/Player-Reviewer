package me.engineersbox.playerrev.methodlib;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Range;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.chunky.CameraObject;
import me.engineersbox.playerrev.chunky.JSONParameter;
import me.engineersbox.playerrev.exceptions.ChunkyParameterException;
import me.engineersbox.playerrev.exceptions.PlotInheritanceException;

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
	
	public static Integer returnInRange(String value) throws NumberFormatException {
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
	
	public static Location getLocPitchYaw(World world, Double x, Double y, Double z, Float pitch, Float yaw) {
		Location newLoc = new Location(world, x, y, z);
		newLoc.setPitch(pitch);
		newLoc.setYaw(yaw);
		return newLoc;
	}
	
	public static Map<String, Object> getCoordsPitchYaw(Location loc) {
		Map<String, Object> retval = new HashMap<>();
		retval.put("world", loc.getWorld());
		retval.put("x", loc.getX());
		retval.put("y", loc.getY());
		retval.put("z", loc.getZ());
		retval.put("yaw", loc.getYaw());
		retval.put("pitch", loc.getPitch());
		return retval;
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
		String retval = ":w:" + loc.getWorld().getName().toString() + ":x:" + loc.getX() + ":y:" + loc.getY() + ":z:" + loc.getZ();
		return retval;
	}
	
	public static String getCoordsStringPitchYaw(Location loc) {
		String retval = ":w:" + loc.getWorld().getName().toString() + ":x:" + loc.getX() + ":y:" + loc.getY() + ":z:" + loc.getZ() + ":p:" + loc.getPitch() + ":ya:" + loc.getYaw();
		return retval;
	}
	
	public static String getCoordsString(com.github.intellectualsites.plotsquared.plot.object.Location location) {
		String retval = ":w:" + location.getWorld().toString() + ":x:" + location.getX() + ":y:" + location.getY() + ":z:" + location.getZ();
		return retval;
	}
	
	public static Location getLoc(String locstring) {
		World world = Bukkit.getWorld(StringUtils.substringBetween(locstring, ":w:", ":x:"));
		Double xpos = Double.parseDouble(StringUtils.substringBetween(locstring, ":x:", ":y:"));
		Double ypos = Double.parseDouble(StringUtils.substringBetween(locstring, ":y:", ":z:"));
		Double zpos = Double.parseDouble(StringUtils.substringAfter(locstring, ":z:"));
		return new Location(world, xpos, ypos, zpos);
	}
	
	public static Location getLocPitchYaw(String locstring) {
		World world = Bukkit.getWorld(StringUtils.substringBetween(locstring, ":w:", ":x:"));
		Double xpos = Double.parseDouble(StringUtils.substringBetween(locstring, ":x:", ":y:"));
		Double ypos = Double.parseDouble(StringUtils.substringBetween(locstring, ":y:", ":z:"));
		Double zpos = Double.parseDouble(StringUtils.substringBetween(locstring, ":z:", ":p:"));
		Float pitch = Float.parseFloat(StringUtils.substringBetween(locstring, ":p:", ":ya:"));
		Float yaw = Float.parseFloat(StringUtils.substringAfter(locstring, ":ya:"));
		Location newLoc = new Location(world, xpos, ypos, zpos);
		newLoc.setPitch(pitch);
		newLoc.setYaw(yaw);
		return newLoc;
	}
	
	public static String capFirstLetter(String value) {
		return value.toLowerCase().substring(0, 1).toUpperCase() + value.toLowerCase().substring(1);
	}
	
	public static String format(String msg) {
    	
		String coloredMsg = "";
		if (msg != null) {
			for(int i = 0; i < msg.length(); i++)
			{
			    if(msg.charAt(i) == '&')
			        coloredMsg += '§';
			    else
			        coloredMsg += msg.charAt(i);
			}
		} else {
			coloredMsg = "§f";
		}

		return coloredMsg;
    	
    }
	
	public static com.github.intellectualsites.plotsquared.plot.object.Location playerOwnsPlot(PlotPlayer p, Plot plot) throws PlotInheritanceException {
		Set<Plot> playerPlots = p.getPlots();
		if (playerPlots.contains(plot)) {
			return plot.getCenter();
		} else {
			throw new PlotInheritanceException(p.getName().toString());
		}
	}
	
	public static String playerJsonParams(Player p) throws ChunkyParameterException {
		MaxSizeHashMap<String, CameraObject> cams = Main.cameras.get(p.getUniqueId());
		JSONParameter jParam = Main.paramMap.get(p.getUniqueId());
		String chunkLoc = Main.chunkList.get(p.getUniqueId());
		String retVal = "{";
		boolean addComma = false;
		
		if (jParam != null) {
			retVal += jParam.toString();
			addComma = true;
		} else {
			addComma = false;
		}
		
		if (cams != null) {
			for (int i = 1; i <= cams.size(); i++) {
				CameraObject camera = cams.get("camera_" + i);
				if (i == 1) {
					if (addComma) {
						retVal += ", " + camera.toString();
					} else {
						retVal += camera.toString();
					}
				} else {
					retVal += ", " + camera.toString();
				}
			}
		} else {
			throw new ChunkyParameterException("camera");
		} 
		
		if(chunkLoc != null) {
			retVal += ", \"chunkList\": " + chunkLoc + "}";
			return retVal;
		} else {
			throw new ChunkyParameterException("position");
		}
	}

}
