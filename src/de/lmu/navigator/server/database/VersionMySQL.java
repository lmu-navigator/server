package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.Version;

public class VersionMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public VersionMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");
		db = new Database();
		connect = db.Get_Connection();
	}
	
	
	/**
	 * Returns the current version of the data pool
	 */
	public Version loadVersion() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM version ORDER BY version DESC LIMIT 1");
			resultSet = preparedStatement.executeQuery();
	
			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				return extractResultSet(resultSet); 
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * Returns all versions stored in the database
	 */
	public ArrayList<Version> loadVersionHistory() {
		ArrayList<Version> result = new ArrayList<Version>();
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM version");
			resultSet = preparedStatement.executeQuery();
	
			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				 result.add(extractResultSet(resultSet)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;		
	}
	
	/**
	 * Insert a new Version to the DB
	 *  
	 * @param log	a text field, for debugging purposes, to find out what was changed in the version upgrade
	 * @return
	 */
	public boolean addVersion(String log) {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("INSERT INTO version VALUES (default, ?, ?)");

			long unixTime = System.currentTimeMillis() / 1000L;
			
			preparedStatement.setInt(1, (int)unixTime);
			preparedStatement.setString(2, log);
			
			if (preparedStatement.executeUpdate() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	

	/* ***** Helper functions ***** */

	/**
	 * Helper Function, to remove redundancy from always extracting the values
	 * from the SQL ResultSet
	 */
	private Version extractResultSet(ResultSet resultSet) {

		Version version = new Version();
		try {
			version.setVersion(resultSet.getInt("version"));
			version.setTimestamp(resultSet.getInt("timestamp"));
			version.setLog(resultSet.getString("log"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return version;
	}



	/**
	 * Closes every connection (Currently called from REST-classes)
	 */
	public void close() {
		try {
			if (resultSet != null)
				resultSet.close();
			if (connect != null)
				connect.close();
		} catch (Exception e) {
		}
	}

}
