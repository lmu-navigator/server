package de.lmu.navigator.server.data;

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.model.Floor;
import de.lmu.navigator.server.model.Settings;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

public class CalculateMapSize extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	
		/* load settings: pathToPNG */
		Settings settings = new Settings();
		String pathToPNG = settings.getPathToPNG();

		FloorMySQL dbFloor = null;
		try {
			/* load	MapUri from DB (5_floor) */
			dbFloor = new FloorMySQL();
			ArrayList<Floor> floors = dbFloor.loadFilteredFloors("", "", true);
		
			for(Floor f : floors) {
				// rename from .pdf to .png
				String filename = f.getMapUri();
				filename = FileUtils.renameFileExtension(filename, "png");
				
				URL imageUrl = new URL(new URL(pathToPNG), filename);
				try {
					BufferedImage image = ImageIO.read(imageUrl);
					if (image != null) {
						boolean success = dbFloor.updateMapSize(image.getWidth(), image.getHeight(), f.getCode());
						if (!success) {
							throw new Exception("Error updating record in DB");
						}
					} else {
						System.out.println("Could not find png file for pdf: " + f.getMapUri());
					}
				} catch (Exception e) {
					System.out.println("Could not find png file for pdf: " + f.getMapUri());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settings.addVersion("updated mapSize of all floorPlans");
			if (dbFloor != null) {
				dbFloor.close();
			}
		}
		
		/* redirect back to DataOverview */
		out.println("Successfully updated all floors");
		response.sendRedirect("../");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
