package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.Street;

public class StreetMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public StreetMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	
	
	public int writeStreet(String code, String cityCode, String name) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 2_street VALUES (default, ?, ?, ?)");

			preparedStatement.setString(1, code);
			preparedStatement.setString(2, cityCode);
			preparedStatement.setString(3, name);
			
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created Street
			ResultSet rs = preparedStatement.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1); // valid INSERT
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;	// mistaken
	}

	

	public boolean deleteStreet(String streetCode) {

		try {
			// delete Street
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `2_street` WHERE `Code` = ?");
			preparedStatement.setString(1, streetCode);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	public boolean updateStreet(Street updatedStreet) {

		try {
			// update Street
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `2_street` SET `Code` = ?, `CityCode` = ?, `Name` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedStreet.getCode());
			preparedStatement.setString(2, updatedStreet.getCityCode());
			preparedStatement.setString(3, updatedStreet.getName());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	/**
	 * Load all Streets from the DB
	 */
	public ArrayList<Street> loadStreets() {

		ArrayList<Street> result = new ArrayList<Street>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 2_street");
			resultSet = preparedStatement.executeQuery();

			// save all Street to objects in an ArrayList
			Street street;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				street = extractResultSet(resultSet);
				result.add(street);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	


	public ArrayList<Street> loadFilteredStreets(String streetCode, String cityCode) {

		ArrayList<Street> result = new ArrayList<Street>();
		Street street;

		try {
			if (!streetCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 2_street WHERE Code = ?");
				preparedStatement.setString(1, streetCode);

			} else if (streetCode.isEmpty() && !cityCode.isEmpty()) {
					preparedStatement = connect
							.prepareStatement("SELECT * FROM 2_street WHERE CityCode = ?");
					
					preparedStatement.setString(1, cityCode);
					
			} else if (!streetCode.isEmpty() && !cityCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 2_street WHERE Code = ? AND CityCode = ?");
				
				preparedStatement.setString(1, streetCode);
				preparedStatement.setString(2, cityCode);
				
			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 2_street");
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all Street objects to ArrayList
			while (resultSet.next()) {
				street = extractResultSet(resultSet);
				result.add(street);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	

	public Street loadSingleStreet(String streetCode) {

		try{
			return loadFilteredStreets(streetCode, "").get(0);
		} catch(Exception e) {
			return null;
		}
	
	}

	
	/**
	 * TODO 
	 * temporary development feature, called from server.upload.ClearData.java
	 * 
	 * @return
	 */
	public boolean deleteAllStreets() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `2_street`");

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
	public int numberOfStreets() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `2_street`");
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
	private Street extractResultSet(ResultSet resultSet) {

		Street street = new Street();

		try {

			street.setId(resultSet.getInt("id"));
			
			street.setCode(resultSet.getString("Code"));
			street.setCityCode(resultSet.getString("CityCode"));
			street.setName(resultSet.getString("Name"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return street;
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
