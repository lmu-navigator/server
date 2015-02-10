package de.lmu.navigator.server.model;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class Street {

	private int id;
    private String code;
    private String name;
    private String cityCode;
    private List<Building> buildings;

    public Street(int id, String code, String name, String city, List<Building> buildings) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.cityCode = city;
		this.buildings = buildings;
	}
    
	/**
	 * for Jersey: must have no-argument constructor
	 */
	public Street() {
		
	}

	@Override
	public String toString() {
		return "Street [id=" + id + ", code=" + code + ", name=" + name
				+ ", city=" + cityCode + ", buildings=" + buildings + "]";
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@XmlTransient
	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	
}
