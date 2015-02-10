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

import de.lmu.navigator.server.database.RoutingNodeMySQL;
import de.lmu.navigator.server.model.RoutingNode;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the REST resource: /routing/nodes
 */

@Path("/routing/nodes")
public class RoutingNodes {
	
	private RoutingNodeMySQL db;	
	
	/**
	 * Create a new RoutingNode
	 * 
	 * @param newRoutingNode		Room object, JSON encoded
	 * @return				HTTP response status (200, 500)
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewRoom(RoutingNode newRoutingNode) throws Exception {

		db = new RoutingNodeMySQL();			
		int id = 0;
		String responseText = "";
		
		// save newRoutingNode to the DB
		id = db.writeRoutingNode(newRoutingNode.getFloorId(), newRoutingNode.getPosX(),
				newRoutingNode.getPosY(), newRoutingNode.isEntrance());
		
		db.close();
		
		if (id == 0)
			return Response.status(500).entity(responseText).build();
		else
			return Response.status(200).entity(responseText).build();

	} 
	
	

	/**
	 * Request a single RoutingNode (identified by an ID)
	 * REST: /routing/nodes/{id} (GET)
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public RoutingNode getRoutingNodeByID (@PathParam("id") int id) throws Exception {
		
		db = new RoutingNodeMySQL();
		
		// load Room
		RoutingNode RoutingNode = db.loadSingleRoutingNode(id);
		
		db.close();
		
		return RoutingNode;
	}
	
	
	/**
	 * Request a filtered list of RoutingNodes. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID), "floor" (floorId)
	 * REST: /routing/nodes/?... (GET)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<RoutingNode> getRoutingNodesByRange(
			@DefaultValue("1") @QueryParam("start") int start,
			@DefaultValue("0") @QueryParam("end") int end,
			@DefaultValue("-1") @QueryParam("floor") int floorId)
			throws Exception {

		db = new RoutingNodeMySQL();
		ArrayList<RoutingNode> list = new ArrayList<RoutingNode>();
		
		list = db.loadFilteredRoutingNodes(start, end, floorId);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified RoutingNode
	 * REST: /routing/nodes/{id} (DELETE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteRoutingNode (@PathParam("id") int RoutingNodeId) throws Exception {
	
		return Response.status(501).entity("not implemented").build();
		
//		db = new RoutingNodeMySQL();			
//		
//		boolean deleted = db.deleteRoutingNode(RoutingNodeId);
//				
//		db.close();
//		
//		if (deleted)
//			return Response.status(200).entity("Successfully removed RoutingNode").build();
//		else
//			return Response.status(500).entity("Could not delete the requested RoutingNode").build();
		
	}
	
	
	/**
	 * Update the specified RoutingNode
	 * REST: /routing/nodes (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoutingNode (RoutingNode updatedRoutingNode) throws Exception {
	
		db = new RoutingNodeMySQL();			
		
		boolean roomUpdated = db.updateRoutingNode(updatedRoutingNode);
				
		db.close();
		
		if (roomUpdated)
			return Response.status(200).entity("Successfully updated RoutingNode").build();
		else
			return Response.status(500).entity("Could not update the requested RoutingNode").build();
		
	}	
	

}
