/*****************************************************************/
/* Copyright 2013 Code Strategies                                */
/* This code may be freely used and distributed in any project.  */
/* However, please do not remove this credit if you publish this */
/* code in paper or electronic form, such as on a web site.      */
/*****************************************************************/

package de.lmu.navigator.server.upload;

import de.lmu.navigator.server.database.StreetMySQL;
import de.lmu.navigator.server.model.Settings;
import de.lmu.navigator.server.model.Street;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreetServlet extends HttpServlet {
	
	// params to validate CSV file against
	final String[] csvParams = new String[] {"Straßencode", "Stadtcode", "Straße", "Dateiname"};

	private static final long serialVersionUID = 1L;
	private Settings settings;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		settings = new Settings();
		
		/* load file */
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(request);
		if (!isMultipartContent) {
			out.println("No file has been sent to the server...<br/>");
			return;
		}
		out.println("Files being processed on server...<br/>");

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
			
		try {
			List<FileItem> fields = upload.parseRequest(request);
			
			if (fields.size() != 2) {
				out.println("Number of fields/files has been varied");
				return;
			}
			
			Iterator<FileItem> it = fields.iterator();
			
			// for NPE
			if (!it.hasNext()) {
				out.println("No fields found <br/>");
				return;
			}
			
			// consider all HTML form fields (hidden + file fields)
			while (it.hasNext()) {
				FileItem fileItem = it.next();

				if (fileItem.isFormField()) {
					// normal HTML form field
					out.println("> regular form field: "+ fileItem.getFieldName() + " = "
							+ fileItem.getString()+" <br/>");
					
					// kein switch(STRING) in Java 1.6
					
				} else {
					// file to process					
					out.println("> uploaded file: " + fileItem.getName()+" <br/>");
					
					/* check for CSV file */					
					if (!fileItem.getName().isEmpty()) {
						String fileName = fileItem.getName();
						
						if (CsvReader.validateFileType(fileName))
							out.println("File ends on .csv <br/>");
						else {
							out.println("<strong>Wrong file ending!</strong><br/>");
							return;
						}
					}
					
					/* validate header / column structure (line 0) */
					CsvReader csv = new CsvReader();
					
					boolean isValid = csv.validateParams(fileItem, csvParams);					
					if (isValid)
						out.println("Valid CSV parameters <br />");
					else {
						out.println("<strong>Invalid CSV!</strong> <br />");
						return;
					}
					

					/* import lines (1;n) */
					List<Street> result = read(fileItem.getInputStream());
					out.println("Loaded "+ result.size() +" rows into memory <br />");

					
					/* save lines to DB */
					int rowsSaved = saveToDB(result);
					
					if (rowsSaved == result.size()) {
						settings.addVersion("uploaded CSV for 2_street");
						out.println("Saved all rows into the DB <br />");
					} else if (rowsSaved == 0) { 
						out.println("<strong>Error saving to DB!</strong> <br />");
						return;
					} else {
						out.println("Saved "+ result.size() +" rows to DB <br />");
					}
				}
			}
			
			/* TODO handle additions, deletions */		 
				
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Load values from CSV file into memory
	 * @param input
	 * @return
	 */
	private List<Street> read(InputStream input) {
		List<Street> list = new ArrayList<Street>();
		
		BufferedReader br = null;
		String line = "";
		
		try {
			br = new BufferedReader(new InputStreamReader(input, CsvReader.charset));
			
			// ignore first line (only parameters)
			br.readLine();
			
			while ((line = br.readLine()) != null) {
	 
				String[] v = line.split(CsvReader.cvsSplitBy);
				
				Street e = new Street();
		
				/* CSV Naming Convention
				 * 	0: Stra�encode		!
				 * 	1: Stadtcode		!
				 * 	2: Stra�e			!
				 *  3: Dateiname  
				 */
				
				e.setCode(v[0]);
				e.setCityCode(v[1]);
				e.setName(v[2]);
				
				list.add(e);
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	
	private int saveToDB(List<Street> input) {
		
		StreetMySQL db;
		int i = 0;
		
		try {
			db = new StreetMySQL();
			
			for (Street c : input) {
				int n = db.writeStreet(c.getCode(), c.getCityCode(), c.getName());
				if (n > 0)	i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}	
		
		return i;
	}
	
}
