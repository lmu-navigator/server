package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SettingsMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	public SettingsMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	
	/* ------------------- PDF LOCATION ------------------- */
	public String readPdfLocation() {

		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM settings WHERE parameter = 'pdfLocation'");
			resultSet = preparedStatement.executeQuery();
	
			if (!resultSet.isBeforeFirst()) // check for NPE
				return "empty result";

			while (resultSet.next()) {
				return resultSet.getString("value"); 
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "empty resultset";
		
	}	
	
	public boolean updatePdfLocation(String pdfLocation) {

		try {
			// update Street
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `settings` SET `value` = ? WHERE `settings`.`parameter` = 'pdfLocation'");

			preparedStatement.setString(1, pdfLocation);
			
			if (preparedStatement.executeUpdate() > 0) {
				System.out.println("updated pdfLocation in DB to: "+pdfLocation);
				return true;
			} else { 
				addPdfLocation(pdfLocation);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean addPdfLocation(String pdfLocation) {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("INSERT INTO `settings` VALUES (?, ?)");

			preparedStatement.setString(1, "pdfLocation");
			preparedStatement.setString(2, pdfLocation);
			
			if (preparedStatement.executeUpdate() > 0) {
				System.out.println("updated pdfLocation in DB to: "+pdfLocation);
				return true;
			} else {
				System.out.println("Error adding pdfLocation");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/* ------------------- PNG LOCATION ------------------- */ 
	public String readPngLocation() {

		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM settings WHERE parameter = 'pngLocation'");
			resultSet = preparedStatement.executeQuery();
	
			if (!resultSet.isBeforeFirst()) // check for NPE
				return "empty result";

			while (resultSet.next()) {
				return resultSet.getString("value"); 
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "empty resultset";
		
	}
	
	public boolean updatePngLocation(String pngLocation) {

		try {
			// update Street
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `settings` SET `value` = ? WHERE `settings`.`parameter` = 'pngLocation'");

			preparedStatement.setString(1, pngLocation);
			
			if (preparedStatement.executeUpdate() > 0) {
				System.out.println("updated pngLocation in DB to: "+pngLocation);
				return true;
			} else { 
				addPngLocation(pngLocation);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	
	
	public boolean addPngLocation(String pngLocation) {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("INSERT INTO `settings` VALUES (?, ?)");

			preparedStatement.setString(1, "pngLocation");
			preparedStatement.setString(2, pngLocation);
			
			if (preparedStatement.executeUpdate() > 0) {
				System.out.println("updated pngLocation in DB to: "+pngLocation);
				return true;
			} else {
				System.out.println("Error adding pngLocation");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* ***** Helper functions ***** */

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
