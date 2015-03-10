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

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.model.Floor;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/floors
 * REST resource. 
 */

@Path("/floors")
public class Floors {
	
	private FloorMySQL db;		

	/**
	 * Request a single Floor (identified by an ID)
	 * REST: /floors/{id} (GET)
	 * 
	 * @param id		of floor
	 * @return			a single floor (if noRooms=false, then also including all associated Rooms)
	 * @throws Exception
	 */
	@GET
	@Path("/{code}")
	@Produces("application/json")
	public Floor getFloorByID (@PathParam("code") String code) throws Exception {
		
		db = new FloorMySQL();
		
		Floor floor = db.loadSingleFloor(code, false);
		
		db.close();
		
		return floor;
	}
	
	/**
	 * returns all floors associated to the same mapUri (PDF file), excluding
	 * the floor itself, required for buildings like the main building
	 * (Geschwister-Scholl-Platz)
	 */
	@GET
	@Path("/{code}/bordering")
	@Produces("application/json")
	public ArrayList<Floor> getBorderingFloors (@PathParam("code") String floorCode) throws Exception {
		db = new FloorMySQL();
		ArrayList<Floor> list = new ArrayList<Floor>();

		// load all floors from the same floorMap (PDF), excluding the floor itself
		list = db.loadBorderingFloors(floorCode);
			
		db.close();
		
		return list;

	}
	
	/**
	 * Request a filtered list of Floors. 
	 * Possible parameters for filtering: "buildingCode"
	 * "buildingPart" (String), "level" (String), "noRooms" (true/false - whether associated Rooms get returned or not)
	 * REST: /floors?... (GET)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<Floor> getFloorsByRange(
			@DefaultValue("") @QueryParam("code") String floorCode,
			@DefaultValue("") @QueryParam("buildingpart") String buildingPartCode)
			throws Exception {

		db = new FloorMySQL();
		ArrayList<Floor> list = new ArrayList<Floor>();

		// load requested floor
		list = db.loadFilteredFloors(floorCode, buildingPartCode, false);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Update the specified Floor
	 * REST: /floors (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoom (Floor updatedFloor) throws Exception {
	
//		db = new FloorMySQL();			
//		
//		boolean floorUpdated = db.updateFloor(updatedFloor);
//				
//		db.close();
//		
//		if (floorUpdated)
//			return Response.status(200).entity("Successfully updated floor").build();
//		else
//			return Response.status(500).entity("Could not update the requested floor").build();
		
		return Response.status(501).entity("Not implemented").build();
	}	
	
}
