package de.lmu.navigator.server.upload;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.fileupload.FileItem;

public class CsvReader {
	
	final static Charset charset = Charset.forName("ISO-8859-1");
	final static String cvsSplitBy = ";";
	
	/**
	 * Match the first line of the CSV file with the parameters passed as a String[] 
	 * @param parameters	the field names from the first CSV line
	 * @return
	 */
	public boolean validateParams(FileItem file, String[] parameters) {

		BufferedReader br = null;
		String firstLine = "";
	 
		try {
			br = new BufferedReader(new InputStreamReader(file.getInputStream(), charset));
			
			// only read first line
			firstLine = br.readLine();
			
			if (firstLine == null)
				return false;
			
			String[] values = firstLine.split(cvsSplitBy);

			// compare parameters
			for (String p : values) {
				if (!Arrays.asList(parameters).contains(p))
					return false;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Checks whether the filename ends on .csv
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean validateFileType(String fileName) {
	
		String[] fileParts = fileName.split("\\.(?=[^\\.]+$)");
		String fileType = fileParts[1];
		
		return fileType.equalsIgnoreCase("csv");
	}

}
