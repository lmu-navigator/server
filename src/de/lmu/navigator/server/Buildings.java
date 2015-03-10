package de.lmu.navigator.server;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.lmu.navigator.server.database.BuildingMySQL;
import de.lmu.navigator.server.model.Building;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/buildings
 * REST resource. 
 */

@Path("/buildings")
public class Buildings {
	
	private BuildingMySQL db;	
	

	/**
	 * Request a single Building (identified by a Code)
	 * REST: /buildings/{code} (GET)
	 * 
	 * @param buildingCode		of building
	 * @return			a single floor (if noRooms=false, then also including all associated Rooms)
	 * @throws Exception
	 */
	@GET
	@Path("/{code}")
	@Produces("application/json")
	public Building getBuildingByID (@PathParam("code") String buildingCode) throws Exception {
		
		db = new BuildingMySQL();
		
		// load Building
		Building building = db.loadSingleBuilding(buildingCode);
		
		db.close();
		
		return building;
	}
	
	
	
	/**
	 * Request a filtered list of Buildings. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID)
	 * "clean" (only Buildings, and NO associated Floors or Rooms)
	 * REST: /buildings?... (GET)
	 * 
	 * @param building
	 * @param street
	 * @param clean
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces("application/json")
	public ArrayList<Building> getBuildingsByRange(
			@DefaultValue("") @QueryParam("building") String building,
			@DefaultValue("") @QueryParam("street") String street)
			throws Exception {

		db = new BuildingMySQL();
		ArrayList<Building> list = new ArrayList<Building>();

		list = db.loadFilteredBuildings(building, street);
		
		db.close();
		
		return list;
	}
	

	/**
	 * Update the specified Building
	 * REST: /buildings (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBuilding (Building updatedBuilding) throws Exception {
	
//		db = new BuildingMySQL();			
//		boolean buildingUpdated = db.updateBuilding(updatedBuilding);
//		db.close();
//		if (buildingUpdated)
//			return Response.status(200).entity("Successfully updated building").build();
//		else
//			return Response.status(405).entity("Could not update the requested building").build();
		
		return Response.status(501).entity("Not implemented").build();
	}	
	
}
