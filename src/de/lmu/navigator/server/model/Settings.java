package de.lmu.navigator.server.model;

import de.lmu.navigator.server.database.SettingsMySQL;
import de.lmu.navigator.server.database.VersionMySQL;

/**
 * Some internal settings, stored in the DB and used for the server-side
 * calculation of the room data. Basically there is no need for any RESTful
 * interaction.
 * 
 * @author Lukas Ziegler
 */
public class Settings {

    private String pathToPDF;
    private String pathToPNG;
    private String pathToHTTP;

    private SettingsMySQL db;
    private VersionMySQL dbVersion;

    public Settings() {
		super();
		
		try {
			db = new SettingsMySQL();
			this.pathToPDF = db.readPdfLocation();
			this.pathToPNG = db.readPngLocation();
			this.pathToHTTP = db.readHttpLocation();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			db.close();
		}
	}
	

	@Override
	public String toString() {
		return "Settings [pathToPDF=" + pathToPDF + ", pathToPNG=" + pathToPNG
				+ ", pathToHTTP=" + pathToHTTP + ", db=" + db + ", dbVersion="
				+ dbVersion + "]";
	}


	/** Getters & Setters **/
	
	public String getPathToPDF() {
		return pathToPDF;
	}

	public void setPathToPDF(String pathToPDF) {
		try {
			db = new SettingsMySQL();
			if (db.updatePdfLocation(pathToPDF))
				this.pathToPDF = pathToPDF;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	public String getPathToPNG() {
		return pathToPNG;
	}

	public void setPathToPNG(String pathToPNG) {
		try {
			db = new SettingsMySQL();
			if (db.updatePngLocation(pathToPNG))
				this.pathToPNG = pathToPNG;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	public String getPathToHTTP() {
		return pathToHTTP;
	}

	public void setPathToHTTP(String pathToHTTP) {
		try {
			db = new SettingsMySQL();
			if (db.updateHttpLocation(pathToHTTP))
				this.pathToHTTP = pathToHTTP;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	
	public Version getVersion() {
		Version v = null;
		try {
			dbVersion = new VersionMySQL();
			v = dbVersion.loadVersion();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dbVersion.close();
		}
		return v;
	}

	public void addVersion(String log) {
		try {
			dbVersion = new VersionMySQL();
			dbVersion.addVersion(log);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dbVersion.close();
		}
	}
	
	

}
