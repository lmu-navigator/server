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

public class UploadOverview extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String[] fileNames = {"01_Stadt", "02_Strasse", "03_Bauwerk", "04_BauteilHaus", "05_Geschoss", "06_Raum"};
	private String[] fileAction = {"city", "street", "building", "buildingpart", "floor", "room"};
	private String[] fileSchema = {"Stadtcode;Stadt;Dateiname", "Stadtcode;Stadt;Dateiname", "BWCode;Stadtcode;Straﬂencode;Benennung", "BTCode;Stadtcode;Straﬂencode;BWCode;BauteilHaus", "GCode;Stadtcode;Straﬂencode;BWCode;BTCode;Geschoss;Benennung;Dateiname", "GCode;Stadtcode;Straﬂencode;BWCode;BTCode;Geschoss;Benennung;Raumnummer;RCode;Dateiname"};
	private String filetype = "csv";
	private int[] numRows = new int[6];

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		/* Calculate number of row for each table */
		try {
			CityMySQL dbCity = new CityMySQL();
			StreetMySQL dbStreet = new StreetMySQL();
			BuildingMySQL dbBuilding = new BuildingMySQL();
			BuildingPartMySQL dbBuildingPart = new BuildingPartMySQL();
			FloorMySQL dbFloor= new FloorMySQL();
			RoomMySQL dbRoom = new RoomMySQL();
			
			numRows[0] = dbCity.numberOfCities();
			numRows[1] = dbStreet.numberOfStreets();
			numRows[2] = dbBuilding.numberOfBuildings();
			numRows[3] = dbBuildingPart.numberOfBuildingParts();
			numRows[4] = dbFloor.numberOfFloors();
			numRows[5] = dbRoom.numberOfRooms();
			
			dbCity.close();
			dbStreet.close();
			dbBuilding.close();
			dbBuildingPart.close();
			dbFloor.close();
			dbRoom.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* print table */
		out.println("<h1>Upload of CSV files</h1>");
					
		/* display all Rooms as HTML */
		out.println("<link href=\"../css/overview.css\" rel=\"stylesheet\" type=\"text/css\"/>");
		
		out.println("<table>");
		out.println("<thead><tr>");
		out.println("	<th>Rows</th>");
		out.println("	<th>Filename</th>");
		out.println("	<th>Action</th>");
		out.println("	<th></th>");
		out.println("</tr></thead>");
		out.println("<tbody>");
		
		for (int i = 0; i < fileNames.length; i++) {
		
			out.println("<tr>");
			
			if (numRows[i] > 0)
				out.println("	<td class=\"okay\">"+numRows[i]+"</td>");
			else
				out.println("	<td class=\"empty\">"+numRows[i]+"</td>");
				
			out.println("	<td><strong>"+fileNames[i]+"."+filetype+"</strong></td>");
			
			out.println("	<td><form name=\"form1\" id=\"form"+i+"\" action=\""+fileAction[i]+"\" method=\"post\" enctype=\"multipart/form-data\">");
			out.println("		<select name=\"action\">");
			out.println("			<option value=\"add\">Add</option>");
			out.println("			<option value=\"dif\">Diff (todo)</option>");
			out.println("		</select>");
			out.println("		<input type=\"file\" size=\"50\" name=\"csvFile\">");
			out.println("		<input type=\"submit\" value=\"Upload\">");
			out.println("	</form></td>");
			
			out.println("	<td><a href=\"clear?type="+fileAction[i]+"\">Clear all data</a></td>");
			out.println("</tr>");
		}
		
		out.println("</tbody></table>");

		
		
		
		out.println("<br />");
		out.println("<a href=\"..\">back to Overview</a>");
		
		out.println("<br /><br />");
		
		out.println("<h1>Schema for CSV files</h1>");

		
		for (int i = 0; i < fileNames.length; i++) {
			
			out.println("<strong>"+fileNames[i]+"."+filetype+":</strong> "+fileSchema[i]+"<br />\n");
		}
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
