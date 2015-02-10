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

import de.lmu.navigator.server.database.RoutingEdgeMySQL;
import de.lmu.navigator.server.model.RoutingEdge;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/routing-edges
 * REST resource. 
 */

@Path("/routing/edges")
public class RoutingEdges {
	
	private RoutingEdgeMySQL db;	
	
	/**
	 * Create a new RoutingEdge
	 * 
	 * @param newRoutingEdge		Room object, JSON encoded
	 * @return				HTTP response status (200, 500)
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewRoom(RoutingEdge newRoutingEdge) throws Exception {

		db = new RoutingEdgeMySQL();			
		int id = 0;
		String responseText = "";
		
		// save newRoutingEdge to the DB
		id = db.writeRoutingEdge(newRoutingEdge.getFloorId(), newRoutingEdge.getSource(),
				newRoutingEdge.getTarget(), newRoutingEdge.getWeight());
		
		db.close();
		
		if (id == 0)
			return Response.status(500).entity(responseText).build();
		else
			return Response.status(200).entity(responseText).build();

	} 
	
	

	/**
	 * Request a single RoutingEdge (identified by an ID)
	 * REST: /routing/edges/{id} (GET)
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public RoutingEdge getRoutingEdgeByID (@PathParam("id") int id) throws Exception {
		
		db = new RoutingEdgeMySQL();
		
		// load Room
		RoutingEdge routingEdge = db.loadSingleRoutingEdge(id);
		
		db.close();
		
		return routingEdge;
	}
	
	
	/**
	 * Request a filtered list of RoutingEdges. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID), "floor" (floorId)
	 * REST: /routing/edges/?... (GET)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<RoutingEdge> getRoutingEdgesByRange(
			@DefaultValue("1") @QueryParam("start") int start,
			@DefaultValue("0") @QueryParam("end") int end,
			@DefaultValue("-1") @QueryParam("floor") int floorId)
			throws Exception {

		db = new RoutingEdgeMySQL();
		ArrayList<RoutingEdge> list = new ArrayList<RoutingEdge>();
		
		list = db.loadFilteredRoutingEdges(start, end, floorId);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified RoutingEdge
	 * REST: /routing/edges/{id} (DELETE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteRoutingEdge (@PathParam("id") int routingEdgeId) throws Exception {
	
		return Response.status(501).entity("not implemented").build();
		
//		db = new RoutingEdgeMySQL();			
//		
//		boolean deleted = db.deleteRoutingEdge(routingEdgeId);
//				
//		db.close();
//		
//		if (deleted)
//			return Response.status(200).entity("Successfully removed RoutingEdge").build();
//		else
//			return Response.status(500).entity("Could not delete the requested RoutingEdge").build();
		
	}
	
	
	/**
	 * Update the specified RoutingEdge
	 * REST: /routing/edges (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoutingEdge (RoutingEdge updatedRoutingEdge) throws Exception {
	
		db = new RoutingEdgeMySQL();			
		
		boolean roomUpdated = db.updateRoutingEdge(updatedRoutingEdge);
				
		db.close();
		
		if (roomUpdated)
			return Response.status(200).entity("Successfully updated RoutingEdge").build();
		else
			return Response.status(500).entity("Could not update the requested RoutingEdge").build();
		
	}	
	

}
