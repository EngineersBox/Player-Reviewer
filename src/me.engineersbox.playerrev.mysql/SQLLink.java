package me.engineersbox.playerrev.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.exceptions.PlotInheritanceException;
import me.engineersbox.playerrev.methodlib.DataSet;
import me.engineersbox.playerrev.methodlib.Lib;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

public class SQLLink {
	
	public static void newApp(Player p, String name, String rank) throws SQLException, PlotInheritanceException {
		
		try {
			
			String sql;
			String tableName = SQLConfig.getTableName();
			sql = "SELECT name FROM " + tableName + " WHERE name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			String coordsstring = null;
			PlotPlayer player = PlotPlayer.wrap(p);
			
			if (rs.next()) {
				throw new SQLException();
			} else {
				
				if (Main.usePlotLoc) {
					try {
						coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
					} catch (Exception e) {
						coordsstring = Lib.getCoordsString(p.getLocation());
					}
				} else {
					coordsstring = Lib.getCoordsString(p.getLocation());
				}
				
				List<String> criteriaList = SQLConfig.getCriteria();
				String criteriaString = "";
				String criteriaValueString = "";
				for (String criteria : criteriaList) {
					criteriaString += criteria + ", ";
					criteriaValueString += "'0', ";
				}
				if (Main.useRanksInApplication) {
					sql = "INSERT INTO " + tableName +" (name, rank, " + criteriaString + "plotloc, totalratings, ratinglist) VALUES ('" + name + "', '" + rank + "', " + criteriaValueString + "'" + coordsstring + "', '0', '');";
				} else {
					sql = "INSERT INTO " + tableName +" (name, rank, " + criteriaString + "plotloc, totalratings, ratinglist) VALUES ('" + name + "', '" + null + "', " + criteriaValueString + "'" + coordsstring + "', '0', '');";
				}
				sql = "INSERT INTO " + tableName +" (name, rank, " + criteriaString + "plotloc, totalratings, ratinglist) VALUES ('" + name + "', '" + rank + "', " + criteriaValueString + "'" + coordsstring + "', '0', '');";
				Main.MySQL.noRetUpdate(sql);
				
			}
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static ArrayList<List<String>> getRatingValues(String name) throws SQLException {
	
		List<String> ratinglist = new ArrayList<String>();
		ArrayList<List<String>> retval = new ArrayList<List<String>>();
		
		try {
			
			String sql;
			String tableName = SQLConfig.getTableName();
			sql = "SELECT * FROM " + tableName + " WHERE name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			
			if (Main.useRanksInApplication) {
				ratinglist.add(retdata.getRank());
			} else {
				ratinglist.add("Ranks Disabled");
			}
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
			String tableName = SQLConfig.getTableName();
			sql = "SELECT * FROM " + tableName + " WHERE name = '" + name + "';";
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
			String tableName = SQLConfig.getTableName();
			sql = "DELETE FROM " + tableName + " WHERE name = '" + name + "';";
			Main.MySQL.noRetUpdate(sql);
			
		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static void ratePlayer(String rater, String name, List<Integer> criteria) throws SQLException {
		
		try {
			
			int totalratings = 0;
			String ratingliststring = "";
			List<String> ratinglist = new ArrayList<String>();
			String sql;
			String tableName = SQLConfig.getTableName();
			
			sql = "SELECT * FROM " + tableName + " WHERE name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			
			totalratings = retdata.getTotalRatings() + 1;
			ratinglist = retdata.getRatings();
			int cIndex = 0;
			List<Float> updatedCriteria = new ArrayList<Float>();
			
			for (Integer cCriteria : criteria) {
				updatedCriteria.add((retdata.getCriteria().get(cIndex) + cCriteria) / totalratings);
				if (ratinglist.size() == 0) {
					if (cIndex == 0) {
        					ratingliststring += ":0:" + rater + "-" + cCriteria;
		    			} else {
		    				ratingliststring += "-" + cCriteria;
		    			}
				}
				cIndex += 1;
			}
			
			int count = 0;
        	for (String cRater : ratinglist) {
        		ratingliststring += ":" + count + ":" + cRater;
        		count += 1;
        		ratingliststring += ":" + count + ":" + rater;
        		for (Integer cCriteria : criteria) {
        			if (count == 0) {
        				ratingliststring += cCriteria;
        			} else {
        				ratingliststring += "-" + cCriteria;
        			}
        		}
        	}
			
			List<String> criteriaList = SQLConfig.getCriteria();
			String criteriaString = "";
			cIndex = 0;
			
			for (String cCriteria : criteriaList) {
				criteriaString += cCriteria + "='" + updatedCriteria.get(cIndex) + "',";
				cIndex += 1;
			}
        	sql = "UPDATE " + tableName + " SET " + criteriaString + "totalratings='" + totalratings + "',ratinglist='" + ratingliststring + "' WHERE name = '" + name + "';";
        	Main.MySQL.noRetUpdate(sql);

		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static String getAppRank(String name) throws SQLException {
		
		if (Main.useRanksInApplication) {
			try {
				
				String rank;
				String sql;
				String tableName = SQLConfig.getTableName();
				sql = "SELECT rank FROM " + tableName + " WHERE name = '" + name + "';";
				ResultSet rs = Main.MySQL.querySQL(sql);
				rs.next();
				
				rank = rs.getString("rank");
				return rank;
				
			} catch (SQLException | ClassNotFoundException se) {
				throw new SQLException(se);
			}
		} else {
			return "Ranks Disabled";
		}
		
	}
	
}