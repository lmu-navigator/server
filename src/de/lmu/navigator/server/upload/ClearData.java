package de.lmu.navigator.server.upload;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.lmu.navigator.server.database.BuildingMySQL;
import de.lmu.navigator.server.database.BuildingPartMySQL;
import de.lmu.navigator.server.database.CityMySQL;
import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.database.StreetMySQL;
import de.lmu.navigator.server.model.Settings;

public class ClearData extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Settings settings;
	private CityMySQL dbCity;
	private StreetMySQL dbStreet;
	private BuildingMySQL dbBuilding;
	private BuildingPartMySQL dbBuildingPart;
	private FloorMySQL dbFloor;
	private RoomMySQL dbRoom;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		settings = new Settings();
	
		/* load GET parameter */
		String action = request.getParameter("type");
		boolean success = false;
		
		if (action.equals("city")) {
			
			try {
				dbCity = new CityMySQL();
				success = dbCity.deleteAllCities();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbCity.close();
			}
			
			if (success) {
				settings.addVersion("cleared all Cities");
				out.println("Successfully cleared all Cities");
			}
			else {
				return;
			}

		} else if (action.equals("street")) {

			try {
				dbStreet = new StreetMySQL();
				success = dbStreet.deleteAllStreets();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbStreet.close();
			}
			
			if (success) {
				settings.addVersion("cleared all Streets");
				out.println("Successfully cleared all Streets");
			}
			else {
				return;
			}

		} else if (action.equals("building")) {
			
			try {
				dbBuilding = new BuildingMySQL();
				success = dbBuilding.deleteAllBuildings();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbBuilding.close();
			}
			
			if (success) {
				settings.addVersion("cleared all Buildings");
				out.println("Successfully cleared all Buildings");
			}
			else {
				return;
			}

		} else if (action.equals("buildingpart")) {
			
			try {
				dbBuildingPart = new BuildingPartMySQL();
				success = dbBuildingPart.deleteAllBuildingParts();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbBuildingPart.close();
			}
			
			if (success) {
				settings.addVersion("cleared all BuildingParts");
				out.println("Successfully cleared all BuildingParts");
			}
			else {
				return;
			}

		} else if (action.equals("floor")) {
			
			try {
				dbFloor = new FloorMySQL();
				success = dbFloor.deleteAllFloors();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbFloor.close();
			}
			
			if (success) {
				settings.addVersion("cleared all Floors");
				out.println("Successfully cleared all Floors");
			}
			else {
				return;
			}

		} else if (action.equals("room")) {

			try {
				dbRoom= new RoomMySQL();
				success = dbRoom.deleteAllRooms();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbRoom.close();
			}
			
			if (success) {
				settings.addVersion("cleared all Rooms");
				out.println("Successfully cleared all Rooms");
			}
			else {
				return;
			}

		} else {
			out.println("no parameter specified");
			return;
		}

		/* redirect back to DataOverview */
		
		out.println("<script>window.location.href = \"./\";</script>");
		
		response.sendRedirect(".");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
