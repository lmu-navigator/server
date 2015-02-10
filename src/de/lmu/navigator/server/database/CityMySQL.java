package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.City;

public class CityMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public CityMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	
	
	public int writeCity(String code, String name) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 1_city VALUES (default, ?, ?)");

			preparedStatement.setString(1, code);
			preparedStatement.setString(2, name);
			
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created City
			ResultSet rs = preparedStatement.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1); // valid INSERT
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;	// mistaken
	}

	

	public boolean deleteCity(String cityCode) {

		try {
			// delete City
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `1_city` WHERE `Code` = ?");
			preparedStatement.setString(1, cityCode);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	public boolean updateCity(City updatedCity) {

		try {
			// update City
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `1_city` SET `Code` = ?, `Name` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedCity.getCode());
			preparedStatement.setString(2, updatedCity.getName());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	/**
	 * Load all Cities from the DB
	 * 
	 * @param floorId
	 * @return
	 */
	public ArrayList<City> loadAllCities() {

		ArrayList<City> result = new ArrayList<City>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 1_city");
			resultSet = preparedStatement.executeQuery();

			// save all City to objects in an ArrayList
			City city;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				city = extractResultSet(resultSet);
				result.add(city);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	
	/**
	 * TODO 
	 * temporary development feature, called from server.upload.ClearData.java
	 * 
	 * @return
	 */
	public boolean deleteAllCities() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `1_city`");

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns the number of elements in the database table, -1 for an error.
	 */
	public int numberOfCities() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `1_city`");
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.isBeforeFirst()) // check for NPE
				return -1;

			while (resultSet.next()) {
				return resultSet.getInt("COUNT(*)");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
		

	/* ***** Helper functions ***** */

	/**
	 * Helper Function, to remove redundancy from always extracting the values
	 * from the SQL ResultSet
	 */
	private City extractResultSet(ResultSet resultSet) {

		City city = new City();

		try {

			city.setId(resultSet.getInt("id"));
			
			city.setCode(resultSet.getString("Code"));
			city.setName(resultSet.getString("Name"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return city;
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
