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
			sql = "SELECT Name FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			String coordsstring = null;
			PlotPlayer player = PlotPlayer.wrap(p);
			
			if (rs.next()) {
				throw new SQLException();
			} else {
				
				if (Main.usePlotLoc) {
					coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
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
				
				sql = "INSERT INTO playerapplications (Name, rank, " + criteriaString + "plotloc, totalratings, ratinglist) VALUES ('" + name + "', '" + rank + "', " + criteriaValueString + "'" + coordsstring + "', '0', '');";
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
			
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = Main.MySQL.querySQL(sql);
			DataSet retdata = new DataSet(rs);
			
			totalratings = retdata.getTotalRatings() + 1;
			ratinglist = retdata.getRatings();
			int cIndex = 0;
			List<Float> updatedCriteria = new ArrayList<Float>();
			
			for (Integer cCriteria : criteria) {
				updatedCriteria.add((retdata.getCriteria().get(cIndex) + cCriteria) / totalratings);
				cIndex += 1;
				if (ratinglist.size() == 0) {
					if (cIndex == 0) {
        					ratingliststring += ":0:" + cCriteria;
		    			} else {
		    				ratingliststring += "-" + cCriteria;
		    			}
				}
			}
			
			int count = 0;
        	for (String cRater : ratinglist) {
        		ratingliststring += ":" + count + ":" + cRater;
        		count += 1;
        		ratingliststring += ":" + count + ":";
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
        	sql = "UPDATE playerapplications SET " + criteriaString + "totalratings='" + totalratings + "',ratinglist='" + ratingliststring + "' WHERE Name = '" + name + "';";
        	Main.MySQL.noRetUpdate(sql);

		} catch (SQLException | ClassNotFoundException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static String getAppRank(String name) throws SQLException {
		
		try {
			
			String rank;
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