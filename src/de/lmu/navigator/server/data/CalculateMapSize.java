package de.lmu.navigator.server.data;

import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.model.Floor;
import de.lmu.navigator.server.model.Settings;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CalculateMapSize extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String pathToPNG;
	private ArrayList<File> pngFiles;
	private FloorMySQL dbFloor;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	
		/* load settings: pathToPNG */
		Settings settings = new Settings();	
		pathToPNG = settings.getPathToPNG();
		
		// TODO: maybe check if path was set correctly */
		
		/* load	MapUri from DB (5_floor) */
		setPngFiles(new ArrayList<File>());
		ArrayList<Floor> floors = new ArrayList<Floor>();
		Dimension d;
		
		try {
			dbFloor = new FloorMySQL();
			floors = dbFloor.loadFilteredFloors("", "", true);
		
			for(Floor f : floors) {
				// rename from .pdf to .png
				String filename = f.getMapUri();
				filename = FileUtils.renameFileExtension(filename, "png");
				
				// check if file exists
				File file = new File(pathToPNG, filename);
	
				if (file.exists()) {
					// call getImageDimension()
					d = FileUtils.getImageDimension(file);
	
					// cast from double -> int
					boolean success = dbFloor.updateMapSize((int)d.getWidth(), (int)d.getHeight(), f.getCode());
					if (!success) {
						out.println("Error updating record in DB");
						return;
					}
				} else {
                    System.out.println("Could not find png file for pdf: " + f.getMapUri());
                }
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settings.addVersion("updated mapSize of all floorPlans");
			dbFloor.close();
		}
		
		/* redirect back to DataOverview */
		out.println("Successfully updated all floors");
		response.sendRedirect("../");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}


	public ArrayList<File> getPngFiles() {
		return pngFiles;
	}


	public void setPngFiles(ArrayList<File> pngFiles) {
		this.pngFiles = pngFiles;
	}

}
