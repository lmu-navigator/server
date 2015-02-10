package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.RoutingEdge;

public class RoutingEdgeMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public RoutingEdgeMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	
	/**
	 * Inserts one new RoutingEdge into the DB
	 * @param floorId
	 * @param source
	 * @param target
	 * @param weight
	 * @return the MySQL ID (int) of the new RoutingEdge, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeRoutingEdge(int floorId, int source, int target, int weight) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO routing_edges VALUES (default, ?, ?, ?, ?)");

			preparedStatement.setInt(1, floorId);
			preparedStatement.setInt(2, source);
			preparedStatement.setInt(3, target);
			preparedStatement.setInt(4, weight);
			
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
	 * Delete routingEdge
	 * 
	 * @param rouingEdgeId
	 * @return
	 */
	public boolean deleteRoutingEdge(int rouingEdgeId) {

		try {
			// delete RoutingEdges
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `routing_edges` WHERE `id` = ?");
			preparedStatement.setInt(1, rouingEdgeId);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update a routingEdge
	 * 
	 * @param updatedRoutingEdge
	 * @return
	 */
	public boolean updateRoutingEdge(RoutingEdge updatedRoutingEdge) {

		try {
			// update RoutingEdge
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `routing_edges` SET `FloorId` = ?, `Source` = ?, `Target` = ?, `Weight` = ? WHERE `id` = ?");

			preparedStatement.setInt(1, updatedRoutingEdge.getFloorId());
			preparedStatement.setInt(2, updatedRoutingEdge.getSource());
			preparedStatement.setInt(3, updatedRoutingEdge.getTarget());
			preparedStatement.setInt(4, updatedRoutingEdge.getWeight());
			preparedStatement.setInt(5, updatedRoutingEdge.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	

	/**
	 * Load a filtered list of RoutingEdges (REST:
	 * /routing/edges?start={id}&end={id})
	 * 
	 * @param start
	 *            id of first RoutingEdge to retrieve (inclusive)
	 * @param end
	 *            id of last RoutingEdge to retrieve (inclusive)
	 * @param building
	 *            the id of the floorId
	 * @return
	 */
	public ArrayList<RoutingEdge> loadFilteredRoutingEdges(int start, int end, int floor) {

		ArrayList<RoutingEdge> result = new ArrayList<RoutingEdge>();
		RoutingEdge routingEdge;

		try {
			// execute sql query
			if (end == 0) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_edges WHERE id >= ? AND FloorId LIKE ?");
				preparedStatement.setInt(1, start);

				if (floor == -1)
					preparedStatement.setString(2, "%");
				else
					preparedStatement.setInt(2, floor);

			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_edges WHERE id >= ? AND id <= ?"
								+ " AND FloorId LIKE ?");
				preparedStatement.setInt(1, start);
				preparedStatement.setInt(2, end);

				if (floor == -1)
					preparedStatement.setString(3, "%");
				else
					preparedStatement.setInt(3, floor);
			}

			resultSet = preparedStatement.executeQuery();
			
			// check for NPE
			if (!resultSet.isBeforeFirst())
				return null;

			// save all RoutingEdge objects to ArrayList
			while (resultSet.next()) {
				routingEdge = extractRoutingEdgeFromRS(resultSet);
				result.add(routingEdge);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single RoutingEdge from the DB (REST: /routing/edge/{id})
	 */
	public RoutingEdge loadSingleRoutingEdge(int id) {

		try{
			return loadFilteredRoutingEdges(id, id, -1).get(0);
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
	 * @return a new RoutingEdge object
	 */
	private RoutingEdge extractRoutingEdgeFromRS(ResultSet resultSet) {

		RoutingEdge routingEdge = new RoutingEdge();

		try {

			routingEdge.setId(resultSet.getInt("id"));
			
			routingEdge.setFloorID(resultSet.getInt("FloorId"));
			routingEdge.setSource(resultSet.getInt("Source"));
			routingEdge.setTarget(resultSet.getInt("Target"));
			routingEdge.setWeight(resultSet.getInt("Weight"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return routingEdge;
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
