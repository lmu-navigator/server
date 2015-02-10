package de.lmu.navigator.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lmu.navigator.server.model.RoutingNode;

public class RoutingNodeMySQL {
	
	private Database db = null;
	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public RoutingNodeMySQL() throws Exception {
		// load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		db = new Database();
		connect = db.Get_Connection();
	}
	

	/**
	 * Inserts one new RoutingNode into the DB
	 * @param floorId
	 * @param posX
	 * @param posY
	 * @param entrance
	 * @return the MySQL ID (int) of the new RoutingNode, or 0 for an erroneous
	 *         SQL Query
	 */
	public int writeRoutingNode(int floorId, double posX, double posY, boolean entrance) {
		try {
			// execute sql query
			preparedStatement = connect
					.prepareStatement("INSERT INTO routing_nodes VALUES (default, ?, ?, ?, ?)");

			preparedStatement.setInt(1, floorId);
			preparedStatement.setDouble(2, posX);
			preparedStatement.setDouble(3, posY);
			preparedStatement.setBoolean(4, entrance);
			
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
	 * Delete RoutingNode
	 * 
	 * @param rouingNodeId
	 * @return
	 */
	public boolean deleteRoutingNode(int rouingNodeId) {

		try {
			// delete RoutingNodes
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("DELETE FROM `routing_nodes` WHERE `id` = ?");
			preparedStatement.setInt(1, rouingNodeId);

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	/**
	 * Update a RoutingNode
	 * 
	 * @param updatedRoutingNode
	 * @return
	 */
	public boolean updateRoutingNode(RoutingNode updatedRoutingNode) {

		try {
			// update RoutingNode
			preparedStatement = null;
			preparedStatement = connect
					.prepareStatement("UPDATE `routing_nodes` SET `FloorId` = ?, `PosX` = ?, `PosY` = ?, `Entrance` = ? WHERE `id` = ?");

			preparedStatement.setInt(1, updatedRoutingNode.getFloorId());
			preparedStatement.setDouble(2, updatedRoutingNode.getPosX());
			preparedStatement.setDouble(3, updatedRoutingNode.getPosY());
			preparedStatement.setBoolean(4, updatedRoutingNode.isEntrance());
			preparedStatement.setInt(5, updatedRoutingNode.getId());

			if (preparedStatement.executeUpdate() > 0)
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	

	/**
	 * Load a filtered list of RoutingNodes (REST:
	 * /routing/nodes?start={id}&end={id})
	 * 
	 * @param start
	 *            id of first RoutingNode to retrieve (inclusive)
	 * @param end
	 *            id of last RoutingNode to retrieve (inclusive)
	 * @param building
	 *            the id of the floorId
	 * @return
	 */
	public ArrayList<RoutingNode> loadFilteredRoutingNodes(int start, int end, int floor) {

		ArrayList<RoutingNode> result = new ArrayList<RoutingNode>();
		RoutingNode RoutingNode;

		try {
			// execute sql query
			if (end == 0) {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_nodes WHERE id >= ? AND FloorId LIKE ?");
				preparedStatement.setInt(1, start);

				if (floor == -1)
					preparedStatement.setString(2, "%");
				else
					preparedStatement.setInt(2, floor);

			} else {
				preparedStatement = connect
						.prepareStatement("SELECT * FROM routing_nodes WHERE id >= ? AND id <= ?"
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

			// save all RoutingNode objects to ArrayList
			while (resultSet.next()) {
				RoutingNode = extractRoutingNodeFromRS(resultSet);
				result.add(RoutingNode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * Load a single RoutingNode from the DB (REST: /routing/nodes/{id})
	 */
	public RoutingNode loadSingleRoutingNode(int id) {

		try{
			return loadFilteredRoutingNodes(id, id, -1).get(0);
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
	 * @return a new RoutingNode object
	 */
	private RoutingNode extractRoutingNodeFromRS(ResultSet resultSet) {

		RoutingNode RoutingNode = new RoutingNode();

		try {

			RoutingNode.setId(resultSet.getInt("id"));
			
			RoutingNode.setFloorID(resultSet.getInt("FloorId"));
			RoutingNode.setPosX(resultSet.getDouble("PosX"));
			RoutingNode.setPosY(resultSet.getDouble("PosY"));
			RoutingNode.setEntrance(resultSet.getBoolean("Entrance"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return RoutingNode;
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
