package de.lmu.navigator.server.data;

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.Floor;
import de.lmu.navigator.server.model.Settings;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class CalculateRoomPosition extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Settings settings = new Settings();	

	private String floorToProcess;
	private String processAllFloors;
	private String pathToPDF;
	
	private FloorMySQL dbFloor;
	private RoomMySQL dbRoom;
	
	/**
	 * use case: process all PDFs at once,  
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		boolean updated = false;
		
		/* load pathToPDF */
		pathToPDF = settings.getPathToPDF();

		/* load GET parameters */
		processAllFloors = request.getParameter("all");
		floorToProcess = request.getParameter("floor");
		
		if (processAllFloors != null && processAllFloors.equals("true")) {
			// process all floors
			
			/* load all Floors from DB */
			ArrayList<Floor> floors = new ArrayList<Floor>();
			
			try {
				dbFloor = new FloorMySQL();
				floors = dbFloor.loadFilteredFloors("", "", true);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbFloor.close();
			}
			out.println("floors loaded: " + floors.size()+" <br/>");

			// loop over floors
			for (Floor f : floors) {
				/* calculate roomPositions for floor */
				updated = updateSingleFloor(f);
				if (updated) {
					out.println("updated floor "+f.getCode());
				} else {
					out.println("ERROR UPDATING FLOOR "+f.getCode());
				}
			}
			
			
		} else if (floorToProcess != null) {
			// process single floor
			
			/* load floor from DB */
			Floor f = null;
			try {
				dbFloor = new FloorMySQL();
				f = dbFloor.loadSingleFloor(floorToProcess, true);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbFloor.close();
			}
			if (f == null) {
				out.println("error loading Floor from DB");
				return;
			}
			
			/* calculate all roomPositions for single floor */
			updated = updateSingleFloor(f);
			
			if (updated) {
				out.println("updated floor "+f.getCode());
			}

		} else {
			out.println("invalid parameters / no floor specified");
			return;
		}
		
		
		/* redirect back to DataOverview */
		response.sendRedirect("../");

		
		
		/* ---------------------------------------------- */
		
		// TODO handle additions, deletions	
				
	}
	
	/**
	 * possible use case: upload a single PDF file, process it (WIP)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);		
	}
	
	
	/**
	 * Method to call the RoomCoordsImporter.findRoomCoords() function. It
	 * matches all rooms of a specified floor with corresponding PDF.
	 */
	private boolean updateSingleFloor(Floor f) throws IOException {
		/* load list of Rooms for Floor from DB */
		ArrayList<String> rooms = new ArrayList<String>();
		// it's possible that one PDF is assigned to multiple FloorParts (Hauptgebaeude) 
		try {
			dbRoom = new RoomMySQL();
			rooms = dbRoom.loadListOfRoomNames(f.getCode());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbFloor.close();
		}
		
		/* link PDF file */
		URL pdfUrl = new URL(new URL(pathToPDF), f.getMapUri());

		/* calculate Room positions */
		Map<String, double[]> roomCoords = RoomCoordsImporter.findRoomCoords(pdfUrl, rooms);
		
		double posX = 0, posY = 0;
		
		try {
			dbRoom = new RoomMySQL();
			rooms = dbRoom.loadListOfRoomNames(f.getCode());
			int roomsPositioned = 0;
			
			for (Map.Entry<String, double[]> coordinate : roomCoords.entrySet())
			{
				/* convert relativePosition to absolutePosition */
				posX = coordinate.getValue()[0] * f.getMapSizeX();
				posY = coordinate.getValue()[1] * f.getMapSizeY();

                if (posX >= 0 && posY >= 0) {
				    /* save coordinates to DB */
                    dbRoom.updateRoomPosition(f.getCode(), coordinate.getKey(), (int) posX, (int) posY);
                	roomsPositioned++;
                	
                	// debug output for absolute+relative position 
//                  System.out.println(coordinate.getKey() + ": x=" + (int) posX + ", y=" + (int) posY);
                }
			}
			
			// print log output (positioned all rooms / some rooms / no rooms)
			if (roomsPositioned == rooms.size()) 
				System.out.println(f.getMapUri()+": positioned all rooms");
			else if (roomsPositioned == 0)
				System.out.println(f.getMapUri()+": PDF NOT READABLE");
			else if (roomsPositioned > 0 && roomsPositioned < rooms.size())
				System.out.println(f.getMapUri()+": not all rooms could be positioned ("+roomsPositioned+" ouf of "+rooms.size()+")");
			else
				System.out.println(f.getMapUri()+": WTF? "+roomsPositioned+" out of "+rooms.size()+" could be positioned");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settings.addVersion("updated roomPositions for floor ["+f.getCode()+"]");
			dbFloor.close();
		}
		
		return true;
	}

	
}
