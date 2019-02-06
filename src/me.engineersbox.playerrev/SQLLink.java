package me.engineersbox.playerrev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

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
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = SQLConfig.getDBURL();
	
	static final String USER = SQLConfig.getUSER();
	static final String PASS = SQLConfig.getPASS();
	
	public static void selectFromWhere(String[] queryvalues, String dbtitle, String name) {
		
		Connection conn = null;
		Statement stmt = null;
		
		String rank = "";
		int atmosphere = 0;
		int originality = 0;
		int skill = 0;
		int totalratings = 0;
		String ratingstring = "";
		List<String> ratinglist = new ArrayList<String>();
		
		try {
			
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			
			String sql;
			sql = "SELECT ";
			for (String cval : queryvalues) {
				sql += cval + " ";
			}
			sql += "FROM " + dbtitle + " WHERE Name = " + name; //<-FIX THIS TO GET BASED ON NAME ARG
			
			ResultSet rs = stmt.executeQuery(sql);
			rank = rs.getString("rank");
			atmosphere  = rs.getInt("atmosphere");
			originality = rs.getInt("originality");
			skill = rs.getInt("skill");
			totalratings = rs.getInt("totalratings");
			ratingstring = rs.getString("ratinglist");
			
			boolean eos = false;
			int count = 0;
			while (eos == false) {
				
				if ((ratingstring.contains(":" + count + ":")) && (ratingstring.contains(":" + (count + 1) + ":"))) {
					ratinglist.add(StringUtils.substringBetween(ratingstring, ":" + count + ":", ":" + (count + 1) + ":"));
					count += 1;
				} else if ((ratingstring.contains(":" + count + ":")) && (!ratingstring.contains(":" + (count + 1) + ":"))) {
					ratinglist.add(StringUtils.substringAfter(ratingstring, ":" + count + ":"));
					count += 1;
					eos = true;
				}
				
			}
			
			rs.close();
			stmt.close();
			conn.close();
			
		} catch (SQLException se) {
			Bukkit.getLogger().warning(se.getStackTrace().toString());
		} catch (Exception e) {
			Bukkit.getLogger().warning(e.getStackTrace().toString());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {
				
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				Bukkit.getLogger().warning(se.getStackTrace().toString());
			}
		}
		
	}
	
}
