package me.engineersbox.playerrev.mysql;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public abstract class Database {

	protected Connection connection;

	//Creates a new database
	protected Database() {
		this.connection = null;
	}

	//Opens a connection with the database
	public abstract Connection openConnection() throws SQLException, ClassNotFoundException;
	
	//Checks if a connection is open with the database
	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	//Gets the connection with the database
	public Connection getConnection() {
		return connection;
	}

	//Closes the connection with the database
	public boolean closeConnection() throws SQLException {
		Bukkit.getLogger().info("[PlayerReviewer] Closing Connection With Database...");
		if (connection == null) {
			Bukkit.getLogger().info("[PlayerReviewer] No Open Connection, Skipping.");
			return false;
		}
		connection.close();
		Bukkit.getLogger().info("[PlayerReviewer] Connection Closed");
		return true;
	}

	//Executes a SQL Query
	public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}

		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		return result;
	}

	//Executes an Update SQL Query
	public int updateSQL(String query) throws SQLException, ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}

		Statement statement = connection.createStatement();
		int result = statement.executeUpdate(query);
		return result;
	}
	
	//Executes an Update SQL Query Without Returns
	public void noRetUpdate(String query) throws SQLException, ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}
		
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}
	
}
