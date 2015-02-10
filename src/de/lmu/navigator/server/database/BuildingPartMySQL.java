package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.BuildingPart;

public class BuildingPartMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public BuildingPartMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	
	public int writeBuildingPart(String code, String buildingCode, String address) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 4_building_part VALUES (default, ?, ?, ?, default)");

			preparedStatement.setString(1, code);
			preparedStatement.setString(2, buildingCode);
			preparedStatement.setString(3, address);
			
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created BuildingPart
			ResultSet rs = preparedStatement.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1); // valid INSERT
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;	// mistaken
	}

	

	public boolean deleteBuildingPart(String buildingPartCode) {

		try {
			// delete BuildingPart
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `4_building_part` WHERE `Code` = ?");
			preparedStatement.setString(1, buildingPartCode);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	public boolean updateBuildingPart(BuildingPart updatedBuildingPart) {

		try {
			// update BuildingPart
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `4_building_part` SET `Code` = ?, `BuildingCode` = ?, `Address` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedBuildingPart.getCode());
			preparedStatement.setString(2, updatedBuildingPart.getBuildingCode());
			preparedStatement.setString(3, updatedBuildingPart.getAddress());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	/**
	 * Load all BuildingParts from the DB
	 */
	public ArrayList<BuildingPart> loadBuildingParts() {

		ArrayList<BuildingPart> result = new ArrayList<BuildingPart>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 4_building_part");
			resultSet = preparedStatement.executeQuery();

			// save all BuildingPart to objects in an ArrayList
			BuildingPart buildingPart;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				buildingPart = extractResultSet(resultSet);
				result.add(buildingPart);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	


	public ArrayList<BuildingPart> loadFilteredBuildingParts(String buildingPartCode, String buildingCode) {

		ArrayList<BuildingPart> result = new ArrayList<BuildingPart>();
		BuildingPart buildingPart;

		try {
			if (!buildingPartCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 4_building_part WHERE Code = ?");
				preparedStatement.setString(1, buildingPartCode);

			} else if (buildingPartCode.isEmpty() && !buildingCode.isEmpty()) {
					preparedStatement = connect
							.prepareStatement("SELECT * FROM 4_building_part WHERE BuildingCode = ?");
					
					preparedStatement.setString(1, buildingCode);
					
			} else if (!buildingPartCode.isEmpty() && !buildingCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 4_building_part WHERE Code = ? AND BuildingCode = ?");
				
				preparedStatement.setString(1, buildingPartCode);
				preparedStatement.setString(2, buildingCode);
				
			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 4_building_part");
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all BuildingPart objects to ArrayList
			while (resultSet.next()) {
				buildingPart = extractResultSet(resultSet);
				result.add(buildingPart);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	

	public BuildingPart loadSingleBuildingPart(String code) {

		try{
			return loadFilteredBuildingParts(code, "").get(0);
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
	public boolean deleteAllBuildingParts() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `4_building_part`");

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
	public int numberOfBuildingParts() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `4_building_part`");
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
	private BuildingPart extractResultSet(ResultSet resultSet) {

		BuildingPart buildingPart = new BuildingPart();

		try {

			buildingPart.setId(resultSet.getInt("id"));
			
			buildingPart.setCode(resultSet.getString("Code"));
			buildingPart.setBuildingCode(resultSet.getString("BuildingCode"));
			buildingPart.setAddress(resultSet.getString("Address"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return buildingPart;
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
