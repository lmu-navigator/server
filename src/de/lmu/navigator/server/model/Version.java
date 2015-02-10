package de.lmu.navigator.server.model;

import javax.xml.bind.annotation.XmlTransient;

import de.lmu.navigator.server.database.SettingsMySQL;

/**
 * @author Lukas Ziegler
 */
public class Version {

    private int version;
    private int timestamp;
    private String log;
    
    private SettingsMySQL db;
	
	public Version(int version, int timestamp, String log) {
		super();
		this.version = version;
		this.timestamp = timestamp;
		this.log = log;
	}
	
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public Version() {
		
	}


	@Override
	public String toString() {
		return "Version [version=" + version + ", timestamp=" + timestamp
				+ ", log=" + log + ", db=" + db + "]";
	}
	


	/** Getters & Setters **/

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@XmlTransient
	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
}
