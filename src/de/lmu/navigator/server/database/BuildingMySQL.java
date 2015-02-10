package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.Building;

public class BuildingMySQL {

	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public BuildingMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new Building into the DB
	 * 
	 * @param buildingId
	 * @param code
	 * @param streetCode
	 * @param displayName
	 * @param coordLat
	 * @param coordLong
	 * @return the MySQL ID (int) of the new Building, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeBuilding(String code, String streetCode, String displayName) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 3_building VALUES (default, ?, ?, ?)");
			
			preparedStatement.setString(1, code);
			preparedStatement.setString(2, streetCode);
			preparedStatement.setString(3, displayName);
						
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created Room
			ResultSet rs = preparedStatement.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1); // valid INSERT
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;	// mistaken
	}

	
	
	/**
	 * Update a Building
	 * 
	 * @param updatedBuilding
	 * @return
	 */
	public boolean updateBuilding(Building updatedBuilding) {

		try {
			// update Building
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `3_building` SET `Code` = ?, `StreetCode` = ?, " +
							"`DisplayName` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedBuilding.getCode());
			preparedStatement.setString(2, updatedBuilding.getStreetCode());
			preparedStatement.setString(3, updatedBuilding.getDisplayName());
			preparedStatement.setInt(4, updatedBuilding.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;
			
			
			// update BuildingPosition
			updateBuildingPosition(updatedBuilding.getCode(),
					updatedBuilding.getLat(), updatedBuilding.getLng());


		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Only update the position of a Building
	 * 
	 * @param buildingCode
	 * @param lat
	 * @param lng
	 * @return
	 */
	public boolean updateBuildingPosition(String buildingCode, double lat, double lng) {

		try {
			// update Building
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `3_building_position` SET `CoordLat` = ?, `CoordLong` = ? WHERE `Code` = ?");

			preparedStatement.setDouble(1, lat);
			preparedStatement.setDouble(2, lng);
			preparedStatement.setString(3, buildingCode);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	
	/**
	 * Load all available Buildings (WITH BuildingPosition) from the DB
	 * 
	 * @param buildingId
	 * @return
	 */
	public ArrayList<Building> loadAllBuildings() {

		ArrayList<Building> result = new ArrayList<Building>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT b.id as id, b.Code as Code, " +
							"b.StreetCode as StreetCode, b.DisplayName as DisplayName, " +
							"p.CoordLat as CoordLat, p.CoordLong as CoordLong FROM 3_building b " +
							"LEFT JOIN 3_building_position p ON (b.Code = p.Code)" +
							"ORDER BY b.DisplayName ASC");
			
			resultSet = preparedStatement.executeQuery();

			// save all Buildings to ArrayList
			Building building;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				building = extractBuildingsFromRS(resultSet);
				result.add(building);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	
	
	/**
	 * Load a filtered list of Buildings (REST: /buildings?start={id}&end={id}&floor={id})
	 * 
	 * @param start
	 *            id of first Building to retrieve (inclusive)
	 * @param end
	 *            id of last Building to retrieve (inclusive)
	 * @return
	 */
	public ArrayList<Building> loadFilteredBuildings(String buildingCode, String streetCode) {

		ArrayList<Building> result = new ArrayList<Building>();
		Building building;

		String mysqlJoin = "SELECT b.id as id, b.Code as Code, " +
							"b.StreetCode as StreetCode, b.DisplayName as DisplayName, " +
							"p.CoordLat as CoordLat, p.CoordLong as CoordLong " +
							"FROM 3_building b " +
							"LEFT JOIN 3_building_position p ON (b.Code = p.Code)";
		
		try {
			if (!buildingCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement(mysqlJoin+" WHERE Code = ?");
				preparedStatement.setString(1, buildingCode);

			} else if (buildingCode.isEmpty() && !streetCode.isEmpty()) {
					preparedStatement = connect
							.prepareStatement(mysqlJoin+" WHERE StreetCode = ?");
					
					preparedStatement.setString(1, streetCode);
					
			} else if (!buildingCode.isEmpty() && !streetCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement(mysqlJoin+" WHERE Code = ? AND StreetCode = ?");
				
				preparedStatement.setString(1, buildingCode);
				preparedStatement.setString(2, streetCode);
				
			} else {
				preparedStatement = connect
						.prepareStatement(mysqlJoin);
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all Floors objects to ArrayList
			while (resultSet.next()) {
				building = extractBuildingsFromRS(resultSet);
				result.add(building);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single Building from the DB (REST: /floors/{id})
	 * 
	 * @param code
	 *            of the Building
	 * @return Floor object
	 */
	public Building loadSingleBuilding(String code) {

		try{
			return loadFilteredBuildings(code, code).get(0);
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
	public boolean deleteAllBuildings() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `3_building`");

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
	public int numberOfBuildings() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `3_building`");
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
	
	
	/**
	 * Load only the objects from 3_building, and don't perform the LEFT JOIN.
	 * @return
	 */
	public ArrayList<Building> loadAllBuildingsWithoutPosition() {

		ArrayList<Building> result = new ArrayList<Building>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 3_building");
			
			resultSet = preparedStatement.executeQuery();

			// save all Buildings to ArrayList
			Building building;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				building = extractBuildingsWithoutPosition(resultSet);
				result.add(building);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	/**
	 * Load only the objects from 3_building, and don't perform the LEFT JOIN.
	 * @return
	 */
	public boolean positionEntryExists(String buildingCode) {

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 3_building_position WHERE `Code` = ?");
			
			preparedStatement.setString(1, buildingCode);

			resultSet = preparedStatement.executeQuery();

			if (!resultSet.isBeforeFirst()) // check for NPE
				return false;
			
			if (resultSet.next() == false)	// check if no results were found
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;

	}
	
	public boolean addBuildingPosition(String buildingCode, double coordLat, double coordLong) {
		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("INSERT INTO 3_building_position VALUES (?, ?, ?)");
			
			preparedStatement.setString(1, buildingCode);
			preparedStatement.setDouble(2, coordLat);
			preparedStatement.setDouble(3, coordLong);
						
			preparedStatement.executeUpdate();


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;

	}


	

	/* ***** Helper functions ***** */

	/**
	 * Helper Function, to remove redundancy from always extracting the values
	 * from the SQL ResultSet
	 * 
	 * @param resultSet
	 * @return a new Floor object
	 */
	private Building extractBuildingsFromRS(ResultSet resultSet) {

		Building building = new Building();

		try {

			building.setId(resultSet.getInt("id"));
			building.setCode(resultSet.getString("Code"));
			building.setStreetCode(resultSet.getString("StreetCode"));
			building.setDisplayName(resultSet.getString("DisplayName"));
			building.setLat(resultSet.getDouble("CoordLat"));
			building.setLng(resultSet.getDouble("CoordLong"));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return building;
	}
	
	
	/**
	 * Identical to extractBuildingsFromRS(), except that no parameters 
	 * "CoordLat" and "CoordLong" are expected from the resultSet
	 * 
	 * @param resultSet
	 * @return
	 */
	private Building extractBuildingsWithoutPosition(ResultSet resultSet) {
		Building building = new Building();
		try {
			building.setId(resultSet.getInt("id"));
			building.setCode(resultSet.getString("Code"));
			building.setStreetCode(resultSet.getString("StreetCode"));
			building.setDisplayName(resultSet.getString("DisplayName"));
			building.setLat(0.0);
			building.setLng(0.0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return building;
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
