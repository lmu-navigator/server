package de.lmu.navigator.server;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.lmu.navigator.server.database.RoutingRoomConnectionMySQL;
import de.lmu.navigator.server.model.RoutingRoomConnection;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/routing-edges
 * REST resource. 
 */

@Path("/routing/connections/rooms")
public class RoutingRoomConnections {
	
	private RoutingRoomConnectionMySQL db;	
	
	/**
	 * Create a new RoutingRoomConnection
	 * 
	 * @param newRoutingRoomConnection		Room object, JSON encoded
	 * @return				HTTP response status (200, 500)
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewRoom(RoutingRoomConnection newRoutingRoomConnection) throws Exception {

		db = new RoutingRoomConnectionMySQL();			
		int id = 0;
		String responseText = "";
		
		// save newRoutingRoomConnection to the DB
		id = db.writeRoutingRoomConnection(newRoutingRoomConnection.getNodeId(), newRoutingRoomConnection.getRoomId());
		
		db.close();
		
		if (id == 0)
			return Response.status(500).entity(responseText).build();
		else
			return Response.status(200).entity(responseText).build();

	} 
	
	

	/**
	 * Request a single RoutingRoomConnection (identified by an ID)
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public RoutingRoomConnection getRoutingRoomConnectionByID (@PathParam("id") int id) throws Exception {
		
		db = new RoutingRoomConnectionMySQL();
		
		// load Object
		RoutingRoomConnection RoutingRoomConnection = db.loadSingleRoutingRoomConnection(id);
		
		db.close();
		
		return RoutingRoomConnection;
	}
	
	
	/**
	 * Request a filtered list of RoutingRoomConnections. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID), "building" (buildingId)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<RoutingRoomConnection> getRoutingRoomConnectionsByRange(
			@DefaultValue("1") @QueryParam("start") int start,
			@DefaultValue("0") @QueryParam("end") int end)
			throws Exception {

		db = new RoutingRoomConnectionMySQL();
		ArrayList<RoutingRoomConnection> list = new ArrayList<RoutingRoomConnection>();
		
		list = db.loadFilteredRoutingRoomConnections(start, end);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified RoutingRoomConnection
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteRoutingRoomConnection (@PathParam("id") int RoutingRoomConnectionId) throws Exception {
	
		return Response.status(501).entity("not implemented").build();
		
//		db = new RoutingRoomConnectionMySQL();			
//		
//		boolean deleted = db.deleteRoutingRoomConnection(RoutingRoomConnectionId);
//				
//		db.close();
//		
//		if (deleted)
//			return Response.status(200).entity("Successfully removed RoutingRoomConnection").build();
//		else
//			return Response.status(500).entity("Could not delete the requested RoutingRoomConnection").build();
		
	}
	
	
	/**
	 * Update the specified RoutingRoomConnection
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoutingRoomConnection (RoutingRoomConnection updatedRoutingRoomConnection) throws Exception {

		return Response.status(501).entity("not implemented").build();

//		db = new RoutingRoomConnectionMySQL();			
//		
//		boolean roomUpdated = db.updateRoutingRoomConnection(updatedRoutingRoomConnection);
//				
//		db.close();
//		
//		if (roomUpdated)
//			return Response.status(200).entity("Successfully updated RoutingRoomConnection").build();
//		else
//			return Response.status(500).entity("Could not update the requested RoutingRoomConnection").build();
		
	}	
	

}
