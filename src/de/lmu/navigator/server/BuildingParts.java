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

import de.lmu.navigator.server.database.BuildingPartMySQL;
import de.lmu.navigator.server.model.BuildingPart;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/buildingparts
 * REST resource. 
 */

@Path("/buildingparts")
public class BuildingParts {
	
	private BuildingPartMySQL db;	
	

	/**
	 * Request a single BuildingPart (identified by a Code)
	 * REST: /buildingparts/{code} (GET)
	 * 
	 * @param buildingPartCode		of building
	 * @return			a single floor (if noRooms=false, then also including all associated Rooms)
	 * @throws Exception
	 */
	@GET
	@Path("/{code}")
	@Produces("application/json")
	public BuildingPart getBuildingByID (@PathParam("buildingpart") String buildingPartCode) throws Exception {
		
		db = new BuildingPartMySQL();
		
		// load Building
		BuildingPart buildingPart = db.loadSingleBuildingPart(buildingPartCode);
		
		db.close();
		
		return buildingPart;
	}
	
	
	
	/**
	 * Request a filtered list of BuildingParts. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID)
	 * "clean" (only BuildingParts, and NO associated Floors or Rooms)
	 * REST: /buildingpartss?... (GET)
	 * 
	 * @param BuildingPart
	 * @param building
	 * @param clean
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces("application/json")
	public ArrayList<BuildingPart> getBuildingPartsByRange(
			@DefaultValue("") @QueryParam("buildingpart") String buildingPart,
			@DefaultValue("") @QueryParam("building") String building)
			throws Exception {

		db = new BuildingPartMySQL();
		ArrayList<BuildingPart> list = new ArrayList<BuildingPart>();

		list = db.loadFilteredBuildingParts(buildingPart, building);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified BuildingPart RECURSIVELY! (all corresponding Floors and Rooms will also be deleted)
	 * REST: /buildingparts/{id} (DELETE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{code}")
	public Response deleteBuildingPart (@PathParam("code") String buildingPartCode) throws Exception {
		
		return Response.status(501).entity("not implemented").build();
		
//		db = new BuildingPartMySQL();
//		boolean elementsDeleted = db.deleteBuildingPart(buildingPartCode);
//		db.close();
//		
//		if (elementsDeleted)
//			return Response.status(200).entity("BuildingPart deleted successfully").build();
//		else
//			return Response.status(422).entity("Could not delete BuildingPart (e.g. invalid ID)").build();
	
	}
	

	/**
	 * Update the specified BuildingPart
	 * REST: /buildingparts (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBuildingPart (BuildingPart updatedBuilding) throws Exception {
	
		db = new BuildingPartMySQL();			
		
		boolean buildingPartUpdated = db.updateBuildingPart(updatedBuilding);
		
		db.close();
		
		if (buildingPartUpdated)
			return Response.status(200).entity("Successfully updated BuildingPart").build();
		else
			return Response.status(405).entity("Could not update the requested BuildingPart").build();
		
	}	
	
}
