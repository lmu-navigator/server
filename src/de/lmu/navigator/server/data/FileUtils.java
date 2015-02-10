package de.lmu.navigator.server.data;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	
	/**
	 * Checks whether the file exists on server-side
	 * 
	 * @param path		absolute path to the file
	 * @param filename	filename
	 * @return			true, false
	 */
	public static boolean fileExists(String path, String filename) {
		  File f = new File(path, filename);
		  return f.exists();
	}
		

	/**
	 * Gets image dimensions for given file 
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
		int pos = imgFile.getName().lastIndexOf(".");
		if (pos == -1)
			throw new IOException("No extension for file: "
					+ imgFile.getAbsolutePath());
		String suffix = imgFile.getName().substring(pos + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		if (iter.hasNext()) {
			ImageReader reader = iter.next();
			try {
				ImageInputStream stream = new FileImageInputStream(imgFile);
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} catch (IOException e) {
				e.printStackTrace();
				// log.warn("Error reading: " + imgFile.getAbsolutePath(), e);
			} finally {
				reader.dispose();
			}
		}
		throw new IOException("Not a known image file: "
				+ imgFile.getAbsolutePath());
	}
	
	
	// File Extension Handling
	// Credit to http://www.rgagnon.com/javadetails/java-0541.html
	
	public static String renameFileExtension(String source, String newExtension) {
		String target;
		String currentExtension = getFileExtension(source);

		if (currentExtension.equals("")) {
			target = source + "." + newExtension;
		} else {
			target = source.replaceFirst(Pattern.quote("." + currentExtension)
					+ "$", Matcher.quoteReplacement("." + newExtension));

		}
		return target;
	}

	public static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}

	public static String removeExtension(String fileName) {
		int extPos = fileName.lastIndexOf(".");
		if (extPos == -1) {
			return fileName;
		} else {
			return fileName.substring(0, extPos);
		}
	}

}
