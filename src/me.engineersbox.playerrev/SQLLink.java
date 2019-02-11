package me.engineersbox.playerrev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLLink {
	
	/* Table Setup
	 * Table Name = applicants
	 * Table format:
	 * NAME | RANK | ATMOSPHERE | ORIGINALITY | SKILL | TOTALRATINGS | RATING LIST
	 * 
	 * NAME = name of the applicant
	 * RANK = rank applied for
	 * ATMOSPHERE = averaged atmosphere rating
	 * ORIGINALITY = averaged originality rating
	 * SKILL = averaged skill rating
	 * TOTALRATINGS = total number of ratings recieved
	 * RATINGLIST = list of previous ratings in format: [:0:Name-0-0-0:1:Name-0-0-0:2:Name-0-0-0]
	 */
	
	public static void newApp(String name, String rank) throws SQLException {
		
		try {
			
			Statement statement = Main.c.createStatement();
			String sql;
			sql = "SELECT Name FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = statement.executeQuery(sql);
			
			if (rs.next()) {
				throw new SQLException();
			} else {
				
				String sql1;
				sql1 = "INSERT INTO playerapplications (Name, rank, atmosphere, originality, skill, totalratings, ratinglist) VALUES ('" + name + "', '" + rank + "', '0', '0', '0', '0', '');";
				statement.executeUpdate(sql1);
				
			}
			
		} catch (SQLException se) {
			throw new SQLException(se);
		}
		
	}
	
	//returns rank, atmosphere average, originality average, skill average, totalratings and ratinglist
	public static ArrayList<List<String>> getRatingValues(String name) throws SQLException {
	
		String rank = "";
		float atmosphere = 0;
		float originality = 0;
		float skill = 0;
		int totalratings = 0;
		String ratingstring = "";
		
		List<String> ratinglist = new ArrayList<String>();
		List<String> averages = new ArrayList<String>();
		ArrayList<List<String>> retval = new ArrayList<List<String>>();

		try {
			
			Statement statement = Main.c.createStatement();
			String sql;
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			
			rank = rs.getString("rank");
			atmosphere  = rs.getFloat("atmosphere");
			originality = rs.getFloat("originality");
			skill = rs.getFloat("skill");
			totalratings = rs.getInt("totalratings");
			ratingstring = rs.getString("ratinglist");
			
			ratinglist = lib.ratingListCreator(ratingstring);
			
			averages.add(rank);
			averages.add(Float.toString(atmosphere));
			averages.add(Float.toString(originality));
			averages.add(Float.toString(skill));
			averages.add(Integer.toString(totalratings));
			
			retval.add(averages);
			retval.add(ratinglist);
			
		} catch (SQLException se) {
			throw new SQLException(se);
		}
		
		return retval;
		
	}
	
	public static void removeApp(String name) throws SQLException {
		
		try {
			
			Statement statement = Main.c.createStatement();
			String sql;
			sql = "DELETE FROM playerapplications WHERE Name = '" + name + "';";
			statement.executeUpdate(sql);
			
		} catch (SQLException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static void ratePlayer(String rater, String name, Integer atmosphere, Integer originality, Integer skill) throws SQLException {
		
		int TotalAt = 0;
		int TotalOr = 0;
		int TotalSk = 0;
		int totalratings = 0;
		String ratingliststring = "";
		String ratingstring = "";
		
		List<String> ratinglist = new ArrayList<String>();
		
		try {
			
			Statement statement = Main.c.createStatement();
			String sql;
			sql = "SELECT * FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			
			totalratings = rs.getInt("totalratings") + 1;
			ratingstring = rs.getString("ratinglist");
			ratinglist = lib.ratingListCreator(ratingstring);
			
			if (ratinglist.size() == 0) {
				ratingliststring += ":0:" + name + "-" + atmosphere + "-" + originality + "-" + skill;
			} else {
				
				List<Integer> valueList = lib.valueListCreator(ratinglist);
				TotalAt = valueList.get(0);
				TotalOr = valueList.get(1);
				TotalSk = valueList.get(2);
	        	
	        	int count2 = 0;
	        	for (String cRater : ratinglist) {
	        		ratingliststring += ":" + count2 + ":" + cRater;
	        		count2 += 1;
	        	}
	        	ratingliststring += ":" + count2 + ":" + name + "-" + atmosphere + "-" + originality + "-" + skill;
			}
			
        	float upAt = (TotalAt + atmosphere) / totalratings;
        	float upOr = (TotalOr + originality) / totalratings;
        	float upSk = (TotalSk + skill) / totalratings;
        	statement.executeUpdate("UPDATE playerapplications SET atmosphere='" + upAt + "',originality='" + upOr +"',skill='" + upSk + "',totalratings='" + totalratings + "',ratinglist='" + ratingliststring + "' WHERE Name = '" + name + "';");

		} catch (SQLException se) {
			throw new SQLException(se);
		}
		
	}
	
	public static String getAppRank(String name) throws SQLException {
		
		String rank;
		
		try {
			
			Statement statement = Main.c.createStatement();
			String sql;
			sql = "SELECT rank FROM playerapplications WHERE Name = '" + name + "';";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			
			rank = rs.getString("rank");
			return rank;
			
		} catch (SQLException se) {
			throw new SQLException(se);
		}
		
	}
	
}
