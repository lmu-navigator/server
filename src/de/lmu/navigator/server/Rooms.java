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

import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.Room;


/**
 * Class for RESTful interaction via Jersey / JAXB with the client for the 
 *		/rooms
 * REST resource. 
 */

@Path("/rooms")
public class Rooms {
	
	private RoomMySQL db;	
	
	/**
	 * Create a new Room
	 * REST: /rooms (POST - is not idempotent)
	 * 
	 * @param newRoom		Room object, JSON encoded
	 * @return				HTTP response status (200, 500)
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewRoom(Room newRoom) throws Exception {

		db = new RoomMySQL();			
		int roomId = 0;
		String responseText = "";
		
		// save newRoom to the DB
		roomId = db.writeRoom(newRoom.getCode(), newRoom.getName(), newRoom.getFloorCode(),
				newRoom.getPosX(), newRoom.getPosY());
		
		db.close();
		
		if (roomId == 0)
			return Response.status(500).entity(responseText).build();
		else
			return Response.status(200).entity(responseText).build();

	} 
	
	

	/**
	 * Request a single Rooms (identified by an ID)
	 * REST: /rooms/{code} (GET)
	 */
	@GET
	@Path("/{code}")
	@Produces("application/json")
	public Room getRoomByID (@PathParam("code") String code) throws Exception {
		
		db = new RoomMySQL();
		
		// load Room
		Room room = db.loadSingleRoom(code);
		
		db.close();
		
		return room;
	}
	
	
	/**
	 * Request a filtered list of Rooms. 
	 * Possible parameters for filtering: "start" (ID), "end" (ID), "floor" (floorCode)
	 * REST: /rooms?... (GET)
	 */
	@GET
	@Produces("application/json")
	public ArrayList<Room> getRoomsByRange(
			@DefaultValue("") @QueryParam("code") String roomCode,
			@DefaultValue("") @QueryParam("floor") String floorCode)
			throws Exception {

		db = new RoomMySQL();
		ArrayList<Room> list = new ArrayList<Room>();
		
		list = db.loadFilteredRooms(roomCode, floorCode);
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * Delete the specified Room
	 * REST: /rooms/{code} (DELETE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@DELETE
	@Path("/{code}")
	public Response deleteRoom (@PathParam("code") String roomCode) throws Exception {
	
		return Response.status(501).entity("not implemented").build();
		
//		db = new RoomMySQL();			
//		
//		boolean roomDeleted = db.deleteRoom(roomCode);
//				
//		db.close();
//		
//		if (roomDeleted)
//			return Response.status(200).entity("Successfully removed room").build();
//		else
//			return Response.status(500).entity("Could not delete the requested room").build();
		
	}
	
	
	/**
	 * Update the specified Room
	 * REST: /rooms (UPDATE)
	 * 
	 * Note: no authentication or security checks implemented so far!
	 * 	--> TODO
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRoom (Room updatedRoom) throws Exception {
	
		db = new RoomMySQL();			
		
		boolean roomUpdated = db.updateRoom(updatedRoom);
				
		db.close();
		
		if (roomUpdated)
			return Response.status(200).entity("Successfully updated room").build();
		else
			return Response.status(500).entity("Could not update the requested room").build();
		
	}	
	

}
