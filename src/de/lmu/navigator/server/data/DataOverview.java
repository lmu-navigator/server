package de.lmu.navigator.server.data;

import de.lmu.navigator.server.database.BuildingPartMySQL;
import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.BuildingPart;
import de.lmu.navigator.server.model.Floor;
import de.lmu.navigator.server.model.Settings;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DataOverview extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static String pathToPDF;
	private static String pathToPNG;
	private static String pathToHTTP;

	private BuildingPartMySQL dbBuildingPart;
	private RoomMySQL dbRoom;
	
	private ArrayList<BuildingPart> buildingParts;
	
	/**
	 * Primary output for the Overview
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<link href=\"../css/overview.css\" rel=\"stylesheet\" type=\"text/css\"/>");
		
		/* configuration */
		Settings settings = new Settings();	
		pathToPDF = settings.getPathToPDF();
		pathToPNG = settings.getPathToPNG();
		pathToHTTP = settings.getPathToHTTP();
		
		/* Specify URIs for PDFs / PNGs */
		out.println("Specify relevant directories on server<br/>");
		
		out.println("<form name=\"location\" id=\"form\" action=\"\" method=\"post\" enctype=\"multipart/form-data\">");
		out.println("PDFs: <input type=\"text\" size=\"50\" name=\"pdfLocation\" value=\""+pathToPDF+"\"><br />");
		out.println("PNGs: <input type=\"text\" size=\"50\" name=\"pngLocation\" value=\""+pathToPNG+"\"><br />");
		out.println("HTTP path: <input type=\"text\" size=\"50\" name=\"httpLocation\" value=\""+pathToHTTP+"\">");
		out.println("<input type=\"submit\" value=\"Update\"></form>");

		/* Instructions and Links with Actions */
		
		out.println("1. set path for PDF/PNG<br />");
		out.println("2. <a href=\"calculate/mapsize\">calculate dimension</a> of PNGs (mapSize)<br />");
		out.println("3. calculate room positions for each floor/PDF individually (beta: <a href=\"calculate/roomposition?all=true\">batch update</a>)<br />");
		out.println("4. <a href=\"buildings\">set building coordinates</a><br />");

		out.println("<br />");

		out.println("TEMP: <a href=\"clear?type=mapsize\">clear mapSize</a>, <a href=\"clear?type=roomposition\">clear roomPositions</a><br />");

		out.println("<br />");
		
		/* load data from DB */
		Map<String, Integer> numRooms = null;
		Map<String, Integer> numNotPositioned = null;
		Map<String, Integer> numHidden = null;
		
		int totalNotPositioned = 0;
		
		try {
			dbBuildingPart = new BuildingPartMySQL();
			buildingParts = dbBuildingPart.loadBuildingParts();
			
			dbRoom = new RoomMySQL();
			numRooms = dbRoom.numRooms();
			numNotPositioned = dbRoom.numNotPositionedRooms();
			numHidden = dbRoom.numHiddenRooms();
			
			/* print values on screen */
			
			out.println("<table>");
			out.println("<thead><tr>");
			out.println("	<th>buildingPart</th>");
			out.println("	<th>floorCode</th>");
			out.println("	<th>street</th>");;
			out.println("	<th>floorLevel</th>");
			out.println("	<th><abbr title=\"number of rooms (including hidden rooms)\">numRooms</abbr></th>");
			out.println("	<th><abbr title=\"number of not positioned rooms\">notPos</abbr></th>");
			out.println("	<th>pdf</th>");
			out.println("	<th>mapSize</th>");
			out.println("	<th>positionRooms</th>");
			out.println("	<th>actions</th>");
			out.println("</tr></thead>");
			out.println("<tbody>");
	
			for(BuildingPart b : buildingParts) {
	
				for(Floor f : b.getFloorsAlsoHiddenOnes()) {
					
					boolean disabled = f.getVisibility();
					
					if (disabled)
						out.println("<tr class=\"disabled\">");
					else
						out.println("<tr>");
					
					out.println("<td>"+f.getBuildingPart()+"</td>");
					out.println("<td>"+f.getCode()+"</td>");
					
					// Street Address
					out.println("<td>"+b.getAddress()+"</td>");
					
					out.println("<td>"+f.getLevel()+"</td>");
					
					// numRooms
					if (numRooms.get(f.getCode()) == null)
						out.println("<td class=\"missing\">"+numRooms.get(f.getCode())+"</td>");
					else
						out.println("<td>"+numRooms.get(f.getCode())+"</td>");
					
					// numHidden rooms
					String hidden = "";
					
					if (numHidden.get(f.getCode()) != null)
						hidden = " / <a href=\"rooms?floor="+f.getCode()+"\"><abbr title=\"number of hidden / disabled rooms\">["+numHidden.get(f.getCode())+"]</abbr></a>";
					
					// roomsPositioned
					if (numNotPositioned.get(f.getCode()) != null) {
						out.println("<td class=\"missing\"><a href=\"rooms?floor="+f.getCode()+"\">"+numNotPositioned.get(f.getCode())+"</a>"+hidden+"</td>");
						
						if (!disabled)
							totalNotPositioned += numNotPositioned.get(f.getCode());
					}
					else 
						out.println("<td>0"+hidden+"</td>");
	
					// PDF URI
					out.println("<td><a href=\""+pathToHTTP+"/pdf/"+f.getMapUri()+"\" target=\"_blank\">"+f.getMapUri()+"</a></td>");
					
					// Map Size
					if (f.getMapSizeX() == 0 || f.getMapSizeY() == 0)
						out.println("<td class=\"missing\">"+f.getMapSizeX() +"x"+f.getMapSizeY()+"</td>");
					else
						out.println("<td>"+f.getMapSizeX() +"x"+f.getMapSizeY()+"</td>");
					
					// Actions
					out.println("<td><a href=\"calculate/roomposition?floor="+f.getCode()+"\">automated</a>, <a href=\"rooms?floor="+f.getCode()+"\">manual positioning</a></td>");
					
					if (disabled)
						out.println("<td><a href=\"floorVisibility?action=show&buildingPart="+b.getCode()+"&floor="+f.getCode()+"\">enable</a></td>");
					else
						out.println("<td><a href=\"floorVisibility?action=hide&buildingPart="+b.getCode()+"&floor="+f.getCode()+"\">disable</a></td>");
					
					out.println("</tr>");
				}
			}
	
			out.println("<tr>");
			out.println("<td></td><td></td><td></td><td>TOTAL</td>");
			out.println("<td></td><td class=\"missing\">"+totalNotPositioned+"</td>");
			out.println("<td></td><td></td><td></td><td></td>");
			out.println("</tr>");
			
			out.println("</tbody></table>");
			
			out.println("<br />");
			out.println("<a href=\"..\">back to Overview</a>");
			out.println("<br />");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbBuildingPart.close();
			dbRoom.close();
		}
		
	}
	
	
	
	/**
	 * Method call for POST update (e.g. when file pdfLocation gets updated)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		/* load file */
		HashMap<String, String> pathLocations = extractPathLocations(request); 
		pathToPDF = pathLocations.get("pdfLocation");
		pathToPNG = pathLocations.get("pngLocation");
		pathToHTTP = pathLocations.get("httpLocation");
		
		/* update pdfLocation in DB */

		Settings settings = new Settings();	
		settings.setPathToPDF(pathToPDF);
		settings.setPathToPNG(pathToPNG);
		settings.setPathToHTTP(pathToHTTP);
		
		/* reload GET page */
		doGet(request, response);
	}
	
	
	
	/**
	 * Extracts form fields from a Http POST Request.
	 * This method is already specific to 
	 * @param request
	 * @return
	 */
	private HashMap<String,String> extractPathLocations(HttpServletRequest request) {
		
		HashMap<String, String> locations = new HashMap<String, String>();
		
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
			
			// important! adjust the field size when adding a new parameter
			if (fields.size() != 3) {
				System.out.println("Number of fields/files has been varied");
				return null;
			}
			
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
					
					locations.put(fileItem.getFieldName(), fileItem.getString());
				}
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		return locations;
	}
	
}
