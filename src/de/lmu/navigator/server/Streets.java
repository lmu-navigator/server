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

import de.lmu.navigator.server.database.StreetMySQL;
import de.lmu.navigator.server.model.Street;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/streets
 * REST resource. 
 */

@Path("/streets")
public class Streets {
	
	private StreetMySQL db;	
	

	/**
	 * Request a single Street (identified by a Code)
	 * REST: /streets/{code} (GET)
	 * 
	 * @param StreetCode		Code of street
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/{code}")
	@Produces("application/json")
	public Street getStreetByCode (@PathParam("code") String streetCode) throws Exception {
		
		db = new StreetMySQL();
		
		// load Street
		Street street = db.loadSingleStreet(streetCode);
		
		db.close();
		
		return street;
	}
	
	
	
	/**
	 * Request a filtered list of streets. 
	 * 
	 * @param Street
	 * @param city
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces("application/json")
	public ArrayList<Street> getStreetsByRange(
			@DefaultValue("") @QueryParam("code") String street,
			@DefaultValue("") @QueryParam("city") String city)
			throws Exception {

		db = new StreetMySQL();
		ArrayList<Street> list = new ArrayList<Street>();

		list = db.loadFilteredStreets(street, city);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified Street RECURSIVELY! (all corresponding Floors and Rooms will also be deleted)
	 * REST: /streets/{id} (DELETE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{code}")
	public Response deleteStreet (@PathParam("code") String streetCode) throws Exception {
		
		return Response.status(501).entity("not implemented").build();
		
//		db = new StreetMySQL();
//		boolean elementsDeleted = db.deleteStreet(streetCode);
//		db.close();
//		
//		if (elementsDeleted)
//			return Response.status(200).entity("Street deleted successfully").build();
//		else
//			return Response.status(422).entity("Could not delete Street (e.g. invalid ID)").build();
	
	}
	

	/**
	 * Update the specified Street
	 * REST: /streets (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateStreet (Street updatedStreet) throws Exception {
	
		db = new StreetMySQL();			
		
		boolean streetUpdated = db.updateStreet(updatedStreet);
		
		db.close();
		
		if (streetUpdated)
			return Response.status(200).entity("Successfully updated Street").build();
		else
			return Response.status(405).entity("Could not update the requested Street").build();
		
	}	
	
}
