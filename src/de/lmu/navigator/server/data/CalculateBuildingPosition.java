package de.lmu.navigator.server.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.lmu.navigator.server.database.BuildingMySQL;
import de.lmu.navigator.server.model.Building;
import de.lmu.navigator.server.model.Settings;

public class CalculateBuildingPosition extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Building> buildings;
	private BuildingMySQL dbBuilding;
	private Settings settings;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		/* load	all Buildings from DB (3_building) */
		buildings = new ArrayList<Building>();
		try {
			dbBuilding = new BuildingMySQL();
			buildings = dbBuilding.loadAllBuildings();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbBuilding.close();
		}
		
		if (buildings == null) {
			out.println("No buildings in database, please use the CSV upload functionality.");
			out.println("<br />");
			out.println("<a href=\"..\">back to Overview</a>");
			return;
		}
		
		/* display all Buildings as HTML */
		out.println("<link href=\"../css/overview.css\" rel=\"stylesheet\" type=\"text/css\"/>");

		/* load JavaScript files */
//		out.println("<script src=\"../js/jquery.js\"></script>");
//		out.println("<script src=\"../js/CalculateBuildingPosition.js\"></script>");
		
		out.println("<form name=\"coordinates\" id=\"form\" action=\"buildings\" method=\"post\" enctype=\"multipart/form-data\">");
		out.println("<input type=\"submit\" value=\"Update Coordinates\">");
		
		out.println("<table>");
		out.println("<thead><tr>");
		out.println("	<th>Code</th>");
		out.println("	<th>DisplayName</th>");
		out.println("	<th>LatCoord</th>");
		out.println("	<th>LngCoord</th>");
		out.println("	<th></th>");
		out.println("</tr></thead>");
		out.println("<tbody>");
		
		for (Building b : buildings) {
			out.println("<tr>");
			out.println("<td>"+b.getCode()+"</td>");
			
			if (b.getLat() == 0.0 || b.getLng() == 0.0)		// not the nicest way, but should be sufficient for here, no range check needed
				out.println("<td class=\"missing\">"+b.getDisplayName()+"</td>");
			else
				out.println("<td>"+b.getDisplayName()+"</td>");
			
			out.println("<td><input type=\"text\" class=\"foo\" size=\"10\" name=\"lat-"+b.getCode()+"\" value=\""+b.getLat()+"\"></td>");
			out.println("<td><input type=\"text\" class=\"foo\" size=\"10\" name=\"lng-"+b.getCode()+"\" value=\""+b.getLng()+"\"></td>");
			
			if (b.getLat() == 0 || b.getLng() == 0)
				out.println("<td><a target=\"_blank\" href=\"http://maps.googleapis.com/maps/api/geocode/json?address="+b.getDisplayName()+"+Munich\">findPosition</a></td>");
			else
				out.println("<td></td>");
			
			out.println("</tr>");
		}

		out.println("</tbody></table>");

		out.println("<input type=\"submit\" value=\"Update Coordinates\"></form>");
		
		
		out.println("<br />");
		out.println("<a href=\".\">back to Overview</a>");
		out.println("<br />");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		settings = new Settings();

		if (buildings == null) {
			out.println("error, first need to GET /calculate/buildings");
			return;
		}
		
		/* fetch all parameters */
		double lat = 0.0, lng = 0.0;
		boolean success = false;
		
		/* load POST parameters */
		HashMap<String, double[]> listOfCoordinates = extractCoordinates(request);
		
		/* update coordinates in DB */
		for (Building b : buildings) {
			
			double[] coord = listOfCoordinates.get(b.getCode());
			
			if (coord != null) {
				lat = coord[0];
				lng = coord[1];
							
				try {
					dbBuilding = new BuildingMySQL();
					success = dbBuilding.updateBuildingPosition(b.getCode(), lat, lng);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbBuilding.close();
				}

				if (!success) {
					out.println("error updating buildingPosition for building ["+b.getCode()+"]<br />");
					return;				
				}
			}
		}
		
		out.println("Successfully updated all building coordinates<br />");
		settings.addVersion("updated building coordinates");
		response.sendRedirect("buildings");
	}

	
	private HashMap<String, double[]> extractCoordinates(HttpServletRequest request) {
		
		HashMap<String, double[]> result = new HashMap<String, double[]>();
		
		HashMap<String, Double> lat = new HashMap<String, Double>();
		HashMap<String, Double> lng = new HashMap<String, Double>();
		
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(request);
		if (!isMultipartContent) {
			System.out.println("No file has been sent to the server...<br/>");
			return null;
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			List<FileItem> fields = upload.parseRequest(request);
			Iterator<FileItem> it = fields.iterator();
			
			// for NPE
			if (!it.hasNext()) {
				System.out.println("No fields found <br/>");
				return null;
			}
			
			// consider all HTML form fields (hidden + file fields)
			while (it.hasNext()) {
				
				FileItem fileItem = it.next();
				boolean isFormField = fileItem.isFormField();
				
				if (isFormField) {
					
					/* debug info for HTML form fields */
//					System.out.println("<td>regular form field</td><td>FIELD NAME: "
//							+ fileItem.getFieldName() + "<br/>STRING: "
//							+ fileItem.getString());
					
					/* split field name into {lat/lng}-{buildingCode} */
					String[] part = fileItem.getFieldName().split("-");
					String buildingCode = "";
					
					if (part.length == 2) {
						buildingCode = part[1];

						if (part[0].equals("lat")) {
							try {
								lat.put(buildingCode, Double.parseDouble(fileItem.getString()));								
							} catch(NumberFormatException e) {
								lat.put(buildingCode, 0.0);		// for all non-numerical input, cast to 0
							}
						} else if (part[0].equals("lng")) {
							try {
								lng.put(buildingCode, Double.parseDouble(fileItem.getString()));								
							} catch(NumberFormatException e) {
								lng.put(buildingCode, 0.0);		// for all non-numerical input, cast to 0
							}
						}
					} else {
						System.out.println("invalid form field");
					}
				}
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		/* convert both double[] to HashMap<String, double[]>*/
		for (Building b : buildings) {
			result.put(b.getCode(), new double[] {lat.get(b.getCode()), lng.get(b.getCode())});
		}
		
		return result;
	}
	

}
