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

import de.lmu.navigator.server.database.BuildingPartMySQL;
import de.lmu.navigator.server.database.FloorMySQL;
import de.lmu.navigator.server.database.RoomMySQL;
import de.lmu.navigator.server.model.BuildingPart;
import de.lmu.navigator.server.model.Floor;
import de.lmu.navigator.server.model.Room;
import de.lmu.navigator.server.model.Settings;

public class RoomOverview extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String floorToProcess;
	private BuildingPartMySQL dbBuildingPart;
	private FloorMySQL dbFloor;
	private RoomMySQL dbRoom;
	private ArrayList<Room> rooms;
	private Settings settings;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		floorToProcess = request.getParameter("floor");
		
		if (floorToProcess != null) {
			
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
			
			// load additional information for output
			BuildingPart buildingPart = null;
			try {
				dbBuildingPart = new BuildingPartMySQL();
				buildingPart = dbBuildingPart.loadSingleBuildingPart(f.getBuildingPart());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbBuildingPart.close();
			}
			

			// TEMP JS TEST
			out.println("<script src=\"//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script>");
			out.println("<script src=\"../js/iframe.js\"></script>");
			
			/* print table */
			out.println("<h1>"+buildingPart.getAddress()+" - "+f.getLevel()+"</h1>");
						
			/* display all Rooms as HTML */
			out.println("<link href=\"../css/overview.css\" rel=\"stylesheet\" type=\"text/css\"/>");
			out.println("<form name=\"coordinates\" id=\"form\" action=\"rooms?floor="+floorToProcess+"\" method=\"post\" enctype=\"multipart/form-data\">");
			out.println("<input type=\"submit\" value=\"Update Coordinates\">");

			out.println("<table class=\"coordinates\">");
			out.println("<thead><tr>");
			out.println("	<th>Code</th>");
			out.println("	<th>Name</th>");
			out.println("	<th>PosX</th>");
			out.println("	<th>PosY<th>");
			out.println("	<th><th>");
			out.println("</tr></thead>");
			out.println("<tbody>");
			
			/* loop over Rooms */
			rooms = f.getRooms();

			for (Room r : rooms) {
				
				boolean disabled = !r.getVisibility();
				
				out.println("<tr floor=\""+r.getCode()+"\">");
				out.println("<td>"+r.getCode()+"</td>");
				out.println("<td>"+r.getName()+"</td>");
				if (r.getPosX() == 0 || r.getPosY() == 0) {
					if (!disabled) {
						out.println("<td><input type=\"text\" size=\"10\" name=\"lat-"+r.getCode()+"\" value=\""+r.getPosX()+"\" disabled=\"disabled\"></td>");
						out.println("<td><input type=\"text\" size=\"10\" name=\"lng-"+r.getCode()+"\" value=\""+r.getPosY()+"\" disabled=\"disabled\"></td>");
					} else {
						out.println("<td><input type=\"text\" size=\"10\" name=\"lat-"+r.getCode()+"\" value=\""+r.getPosX()+"\" class=\"missing\"></td>");
						out.println("<td><input type=\"text\" size=\"10\" name=\"lng-"+r.getCode()+"\" value=\""+r.getPosY()+"\" class=\"missing\"></td>");
					}

				} else {
					if (!disabled) {
						out.println("<td><input type=\"text\" size=\"10\" name=\"lat-"+r.getCode()+"\" value=\""+r.getPosX()+"\" disabled=\"disabled\"></td>");
						out.println("<td><input type=\"text\" size=\"10\" name=\"lng-"+r.getCode()+"\" value=\""+r.getPosY()+"\" disabled=\"disabled\"></td>");
					} else {
						out.println("<td><input type=\"text\" size=\"10\" name=\"lat-"+r.getCode()+"\" value=\""+r.getPosX()+"\"></td>");
						out.println("<td><input type=\"text\" size=\"10\" name=\"lng-"+r.getCode()+"\" value=\""+r.getPosY()+"\"></td>");
					}
				}
				if (disabled)
					out.println("<td><a href=\"roomVisibility?action=hide&floor="+floorToProcess+"&room="+r.getCode()+"\">disable</a>, <a href=\"javascript:scrollToIFrame()\" class=\"position\">position room manually</a></td>");
				else
					out.println("<td><a href=\"roomVisibility?action=show&floor="+floorToProcess+"&room="+r.getCode()+"\">enable</a></td>");

				out.println("</tr>");
			}
			
			out.println("</tbody></table>");
			out.println("<input type=\"submit\" value=\"Update Coordinates\"></form>");
		}
		else {
			out.println("wrong parameters");
		}
		
		out.println("<a href=\".\">back to Floor overview</a>");
		
		
		/* print canvas */
		out.println("<br /><br /><br />");
		out.println("<iframe src=\"../canvas.html?floor="+floorToProcess+"\" width=\"100%\" height=\"620\" scrolling=\"no\" frameBorder=\"0\" id=\"TileViewer\">");
				
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		settings = new Settings();

		if (rooms == null) {
			out.println("error, first need to GET /data/rooms");
			return;
		}
		
		/* fetch all parameters */
		double posX = 0.0, posY = 0.0;
		boolean success = false;
		
		/* load POST parameters */
		HashMap<String, double[]> listOfCoordinates = extractCoordinates(request);
		
		/* update coordinates in DB */
		for (Room r : rooms) {
			
			double[] coord = listOfCoordinates.get(r.getCode());
			
			if (coord != null) {
				posX = coord[0];
				posY = coord[1];
							
				try {
					dbRoom = new RoomMySQL();
					success = dbRoom.updateRoomPosition(floorToProcess, r.getName(), (int)posX, (int)posY);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbRoom.close();
				}

				if (!success) {
					out.println("error updating roomPositions for floor ["+floorToProcess+"]<br />");
					return;				
				}
			}
		}
		
		out.println("Successfully updated all room positions for floor<br />");
		settings.addVersion("updated room positions manually for floor ["+floorToProcess+"]");
		
		/* return to same window */ 
		response.sendRedirect("rooms?floor="+floorToProcess);
		
		/* return to Overview */
//		response.sendRedirect("./");
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
					String[] part = fileItem.getFieldName().split("-", 2);
					String buildingCode = "";
					
					if (part.length >= 2) {
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
						System.out.println("invalid form field (see split '-')");
					}
				}
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		/* convert both double[] to HashMap<String, double[]>*/
		for (Room r : rooms) {
			if (lat.get(r.getCode()) != null && lng.get(r.getCode()) != null)	// catch NPE for disabled form fields
				result.put(r.getCode(), new double[] {lat.get(r.getCode()), lng.get(r.getCode())});
		}
		
		return result;
	}
}
