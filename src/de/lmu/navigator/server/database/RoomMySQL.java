package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.lmu.navigator.server.model.Room;

public class RoomMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public RoomMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new Room into the DB (REST: POST/PUT /rooms)
	 * 
	 * @param code
	 * @param roomName
	 * @param floorCode
	 * @param posX
	 * @param posY
	 * @return the MySQL ID (int) of the new Room, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeRoom(String code, String roomName, String floorCode, double posX, double posY) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO 6_room VALUES (default, ?, ?, ?, ?, ?, default)");

			
			preparedStatement.setString(1, code);
			preparedStatement.setString(2, roomName);
			preparedStatement.setString(3, floorCode);
			preparedStatement.setDouble(4, posX);
			preparedStatement.setDouble(5, posY);
			
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created Rooms
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
	 * Delete room
	 * 
	 * @param roomId
	 * @return
	 */
	public boolean deleteRoom(String roomCode) {

		try {
			// delete Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `6_room` WHERE `code` LIKE ?");
			preparedStatement.setString(1, roomCode);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update a room (object)
	 * 
	 * @param updatedRoom	object
	 * @return
	 */
	public boolean updateRoom(Room updatedRoom) {

		try {
			// update Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `6_room` SET `Code` = ?, `Name` = ?, `FloorCode` = ?, `PosX` = ?, `PosY` = ? WHERE `id` = ?");

			preparedStatement.setString(1, updatedRoom.getCode());
			preparedStatement.setString(2, updatedRoom.getName());
			preparedStatement.setString(3, updatedRoom.getFloorCode());
			preparedStatement.setDouble(4, updatedRoom.getPosX());
			preparedStatement.setDouble(5, updatedRoom.getPosY());
			preparedStatement.setInt(6, updatedRoom.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update only the position of the room
	 * 
	 */
	public boolean updateRoomPosition(String floorCode, String roomName, int posX, int posY) {

		try {
			// update Rooms
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `6_room` SET `PosX` = ?, `PosY` = ? WHERE `FloorCode` = ? AND `Name` = ?");

			preparedStatement.setInt(1, posX);
			preparedStatement.setInt(2, posY);
			preparedStatement.setString(3, floorCode);
			preparedStatement.setString(4, roomName);

			if (preparedStatement.executeUpdate() == 1) {
				return true;
			} else if (preparedStatement.executeUpdate() > 1) {
				System.out.println("Warning: multiple rows were updated (RoomMySQL.updateRoomPosition");
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Toggle the visibility of the room (1 = hidden, 0 = visible)
	 * 
	 */
	public boolean updateRoomVisibility (String floorCode, String roomCode, boolean hideRoom) {

		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `6_room` SET `hidden` = ? WHERE `FloorCode` = ? AND `Code` = ?");

			if (hideRoom == true)
				preparedStatement.setInt(1, 1);
			else
				preparedStatement.setInt(1, 0);
			
			preparedStatement.setString(2, floorCode);
			preparedStatement.setString(3, roomCode);

			if (preparedStatement.executeUpdate() == 1) {
				return true;
			} else if (preparedStatement.executeUpdate() > 1) {
				System.out.println("Warning: multiple rows were updated (RoomMySQL.updateRoomVisibility");
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	
	
	
	/**
	 * Load all Rooms from the DB, ignoring filters for Pos!=0 and !hidden
	 * 
	 * @param floorCode
	 * @return
	 */
	public ArrayList<Room> loadAllRooms(String floorCode) {

		ArrayList<Room> result = new ArrayList<Room>();

		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT * FROM 6_room WHERE FloorCode LIKE ?");
			preparedStatement.setString(1, floorCode);
			resultSet = preparedStatement.executeQuery();

			// save all Rooms to objects in an ArrayList
			Room room;

			if (!resultSet.isBeforeFirst()) // check for NPE
				return null;

			while (resultSet.next()) {
				room = extractRoomFromRS(resultSet);
				result.add(room);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	

	/**
	 * @param start
	 *            id of first Room to retrieve (inclusive)
	 * @param end
	 *            id of last Room to retrieve (inclusive)
	 * @param building
	 *            the id of the floorId
	 * @return
	 */
	public ArrayList<Room> loadFilteredRooms(String roomCode, String floorCode) {

		ArrayList<Room> result = new ArrayList<Room>();
		Room room;

		try {
			if (roomCode.isEmpty() && floorCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 6_room" +
								" WHERE PosX != 0 AND PosY != 0 AND hidden = 0");

			} else if (roomCode.isEmpty() && !floorCode.isEmpty()) {
					preparedStatement = connect
							.prepareStatement("SELECT * FROM 6_room WHERE FloorCode = ?" +
									" AND PosX != 0 AND PosY != 0 AND hidden = 0");
					
					preparedStatement.setString(1, floorCode);
					
			} else if (!roomCode.isEmpty() && !floorCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM 6_room WHERE Code = ? AND FloorCode = ?" +
								" AND PosX != 0 AND PosY != 0 AND hidden = 0");
				
				preparedStatement.setString(1, roomCode);
				preparedStatement.setString(2, floorCode);
				
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all Room objects to ArrayList
			while (resultSet.next()) {
				room = extractRoomFromRS(resultSet);
				result.add(room);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single Room from the DB (REST: /rooms/{code})
	 * 
	 * @param id
	 *            of Room
	 * @return Room object
	 */
	public Room loadSingleRoom(String roomCode) {

		try{
			return loadFilteredRooms(roomCode, "").get(0);
		} catch(Exception e) {
			return null;
		}
	
	}
	
	/**
	 * Return the number of rooms saved in the DB
	 */
	public Map<String, Integer> numRooms() {

		Map<String, Integer> result = new HashMap<String, Integer>();
		
		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT FloorCode, Count(*) FROM 6_room GROUP BY FloorCode");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				result.put(resultSet.getString("FloorCode"), resultSet.getInt("Count(*)")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * Return the number of not yet positioned rooms saved in the DB
	 */
	public Map<String, Integer> numNotPositionedRooms() {

		Map<String, Integer> result = new HashMap<String, Integer>();
		
		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT FloorCode, Count(*) FROM 6_room WHERE PosX = 0 AND PosY = 0 AND hidden = 0 GROUP BY FloorCode");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				result.put(resultSet.getString("FloorCode"), resultSet.getInt("Count(*)")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	/**
	 * Return the number of disabled / hidden rooms
	 */
	public Map<String, Integer> numHiddenRooms() {

		Map<String, Integer> result = new HashMap<String, Integer>();
		
		try {
			// execute sql query
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT FloorCode, Count(*) FROM 6_room WHERE hidden = 1 GROUP BY FloorCode");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				result.put(resultSet.getString("FloorCode"), resultSet.getInt("Count(*)")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * TODO 
	 * temporary development feature, called from server.data.ClearData.java
	 * 
	 * @return
	 */
	public boolean clearAllRoomPositions() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `6_room` SET `PosX` = 0, `PosY` = 0");

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Load a list of room names (including unique code), for PDF positioning
	 */
	public ArrayList<String> loadListOfRoomNames(String floorCode) {

//		HashMap<String, String> result = new HashMap<String, String>();
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			preparedStatement = null;
			
			if(floorCode.isEmpty()) {
				preparedStatement = connect
						.prepareStatement("SELECT Name, Code FROM 6_room");
			} else {
				preparedStatement = connect
						.prepareStatement("SELECT Name, Code FROM 6_room WHERE FloorCode = ?");
				preparedStatement.setString(1, floorCode);
			}
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
//				result.put(resultSet.getString("Code"), resultSet.getString("Name"));
				result.add(resultSet.getString("Name"));
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
	public boolean deleteAllRooms() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("TRUNCATE TABLE `6_room`");

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
	public int numberOfRooms() {
		try {
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("SELECT COUNT(*) FROM `6_room`");
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
	 * 
	 * @param resultSet
	 * @return a new Room object
	 */
	private Room extractRoomFromRS(ResultSet resultSet) {

		Room room = new Room();

		try {

			room.setId(resultSet.getInt("id"));
			
			room.setCode(resultSet.getString("Code"));
			room.setName(resultSet.getString("Name"));
			room.setFloorCode(resultSet.getString("FloorCode"));
			room.setPosX(resultSet.getDouble("PosX"));
			room.setPosY(resultSet.getDouble("PosY"));
			room.setVisibile(resultSet.getBoolean("hidden"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return room;
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
