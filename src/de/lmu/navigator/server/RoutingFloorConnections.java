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

import de.lmu.navigator.server.database.RoutingFloorConnectionMySQL;
import de.lmu.navigator.server.model.RoutingFloorConnection;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/routing-edges
 * REST resource. 
 */

@Path("/routing/connections/floors")
public class RoutingFloorConnections {
	
	private RoutingFloorConnectionMySQL db;	
	
	/**
	 * Create a new RoutingFloorConnection
	 * 
	 * @param newRoutingFloorConnection		Room object, JSON encoded
	 * @return				HTTP response status (200, 500)
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewRoom(RoutingFloorConnection newRoutingFloorConnection) throws Exception {

		db = new RoutingFloorConnectionMySQL();			
		int id = 0;
		String responseText = "";
		
		// save newRoutingFloorConnection to the DB
		id = db.writeRoutingFloorConnection(newRoutingFloorConnection.getBuildingId(), newRoutingFloorConnection.getSource(),
				newRoutingFloorConnection.getTarget(), newRoutingFloorConnection.getType());
		
		db.close();
		
		if (id == 0)
			return Response.status(500).entity(responseText).build();
		else
			return Response.status(200).entity(responseText).build();

	} 
	
	

	/**
	 * Request a single RoutingFloorConnection (identified by an ID)
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public RoutingFloorConnection getRoutingFloorConnectionByID (@PathParam("id") int id) throws Exception {
		
		db = new RoutingFloorConnectionMySQL();
		
		// load Object
		RoutingFloorConnection RoutingFloorConnection = db.loadSingleRoutingFloorConnection(id);
		
		db.close();
		
		return RoutingFloorConnection;
	}
	
	
	/**
	 * Request a filtered list of RoutingFloorConnections. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID), "building" (buildingId)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<RoutingFloorConnection> getRoutingFloorConnectionsByRange(
			@DefaultValue("1") @QueryParam("start") int start,
			@DefaultValue("0") @QueryParam("end") int end,
			@DefaultValue("-1") @QueryParam("building") int buildingId)
			throws Exception {

		db = new RoutingFloorConnectionMySQL();
		ArrayList<RoutingFloorConnection> list = new ArrayList<RoutingFloorConnection>();
		
		list = db.loadFilteredRoutingFloorConnections(start, end, buildingId);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified RoutingFloorConnection
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteRoutingFloorConnection (@PathParam("id") int RoutingFloorConnectionId) throws Exception {
	
		return Response.status(501).entity("not implemented").build();
		
//		db = new RoutingFloorConnectionMySQL();			
//		
//		boolean deleted = db.deleteRoutingFloorConnection(RoutingFloorConnectionId);
//				
//		db.close();
//		
//		if (deleted)
//			return Response.status(200).entity("Successfully removed RoutingFloorConnection").build();
//		else
//			return Response.status(500).entity("Could not delete the requested RoutingFloorConnection").build();
		
	}
	
	
	/**
	 * Update the specified RoutingFloorConnection
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoutingFloorConnection (RoutingFloorConnection updatedRoutingFloorConnection) throws Exception {
	
		db = new RoutingFloorConnectionMySQL();			
		
		boolean roomUpdated = db.updateRoutingFloorConnection(updatedRoutingFloorConnection);
				
		db.close();
		
		if (roomUpdated)
			return Response.status(200).entity("Successfully updated RoutingFloorConnection").build();
		else
			return Response.status(500).entity("Could not update the requested RoutingFloorConnection").build();
		
	}	
	

}
