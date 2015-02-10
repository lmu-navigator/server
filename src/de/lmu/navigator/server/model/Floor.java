package de.lmu.navigator.server.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

import de.lmu.navigator.server.database.RoomMySQL;

public class Floor {

	private int id;
	private String code;
	private String buildingPart;
	private String level;
	private String name;
	private String mapUri;
	private int mapSizeX;
	private int mapSizeY;
	private boolean hidden;
	private ArrayList<Room> rooms;
	
	public Floor(int id, String code, String buildingPart, String level,
			String name, String mapURI, int mapSizeX, int mapSizeY, boolean hidden, ArrayList<Room> rooms) {
		super();
		this.id = id;
		this.code = code;
		this.buildingPart = buildingPart;
		this.level = level;
		this.name = name;
		this.mapUri = mapURI;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.hidden = hidden;
		this.rooms = rooms;
	}
	
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public Floor() {
		
	}

	
	
	@Override
	public String toString() {
		return "Floor [id=" + id + ", code="
				+ code + ", buildingPart=" + buildingPart + ", level=" + level
				+ ", name=" + name + ", mapUri=" + mapUri + ", mapSizeX="
				+ mapSizeX + ", mapSizeY=" + mapSizeY + ", hidden=" + hidden
				+ ", rooms=" + rooms + "]";
	}

	
	/** Getters & Setters **/
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBuildingPart() {
		return buildingPart;
	}

	public void setBuildingPart(String buildingPart) {
		this.buildingPart = buildingPart;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMapUri() {
		return mapUri;
	}

	public void setMapUri(String mapURI) {
		this.mapUri = mapURI;
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public void setMapSizeX(int mapSizeX) {
		this.mapSizeX = mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}

	public void setMapSizeY(int mapSizeY) {
		this.mapSizeY = mapSizeY;
	}

	@XmlTransient
	public ArrayList<Room> getRooms() {
		
		RoomMySQL dbRoom = null;
		try {
			dbRoom = new RoomMySQL();
			this.rooms = dbRoom.loadAllRooms(this.code);
//			this.rooms = dbRoom.loadFilteredRooms("", this.code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbRoom.close();
		}
		
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	/**
	 * 0 = not hidden (visible), 1 = hidden
	 */
	@XmlTransient
	public boolean getVisibility() {
		return hidden;
	}

	/**
	 * 0 = not hidden (visible), 1 = hidden
	 */
	public void setVisibile(boolean hidden) {
		this.hidden = hidden;
	}
	
}
