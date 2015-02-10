package de.lmu.navigator.server.data;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.model.Settings;

public class ChangeFloorVisibility extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private FloorMySQL dbFloor;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
		String buildingPart = request.getParameter("buildingPart");
		String floorCode = request.getParameter("floor");
		
		boolean hideFloor = false;
		
		if (action.equals("hide"))
			hideFloor = true;
		else if (action.equals("show"))
			hideFloor = false;

		/* load settings: pathToPNG */
		Settings settings = new Settings();	
		
		try {
			dbFloor = new FloorMySQL();
			dbFloor.updateFloorVisibility(buildingPart, floorCode, hideFloor);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settings.addVersion("changed visibility of floor "+floorCode);
			dbFloor.close();
		}
		
		/* redirect back to DataOverview */
		out.println("Successfully updated floor visibility");
		response.sendRedirect("./");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
