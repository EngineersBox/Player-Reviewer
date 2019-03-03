package me.engineersbox.playerrev.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.exceptions.PlotInheritanceException;
import me.engineersbox.playerrev.methodlib.DataSet;
import me.engineersbox.playerrev.methodlib.Lib;

public class SQLLink {
	
	public static void newApp(Player p, String name, String rank) throws SQLException, PlotInheritanceException {
		
		try {
			
			String sql;
			sql = "SELECT Name FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			String coordsstring = null;
			PlotPlayer player = PlotPlayer.wrap(p);
			if (Main.usePlotLoc) {
				coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
			} else {
				coordsstring = Lib.getCoordsString(p.getLocation());
			}
			
			if (rs.next()) {
				throw new SQLException();
			} else {
				
				sql = "INSERT INTO playerapplications (Name, rank, atmosphere, originality, terrain, structure, layout, plotloc, totalratings, ratinglist) VALUES ('" + name + "', '" + rank + "', '0', '0', '0', '0', '0', '" + coordsstring + "', '0', '');";
				Main.MySQL.noRetUpdate(sql);
				
			}
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		} catch (PlotInheritanceException e) {
			throw new PlotInheritanceException(e.toString());
		}
		
	}
	
	public static ArrayList<List<String>> getRatingValues(String name) throws SQLException {
	
		List<String> ratinglist = new ArrayList<String>();
		ArrayList<List<String>> retval = new ArrayList<List<String>>();
		
		try {
			
			String sql;
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			ratinglist.add(retdata.getRank());
			ratinglist.addAll(retdata.getCriteriaString());
			retval.add(ratinglist);
			retval.add(retdata.getRatings());
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
		return retval;
		
	}
	
	public static Location getPlotLocation(String name) throws SQLException {
		
		try {
			
			String sql;
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			return retdata.getPlotLoc();
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static void removeApp(String name) throws SQLException {
		
		try {
			
			String sql;
			sql = "DELETE FROM playerapplications WHERE Name = '" + name + "';";
			Main.MySQL.noRetUpdate(sql);
			Bukkit.getLogger().info("test");
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static void ratePlayer(String rater, String name, Integer atmosphere, Integer originality, Integer terrain, Integer structure, Integer layout) throws SQLException {
		
		int totalratings = 0;
		String ratingliststring = "";
		List<String> ratinglist = new ArrayList<String>();
		
		try {
			
			String sql;
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			
			totalratings = retdata.getTotalRatings() + 1;
			ratinglist = retdata.getRatings();
			
			
			if (ratinglist.size() == 0) {
				ratingliststring += ":0:" + name + "-" + atmosphere + "-" + originality + "-" + terrain + "-" + structure + "-" + layout;
			} else {
	        	int count = 0;
	        	for (String cRater : ratinglist) {
	        		ratingliststring += ":" + count + ":" + cRater;
	        		count += 1;
	        	}
	        	ratingliststring += ":" + count + ":" + name + "-" + atmosphere + "-" + originality + "-" + terrain + "-" + structure + "-" + layout;
			}
			
        	float upAt = (retdata.getCriteria().get(0) + atmosphere) / totalratings;
        	float upOr = (retdata.getCriteria().get(1) + originality) / totalratings;
        	float upTr = (retdata.getCriteria().get(2) + terrain) / totalratings;
        	float upSt = (retdata.getCriteria().get(3) + structure) / totalratings;
        	float upLa = (retdata.getCriteria().get(4) + layout) / totalratings;
        	sql = "UPDATE playerapplications SET atmosphere='" + upAt + "',originality='" + upOr + "',terrain='" + upTr + "',structure='" + upSt + "',layout='" + upLa + "',totalratings='" + totalratings + "',ratinglist='" + ratingliststring + "' WHERE Name = '" + name + "';";
        	Main.MySQL.noRetUpdate(sql);

		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static String getAppRank(String name) throws SQLException {
		
		String rank;
		
		try {
			
			String sql;
			sql = "SELECT rank FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			rs.next();
			
			rank = rs.getString("rank");
			return rank;
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
}
