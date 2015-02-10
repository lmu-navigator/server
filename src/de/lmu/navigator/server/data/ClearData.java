package de.lmu.navigator.server.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.Settings;

public class ClearData extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Settings settings;
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
		
		if (action.equals("mapsize")) {
			
			try {
				dbFloor = new FloorMySQL();
				success = dbFloor.clearAllMapSizes();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbFloor.close();
			}
			
			if (success) {
				settings.addVersion("cleared all mapSizes");
				out.println("Successfully cleared all mapSize");
			}
			else {
				return;
			}
			
		} else if (action.equals("roomposition")) {
			
			try {
				dbRoom = new RoomMySQL();
				success = dbRoom.clearAllRoomPositions();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbRoom.close();
			}
			
			if (success) {
				settings.addVersion("cleared all roomPositions");
				out.println("Successfully cleared all roomPositions");
			} else {
				return;
			}
			
		} else {
			out.println("no parameter specified");
			return;
		}

		/* redirect back to DataOverview */
		response.sendRedirect("../data/");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
