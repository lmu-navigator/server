package de.lmu.navigator.server.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.Settings;

public class ChangeRoomVisibility extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private RoomMySQL dbRoom;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
		String floorCode = request.getParameter("floor");
		String roomCode = request.getParameter("room");
		
		boolean hideRoom = false;
		
		if (action.equals("hide"))
			hideRoom = true;
		else if (action.equals("show"))
			hideRoom = false;

		/* load settings: pathToPNG */
		Settings settings = new Settings();	
		
		try {
			dbRoom = new RoomMySQL();
			dbRoom.updateRoomVisibility(floorCode, roomCode, hideRoom);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settings.addVersion("changed visibility of room "+roomCode);
			dbRoom.close();
		}
		
		/* redirect back to DataOverview */
		out.println("Successfully updated room visibility");
		response.sendRedirect("./rooms?floor="+floorCode);
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
