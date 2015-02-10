package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.RoutingFloorConnection;

public class RoutingFloorConnectionMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public RoutingFloorConnectionMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new RoutingFloorConnection into the DB
	 * @param buildingId
	 * @param source
	 * @param target
	 * @param type
	 * @return the MySQL ID (int) of the new RoutingFloorConnection, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeRoutingFloorConnection(int buildingId, int source, int target, String type) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO routing_floor_connections VALUES (default, ?, ?, ?, ?)");

			preparedStatement.setInt(1, buildingId);
			preparedStatement.setInt(2, source);
			preparedStatement.setInt(3, target);
			preparedStatement.setString(4, type);
			
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
	 * Delete RoutingFloorConnection
	 * 
	 * @param rouingFloorConnectionId
	 * @return
	 */
	public boolean deleteRoutingFloorConnection(int rouingFloorConnectionId) {

		try {
			// delete RoutingFloorConnections
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `routing_floor_connections` WHERE `id` = ?");
			preparedStatement.setInt(1, rouingFloorConnectionId);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update a RoutingFloorConnection
	 * 
	 * @param updatedRoutingFloorConnection
	 * @return
	 */
	public boolean updateRoutingFloorConnection(RoutingFloorConnection updatedRoutingFloorConnection) {

		try {
			// update RoutingFloorConnection
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `routing_floor_connections` SET `BuildingId` = ?, `Source` = ?, `Target` = ?, `Type` = ? WHERE `id` = ?");

			preparedStatement.setInt(1, updatedRoutingFloorConnection.getBuildingId());
			preparedStatement.setInt(2, updatedRoutingFloorConnection.getSource());
			preparedStatement.setInt(3, updatedRoutingFloorConnection.getTarget());
			preparedStatement.setString(4, updatedRoutingFloorConnection.getType());
			preparedStatement.setInt(5, updatedRoutingFloorConnection.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	

	/**
	 * Load a filtered list of RoutingFloorConnections
	 * 
	 * @param start
	 *            id of first RoutingFloorConnection to retrieve (inclusive)
	 * @param end
	 *            id of last RoutingFloorConnection to retrieve (inclusive)
	 * @param building
	 *            the id of the buildingId
	 * @return
	 */
	public ArrayList<RoutingFloorConnection> loadFilteredRoutingFloorConnections(int start, int end, int building) {

		ArrayList<RoutingFloorConnection> result = new ArrayList<RoutingFloorConnection>();
		RoutingFloorConnection RoutingFloorConnection;

		try {
			// execute sql query
			if (end == 0) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_floor_connections WHERE id >= ? AND BuildingId LIKE ?");
				preparedStatement.setInt(1, start);

				if (building == -1)
					preparedStatement.setString(2, "%");
				else
					preparedStatement.setInt(2, building);

			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_floor_connections WHERE id >= ? AND id <= ?"
								+ " AND BuildingId LIKE ?");
				preparedStatement.setInt(1, start);
				preparedStatement.setInt(2, end);

				if (building == -1)
					preparedStatement.setString(3, "%");
				else
					preparedStatement.setInt(3, building);
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all RoutingFloorConnection objects to ArrayList
			while (resultSet.next()) {
				RoutingFloorConnection = extractRoutingFloorConnectionFromRS(resultSet);
				result.add(RoutingFloorConnection);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single RoutingFloorConnection from the DB
	 */
	public RoutingFloorConnection loadSingleRoutingFloorConnection(int id) {

		try{
			return loadFilteredRoutingFloorConnections(id, id, -1).get(0);
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
	 * @return a new RoutingFloorConnection object
	 */
	private RoutingFloorConnection extractRoutingFloorConnectionFromRS(ResultSet resultSet) {

		RoutingFloorConnection routingFloorConnection = new RoutingFloorConnection();

		try {

			routingFloorConnection.setId(resultSet.getInt("id"));
			
			routingFloorConnection.setBuildingId(resultSet.getInt("BuildingId"));
			routingFloorConnection.setSource(resultSet.getInt("Source"));
			routingFloorConnection.setTarget(resultSet.getInt("Target"));
			routingFloorConnection.setType(resultSet.getString("Type"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return routingFloorConnection;
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
