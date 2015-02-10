package de.lmu.navigator.server.model;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class Building {

	private int id;
	private String code;
    private String streetCode;
    private String displayName;
    private double lat;
    private double lng;
    private List<Floor> buildingParts;

    public Building(int id, String code, String streetCode, String displayName, double lat,
			double lng, List<Floor> buildingParts) {
		super();
		this.id = id;
		this.code = code;
		this.streetCode = streetCode;
		this.displayName = displayName;
		this.lat = lat;
		this.lng = lng;
		this.buildingParts = buildingParts;
	}
    
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public Building() {
		
	}
	
	
	@Override
	public String toString() {
		return "Building [id=" + id + ", code=" + code + ", streetCode="
				+ streetCode + ", displayName=" + displayName + ", lat=" + lat
				+ ", lng=" + lng + ", buildingParts=" + buildingParts + "]";
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

	public String getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(String streetCode) {
		this.streetCode = streetCode;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@XmlTransient
	public List<Floor> getBuildingParts() {
		return buildingParts;
	}

	public void setBuildingParts(List<Floor> floors) {
		this.buildingParts = floors;
	}
	
	
}
