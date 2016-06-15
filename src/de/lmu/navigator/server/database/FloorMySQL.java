package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.Floor;

public class FloorMySQL {

	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public FloorMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new Floor into the DB
	 * @param code
	 * @param buildingPartCode
	 * @param floorLevel
	 * @param name
	 * @param mapUri
	 * @param mapSizeX
	 * @param mapSizeY
	 * @return the MySQL ID (int) of the new Object, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeFloor(String code, String buildingPartCode,
			String floorLevel, String name, String mapUri, int mapSizeX, int mapSizeY) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 5_floor VALUES (default, ?, ?, ?, ?, ?, ?, ?, default)");
			
			preparedStatement.setString(1, code);
			preparedStatement.setString(2, buildingPartCode);
			preparedStatement.setString(3, floorLevel);
			preparedStatement.setString(4, name);
			preparedStatement.setString(5, mapUri);
			preparedStatement.setInt(6, mapSizeX);
			preparedStatement.setInt(7, mapSizeY);
			
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
	 * Delete the Floor + all corresponding Rooms
	 * 
	 * @param floorCode
	 * @return
	 */
	public boolean deleteFloorAndRooms(String floorCode) {

		try {
			// delete Floor
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `5_floor` WHERE `Code` = ?");
			preparedStatement.setString(1, floorCode);

			if (preparedStatement.executeUpdate() < 1)
				return false;

			// delete corresponding Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `6_room` WHERE `FloorCode` = ?");
			preparedStatement.setString(1, floorCode);
			
			preparedStatement.execute();
			// no check for executeUpdate() > 0, since floors can also be deleted without corresponding Rooms
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	

	/**
	 * Update floor (excluding possibly nested floors)
	 * 
	 * @param updatedFloor
	 * @return
	 */
	public boolean updateFloor(Floor updatedFloor) {

		try {
			// update Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `5_floor` SET `Code` = ?, `BuildingPartCode` = ?," +
							" `FloorLevel` = ?, `Name` = ?, `MapUri` = ?, `MapSizeX` = ?, `MapSizeY` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedFloor.getCode());
			preparedStatement.setString(2, updatedFloor.getBuildingPart());
			preparedStatement.setString(3, updatedFloor.getLevel());
			preparedStatement.setString(4, updatedFloor.getName());
			preparedStatement.setString(5, updatedFloor.getMapUri());
			preparedStatement.setInt(6, updatedFloor.getMapSizeX());
			preparedStatement.setInt(7, updatedFloor.getMapSizeY());
			
			preparedStatement.setInt(8,  updatedFloor.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Toggle the visibility of the room (1 = hidden, 0 = visible)
	 * 
	 */
	public boolean updateFloorVisibility (String buildingPartCode, String floorCode, boolean hideRoom) {

		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `5_floor` SET `hidden` = ? WHERE `BuildingPartCode` = ? AND `Code` = ?");

			if (hideRoom == true)
				preparedStatement.setInt(1, 1);
			else
				preparedStatement.setInt(1, 0);
			
			preparedStatement.setString(2, buildingPartCode);
			preparedStatement.setString(3, floorCode);

			if (preparedStatement.executeUpdate() == 1) {
				return true;
			} else if (preparedStatement.executeUpdate() > 1) {
				System.out.println("Warning: multiple rows were updated (FloorMySQL.updateFloorVisibility");
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * Load all available Floors (without Rooms) from the DB, ignoring the filtering for HIDDEN != 1
	 * @return
	 */
	public ArrayList<Floor> loadAllFloors(String buildingPartCode) {
		return loadFilteredFloors("", buildingPartCode, true);
	}

	
	/**
	 * Load a filtered list of Floors (REST: /floors?start={id}&end={id}&floor={id})
	 * 
	 * @param start
	 *            id of first Floor to retrieve (inclusive)
	 * @param end
	 *            id of last Floor to retrieve (inclusive)
	 * @param buildingPartCode
	 *            the id of the buildingID database
	 * @return
	 */
	public ArrayList<Floor> loadFilteredFloors(String floorCode,
			String buildingPartCode, boolean loadHiddenFloors) {

		ArrayList<Floor> result = new ArrayList<Floor>();
		Floor floor;
		
		String addendum = "";
		if (!loadHiddenFloors)
			addendum += " AND hidden = 0";

		try {
			if (!floorCode.isEmpty() && !buildingPartCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 5_floor WHERE Code = ? AND BuildingPartCode = ?"+addendum);
		
				preparedStatement.setString(1, floorCode);
				preparedStatement.setString(2, buildingPartCode);

			} else if (floorCode.isEmpty() && !buildingPartCode.isEmpty()) {
					preparedStatement = connect
						.prepareStatement("SELECT * FROM 5_floor WHERE BuildingPartCode = ?"+addendum);
					
					preparedStatement.setString(1, buildingPartCode);
					
			} else if (!floorCode.isEmpty() && buildingPartCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 5_floor WHERE Code = ?"+addendum);
				
				preparedStatement.setString(1, floorCode);
				
			} else {
				if (loadHiddenFloors)
					preparedStatement = connect.prepareStatement("SELECT * FROM 5_floor");
				else
					preparedStatement = connect.prepareStatement("SELECT * FROM 5_floor WHERE hidden = 0");
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all Floors objects to ArrayList
			while (resultSet.next()) {
				floor = extractFingerprintFromRS(resultSet);
				result.add(floor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single Floor from the DB (REST: /floors/{id})
	 * 
	 * @param code
	 *            of Floor (from DB)
	 * @return Floor object
	 */
	public Floor loadSingleFloor(String code, boolean loadHiddenFloors) {

		try{
			return loadFilteredFloors(code, "", loadHiddenFloors).get(0);
		} catch(Exception e) {
			return null;
		}
	
	}
	
	/**
	 * Load all of the adjacent/bordering floors from the DB, which are associated to the same
	 * PDF file (MapUri) in the database. This is a special case for buildings like the Hauptgebaeude
	 * (Ludwigstra�e 27 / Schellingstra�e / Geschwister-Scholl-Platz).
	 * @param floorCode		of the floor to find the neighboors for
	 * @return				will return all floors, excluding the floor itself (floorCode)
	 */
	public ArrayList<Floor> loadBorderingFloors (String floorCode) {
		ArrayList<Floor> result = new ArrayList<Floor>();
		Floor floor;
		
		floor = loadSingleFloor(floorCode, false);
		String mapURI = floor.getMapUri();

		try {
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 5_floor WHERE Code != ? AND MapUri LIKE ?");
	
			preparedStatement.setString(1, floorCode);
			preparedStatement.setString(2, mapURI);

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all Floors objects to ArrayList
			while (resultSet.next()) {
				floor = extractFingerprintFromRS(resultSet);
				result.add(floor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;

	}

	

	/* ***** Helper functions ***** */

	/**
	 * Helper Function, to remove redundancy from always extracting the values
	 * from the SQL ResultSet
	 * 
	 * @param resultSet
	 * @return a new Floor object
	 */
	private Floor extractFingerprintFromRS(ResultSet resultSet) {

		Floor floor = new Floor();

		try {

			floor.setId(resultSet.getInt("id"));
			floor.setCode(resultSet.getString("Code"));
			floor.setBuildingPart(resultSet.getString("BuildingPartCode"));
			floor.setLevel(resultSet.getString("FloorLevel"));
			floor.setName(resultSet.getString("Name"));
			floor.setMapUri(resultSet.getString("MapUri"));
			floor.setMapSizeX(resultSet.getInt("MapSizeX"));
			floor.setMapSizeY(resultSet.getInt("MapSizeY"));
			floor.setVisibile(resultSet.getBoolean("hidden"));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return floor;
	}
	
	public ArrayList<Floor> getFloorByFileName(String filename) {
		
		ArrayList<Floor> floors = new ArrayList<Floor>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 5_floor WHERE MapUri = ?");
			preparedStatement.setString(1, filename);
			resultSet = preparedStatement.executeQuery();


			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;
			
			while (resultSet.next()) {
				floors.add(extractFingerprintFromRS(resultSet));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return floors;
	}
	
	/**
	 * Only update the map size for the specified floor
	 * 
	 * @param width		width of the map
	 * @param height	height of the map
	 * @param code		FloorCode, from the DB
	 * @return			true if updated, else false
	 */
	public boolean updateMapSize(int width, int height,String code) {

		try {
			// update Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `5_floor` SET `MapSizeX` = ?, `MapSizeY` = ?" +
							" WHERE `code` = ?");

			preparedStatement.setInt(1, width);
			preparedStatement.setInt(2, height);
			preparedStatement.setString(3, code);
			
			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	

	/**
	 * TODO 
	 * temporary development feature, called from server.data.ClearData.java
	 * 
	 * @return
	 */
	public boolean clearAllMapSizes() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `5_floor` SET `MapSizeX` = 0, `MapSizeY` = 0");

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * TODO 
	 * temporary development feature, called from server.upload.ClearData.java
	 * 
	 * @return
	 */
	public boolean deleteAllFloors() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `5_floor`");

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
	public int numberOfFloors() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `5_floor`");
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
