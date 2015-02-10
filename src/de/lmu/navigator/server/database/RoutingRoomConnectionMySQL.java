package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.RoutingRoomConnection;

public class RoutingRoomConnectionMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public RoutingRoomConnectionMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new RoutingRoomConnection into the DB
	 * @param buildingId
	 * @param source
	 * @param target
	 * @param type
	 * @return the MySQL ID (int) of the new RoutingRoomConnection, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeRoutingRoomConnection(int nodeId, int roomId) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO routing_room_connections VALUES (default, ?, ?)");

			preparedStatement.setInt(1, nodeId);
			preparedStatement.setInt(2, roomId);
			
			preparedStatement.executeUpdate();

			// get auto increment ID of newly created object
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
	 * Delete RoutingRoomConnection
	 * 
	 * @param rouingRoomConnectionId
	 * @return
	 */
	public boolean deleteRoutingRoomConnection(int rouingRoomConnectionId) {

		try {
			// delete RoutingRoomConnections
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `routing_room_connections` WHERE `id` = ?");
			preparedStatement.setInt(1, rouingRoomConnectionId);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update a RoutingRoomConnection
	 * 
	 * @param updatedRoutingRoomConnection
	 * @return
	 */
	public boolean updateRoutingRoomConnection(RoutingRoomConnection updatedRoutingRoomConnection) {

		try {
			// update RoutingRoomConnection
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `routing_room_connections` SET `Node` = ?, `Room` = ? WHERE `id` = ?");

			preparedStatement.setInt(1, updatedRoutingRoomConnection.getNodeId());
			preparedStatement.setInt(2, updatedRoutingRoomConnection.getRoomId());;
			preparedStatement.setInt(3, updatedRoutingRoomConnection.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	

	/**
	 * Load a filtered list of RoutingRoomConnections
	 * 
	 * @param start
	 *            id of first RoutingRoomConnection to retrieve (inclusive)
	 * @param end
	 *            id of last RoutingRoomConnection to retrieve (inclusive)
	 * @param building
	 *            the id of the buildingId
	 * @return
	 */
	public ArrayList<RoutingRoomConnection> loadFilteredRoutingRoomConnections(int start, int end) {

		ArrayList<RoutingRoomConnection> result = new ArrayList<RoutingRoomConnection>();
		RoutingRoomConnection RoutingRoomConnection;

		try {
			// execute sql query
			if (end == 0) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_room_connections WHERE id >= ?");
				preparedStatement.setInt(1, start);

			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_room_connections WHERE id >= ? AND id <= ?");
				preparedStatement.setInt(1, start);
				preparedStatement.setInt(2, end);
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all RoutingRoomConnection objects to ArrayList
			while (resultSet.next()) {
				RoutingRoomConnection = extractRoutingRoomConnectionFromRS(resultSet);
				result.add(RoutingRoomConnection);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single RoutingRoomConnection from the DB
	 */
	public RoutingRoomConnection loadSingleRoutingRoomConnection(int id) {

		try{
			return loadFilteredRoutingRoomConnections(id, id).get(0);
		} catch(Exception e) {
			return null;
		}
	
	}
	

	/* ***** Helper functions ***** */

	/**
	 * Helper Function, to remove redundancy from always extracting the values
	 * from the SQL ResultSet
	 * 
	 * @param resultSet
	 * @return a new RoutingRoomConnection object
	 */
	private RoutingRoomConnection extractRoutingRoomConnectionFromRS(ResultSet resultSet) {

		RoutingRoomConnection routingRoomConnection = new RoutingRoomConnection();

		try {

			routingRoomConnection.setId(resultSet.getInt("id"));
			
			routingRoomConnection.setNodeId(resultSet.getInt("Node"));
			routingRoomConnection.setRoomId(resultSet.getInt("Room"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return routingRoomConnection;
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
