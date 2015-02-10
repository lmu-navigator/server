package de.lmu.navigator.server.model;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import de.lmu.navigator.server.database.FloorMySQL;

public class BuildingPart {

	private int id;
    private String code;
    private String buildingCode;
    private String address;
    private List<Floor> floors;

    public BuildingPart(int id, String code, String buildingCode, String address, List<Floor> floors) {
		super();
		this.id = id;
		this.code = code;
		this.buildingCode = buildingCode;
		this.address = address;
		this.floors = floors;
	}
    
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public BuildingPart() {
		
	}

	
	@Override
	public String toString() {
		return "BuildingPart [id=" + id + ", code=" + code + ", buildingCode="
				+ buildingCode + ", address=" + address + ", floors=" + floors
				+ "]";
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

	public String getBuildingCode() {
		return buildingCode;
	}

	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@XmlTransient
	public List<Floor> getFloors() {
		
		FloorMySQL db;
		try {
			db = new FloorMySQL();
			this.floors = db.loadFilteredFloors("", this.code, false);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return floors;
	}	
	
	/**
	 * Load all floors, even the hidden ones
	 * @return
	 */
	@XmlTransient
	public List<Floor> getFloorsAlsoHiddenOnes() {
		
		FloorMySQL db;
		try {
			db = new FloorMySQL();
			this.floors = db.loadFilteredFloors("", this.code, true);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return floors;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}
	
	
}
